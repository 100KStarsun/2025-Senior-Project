package com.agora.app.frontend;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import androidx.appcompat.app.AppCompatActivity;
import com.agora.app.R;
import com.agora.app.databinding.ActivitySettingsBinding;
import java.util.Objects;

/**
 * @class SettingsActivity
 * @brief Activity for application settings.
 */
public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Objects.requireNonNull(getSupportActionBar()).hide();
        Button buttonUserInfo = findViewById(R.id.buttonUserInfo);
        buttonUserInfo.setOnClickListener(v -> startActivity(new Intent(SettingsActivity.this, UserInfoActivity.class)));

        CheckBox paypalCheckBox = (CheckBox) findViewById(R.id.paypalCheckBox);
        CheckBox zelleCheckBox = (CheckBox) findViewById(R.id.zelleCheckBox);
        CheckBox cashappCheckBox = (CheckBox) findViewById(R.id.cashappCheckBox);
        CheckBox venmoCheckBox = (CheckBox) findViewById(R.id.venmoCheckBox);
        CheckBox applepayCheckBox = (CheckBox) findViewById(R.id.applepayCheckBox);
        CheckBox googlepayCheckBox = (CheckBox) findViewById(R.id.googlepayCheckBox);
        CheckBox samsungpayCheckBox = (CheckBox) findViewById(R.id.samsungpayCheckBox);
        CheckBox cashCheckBox = (CheckBox) findViewById(R.id.cashCheckBox);
        CheckBox checkCheckBox = (CheckBox) findViewById(R.id.checkCheckBox);

        ActivitySettingsBinding binding = ActivitySettingsBinding.inflate(getLayoutInflater());

        binding.chatnotifSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                // The switch is checked.
            } else {
                // The switch isn't checked.
            }
        });

        binding.postnotifSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                // The switch is checked.
            } else {
                // The switch isn't checked.
            }
        });

        binding.pushnotifSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                // The switch is checked.
            } else {
                // The switch isn't checked.
            }
        });
    }
}
