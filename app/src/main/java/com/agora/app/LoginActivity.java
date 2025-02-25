package com.agora.app;

import com.agora.app.backend.LoginException;
import com.agora.app.backend.LoginHandler;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);

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

                // Code to confirm user is in database, based on similar methods to being done in TestClass
                else if (!password.isEmpty() && !username.isEmpty()) {
                    try {
                        boolean loginSuccessful = LoginHandler.login(username, password);
                        if (loginSuccessful) {
                            Intent intent = new Intent(LoginActivity.this, UserInfoActivity.class);
                            startActivity(intent);
                        } else {
                            Toast.makeText(LoginActivity.this, "Error logging in. Please try again.", Toast.LENGTH_SHORT).show();
                        }
                    }
                    catch (LoginException e) {
                        Toast.makeText(LoginActivity.this, "Username or password is incorrect", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }
}
