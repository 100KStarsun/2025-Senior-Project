package com.agora.app;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class RegisterActivity extends AppCompatActivity {

    public static String EMAIL_TEXT;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        EditText email = findViewById(R.id.email_text_id);
        Button button = findViewById(R.id.button_id);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EMAIL_TEXT = email.getText().toString();
                if (caseMailAuth(EMAIL_TEXT)){
                    System.out.println("Successfully evaluated email, attempting send");
                    new EmailSender().execute(EMAIL_TEXT);
                }
                System.out.println("Final message");
            }

            public boolean caseMailAuth(String email) {
                String[] email_tokens = email.split("@");
                if (email_tokens.length > 2 || email_tokens[1] != "case.edu") 
                    return false;
                return true;
            }
        });
    }
}