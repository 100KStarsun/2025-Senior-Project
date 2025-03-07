package com.agora.app.frontend;

import com.agora.app.R;
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
import com.agora.app.frontend.ListingView;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class UserInfoActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;

    // variables for the list of listing and the view in which to see listings on page
    private List<Listing> listings = new ArrayList<>();
    private ListingView view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);
        Objects.requireNonNull(getSupportActionBar()).hide();

        // navigation bar routing section
        BottomNavigationView navBar = findViewById(R.id.nav_bar);

        // maps nav bar item to correct page redirection
        navBar.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.nav_messaging) {
                startActivity(new Intent(this, MessagingActivity.class));
                return true;
            } else if (itemId == R.id.nav_marketplace) {
                startActivity(new Intent(this, MarketplaceActivity.class));
                return true;
            } else if (itemId == R.id.nav_swiping) {
                startActivity(new Intent(this, SwipingActivity.class));
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
                    startActivity(new Intent(UserInfoActivity.this, PreferencesActivity.class));
                }

                if (item.getItemId() == R.id.nav_transaction_history) {
                    startActivity(new Intent(UserInfoActivity.this, TransactionHistoryActivity.class));
                }

                if (item.getItemId() == R.id.nav_saved_posts) {
                    startActivity(new Intent(UserInfoActivity.this, SavedPostsActivity.class));
                }

                if (item.getItemId() == R.id.nav_settings) {
                    startActivity(new Intent(UserInfoActivity.this, SettingsActivity.class));
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
        Button saveButton = dialogView.findViewById(R.id.save_listing);
        AlertDialog dialog = builder.create();

        // save button on popup to pass through listing information
        saveButton.setOnClickListener(v -> {
            String title = titleInput.getText().toString();
            String description = descriptionInput.getText().toString();

            if(!title.isEmpty() && !description.isEmpty()) {
                listings.add(new Listing(title, description));
                view.notifyItemInserted(listings.size() - 1);
                dialog.dismiss();
            }
        });

        dialog.show();
    }
}
