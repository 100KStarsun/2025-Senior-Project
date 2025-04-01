package com.agora.app.frontend;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.agora.app.R;

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
        int imageResource = getIntent().getIntExtra("image", R.drawable.ic_placeholder);
        String tag1 = getIntent().getStringExtra("tag1");
        String tag2 = getIntent().getStringExtra("tag2");
        String tag3 = getIntent().getStringExtra("tag3");

        // Set the values to the respective views
        titleTextView.setText(title);
        descriptionTextView.setText(description);
        priceTextView.setText("$" + String.format("%.2f", price));
        listingImageView.setImageResource(imageResource);
        tag1TextView.setText(tag1);
        tag2TextView.setText(tag2);
        tag3TextView.setText(tag3);



    }
}
