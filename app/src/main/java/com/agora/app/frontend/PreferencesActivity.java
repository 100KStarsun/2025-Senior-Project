package com.agora.app.frontend;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.agora.app.R;

import java.util.Objects;

public class PreferencesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preferences);
        Objects.requireNonNull(getSupportActionBar()).hide();
        Button buttonUserInfo = findViewById(R.id.buttonUserInfo);
        buttonUserInfo.setOnClickListener(v -> startActivity(new Intent(PreferencesActivity.this, UserInfoActivity.class)));
    }
}

