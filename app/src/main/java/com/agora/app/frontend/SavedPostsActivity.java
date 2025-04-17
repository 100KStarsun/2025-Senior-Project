package com.agora.app.frontend;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import com.agora.app.R;
import com.agora.app.backend.base.User;
import com.agora.app.backend.base.Listing;
import com.agora.app.backend.lambda.LambdaHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            currentUser = getIntent().getSerializableExtra("userobject", User.class);
        }
        else {
            currentUser = (User) getIntent().getSerializableExtra("userobject");
        }
        if (currentUser == null) {
            finish();
            return;
        }

        SavedListingsManager.getInstance().initializeSavedListings(currentUser);

        Button buttonUserInfo = findViewById(R.id.buttonUserInfo);
        buttonUserInfo.setOnClickListener(v -> {
            Intent intent = new Intent(SavedPostsActivity.this, UserInfoActivity.class);
            intent.putExtra("username", currentUser.getUsername());
            intent.putExtra("userobject", currentUser);
            startActivity(intent);
        });

        List<Listing> savedListings = new ArrayList<>();
        RecyclerView recyclerView = findViewById(R.id.saved_listings);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        view = new ListingView(savedListings, true);
        recyclerView.setAdapter(view);
        //new ListingRetrievalTask().execute();

        List<Listing> existingSavedListings = SavedListingsManager.getInstance().getSavedListings();
        if (existingSavedListings != null && !existingSavedListings.isEmpty()) {
            view.updateListings(existingSavedListings);
            view.notifyDataSetChanged();
        }
        else {
            SavedListingsManager.getInstance().initializeSavedListings(currentUser);
            new ListingRetrievalTask().execute();
        }
    }

    private class ListingRetrievalTask extends AsyncTask<Void, Void, HashMap<String, Listing>> {
        @Override
        protected HashMap<String, Listing> doInBackground(Void... params) {
            try {
                return LambdaHandler.scanListings();
            } catch (Exception e) {
                return null;
            }
        }
        protected void onPostExecute(HashMap<String, Listing> dblistings) {
            if (dblistings == null) {
                Toast.makeText(SavedPostsActivity.this, "Error obtaining all listings", Toast.LENGTH_SHORT).show();
                return;
            }

            List<Listing> allListings = new ArrayList<>(dblistings.values());

            if (currentUser.getLikedListings() == null) {
                Toast.makeText(SavedPostsActivity.this, "No saved listings", Toast.LENGTH_SHORT).show();
                currentUser.setLikedListings(new ArrayList<>());
                return;
            }

            SavedListingsManager.getInstance().populateSavedListings(allListings);
            List<Listing> savedListings = SavedListingsManager.getInstance().getSavedListings();

            if (savedListings.isEmpty()) {
                Toast.makeText(SavedPostsActivity.this, "No saved listings", Toast.LENGTH_SHORT).show();
            }
            else {
                view.updateListings(savedListings);
                view.notifyDataSetChanged();
            }

        }

    }
}