package com.agora.app.frontend;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.agora.app.R;

public class RegisterActivity extends AppCompatActivity {

    public static String CASE_ID_TEXT;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        EditText caseid = findViewById(R.id.case_id);
        Button button = findViewById(R.id.button_id);

        Button buttonLogin = findViewById(R.id.buttonLoginActivity);

        buttonLogin.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v){
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CASE_ID_TEXT = caseid.getText().toString().trim();

                // Check if email is empty or invalid
                if (CASE_ID_TEXT.isEmpty()) {
                    Toast.makeText(RegisterActivity.this, "Please enter a valid case id!", Toast.LENGTH_SHORT).show();
                } else if (!caseMailAuth(CASE_ID_TEXT)) {
                    // Valid case email
                    Toast.makeText(RegisterActivity.this, "Case ID verified!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(RegisterActivity.this, AccountCreatorActivity.class);
                    //intent.putExtra("email", EMAIL_TEXT);
                    startActivity(intent);
                    //finish();
                } else {
                    // Invalid email
                    Toast.makeText(RegisterActivity.this, "Invalid .edu email!", Toast.LENGTH_SHORT).show();
                }
            }

            /**
             * Returns a boolean that details the success of verifying a CWRU email.
             * Input emails should be of the form cccnnn@case.edu, where c denotes individual characters, and
             * n denotes the numbers of the case id.
             * Currently, case ids are directly input as opposed to emails, pseudo-deprecating this method.
             * 
             * @param  email  the email that is being authenticated
             * @return        true if the email is a case email, false if not
             */
            public boolean caseMailAuth(String email) {
                String[] emailTokens = email.split("@");
                return emailTokens.length == 2 && emailTokens[1].equals("case.edu");
            }
        });
    }
}
