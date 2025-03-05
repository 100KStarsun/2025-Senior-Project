package com.agora.app.frontend;

import com.agora.app.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import java.util.Objects;

import com.agora.app.backend.base.Listing;
import com.yuyakaido.android.cardstackview.CardStackLayoutManager;
import com.yuyakaido.android.cardstackview.CardStackView;
import com.yuyakaido.android.cardstackview.CardStackListener;
import com.yuyakaido.android.cardstackview.Direction;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_swiping);
        Objects.requireNonNull(getSupportActionBar()).hide();

        // navigation bar routing section
        BottomNavigationView navBar = findViewById(R.id.nav_bar);

        // maps nav bar item to correct page redirection
        navBar.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.nav_messaging) {
                startActivity(new Intent(this, MessagingActivity.class));
                return true;
            }
            else if (itemId == R.id.nav_marketplace) {
                startActivity(new Intent(this, MarketplaceActivity.class));
                return true;
            }
            else if (itemId == R.id.nav_swiping) {
                return true;
            }
            else if (itemId == R.id.nav_user_info) {
                startActivity(new Intent(this, UserInfoActivity.class));
                return true;
            }
            return false;
        });


        cardStackView = findViewById(R.id.listing_card_stack);
        listings = new ArrayList<>();
        savedListings = new ArrayList<>();
        layoutManager = new CardStackLayoutManager(this, this);
        cardStackView.setLayoutManager(layoutManager);
        swipingView = new SwipingView(listings);
        cardStackView.setAdapter(swipingView);
    }

    @Override
    public void onCardSwiped(Direction direction) {
        int i = layoutManager.getTopPosition() - 1;
        if (i >= 0 && i < listings.size()) {
            Listing currentListing = listings.get(i);
            if (direction == Direction.Right) {
                savedListings.add(currentListing);
                Toast.makeText(this, currentListing.getTitle() + " has been saved.", Toast.LENGTH_SHORT).show();
            }
            else if (direction == Direction.Left) {
                listings.remove(i);
                swipingView.notifyItemRemoved(i);
                Toast.makeText(this, "X", Toast.LENGTH_SHORT).show();
            }
        }
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

    public List<Listing> getSavedListings() {
        return savedListings;
    }
}
