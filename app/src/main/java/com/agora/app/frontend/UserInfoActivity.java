package com.agora.app.frontend;
import com.agora.app.backend.base.Listing;

import com.agora.app.R;
import com.agora.app.backend.lambda.LambdaHandler;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import android.view.MenuItem;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentTransaction;
import com.google.android.material.navigation.NavigationView;
import com.agora.app.backend.base.Listing;
import com.agora.app.backend.base.User;
import com.agora.app.frontend.ListingView;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.os.AsyncTask;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * @class UserInfoActivity
 * @brief Activity for the user information page.
 */
public class UserInfoActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;
    private User currentUser; //Global user for this instance

    // variables for the list of listing and the view in which to see listings on page
    private List<Listing> listings = ListingManager.getInstance().getListings();
    private ListingView view;
    private String username;
    private TextView textUsername;
    private TextView textCaseId;
    private TextView textTransactions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);
        username = getIntent().getStringExtra("username");
        Objects.requireNonNull(getSupportActionBar()).hide();
        textUsername = findViewById(R.id.textUsername);
        textCaseId = findViewById(R.id.textCaseId);
        textTransactions = findViewById(R.id.textTransactions);
        new UserInfoTask().execute(username);
        // navigation bar routing section
        BottomNavigationView navBar = findViewById(R.id.nav_bar);

        // maps nav bar item to correct page redirection
        navBar.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.nav_messaging) {
                Intent intent = new Intent(this, MessagingActivity.class);
                intent.putExtra("username", username);
                startActivity(intent);
                return true;
            } else if (itemId == R.id.nav_marketplace) {
                Intent intent = new Intent(this, MarketplaceActivity.class);
                intent.putExtra("username", username);
                startActivity(intent);
                return true;
            } else if (itemId == R.id.nav_swiping) {
                Intent intent = new Intent(this, SwipingActivity.class);
                intent.putExtra("username", username);
                startActivity(intent);
                return true;
            } else if (itemId == R.id.nav_user_info) {
                return true;
            }
            return false;
        });

        //hamburger menu

        // Initialize the DrawerLayout, Toolbar, and NavigationView
        drawerLayout = findViewById(R.id.drawer_layout);
        toolbar = findViewById(R.id.toolbar);
        navigationView = findViewById(R.id.nav_view);

        // Create an ActionBarDrawerToggle to handle
        // the drawer's open/close state
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.nav_open, R.string.nav_close);

        // Add the toggle as a listener to the DrawerLayout
        drawerLayout.addDrawerListener(toggle);

        // Synchronize the toggle's state with the linked DrawerLayout
        toggle.syncState();

        // Set a listener for when an item in the NavigationView is selected
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {

            // Called when an item in the NavigationView is selected.
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                // Handle the selected item based on its ID
                if (item.getItemId() == R.id.nav_preferences) {
                    Intent intent = new Intent(UserInfoActivity.this, PreferencesActivity.class);
                    intent.putExtra("userobject", currentUser);
                    startActivity(intent);
                }

                if (item.getItemId() == R.id.nav_transaction_history) {
                    Intent intent = new Intent(UserInfoActivity.this, TransactionHistoryActivity.class);
                    intent.putExtra("userobject", currentUser);
                    startActivity(intent);
                }

                if (item.getItemId() == R.id.nav_saved_posts) {
                    Intent intent = new Intent(UserInfoActivity.this, SavedPostsActivity.class);
                    intent.putExtra("userobject", currentUser);
                    startActivity(intent);
                }

                if (item.getItemId() == R.id.nav_settings) {
                    Intent intent = new Intent(UserInfoActivity.this, SettingsActivity.class);
                    intent.putExtra("userobject", currentUser);
                    startActivity(intent);
                }

                // Close the drawer after selection
                drawerLayout.closeDrawers();
                // Indicate that the item selection has been handled
                return true;
            }
        });

        // listings scroller
        // finds and displays listing view on page
        RecyclerView recyclerView = findViewById(R.id.item_listings);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        view = new ListingView(listings);
        recyclerView.setAdapter(view);

        // creates button for ability to add listing
        Button addListingButton = findViewById(R.id.add_listing);
        addListingButton.setOnClickListener(view -> addListingDialog());
    }

    // method to open up popup to for user to enter listing information
    private void addListingDialog() {

        // builds dialogue popup with input fields
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_listing, null);
        builder.setView(dialogView);
        EditText titleInput = dialogView.findViewById(R.id.input_listing_title);
        EditText descriptionInput = dialogView.findViewById(R.id.input_listing_description);
        EditText priceInput = dialogView.findViewById(R.id.input_listing_price);
        EditText tag1Input = dialogView.findViewById(R.id.input_listing_tag1);
        EditText tag2Input = dialogView.findViewById(R.id.input_listing_tag2);
        EditText tag3Input = dialogView.findViewById(R.id.input_listing_tag3);
        Button saveButton = dialogView.findViewById(R.id.save_listing);
        AlertDialog dialog = builder.create();

        // save button on popup to pass through listing information
        saveButton.setOnClickListener(v -> {
            String title = titleInput.getText().toString();
            String description = descriptionInput.getText().toString();
            
            
            String tag1 = tag1Input.getText().toString();
            String tag2 = tag2Input.getText().toString();
            String tag3 = tag3Input.getText().toString();

            List<String> tagsList = new ArrayList<>();
            if (!tag1.isEmpty()) tagsList.add(tag1);
            if (!tag2.isEmpty()) tagsList.add(tag2);
            if (!tag3.isEmpty()) tagsList.add(tag3);

            

            float price = 0.0f;
            String priceString = priceInput.getText().toString();
 
            if (!priceString.isEmpty()) {
                try {
                    price = Float.parseFloat(priceString); // Parse to float
                } catch (NumberFormatException e) {
                    price = 0.0f;  //default price if not entered
                    // Add toast error message later
                }
            }    

            if(!title.isEmpty() && !description.isEmpty()) {
                UUID uuid = UUID.randomUUID();
                String displayName = "temp"; 
                String username = "user"; 
                String type = "default"; 
                String[] tags = tagsList.toArray(new String[0]); 

                Listing newListing = new Listing(uuid, title, price, description, displayName, username, type, tags);
    
                ListingManager.getInstance().addListing(newListing);
                view.notifyItemInserted(listings.size() - 1);
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private class UserInfoTask extends AsyncTask<String, Void, User> {
        private String errorMessage = "";
        private String username;
        @Override
        protected User doInBackground(String... params) {
            username = params[0];
            try {
                return LambdaHandler.getUsers(new String[]{username}).get(username);
            } catch (Exception e) {
                return null;
            }
        }

        protected void onPostExecute(User user) {
            if (user != null) {
                currentUser = user;
                textUsername.setText(user.getUsername());
            } else {
                Toast.makeText(UserInfoActivity.this, "Failed to load user info", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
