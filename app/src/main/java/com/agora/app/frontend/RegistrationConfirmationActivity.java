package com.agora.app.frontend;

import com.agora.app.R;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import java.util.Objects;


/**
 * A middleware class designated specifically to prompt the user to enter the OTP sent to their .edu email.
 */
public class RegistrationConfirmationActivity extends AppCompatActivity{
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        String confirmOTP = intent.getStringExtra("confirmOTP");
        setContentView(R.layout.registration_confirmation);
        Objects.requireNonNull(getSupportActionBar()).hide();

        EditText otp_field = findViewById(R.id.user_otp);
        Button verify = findViewById(R.id.verify_button);
        Toast.makeText(RegistrationConfirmationActivity.this, confirmOTP, Toast.LENGTH_SHORT).show();

        verify.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String inputOTP = otp_field.getText().toString().trim();
                if (inputOTP.isEmpty()) {
                    Toast.makeText(RegistrationConfirmationActivity.this, "Please enter a valid OTP!", Toast.LENGTH_SHORT).show();
                } else if (inputOTP.equals(confirmOTP)) {
                    Toast.makeText(RegistrationConfirmationActivity.this, "Account Verified!", Toast.LENGTH_SHORT).show();
                    Intent newintent = new Intent(RegistrationConfirmationActivity.this, AccountCreatorActivity.class);
                    startActivity(newintent);
                } else {
                    Toast.makeText(RegistrationConfirmationActivity.this, "Invalid OTP Entered!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
