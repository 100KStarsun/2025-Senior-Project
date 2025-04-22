package com.agora.app.frontend;
import com.agora.app.backend.base.Listing;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.agora.app.R;
import java.util.ArrayList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.content.Intent;
import android.view.LayoutInflater;


public class ExpandedListingActivity extends AppCompatActivity {

    private TextView titleTextView;
    private TextView descriptionTextView;
    private TextView priceTextView;
    private ImageView listingImageView;
    private TextView tag1TextView;
    private TextView tag2TextView;
    private TextView tag3TextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expanded_listing);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        // Initialize views
        titleTextView = findViewById(R.id.expanded_listing_title);
        descriptionTextView = findViewById(R.id.expanded_listing_description);
        priceTextView = findViewById(R.id.expanded_listing_price); 
        listingImageView = findViewById(R.id.expanded_listing_image);
        tag1TextView = findViewById(R.id.expanded_listing_tag1);
        tag2TextView = findViewById(R.id.expanded_listing_tag2);
        tag3TextView = findViewById(R.id.expanded_listing_tag3);

        // Get the data passed from the previous activity
        String title = getIntent().getStringExtra("title");
        String description = getIntent().getStringExtra("description");
        float price = getIntent().getFloatExtra("price", 0.0f); 
        //Image image = getIntent().getStringExtra("image");
        ArrayList<String> tags = getIntent().getStringArrayListExtra("tags"); 

        // Set the values to the respective views
        titleTextView.setText(title);
        descriptionTextView.setText(description);
        priceTextView.setText("$" + String.format("%.2f", price)); // Set price
        //Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
            //if (bitmap != null) {
                //listingImageView.setImageBitmap(bitmap);
            //} else {
                // Fallback to a placeholder image if decoding fails
                listingImageView.setImageResource(R.drawable.ic_placeholder);
            //}

        // Dynamically set the tags (check if tags are not null)
        if (tags != null) {
            // Make sure to handle cases where the tag list may be smaller than 3
            tag1TextView.setText(tags.size() > 0 ? tags.get(0) : "");
            tag2TextView.setText(tags.size() > 1 ? tags.get(1) : "");
            tag3TextView.setText(tags.size() > 2 ? tags.get(2) : "");
        }
    }
}