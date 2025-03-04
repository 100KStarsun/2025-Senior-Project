package com.agora.app.frontend;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;

import androidx.appcompat.app.AppCompatActivity;
import com.agora.app.R;
import com.agora.app.databinding.ActivityPreferencesBinding;
import com.agora.app.databinding.ActivitySettingsBinding;

import java.util.Objects;

/**
 * @class PreferencesActivity
 * @brief Activity for setting account preferences.
 */
public class PreferencesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preferences);
        Objects.requireNonNull(getSupportActionBar()).hide();
        Button buttonUserInfo = findViewById(R.id.buttonUserInfo);
        buttonUserInfo.setOnClickListener(v -> startActivity(new Intent(PreferencesActivity.this, UserInfoActivity.class)));

        CheckBox furnitureCheckBox = (CheckBox) findViewById(R.id.furnitureCheckBox);
        CheckBox householdCheckBox = (CheckBox) findViewById(R.id.householdCheckBox);
        CheckBox apparelCheckBox = (CheckBox) findViewById(R.id.apparelCheckBox);

        ActivityPreferencesBinding binding = ActivityPreferencesBinding.inflate(getLayoutInflater());

        binding.exchangeSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                // The switch is checked.
            } else {
                // The switch isn't checked.
            }
        });

        binding.cashSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                // The switch is checked.
            } else {
                // The switch isn't checked.
            }
        });
    }
}

