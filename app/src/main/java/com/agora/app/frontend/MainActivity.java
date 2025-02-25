package com.agora.app.frontend;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.Button;
import com.agora.app.R;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button buttonUserInfo = findViewById(R.id.buttonUserInfo);
        Button buttonMarketplace = findViewById(R.id.buttonMarketplace);
        Button buttonSwiping = findViewById(R.id.buttonSwiping);
        buttonUserInfo.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, UserInfoActivity.class)));
        buttonMarketplace.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, MarketplaceActivity.class)));
        buttonSwiping.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, SwipingActivity.class)));
    }
}
