package com.agora.app;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class MarketplaceActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_marketplace);
        Button buttonUserInfo = findViewById(R.id.buttonUserInfo);
        Button buttonSwiping = findViewById(R.id.buttonSwiping);
        buttonUserInfo.setOnClickListener(v -> startActivity(new Intent(MarketplaceActivity.this, UserInfoActivity.class)));
        buttonSwiping.setOnClickListener(v -> startActivity(new Intent(MarketplaceActivity.this, SwipingActivity.class)));
    }
}
