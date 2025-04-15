package com.agora.app.frontend;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;
import android.os.AsyncTask;
import com.google.android.material.switchmaterial.SwitchMaterial;

import androidx.appcompat.app.AppCompatActivity;
import com.agora.app.R;
import com.agora.app.databinding.ActivityPreferencesBinding;
import com.agora.app.databinding.ActivitySettingsBinding;
import com.agora.app.backend.base.User;
import com.agora.app.backend.lambda.LambdaHandler;

import java.util.Objects;
import java.util.ArrayList;

/**
 * @class PreferencesActivity
 * @brief Activity for setting account preferences.
 */
public class PreferencesActivity extends AppCompatActivity {

    private User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityPreferencesBinding binding = ActivityPreferencesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Objects.requireNonNull(getSupportActionBar()).hide();
        Button buttonUserInfo = binding.buttonUserInfo;
        currentUser = getIntent().getSerializableExtra("userobject", User.class);
        buttonUserInfo.setOnClickListener(v -> {
            Intent intent = new Intent(PreferencesActivity.this, UserInfoActivity.class);
            intent.putExtra("username", currentUser.getUsername());
            startActivity(intent);
        });

        ArrayList<Boolean> prefs = currentUser.getPreferences();
        if (prefs == null || prefs.isEmpty() || prefs.size() < 5) {
            prefs = new ArrayList<>();
            prefs.add(false);
            prefs.add(false); 
            prefs.add(false); 
            prefs.add(false); 
            prefs.add(false); 
            currentUser.setPreferences(prefs);
        }
        Boolean[] savedPrefs = prefs.toArray(new Boolean[0]);

        binding.exchangeSwitch.setChecked(savedPrefs[0]);
        binding.cashSwitch.setChecked(savedPrefs[1]);
        binding.furnitureCheckBox.setChecked(savedPrefs[2]);
        binding.householdCheckBox.setChecked(savedPrefs[3]);
        binding.apparelCheckBox.setChecked(savedPrefs[4]);
 
        binding.savePreferencesButton.setOnClickListener(v -> {
            ArrayList<Boolean> preferencesList = new ArrayList<>();
            preferencesList.add(binding.exchangeSwitch.isChecked());
            preferencesList.add(binding.cashSwitch.isChecked());
            preferencesList.add(binding.furnitureCheckBox.isChecked());
            preferencesList.add(binding.householdCheckBox.isChecked());
            preferencesList.add(binding.apparelCheckBox.isChecked());
            currentUser.setPreferences(preferencesList);
            new SavePreferencesTask().execute(currentUser.getUsername(), currentUser.toBase64String());
        });
    }

    private class SavePreferencesTask extends AsyncTask<String, Void, Boolean> {
        private String errorMessage = "";
        @Override
        protected Boolean doInBackground(String... params) {
            String username = params[0];
            String userBase64 = params[1];
            try {
                LambdaHandler.putUsers(new String[] {username}, new String[]{userBase64});
                return true;
            } catch (Exception e) {
                return false;
            }
        }
        protected void onPostExecute(Boolean result) {
            if (result) {
                Toast.makeText(PreferencesActivity.this, "Preferences Saved!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(PreferencesActivity.this, "Failed to save preferences to the backend...", Toast.LENGTH_SHORT).show();
            }
        }
    }
}

