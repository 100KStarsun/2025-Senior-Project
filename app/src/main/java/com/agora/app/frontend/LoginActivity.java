package com.agora.app.frontend;

import com.agora.app.R;
import com.agora.app.backend.LoginException;
import com.agora.app.backend.LoginHandler;
import com.agora.app.backend.lambda.LambdaHandler;
import com.agora.app.backend.AppSession;
import com.agora.app.backend.base.User;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.util.Log;

import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.security.NoSuchAlgorithmException;
import com.agora.app.backend.lambda.KeyNotFoundException;
import java.io.IOException;
import org.json.JSONException;

/**
 * @class LoginActivity
 * @brief Activity that allows the user to login to the app with their saved credentials.
 */
public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);
        Objects.requireNonNull(getSupportActionBar()).hide();

        EditText user = findViewById(R.id.username_id);
        EditText pass = findViewById(R.id.password_id);

        Button button = findViewById(R.id.button_id);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get values from the EditTexts when the button is clicked
                String username = user.getText().toString();
                String password = pass.getText().toString();

                // Check if username or password fields are empty
                if (username.isEmpty()) {
                    Toast.makeText(LoginActivity.this, "Username cannot be empty", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (password.isEmpty()) {
                    Toast.makeText(LoginActivity.this, "Password cannot be empty", Toast.LENGTH_SHORT).show();
                    return;
                }
                // Start background login task
                new LoginTask().execute(username, password);
            }
        });
    }

    /**
     * Handles background login process of LoginHandler.login
     * login is carried out on a background thread in the doInBackground method
     * onPostExecute is carried out once background thread operation completes
     */
    private class LoginTask extends AsyncTask<String, Void, User> {
        private String errorMessage = "";
        private String username;
        /**
         * Carries out the login operation on a background thread
         * Method from the AsyncTask superclass (abstract)
         * 
         * @param params  String array of params given by .execute() call (username, password)
         * @return        Boolean based on the success of login operation
         */
        @Override
        protected User doInBackground(String... params) {
            username = params[0];
            String password = params[1];
            try {
                LoginHandler.login(username, password);   // No boolean needed because if login fails LoginException will be thrown
                return LambdaHandler.getUsers(new String[]{username}).get(username);
            } catch (LoginException e) {
                Log.d("Login", "This mf password not right");
                return null;
            } catch (NullPointerException e) {
                Log.d("Login", "Dat Bih Not there frfr");
                return null;
            } catch (KeyNotFoundException ex) {
                Log.d("Login", "KeyNotFoundException");
                return null;
            }
        }

        /**
         * Executes next activity once the background thread completes operation
         * Overrides method of the AsyncTask super class (abstract)
         * 
         * @param result  Result of the login operation on the background thread
         */
        @Override
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
                Intent intent = new Intent(LoginActivity.this, UserInfoActivity.class);
                intent.putExtra("username", username);
                intent.putExtra("userObj", user);
                startActivity(intent);
                Toast.makeText(LoginActivity.this, "Login successful!", Toast.LENGTH_SHORT).show();
            } else {
                Log.d("Login", "Login failed: " + errorMessage);
                Toast.makeText(LoginActivity.this, "Error logging in. Please try again.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
