package com.agora.app.frontend;

import com.agora.app.R;
import com.agora.app.backend.LoginException;
import com.agora.app.backend.LoginHandler;
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
import java.security.NoSuchAlgorithmException;
import java.io.IOException;
import org.json.JSONException;

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
    private class LoginTask extends AsyncTask<String, Void, Boolean> {
        private String errorMessage = "";

        /**
         * Carries out the login operation on a background thread
         * Method from the AsyncTask superclass (abstract)
         * 
         * @param params  String array of params given by .execute() call (username, password)
         * @return        Boolean based on the success of login operation
         */
        @Override
        protected Boolean doInBackground(String... params) {
            String username = params[0];
            String password = params[1];
            try {
                return LoginHandler.login(username, password);
            } catch (LoginException e) {
                return false;
            }
        }

        /**
         * Executes next activity once the background thread completes operation
         * Overrides method of the AsyncTask super class (abstract)
         * 
         * @param result  Result of the login operation on the background thread
         */
        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            if (result) {
                Intent intent = new Intent(LoginActivity.this, UserInfoActivity.class);
                startActivity(intent);
                Toast.makeText(LoginActivity.this, "Login successful!", Toast.LENGTH_SHORT).show();
            } else {
                Log.d("Login", "Login failed: " + errorMessage);
                Toast.makeText(LoginActivity.this, "Error logging in. Please try again.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
