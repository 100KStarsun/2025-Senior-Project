package com.agora.app;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class UserInfoActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);
        Button buttonLanding = findViewById(R.id.buttonLanding);
        Button buttonMarketplace = findViewById(R.id.buttonMarketplace);
        Button buttonSwiping = findViewById(R.id.buttonSwiping);
        buttonLanding.setOnClickListener(v -> startActivity(new Intent(UserInfoActivity.this, MainActivity.class)));
        buttonMarketplace.setOnClickListener(v -> startActivity(new Intent(UserInfoActivity.this, MarketplaceActivity.class)));
        buttonSwiping.setOnClickListener(v -> startActivity(new Intent(UserInfoActivity.this, SwipingActivity.class)));
    }
}
