package com.agora.app.frontend;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.SeekBar;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;
import com.google.android.material.switchmaterial.SwitchMaterial;

import androidx.appcompat.app.AppCompatActivity;
import com.agora.app.R;
import com.agora.app.databinding.ActivityPreferencesBinding;
import com.agora.app.databinding.ActivitySettingsBinding;
import com.agora.app.backend.lambda.LambdaHandler;

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

        SwitchMaterial exchangeSwitch = (CheckBox) findViewById(R.id.exchanceSwitch);
        SwitchMaterial cashSwitch = (CheckBox) findViewById(R.id.cashSwitch);
        SeekBar priceMaxBar = findViewById(R.id.priceMax);
        SeekBar priceMinBar = findViewById(R.id.priceMin);
        CheckBox furnitureCheckBox = (CheckBox) findViewById(R.id.furnitureCheckBox);
        CheckBox householdCheckBox = (CheckBox) findViewById(R.id.householdCheckBox);
        CheckBox apparelCheckBox = (CheckBox) findViewById(R.id.apparelCheckBox);

        ActivityPreferencesBinding binding = ActivityPreferencesBinding.inflate(getLayoutInflater());

        //Button to save preferences to the backend
        Button savePreferences = findViewById(R.id.savePreferencesButton); //Note that this button isn't real right now, needs to be created in xml

        savePreferences.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean furnitureSelected = binding.furnitureCheckBox.isChecked();
                boolean householdSelected = binding.householdCheckbox.isChecked();
                int priceMax = binding.priceMaxBar.getProgress();
                int priceMin = binding.priceMinBar.getProgress();
                boolean apparelSelected = binding.apparelCheckbox.isChecked();
                boolean exchangeAllowed = binding.exchangeSwitch.isChecked();
                boolean cashAllowed = binding.caseSwitch.isChecked();

                Object[] preferencesList = {furnitureSelected, householdSelected, priceMax, priceMin,
                                            apparelSelected, exchangeAllowed, cashAllowed};

                new PreferencesSaveTask().execute(preferencesList);
            }
        });
    }

        /**
         * Handles background process of passing user preferences to the backend
         */
        private class PreferencesSaveTask extends AsyncTask<String, Void, Boolean> {
            private String errorMessage = "";

            protected Boolean doInBackground(Object[] preferences) {
                LambdaHandler.putUserPreferences(preferences);
                return true;
            }

            protected Boolean onPostExecute(Boolean result) {
                super.onPostExecute(result);
                if (result) {
                    Toast.makeText(PreferencesActivity.this, "Preferences Saved Successfully!", Toast.LENGTH_SHORT).show();
                } else {
                    Log.d("Preferences", "Saving Preferences Failed: " + errorMessage);
                    Toast.makeText(LoginActivity.this, "Error saving Preferences. Please try again.", Toast.LENGTH_SHORT).show();
                }
            }
        }

        /**
         * Handles background process of retrieving user preferences from the backend
         */
        private class PreferencesRetrievalTask extends AsyncTask<String, Void, Boolean> {
            private String errorMessage = "";
            
            protected Boolean doInBackground(String... params) {
            }
        }
    }

