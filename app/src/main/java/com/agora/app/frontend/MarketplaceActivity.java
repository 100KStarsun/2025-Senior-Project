package com.agora.app.frontend;

import com.agora.app.R;
import com.agora.app.backend.base.Listing;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import android.content.Intent;
import android.os.Bundle;
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


/**
 * @class MarketplaceActivity
 * @brief Activity for the bottom navigation bar for the marketplace.
 */
public class MarketplaceActivity extends AppCompatActivity {

    private List<Listing> listings = ListingManager.getInstance().getListings();
    private ListingView view;
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

        // navigation bar routing section
        BottomNavigationView navBar = findViewById(R.id.nav_bar);
        filteredListings = new ArrayList<>(listings);

        // maps nav bar item to correct page redirection
        navBar.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.nav_messaging) {
                startActivity(new Intent(this, MessagingActivity.class));
                return true;
            }
            else if (itemId == R.id.nav_marketplace) {
                return true;
            }
            else if (itemId == R.id.nav_swiping) {
                startActivity(new Intent(this, SwipingActivity.class));
                return true;
            }
            else if (itemId == R.id.nav_user_info) {
                startActivity(new Intent(this, UserInfoActivity.class));
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

        if (minPrice > maxPrice) {
            maxPriceInput.setError("The maximum is less than the minimum!");
            return;
        }

        for (Listing listing : listings) {
            boolean searchCriteria = search.isEmpty() || listing.getTitle().toLowerCase().contains(search.toLowerCase());
            boolean priceCriteria = listing.getPrice() >= minPrice && listing.getPrice() <= maxPrice;

            if (searchCriteria && priceCriteria) {
                filteredListings.add(listing);
            }
        }

        /*
        if (search.isEmpty()) {
            filteredListings.addAll(listings);
        }
        else {
            for (Listing listing : listings) {
                if (listing.getTitle().toLowerCase().contains(search.toLowerCase())) {
                    filteredListings.add(listing);
                }
            }
        }
         */

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
}
