package com.agora.app.frontend;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.Button;
import com.agora.app.R;

public class UserInfoActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);
        Button buttonMarketplace = findViewById(R.id.buttonMarketplace);
        Button buttonSwiping = findViewById(R.id.buttonSwiping);
        buttonMarketplace.setOnClickListener(v -> startActivity(new Intent(UserInfoActivity.this, MarketplaceActivity.class)));
        buttonSwiping.setOnClickListener(v -> startActivity(new Intent(UserInfoActivity.this, SwipingActivity.class)));
    }
}
