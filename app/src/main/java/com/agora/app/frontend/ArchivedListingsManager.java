package com.agora.app.frontend;

import android.os.AsyncTask;
import android.util.Log;

import com.agora.app.backend.base.Listing;
import com.agora.app.backend.lambda.DynamoTables;
import com.agora.app.backend.lambda.LambdaHandler;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import software.amazon.awssdk.services.sso.endpoints.internal.Value;

public class ArchivedListingsManager {

    private static ArchivedListingsManager archivedListingsManager;
        private List<Listing> archivedListings = new ArrayList<>();
        private Set<UUID> archivedIds = new HashSet<>();

        private ArchivedListingsManager() {}

        public static ArchivedListingsManager getInstance() {
            if (archivedListingsManager == null) {
                archivedListingsManager = new ArchivedListingsManager();
            }
            return archivedListingsManager;
        }

        public void initializeArchivedListings() {
            this.archivedListings.clear();
            this.archivedIds.clear();
            new LoadArchivedListingsTask().execute();
        }

        public void populateArchivedListings(List<Listing> allListings) {
            archivedListings.clear();
            for (Listing listing : allListings) {
                if (archivedIds.contains((listing.getUUID()))) {
                    if (!listing.getIsArchived()) {
                        listing.toggleIsArchived();
                    }
                }
            }
        }

        public void addArchivedListing(Listing listing) {
            if (!archivedListings.contains(listing)) {
                archivedListings.add(listing);
                archivedIds.add(listing.getUUID());
                if (!listing.getIsArchived()) {
                    listing.toggleIsArchived();
                }
                syncBackend(listing);
                Log.d("ListingManager", "Added: " + listing.getTitle() + ", total: " + archivedListings.size());
            }
        }

        public void removeArchivedListing(Listing listing) {
            if (archivedListings.contains(listing)) {
                archivedListings.remove(listing);
                archivedIds.remove(listing.getUUID());
                if (!listing.getIsArchived()) {
                    listing.toggleIsArchived();
                }
                syncBackend(listing);
                Log.d("ListingManager", "Removed: " + listing.getTitle() + ", total: " + archivedListings.size());
            }
        }

        public List<Listing> getArchivedListings() {
            return archivedListings;
        }

        public boolean isArchived(UUID listingId) {
            return archivedIds.contains(listingId);
        }
        public boolean isArchived(Listing listing) {
            return listing != null && isArchived(listing.getUUID());
        }

        private void syncBackend(Listing listing) {
            new SaveArchivedListingsTask().execute(listing);
        }

        private class SaveArchivedListingsTask extends AsyncTask<Listing, Void, Boolean> {
            @Override
            protected  Boolean doInBackground(Listing... params) {
                Listing listing = params[0];
                try {
                    LambdaHandler.putListings(new String[] {listing.getUUID().toString()}, new String[]{listing.toBase64String()});
                    return true;
                }
                catch (Exception e) {
                    return false;
                }
            }

            protected void  onPostExecute(Boolean result) {
                if (result) {
                    Log.d("ArchivedListingsManager", "Archived");
                }
                else {
                    Log.d("ArchivedListingsManager", "Failed to be archived");
                }
            }
        }

        private class LoadArchivedListingsTask extends AsyncTask<Void, Void, HashMap<String, Listing>> {
            @Override
            protected HashMap<String, Listing> doInBackground(Void... params) {
                try {

                    return LambdaHandler.getListings();
                }
                catch (Exception e) {
                    return null;
                }
            }

            @Override
            protected void onPostExecute(HashMap<String, Listing> result) {
                if (result != null && result.containsKey(ARCHIVE_KEY)) {
                    Listing container = result.get(ARCHIVE_KEY);
                    String archivedData = container != null ? container.getTitle() : "";

                    if (archivedData != null && !archivedData.isEmpty()) {
                        String[] idStrings = archivedData.split(",");
                        for (String idString : idStrings) {
                            try {
                                UUID id = UUID.fromString(idString.trim());
                                archivedIds.add(id);
                            }
                            catch (IllegalArgumentException e){
                                Log.e("ArchivedListingsManager", "Archiving failed on UUID");
                            }
                        }

                        List<Listing> allListings = ListingManager.getInstance().getListings();
                        if (!allListings.isEmpty()) {
                            populateArchivedListings(allListings);
                        }
                    }
                }
            }
        }

}
