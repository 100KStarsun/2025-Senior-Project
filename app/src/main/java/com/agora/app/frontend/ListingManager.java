package com.agora.app.frontend;

import android.util.Log;

import com.agora.app.backend.base.Listing;
import com.agora.app.backend.lambda.LambdaHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ListingManager {
    private static ListingManager listingManager;
    private List<Listing> listings = new ArrayList<>();
    private String username;
    private Boolean[] userPrefs = new Boolean[5];

    private ListingManager() {}

    public static ListingManager getInstance() {
        if (listingManager == null) {
            listingManager = new ListingManager();
        }
        return listingManager;
    }

    public void addListing(Listing listing) {
        listings.add(listing);
        Log.d("ListingManager", "Added: " + listing.getTitle() + ", total: " + listings.size());
    }

    public void removeListing(Listing listing) {
        listings.remove(listing);
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            LambdaHandler.deleteListings(new String[] {listing.toBase64String()});
        });
        Log.d("ListingManager", "Removed: " + listing.getTitle() + ", total: " + listings.size());
    }

    public List<Listing> getListings() {
        return listings;
    }

    
    public List<Listing> noPersonalListings(String username) {
        List<Listing> refinedList = new ArrayList<>();
        for (Listing listing : listings) {
            if (listing.getSellerUsername() != null && !listing.getSellerUsername().equals(username)) {
                refinedList.add(listing);
            }
        }
        return refinedList;
    }

    public Boolean[] getUserPrefs(){
        return userPrefs;
    }

    public void setUserPrefs(Boolean[] prefs) {
        userPrefs[0] = prefs[0];   // Exchange Preference
        userPrefs[1] = prefs[1];   // Cash Preference
        userPrefs[2] = prefs[2];   // Furniture Preference
        userPrefs[3] = prefs[3];   // Household Preference
        userPrefs[4] = prefs[4];   // Apparel Preference
    }
}
