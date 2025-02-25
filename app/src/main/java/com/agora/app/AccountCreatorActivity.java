package com.agora.app;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

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

                    // Example: Navigate to the next screen
                    Intent intent = new Intent(AccountCreatorActivity.this, UserInfoActivity.class);
                    startActivity(intent);
                } else {
                    // Inform the user the passwords don't match
                    Toast.makeText(AccountCreatorActivity.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
