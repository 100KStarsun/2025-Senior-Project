package com.agora.app.frontend;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.agora.app.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import java.util.Objects;

/**
 * @class MessagingActivity
 * @brief Activity for messaging between users.
 */
public class MessagingActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messaging);
        Objects.requireNonNull(getSupportActionBar()).hide();

        // navigation bar routing section
        BottomNavigationView navBar = findViewById(R.id.nav_bar);

        // maps nav bar item to correct page redirection
        navBar.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.nav_messaging) {
                return true;
            }
            else if (itemId == R.id.nav_marketplace) {
                startActivity(new Intent(this, MarketplaceActivity.class));
                return true;
            }
            else if (itemId == R.id.nav_swiping) {
                startActivity(new Intent(this, SwipingActivity.class));
                return true;
            }
            else if (itemId == R.id.nav_user_info) {
                startActivity(new Intent(this, UserInfoActivity.class));
                return true;
            }
            return false;
        });
    }
}
