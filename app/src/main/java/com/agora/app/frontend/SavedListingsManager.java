package com.agora.app.frontend;

import android.util.Log;

import com.agora.app.backend.base.Listing;

import java.util.ArrayList;
import java.util.List;

public class SavedListingsManager {

    private static SavedListingsManager savedListingsManager;
    private List<Listing> savedListings = new ArrayList<>();

    private SavedListingsManager() {}

    public static SavedListingsManager getInstance() {
        if (savedListingsManager == null) {
            savedListingsManager = new SavedListingsManager();
        }
        return savedListingsManager;
    }

    public void addSavedListing(Listing listing) {
        if (!savedListings.contains(listing)) {
            savedListings.add(listing);
            Log.d("ListingManager", "Added: " + listing.getTitle() + ", total: " + savedListings.size());
        }
    }

    public void removeSavedListing(Listing listing) {
        if (savedListings.contains(listing)) {
            savedListings.remove(listing);
            Log.d("ListingManager", "Removed: " + listing.getTitle() + ", total: " + savedListings.size());
        }
    }

    public List<Listing> getSavedListings() {
        return savedListings;
    }
}
