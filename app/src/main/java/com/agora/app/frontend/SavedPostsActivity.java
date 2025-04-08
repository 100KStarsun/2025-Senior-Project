package com.agora.app.frontend;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import com.agora.app.R;
import com.agora.app.backend.base.User;
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

    private User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_posts);
        Objects.requireNonNull(getSupportActionBar()).hide();
        currentUser = getIntent().getSerializableExtra("userobject", User.class);
        Button buttonUserInfo = findViewById(R.id.buttonUserInfo);
        buttonUserInfo.setOnClickListener(v -> {
            Intent intent = new Intent(SavedPostsActivity.this, UserInfoActivity.class);
            intent.putExtra("username", currentUser.getUsername());
            startActivity(intent);
        });

        List<Listing> savedListings = SavedListingsManager.getInstance().getSavedListings();
        RecyclerView recyclerView = findViewById(R.id.saved_listings);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        view = new ListingView(savedListings, true);
        recyclerView.setAdapter(view);
    }
}