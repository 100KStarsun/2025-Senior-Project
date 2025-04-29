package com.agora.app.frontend;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.agora.app.backend.base.Listing;
import com.agora.app.backend.base.User;
import com.agora.app.backend.lambda.LambdaHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class SavedListingsManager {

    private static SavedListingsManager savedListingsManager;
    private List<Listing> savedListings = new ArrayList<>();
    private Map<UUID, Listing> listingMap = new HashMap<>();
    private User currentUser;

    private SavedListingsManager() {}

    public static SavedListingsManager getInstance() {
        if (savedListingsManager == null) {
            savedListingsManager = new SavedListingsManager();
        }
        return savedListingsManager;
    }

    public void initializeSavedListings(User user) {
        if (user == null) {
            return;
        }
        this.currentUser = user;
        this.savedListings.clear();
        this.listingMap.clear();
        if (currentUser.getLikedListings() == null) {
            currentUser.setLikedListings(new ArrayList<>());
        }
    }

    public void populateSavedListings(List<Listing> allListings) {
        if (currentUser == null || currentUser.getLikedListings() == null) {
            return;
        }
        savedListings.clear();
        listingMap.clear();

        Map<UUID, Listing> allListingsMap = new HashMap<>();
        for (Listing listing : allListings) {
            allListingsMap.put(listing.getUUID(), listing);
        }
        for (UUID savedId : currentUser.getLikedListings()) {
            Listing listing = allListingsMap.get(savedId);
            if (listing != null) {
                savedListings.add(listing);
                listingMap.put(savedId, listing);
            }
        }
    }


    public void addSavedListing(Listing listing) {
        UUID listingId = listing.getUUID();
        if (currentUser == null) {
            return;
        }
        if (currentUser.getLikedListings() == null) {
            currentUser.setLikedListings((new ArrayList<>()));
        }
        if (!listingMap.containsKey(listingId)) {
            savedListings.add(listing);
            listingMap.put(listingId, listing);
        }
        if (!currentUser.getLikedListings().contains(listingId)) {
            currentUser.getLikedListings().add(listingId);
            syncBackend();
            Log.d("ListingManager", "Added: " + listing.getTitle() + ", total: " + savedListings.size());
        }
    }

    public void removeSavedListing(Listing listing) {
        UUID listingId = listing.getUUID();
        if (currentUser == null) {
            return;
        }
        if (listingMap.containsKey(listingId)) {
            savedListings.remove(listing);
            listingMap.remove(listingId);
            currentUser.getLikedListings().remove(listingId);
            syncBackend();
            Log.d("ListingManager", "Removed: " + listing.getTitle() + ", total: " + savedListings.size());
        }
    }

    public List<Listing> getSavedListings() {
        return savedListings;
    }

    public boolean isSaved(UUID listingId) {
        return listingMap.containsKey(listingId);
    }
    public boolean isSaved(Listing listing) {
        return listing != null && isSaved(listing.getUUID());
    }

    public void syncBackend() {
        if (currentUser != null) {
            new saveSavedListingsTask().execute(currentUser.getUsername(), currentUser.toBase64String());
        }
    }

    private class saveSavedListingsTask extends AsyncTask<String, Void, Boolean> {
        private String errorMessage = "";

        @Override
        protected Boolean doInBackground(String... params) {
            String username = params[0];
            String userBase64 = params[1];
            try {
                LambdaHandler.putUsers(new String[] {username}, new String[]{userBase64});
                return true;
            }
            catch (Exception e) {
                return false;
            }
        }
        protected void onPostExecute(Boolean result) {
            if (result) {
                Log.d("SavedListingsManager", "Saved");
            }
            else {
                Log.d("SavedListingsManager", "Failed");
            }
        }

    }
}
