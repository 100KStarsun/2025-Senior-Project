package com.agora.app.backend.base;

import android.graphics.drawable.Drawable;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.file.Files;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.UUID;

public class Image implements Serializable {
    private static final long serialVersionUID = 2042010294253052140L;
    /**
     * Max record size in DynamoDB is 400kb so leaving a little bit of buffer.
     * 294KB of binary gets turned into 392KB of base64 encoded binary
     * because 3 bytes of binary gets encoded into one character in base64,
     * which takes 4 bytes of data to represent.
     */
    public static final int MAX_SIZE_OF_CHUNK_IN_BASE_64_CHARS = 392000;
    /**
     * This number times 294KB is the max file size: 4.41 MB
     */
    public static final int MAX_IMAGE_CHUNKS_ALLOWED = 15;
    /**
     * A UUID with an underscore {@code } appended to the end. After the underscore will be the phrase {@code --XX} where {@code XX} is number of imageChunks - 1
     */
    private String id;
    /**
     * An array of this image's {@code id} with {@code _xx} appended, where {@code xx} is the index of the array that id is at. The length of this array is how many chunks this image is broken into in the database
     */
    private String[] chunkIDs;
    /**
     * The data that makes up the image. Marked as transient so that this doesn't get written to the base64-encoded string
     */
    private transient byte[] data;
    private transient String base64;

    public Image (File image) throws IOException {
        this.id = UUID.randomUUID() + "";
        this.data = Files.readAllBytes(image.toPath());
        Base64.Encoder encoder = Base64.getEncoder().withoutPadding();
        this.base64 = encoder.encodeToString(this.data);
        this.chunkIDs = new String[base64.length()/MAX_SIZE_OF_CHUNK_IN_BASE_64_CHARS + (data.length % MAX_SIZE_OF_CHUNK_IN_BASE_64_CHARS == 0 ? 0 : 1)];
        if (this.chunkIDs.length > MAX_IMAGE_CHUNKS_ALLOWED) {
            throw new ImageTooBigException("The image you have selected (" + new DecimalFormat("###.00").format((float)data.length/1000000) + "MB) is too large. Max image size allowed is " + new DecimalFormat("###.00").format(((float)MAX_IMAGE_CHUNKS_ALLOWED * (MAX_SIZE_OF_CHUNK_IN_BASE_64_CHARS / 4) * 3)/1000000) + "MB.");
        }
        for (int i = 0; i < this.chunkIDs.length; i++) {
            this.chunkIDs[i] = this.id + "_" + new DecimalFormat("00").format(i);
        }
        this.id += "--" + new DecimalFormat("00").format(chunkIDs.length);
    }

    public Image (File image, String id) throws IOException {
        this.id = id.substring(0,36);
        this.data = Files.readAllBytes(image.toPath());
        Base64.Encoder encoder = Base64.getEncoder().withoutPadding();
        this.base64 = encoder.encodeToString(this.data);
        this.chunkIDs = new String[base64.length()/MAX_SIZE_OF_CHUNK_IN_BASE_64_CHARS + (data.length % MAX_SIZE_OF_CHUNK_IN_BASE_64_CHARS == 0 ? 0 : 1)];
        if (this.chunkIDs.length > MAX_IMAGE_CHUNKS_ALLOWED) {
            throw new ImageTooBigException("The image you have selected (" + new DecimalFormat("###.00").format((float)data.length/1000000) + "MB) is too large. Max image size allowed is " + new DecimalFormat("###.00").format(((float)MAX_IMAGE_CHUNKS_ALLOWED * (MAX_SIZE_OF_CHUNK_IN_BASE_64_CHARS / 4) * 3)/1000000) + "MB.");
        }
        for (int i = 0; i < this.chunkIDs.length; i++) {
            this.chunkIDs[i] = this.id + "_" + new DecimalFormat("00").format(i);
        }
        this.id += "--" + new DecimalFormat("00").format(chunkIDs.length);
    }

    private Image (String id, String[] chunkIDs, byte[] data, String base64) {
        this.id = id;
        this.chunkIDs = chunkIDs;
        this.data = data;
        this.base64 = base64;
    }

    public Drawable getDrawable () {
        return Drawable.createFromStream(new ByteArrayInputStream(this.data), this.id);
    }

    public static HashMap<String, Drawable> getDrawables (HashMap<String, Image> images) {
        HashMap<String, Drawable> drawables = new HashMap<>(images.size());
        for (String id : images.keySet()) {
            drawables.put(id, images.get(id).getDrawable());
        }
        return drawables;
    }

    public ImageChunk[] getChunks () {
        ImageChunk[] chunks = new ImageChunk[chunkIDs.length];
        for (int i = 0; i < chunks.length; i++) {
            String chunkData = this.base64.substring(i*MAX_SIZE_OF_CHUNK_IN_BASE_64_CHARS, Math.min((i+1)*MAX_SIZE_OF_CHUNK_IN_BASE_64_CHARS, this.base64.length()));
            chunks[i] = new ImageChunk(chunkIDs[i], chunkData);
        }
        return chunks;
    }

    public String[] getChunkBase64s () {
        ImageChunk[] chunks = this.getChunks();
        String[] chunkBase64s = new String[chunks.length];
        for (int i = 0; i < chunks.length; i++) {
            chunkBase64s[i] = chunks[i].getBase64();
        }
        return chunkBase64s;
    }

    public static Image fromChunks (ImageChunk[] chunks) {
        String id = chunks[0].getId().substring(0,36);
        String[] chunkIDs = new String[chunks.length];
        for (int i = 0; i < chunkIDs.length; i++) {
            chunkIDs[i] = id + "_" + new DecimalFormat("00").format(i);
        }
        id += "--" + new DecimalFormat("00").format(chunks.length);
        String base64 = "";
        for (ImageChunk chunk : chunks) {
            base64 += chunk.getBase64();
        }
        Base64.Decoder decoder = Base64.getDecoder();
        byte[] data = decoder.decode(base64);
        return new Image(id, chunkIDs, data, base64);
    }

    public byte[] getData () { return data; }
    public String getId () { return id; }
    public String[] getChunkIDs () { return chunkIDs; }
    public String getBase64 () { return base64; }

    /**
     * Used by the {@code LambdaHandler} class to turn the base64-encoded string back into an {@code Image}, using an {@code ObjectInputStream}, {@code ByteArrayInputStream}, and the {@code Base64.Decoder} class.
     *
     * @param encodedImage a base64 string that should have been created by the {@code toBase64String()} method
     * @return a {@code Image} object that holds all the data it did before it was converted to a base64 string
     */
    public static Image createFromBase64String (String encodedImage) {
        Base64.Decoder decoder = Base64.getDecoder();
        byte[] decodedBytes = decoder.decode(encodedImage);
        try (ByteArrayInputStream bytesIn = new ByteArrayInputStream(decodedBytes); ObjectInputStream objectIn = new ObjectInputStream(bytesIn)) {
            return (Image) objectIn.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Used by the {@code LambdaHandler} class to turn an {@code Image} object into a base64-encoded string for easy database storage. This is done using a {@code ObjectOutputStream}, {@code ByteArrayOutputStream}, and the {@code Base64.Encoder} class.
     *
     * @return a string containing the base64 representation of the {@code Image} object this method was called using.
     */
    public String toBase64String () {
        Base64.Encoder encoder = Base64.getEncoder();
        try (ByteArrayOutputStream bytesOut = new ByteArrayOutputStream(); ObjectOutputStream objectOut = new ObjectOutputStream(bytesOut)) {
            objectOut.writeObject(this);
            return encoder.encodeToString(bytesOut.toByteArray());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean equals (Object o) {
        if (o == null) { return false; }
        return Arrays.equals(this.data,((Image) o).data);
    }

    @Override
    public String toString () {
        return "ID: " + this.id + "\nChunks: " + Arrays.toString(chunkIDs) + "\nLength of Data: " + this.data.length + "\nData Hash: " + Arrays.hashCode(this.data) + "\nBase64: " + this.base64;
    }

    public static String[] getChunkIDsFromID (String id) {
        int count = Integer.parseInt(id.substring(38));
        String[] chunkIDs = new String[count];
        DecimalFormat formatter = new DecimalFormat("00");
        for (int i = 0; i < count; i++) {
            chunkIDs[i] = id.substring(0,36) + "_" + formatter.format(i);
        }
        return chunkIDs;
    }
}
