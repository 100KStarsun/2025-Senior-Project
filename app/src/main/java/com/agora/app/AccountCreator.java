package com.agora.app;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class AccountCreator extends AppCompatActivity {
    
    public static String USERNAME;
    public static String PASSWORD;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.account_creator);
        EditText user = findViewById(R.id.username_id);
        EditText pass = findViewById(R.id.password_id);
        EditText confirm_pass = findViewById(R.id.confirm_pass_id);
        USERNAME = user.getText().toString();
        PASSWORD = pass.getText().toString();
        Button button = findViewById(R.id.button_id);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (PASSWORD == confirm_pass.getText().toString()){
                    /*This is where things will go to add user information to the database
                     * Will need to work with backend team/merge with the DynamoDB handler to complete.
                     */
                }
            }
        });
    }
}
