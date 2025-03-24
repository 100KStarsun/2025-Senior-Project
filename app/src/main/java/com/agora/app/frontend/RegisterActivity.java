package com.agora.app.frontend;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.AsyncTask;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.agora.app.R;

import java.util.Objects;
import java.security.SecureRandom;
import com.agora.app.backend.EmailSender;

public class RegisterActivity extends AppCompatActivity {

    public static String CASE_ID_TEXT;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        Objects.requireNonNull(getSupportActionBar()).hide();

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
                if (CASE_ID_TEXT.isEmpty()) {
                    Toast.makeText(RegisterActivity.this, "Please enter a valid case id!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(RegisterActivity.this, "Please check your email for verification!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(RegisterActivity.this, RegistrationConfirmationActivity.class);
                    String email = CASE_ID_TEXT + "@case.edu";
                    String userOTP = generateOTP();
                    
                    //Email sending must be done on background thread, cannot be done on UI thread
                    new AsyncTask<Void, Void, Void>() {
                        protected Void doInBackground (Void... voids) {
                            new EmailSender().sendEmail(email, userOTP);
                            return null;
                        }
                    }.execute();
                    intent.putExtra("confirmOTP", userOTP);
                    startActivity(intent);
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

            /**
             * Securely generates a random 6 digit number using the SecureRandom class
             * 
             * @return  String representation of the 6-digit OTP, used for authentication
             */
            public String generateOTP() {
                int length = 6;
                SecureRandom random = new SecureRandom();
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < length; i++) {
                    sb.append(random.nextInt(10));
                }
                return sb.toString();
            }
        });
    }
}
