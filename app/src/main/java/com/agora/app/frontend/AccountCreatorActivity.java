package com.agora.app.frontend;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.agora.app.R;

import java.util.Objects;

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
                    // If passwords match, start UserInfoActivity
                    Intent intent = new Intent(AccountCreatorActivity.this, UserInfoActivity.class);
                    startActivity(intent);
                } else {
                    // Inform the user that the passwords don't match
                    Toast.makeText(AccountCreatorActivity.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
