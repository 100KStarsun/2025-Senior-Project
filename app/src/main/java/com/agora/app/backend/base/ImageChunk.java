package com.agora.app.backend.base;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Base64;

public class ImageChunk implements Serializable, Comparable<ImageChunk> {
    private static final long serialVersionUID = 2042010294253052140L;
    private String id;
    private String base64;

    public ImageChunk (String id, String base64) {
        this.id = id;
        this.base64 = base64;
    }

    public ImageChunk (String id) {
        this.id = id;
    }

    public String getId () { return this.id; }
    public String getBase64 () { return this.base64; }

    /**
     * Used by the {@code LambdaHandler} class to turn the base64-encoded string back into an {@code ImageChunk}, using an {@code ObjectInputStream}, {@code ByteArrayInputStream}, and the {@code Base64.Decoder} class.
     *
     * @param encodedImageChunk a base64 string that should have been created by the {@code toBase64String()} method
     * @return a {@code ImageChunk} object that holds all the data it did before it was converted to a base64 string
     */
    public static ImageChunk createFromBase64String (String encodedImageChunk) {
        Base64.Decoder decoder = Base64.getDecoder();
        byte[] decodedBytes = decoder.decode(encodedImageChunk);
        try (ByteArrayInputStream bytesIn = new ByteArrayInputStream(decodedBytes); ObjectInputStream objectIn = new ObjectInputStream(bytesIn)) {
            return (ImageChunk) objectIn.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Used by the {@code LambdaHandler} class to turn an {@code ImageChunk} object into a base64-encoded string for easy database storage. This is done using a {@code ObjectOutputStream}, {@code ByteArrayOutputStream}, and the {@code Base64.Encoder} class.
     *
     * @return a string containing the base64 representation of the {@code ImageChunk} object this method was called using.
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
    public int compareTo (ImageChunk chunk) {
        return this.id.compareTo(chunk.id);
    }
}
