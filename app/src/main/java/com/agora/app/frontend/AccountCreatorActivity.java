package com.agora.app.frontend;

import com.agora.app.backend.RegistrationHandler;
import com.agora.app.backend.lambda.LambdaHandler;
import com.agora.app.backend.base.User;
import com.agora.app.backend.AppSession;
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
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.io.IOException;

/**
 * @class AccountCreatorActivity
 * @brief Activity for creating a new account with username, password, and confirmation.
 */
public class AccountCreatorActivity extends AppCompatActivity {

    /**
     * @brief This method is called when the activity is created.
     * 
     * It sets up the layout, hides the action bar, and initializes the UI elements such as
     * the EditText fields for username, password, and confirm password, and the button to
     * trigger account creation. The method also sets an OnClickListener for the button to
     * validate input and proceed with account creation if the inputs are valid.
     *
     * @param savedInstanceState A bundle containing data passed to the activity, if any.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.account_creator);
        
        // Hides the action bar
        Objects.requireNonNull(getSupportActionBar()).hide();

        // Initializes the UI elements
        EditText user = findViewById(R.id.username_id);
        EditText pass = findViewById(R.id.password_id);
        EditText confirm_pass = findViewById(R.id.confirm_pass_id);
        Button button = findViewById(R.id.button_id);

        // Sets an OnClickListener for the button to handle account creation
        button.setOnClickListener(new View.OnClickListener() {
            /**
             * @brief Handles the click event for the account creation button.
             * 
             * This method checks the user input for the username and password fields. 
             * It ensures the username and password are not empty, and verifies that 
             * the password and confirm password fields match. If all checks pass, it 
             * proceeds to the UserInfoActivity. If any validation fails, it shows a 
             * Toast message with the error.
             *
             * @param v The view that was clicked (the button).
             */
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

                // Check if the password and confirm password fields match
                if (password.equals(confirm_pass.getText().toString())) {
                    // Code to handle valid input, e.g., add user to database (done on background thread)
                    new AccountCreationTask().execute(username, password);
                } else {
                    // Inform the user that the passwords don't match
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
    private class AccountCreationTask extends AsyncTask<String, Void, User> {
        private String errorMessage = "";
        private String username;

        /**
         * Carries out the background operation of creating an account
         * Overriden method from the AsyncTask superclass
         * 
         * @param params  String array of params being passed
         * @return        true if account creation is successful, false if not
         */
        @Override
        protected User doInBackground(String... params) {
            String username = params[0];
            String password = params[1];
            try {
                boolean registration = RegistrationHandler.register(username, password);
                if (registration) {
                    return LambdaHandler.getUsers(new String[]{username}).get(username); 
                } else {
                    return null;
                }
            } catch (NullPointerException e) {
                return null;
            }
        }

        /**
         * Executes the next predetermined activity once the background thread completes
         * Overrides the onPostExecute method of AsyncTask super
         * 
         * @param result  The result of the background thread's operation
         */
        protected void onPostExecute(User user) {
            if (user != null) {
                ExecutorService executor = Executors.newSingleThreadExecutor();
                executor.execute(() -> {
                    try {
                        AppSession session = new AppSession(user);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
                Intent intent = new Intent(AccountCreatorActivity.this, UserInfoActivity.class);
                intent.putExtra("username", username);
                intent.putExtra("userObj", user);
                startActivity(intent);
            } else {
                Log.d("Account creation", "Account creation failed: " + errorMessage);
                Toast.makeText(AccountCreatorActivity.this, "Error creating account.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
