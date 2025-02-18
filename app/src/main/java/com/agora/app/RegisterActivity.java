package com.agora.app;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

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
                EMAIL_TEXT = email.getText().toString().trim();

                // Check if email is empty or invalid
                if (EMAIL_TEXT.isEmpty()) {
                    Toast.makeText(RegisterActivity.this, "Please enter an email!", Toast.LENGTH_SHORT).show();
                } else if (caseMailAuth(EMAIL_TEXT)) {
                    // Valid case email
                    Toast.makeText(RegisterActivity.this, "Email verified!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(RegisterActivity.this, AccountCreatorActivity.class); 
                    //intent.putExtra("email", EMAIL_TEXT);
                    startActivity(intent);
                    //finish();
                } else {
                    // Invalid email
                    Toast.makeText(RegisterActivity.this, "Invalid .edu email!", Toast.LENGTH_SHORT).show();
                }
            }

            public boolean caseMailAuth(String email) {
                String[] emailTokens = email.split("@");
                return emailTokens.length == 2 && emailTokens[1].equals("case.edu");
            }
        });
    }
}
