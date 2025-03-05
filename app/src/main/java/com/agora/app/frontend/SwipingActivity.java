package com.agora.app.frontend;

import com.agora.app.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Objects;

import com.agora.app.backend.base.Listing;
import com.yuyakaido.android.cardstackview.CardStackView;
import com.yuyakaido.android.cardstackview.CardStackListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @class SwipingActivity
 * @brief Activity for swiping page.
 */ 
public class SwipingActivity extends AppCompatActivity {
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


        CardStackView cardStackView = findViewById(R.id.listing_card_stack);
        List<Listing> listings = new ArrayList<>();

        SwipingView swipingView = new SwipingView(listings);
        cardStackView.setAdapter(swipingView);



    }
}
