package com.agora.app.frontend;

import android.util.Log;

import com.agora.app.backend.base.Listing;

import java.util.ArrayList;
import java.util.List;

public class ListingManager {
    private static ListingManager listingManager;
    private List<Listing> listings = new ArrayList<>();
    private String username;

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
        Log.d("ListingManager", "Removed: " + listing.getTitle() + ", total: " + listings.size());
    }

    public List<Listing> getListings() {
        return listings;
    }

    /*
    public List<Listing> noPersonalListings(String username) {
        List<Listing> refinedList = new ArrayList<>();
        for (Listing listing : listings) {
            if (!listing.getSellerUsername().equals(username)) {
                refinedList.add(listing);
            }
        }
        return refinedList;
    }
        */

}
