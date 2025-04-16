package com.agora.app.frontend;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.agora.app.R;
import com.agora.app.backend.base.User;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import java.util.Objects;

/**
 * @class MessagingActivity
 * @brief Activity for messaging between users.
 */
public class MessagingActivity extends AppCompatActivity {

    private String username;
    private User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messaging);
        Objects.requireNonNull(getSupportActionBar()).hide();
        username = getIntent().getStringExtra("username");
        currentUser = getIntent().getSerializableExtra("userObj", User.class);

        // navigation bar routing section
        BottomNavigationView navBar = findViewById(R.id.nav_bar);

        navBar.setSelectedItemId(R.id.nav_messaging);

        // maps nav bar item to correct page redirection
        navBar.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.nav_messaging) {
                return true;
            }
            else if (itemId == R.id.nav_marketplace) {
                Intent intent = new Intent(this, MarketplaceActivity.class);
                intent.putExtra("username", username);
                intent.putExtra("userObj", currentUser);
                startActivity(intent);
                return true;
            }
            else if (itemId == R.id.nav_swiping) {
                Intent intent = new Intent(this, SwipingActivity.class);
                intent.putExtra("username", username);
                intent.putExtra("userObj", currentUser);
                startActivity(intent);
                return true;
            }
            else if (itemId == R.id.nav_user_info) {
                Intent intent = new Intent(this, UserInfoActivity.class);
                intent.putExtra("username", username);
                intent.putExtra("userObj", currentUser);
                startActivity(intent);
                return true;
            }
            return false;
        });
    }
}
