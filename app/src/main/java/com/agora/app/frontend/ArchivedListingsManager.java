package com.agora.app.frontend;

import android.util.Log;

import com.agora.app.backend.base.Listing;

import java.util.ArrayList;
import java.util.List;

public class ArchivedListingsManager {

        private static com.agora.app.frontend.ArchivedListingsManager archivedListingsManager;
        private List<Listing> archivedListings = new ArrayList<>();

        private ArchivedListingsManager() {}

        public static com.agora.app.frontend.ArchivedListingsManager getInstance() {
            if (archivedListingsManager == null) {
                archivedListingsManager = new com.agora.app.frontend.ArchivedListingsManager();
            }
            return archivedListingsManager;
        }

        public void addSavedListing(Listing listing) {
            if (!archivedListings.contains(listing)) {
                archivedListings.add(listing);
                Log.d("ListingManager", "Added: " + listing.getTitle() + ", total: " + archivedListings.size());
            }
        }

        public void removeSavedListing(Listing listing) {
            if (archivedListings.contains(listing)) {
                archivedListings.remove(listing);
                Log.d("ListingManager", "Removed: " + listing.getTitle() + ", total: " + archivedListings.size());
            }
        }

        public List<Listing> getArchivedListings() {
            return archivedListings;
        }

}
