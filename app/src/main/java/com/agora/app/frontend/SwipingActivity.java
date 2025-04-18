package com.agora.app.frontend;

import com.agora.app.R;
import com.agora.app.backend.base.User;
import com.agora.app.backend.lambda.LambdaHandler;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.agora.app.backend.base.User;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import android.widget.Button;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;

import com.agora.app.backend.base.Listing;
import com.yuyakaido.android.cardstackview.CardStackLayoutManager;
import com.yuyakaido.android.cardstackview.CardStackView;
import com.yuyakaido.android.cardstackview.CardStackListener;
import com.yuyakaido.android.cardstackview.Direction;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * @class SwipingActivity
 * @brief Activity for swiping page.
 */ 
public class SwipingActivity extends AppCompatActivity implements CardStackListener {


    //variables for card stack
    private CardStackView cardStackView;
    private CardStackLayoutManager layoutManager;
    private SwipingView swipingView;
    private List<Listing> listings;
    private List<Listing> savedListings;
    private Set<String> swipedCards;
    private String username;
    private User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_swiping);
        Objects.requireNonNull(getSupportActionBar()).hide();
        username = getIntent().getStringExtra("username");
        currentUser = getIntent().getSerializableExtra("userObj", User.class);
        listings = ListingManager.getInstance().noPersonalListings(username);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            currentUser = getIntent().getSerializableExtra("userObj", User.class);
        }
        else {
            currentUser = (User) getIntent().getSerializableExtra("userObj");
        }

        if (currentUser == null) {
            if (username != null) {
                new UserInfoTask().execute(username);
            }
            else {
                return;
            }
        }
        else {
            SavedListingsManager.getInstance().initializeSavedListings(currentUser);
            new ListingRetrievalTask().execute();
        }

        // navigation bar routing section
        BottomNavigationView navBar = findViewById(R.id.nav_bar);

        navBar.setSelectedItemId(R.id.nav_swiping);

        // maps nav bar item to correct page redirection
        navBar.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.nav_messaging) {
                Intent intent = new Intent(this, MessagingActivity.class);
                intent.putExtra("username", username);
                intent.putExtra("userObj", currentUser);
                startActivity(intent);
                return true;
            }
            else if (itemId == R.id.nav_marketplace) {
                Intent intent = new Intent(this, MarketplaceActivity.class);
                intent.putExtra("username", username);
                intent.putExtra("userObj", currentUser);
                startActivity(intent);
                return true;
            }
            else if (itemId == R.id.nav_swiping) {
                return true;
            }
            else if (itemId == R.id.nav_user_info) {
                Intent intent = new Intent(this, UserInfoActivity.class);
                intent.putExtra("username", username);
                intent.putExtra("userObj", currentUser);
                startActivity(intent);
                return true;
            }
            return false;
        });

        cardStackView = findViewById(R.id.listing_card_stack);
        //savedListings = new ArrayList<>();
        swipedCards = new HashSet<>();
        layoutManager = new CardStackLayoutManager(this, this);
        cardStackView.setLayoutManager(layoutManager);
        updateListings();
        swipingView = new SwipingView(listings);
        cardStackView.setAdapter(swipingView);
    }

    @Override
    public void onCardSwiped(Direction direction) {
        int i = layoutManager.getTopPosition() - 1;
        if (i >= 0 && i < listings.size()) {
            Listing currentListing = listings.get(i);
            swipedCards.add(currentListing.getTitle());
            if (direction == Direction.Right) {
                SavedListingsManager.getInstance().addSavedListing(currentListing);
                //savedListings.add(currentListing);
                Toast.makeText(this, currentListing.getTitle() + " is saved.", Toast.LENGTH_SHORT).show();

                // Flash a green overlay effect
                flashColor("#A5D6A7"); // light green
            }
            else if (direction == Direction.Left) {
                Toast.makeText(this, currentListing.getTitle() + " is skipped.", Toast.LENGTH_SHORT).show();

                // Flash a red overlay effect
                flashColor("#EF9A9A"); // light red

            }
            listings.remove(i);
            swipingView.notifyItemRemoved(i);
        }
        refreshCards();
    }

    // Helper method to flash a color overlay on the screen
    private void flashColor(String hexColor) {
        // Get the root view of the activity
        final View rootView = findViewById(android.R.id.content);

        // Set the initial background color to the desired color
        rootView.setBackgroundColor(android.graphics.Color.parseColor(hexColor));

        // Animate fading the color out over 500ms, then reset the background to transparent
        rootView.animate().alpha(0f).setDuration(700).withEndAction(new Runnable() {
            @Override
            public void run() {
                // Reset alpha and background
                rootView.setAlpha(1f);
                rootView.setBackgroundColor(android.graphics.Color.TRANSPARENT);
            }
        });
    }

    @Override
    public void onCardDragging(Direction direction, float amount) {
    }
    @Override
    public void onCardAppeared(View view, int index) {
    }
    @Override
    public void onCardDisappeared(View view, int index) {
    }
    @Override
    public void onCardRewound() {
    }
    @Override
    public void onCardCanceled() {
    }
    public void refreshCards() {
        if (listings.isEmpty()) {
            findViewById(R.id.no_listings).setVisibility(View.VISIBLE);
        }
        else {
            findViewById(R.id.no_listings).setVisibility(View.GONE);
            swipingView.notifyDataSetChanged();
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        updateListings();
        swipingView.notifyDataSetChanged();
        refreshCards();
    }

    /*
    public List<Listing> getSavedListings() {

        return savedListings;
    }
     */
    private void updateListings() {
        List<Listing> newListings = new ArrayList<>();
        for (Listing listing : ListingManager.getInstance().getListings()) {
            if (listing.getSellerUsername() != null && !swipedCards.contains(listing.getTitle()) && !listing.getSellerUsername().equals(username)) {
                newListings.add(listing);
            }
        }
        listings.clear();
        listings.addAll(newListings);
        if (swipingView != null) {
            swipingView.notifyDataSetChanged();  // Update adapter
        }
    }

    private class UserInfoTask extends AsyncTask<String, Void, User> {
        private String errorMessage = "";
        private String username;

        @Override
        protected User doInBackground(String... params) {
            username = params[0];
            try {
                return LambdaHandler.getUsers(new String[]{username}).get(username);
            } catch (Exception e) {
                return null;
            }
        }

        protected void onPostExecute(User user) {
            if (user != null) {
                currentUser = user;
                SavedListingsManager.getInstance().initializeSavedListings(currentUser);
                new ListingRetrievalTask().execute();
            } else {
                Toast.makeText(SwipingActivity.this, "Failed to load user info", Toast.LENGTH_SHORT).show();
            }
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
                Toast.makeText(SwipingActivity.this, "Error obtaining all listings", Toast.LENGTH_SHORT).show();
                return;
            }
            List<Listing> allListings = new ArrayList<>();
            SavedListingsManager.getInstance().populateSavedListings(allListings);

        }
    }


}
