package com.agora.app.frontend;

import com.agora.app.R;
import com.agora.app.backend.base.Listing;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import com.google.android.material.bottomnavigation.BottomNavigationView;


/**
 * @class MarketplaceActivity
 * @brief Activity for the bottom navigation bar for the marketplace.
 */
public class MarketplaceActivity extends AppCompatActivity {

    private List<Listing> listings;
    private ListingView view;
    private String username;
    private List<Listing> filteredListings;
    private SearchView searchBar;
    EditText minPriceInput;
    EditText maxPriceInput;
    CheckBox userPrefsCheck;
    Button addPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_marketplace);
        Objects.requireNonNull(getSupportActionBar()).hide();
        username = getIntent().getStringExtra("username");
        listings = ListingManager.getInstance().noPersonalListings(username);

        // navigation bar routing section
        BottomNavigationView navBar = findViewById(R.id.nav_bar);

        navBar.setSelectedItemId(R.id.nav_marketplace);

        filteredListings = new ArrayList<>(listings);

        // maps nav bar item to correct page redirection
        navBar.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.nav_messaging) {
                Intent intent = new Intent(this, MessagingActivity.class);
                intent.putExtra("username", username);
                startActivity(intent);
                return true;
            }
            else if (itemId == R.id.nav_marketplace) {
                return true;
            }
            else if (itemId == R.id.nav_swiping) {
                Intent intent = new Intent(this, SwipingActivity.class);
                intent.putExtra("username", username);
                startActivity(intent);
                return true;
            }
            else if (itemId == R.id.nav_user_info) {
                Intent intent = new Intent(this, UserInfoActivity.class);
                intent.putExtra("username", username);
                startActivity(intent);
                return true;
            }
            return false;
        });

        // listings scroller
        // finds and displays listing view on page
        RecyclerView recyclerView = findViewById(R.id.item_listings);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        view = new ListingView(filteredListings, true);
        recyclerView.setAdapter(view);

        searchBar = findViewById(R.id.search_bar);
        Button expand = findViewById(R.id.filter_expansion);
        LinearLayout filters = findViewById(R.id.filters);
        minPriceInput = findViewById(R.id.min_price);
        maxPriceInput = findViewById(R.id.max_price);
        userPrefsCheck = findViewById(R.id.user_prefs);
        addPrefs = findViewById(R.id.add_prefs);

        searchBar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String search) {
                filterListings(search);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String change) {
                filterListings(change);
                return false;
            }
        });

        expand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (filters.getVisibility() == View.VISIBLE) {
                    filters.setVisibility(View.GONE);
                    expand.setText("View Filter Options");
                }
                else {
                    filters.setVisibility(View.VISIBLE);
                    expand.setText("Collapse");
                }
            }
        });

        addPrefs.setOnClickListener(view -> {
            filterListings(searchBar.getQuery().toString());
        });

        refreshListings();

    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshListings();
    }

    private void refreshListings() {
        filteredListings.clear();
        filteredListings.addAll(listings);
        view.notifyDataSetChanged();

        if (listings.isEmpty()) {
            findViewById(R.id.no_listings).setVisibility(View.VISIBLE);
        }
        else {
            findViewById(R.id.no_listings).setVisibility(View.GONE);
        }
    }

    private void filterListings(String search) {
        filteredListings.clear();

        float minPrice = parsePrice(minPriceInput, 0.0f);
        float maxPrice = parsePrice(maxPriceInput, Float.MAX_VALUE);
        boolean userPrefs = userPrefsCheck.isChecked();
        Boolean[] filterPrefs = new Boolean[5];
        if (userPrefs) {
            filterPrefs = ListingManager.getInstance().getUserPrefs();
        }

        //boolean furnitureBoolean = filterPrefs[2];
        boolean furnitureBoolean = (filterPrefs.length > 2 && filterPrefs[2] != null) ? filterPrefs[2] : false;
        //boolean householdBoolean = filterPrefs[3];
        boolean householdBoolean = (filterPrefs.length > 3 && filterPrefs[3] != null) ? filterPrefs[3] : false;
        //boolean apparelBoolean = filterPrefs[4];
        boolean apparelBoolean = (filterPrefs.length > 4 && filterPrefs[4] != null) ? filterPrefs[4] : false;


        if (minPrice > maxPrice) {
            maxPriceInput.setError("The maximum is less than the minimum!");
            return;
        }

        ArrayList<String> tags = new ArrayList<>();
        for (Listing listing : listings) {
            boolean searchCriteria = textSearch(listing, search);
            boolean priceCriteria = listing.getPrice() >= minPrice && listing.getPrice() <= maxPrice;
            boolean prefCriteria = true;
            tags = listing.getTags();
            Log.d("FILTER_DEBUG", "---- Listing: " + listing.getTitle() + " ----");
            Log.d("FILTER_DEBUG", "Tags: " + tags.toString());
            Log.d("FILTER_DEBUG", "FurniturePref: " + furnitureBoolean);
            Log.d("FILTER_DEBUG", "HouseholdPref: " + householdBoolean);
            Log.d("FILTER_DEBUG", "ApparelPref: " + apparelBoolean);
            Log.d("FILTER_DEBUG", "UserPrefs enabled: " + userPrefs);

            if (userPrefs) {
                prefCriteria = (furnitureBoolean && tags.contains("Furniture")) || (householdBoolean && tags.contains("Household")) || (apparelBoolean && tags.contains("Apparel"));
            }
            if (searchCriteria && priceCriteria && prefCriteria) {
                filteredListings.add(listing);
            }
            /*
             * Gotta enforce that people are using correct tags, would be nice to have these fields align
             * with checkboxes in posting but this should work regardless
             */

            /*
            if (furnitureBoolean && tags.contains("Furniture")) {
                filteredListings.add(listing);
            }
            if (householdBoolean && tags.contains("Household")) {
                filteredListings.add(listing);
            }
            if (apparelBoolean && tags.contains("Apparel")) {
                filteredListings.add(listing);
            }

             */
        }

        view.notifyDataSetChanged();
        View noListings = findViewById(R.id.no_listings);
        if (filteredListings.isEmpty()) {
            noListings.setVisibility(View.VISIBLE);
        }
        else {
            noListings.setVisibility(View.GONE);
        }
    }

    private float parsePrice(EditText text, float auto) {
        String input = text.getText().toString().trim();
        if (input.isEmpty()) {
            return auto;
        }
        if (!input.matches("-?\\d+(\\.\\d+)?")) {
            text.setError("This isn't a valid number!");
            return auto;
        }
        return Float.parseFloat(input);
    }

    private boolean textSearch(Listing listing, String text) {
        if (text.isEmpty()) {
            return true;
        }
        String search = text.toLowerCase();
        boolean inTitle = listing.getTitle().toLowerCase().contains(search);
        boolean inDescription = listing.getDescription().toLowerCase().contains(search);
        boolean inTags = false;
        for (String tag : listing.getTags()) {
            if (tag.toLowerCase().contains(search)) {
                inTags = true;
                break;
            }
        }
        return inTitle || inDescription || inTags;
    }
}
