package com.agora.app.frontend;

import com.agora.app.backend.RegistrationHandler;
import android.content.Intent;
import android.os.Bundle;
import android.os.AsyncTask;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.agora.app.R;

import java.security.NoSuchAlgorithmException;
import org.json.JSONException;
import android.util.Log;


public class AccountCreatorActivity extends AppCompatActivity {

    //public static String USERNAME;
    //public static String PASSWORD;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.account_creator);

        EditText user = findViewById(R.id.username_id);
        EditText pass = findViewById(R.id.password_id);
        EditText confirm_pass = findViewById(R.id.confirm_pass_id);
        Button button = findViewById(R.id.button_id);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get values from the EditTexts when the button is clicked
                String username = user.getText().toString();
                String password = pass.getText().toString();

                // Check if username or password fields are empty
                if (username.isEmpty()) {
                    Toast.makeText(AccountCreatorActivity.this, "Username cannot be empty", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (password.isEmpty()) {
                    Toast.makeText(AccountCreatorActivity.this, "Password cannot be empty", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (password.equals(confirm_pass.getText().toString())) {
                    // Code to handle valid input, e.g., add user to database
                    /*
                    boolean registrationSuccess = RegistrationHandler.register(username, password);
                    if (registrationSuccess) {
                        Intent intent = new Intent(AccountCreatorActivity.this, UserInfoActivity.class);
                        startActivity(intent);
                    } else {
                        Toast.makeText(AccountCreatorActivity.this, "Error creating account.", Toast.LENGTH_SHORT).show();
                    }
                    // Example: Navigate to the next screen
                    */
                    new AccountCreationTask().execute(username, password);
                } else {
                    // Inform the user the passwords don't match
                    Toast.makeText(AccountCreatorActivity.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**
     * Handles the background task of account creation and the following actions given the result.
     * creation is carried out by the RegistrationHandler.register method
     * onPostExecute carries out once the background task completes/fails
     */
    private class AccountCreationTask extends AsyncTask<String, Void, Boolean> {
        private String errorMessage = "";

        /**
         * Carries out the background operation of creating an account
         * Overriden method from the AsyncTask superclass
         * 
         * @param params  String array of params being passed
         * @return        true if account creation is successful, false if not
         */
        @Override
        protected Boolean doInBackground(String... params) {
            String username = params[0];
            String password = params[1];
            return RegistrationHandler.register(username, password);
        }

        /**
         * Executes the next predetermined activity once the background thread completes
         * Overrides the onPostExecute method of AsyncTask super
         * 
         * @param result  The result of the background thread's operation
         */
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            if (result) {
                Intent intent = new Intent(AccountCreatorActivity.this, UserInfoActivity.class);
                startActivity(intent);
            } else {
                Log.d("Account creation", "Account creation failed: " + errorMessage);
                Toast.makeText(AccountCreatorActivity.this, "Error creating account.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
