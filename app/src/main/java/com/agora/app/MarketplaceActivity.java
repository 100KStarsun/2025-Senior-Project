package com.agora.app;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MarketplaceActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_marketplace);

        BottomNavigationView navBar = findViewById(R.id.nav_bar);

        navBar.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.nav_messaging) {
                startActivity(new Intent(this, MessagingActivity.class));
                return true;
            }
            else if (itemId == R.id.nav_marketplace) {
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
