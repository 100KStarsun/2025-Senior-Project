package com.agora.app.frontend;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import com.agora.app.R;
import com.agora.app.backend.base.Listing;

import java.util.Objects;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

/**
 * @class SavedPostsActivity
 * @brief Activity for posts a user has saved to come back to later.
 */
public class SavedPostsActivity extends AppCompatActivity {
    private ListingView view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_posts);
        Objects.requireNonNull(getSupportActionBar()).hide();
        Button buttonUserInfo = findViewById(R.id.buttonUserInfo);
        buttonUserInfo.setOnClickListener(v -> startActivity(new Intent(SavedPostsActivity.this, UserInfoActivity.class)));

        List<Listing> savedListings = SavedListingsManager.getInstance().getSavedListings();
        RecyclerView recyclerView = findViewById(R.id.saved_listings);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        view = new ListingView(savedListings, true);
        recyclerView.setAdapter(view);
    }
}