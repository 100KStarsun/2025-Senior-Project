package com.agora.app.frontend;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.Button;
import com.agora.app.R;

public class SwipingActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_swiping);
        Button buttonUserInfo = findViewById(R.id.buttonUserInfo);
        Button buttonMarketplace = findViewById(R.id.buttonMarketplace);
        buttonUserInfo.setOnClickListener(v -> startActivity(new Intent(SwipingActivity.this, UserInfoActivity.class)));
        buttonMarketplace.setOnClickListener(v -> startActivity(new Intent(SwipingActivity.this, MarketplaceActivity.class)));
    }
}
