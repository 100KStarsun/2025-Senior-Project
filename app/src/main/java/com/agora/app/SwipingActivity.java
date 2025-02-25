package com.agora.app;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

public class SwipingActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_swiping);

        BottomNavigationView navBar = findViewById(R.id.nav_bar);

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
    }
}
