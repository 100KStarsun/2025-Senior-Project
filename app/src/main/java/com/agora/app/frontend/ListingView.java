package com.agora.app.frontend;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.agora.app.R;
import com.agora.app.backend.base.Listing;
import com.agora.app.backend.base.User;
import com.agora.app.backend.base.Chat;
import com.agora.app.backend.AppSession;

import android.widget.LinearLayout;
import androidx.annotation.NonNull;
import java.util.List;
import android.content.Intent;
import java.util.ArrayList;
import android.widget.Button;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.content.Intent;
import android.view.LayoutInflater;
import android.widget.ImageView;


/**
 * @class ListingView
 * @brief Adapter for displaying listings that a user has posted.
 */
public class ListingView extends RecyclerView.Adapter<ListingView.ViewHolder> {

    private List<Listing> listings;
    private boolean useSaveButton;
    private boolean isArchived = false;

    private Context context;

    public ListingView(List<Listing> listings, boolean useSaveButton) {
        this.listings = listings;
        this.useSaveButton = useSaveButton;
    }

    public ListingView(List<Listing> listings, boolean useSaveButton, Context context) {
        this.listings = listings;
        this.useSaveButton = useSaveButton;
        this.context = context;
    }

    @Override
    @NonNull
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_listing, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Listing listing = listings.get(position);

    // Set data to views
    holder.title.setText(listing.getTitle());
    holder.description.setText(listing.getDescription());
    holder.price.setText("$" + String.format("%.2f", listing.getPrice()));

    String imagePath = listing.getImagePath();

    Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
        if (bitmap != null) {
            holder.imageView.setImageBitmap(bitmap); 
        } else {
            // Fallback to a placeholder image if decoding failed
            holder.imageView.setImageResource(R.drawable.ic_placeholder);
        }

    // Set click listener for each listing card
    holder.itemView.setOnClickListener(v -> {
        Intent intent = new Intent(v.getContext(), ExpandedListingActivity.class);
        intent.putExtra("title", listing.getTitle());
        intent.putExtra("description", listing.getDescription());
        intent.putExtra("price", listing.getPrice());
        intent.putExtra("image", imagePath); 
        intent.putStringArrayListExtra("tags", new ArrayList<String>(listing.getTags())); // pass tags as ArrayList
        v.getContext().startActivity(intent);
    });
        boolean isSaved = SavedListingsManager.getInstance().getSavedListings().contains(listing);
        holder.saveButton.setText(isSaved ? "Unsave" : "Save");

        isArchived = ArchivedListingsManager.getInstance().getArchivedListings().contains(listing);
        holder.archiveButton.setText(isArchived ? "Unarchive" : "Archive");

        if (useSaveButton) {
            holder.saveButton.setVisibility(View.VISIBLE);
            holder.archiveButton.setVisibility(View.GONE);
            holder.deleteButton.setVisibility(View.GONE);
            holder.messageButton.setVisibility(View.VISIBLE);
            holder.messageButton.setOnClickListener(v -> {
                Intent intent = new Intent(v.getContext(), ChatActivity.class);
                intent.putExtra("listingUuid", listing.getUUID());
                intent.putExtra("listingTitle", listing.getTitle());
                intent.putExtra("ownerUser", listing.getSellerUsername());
                Chat chat = new Chat(AppSession.currentUser.getUsername(), listing.getSellerUsername(), 0);
                intent.putExtra("chatObj", chat);
                v.getContext().startActivity(intent);
            });
            holder.saveButton.setOnClickListener(v -> {
                if (isSaved) {
                    SavedListingsManager.getInstance().removeSavedListing(listing);
                    holder.saveButton.setText("Save");
                    notifyItemChanged(holder.getAdapterPosition());
                }
                else {
                    SavedListingsManager.getInstance().addSavedListing(listing);
                    holder.saveButton.setText("Unsave");
                    notifyItemChanged(holder.getAdapterPosition());
                }
            });
        }
        else {
            holder.saveButton.setVisibility(View.GONE);
            holder.deleteButton.setVisibility(View.VISIBLE);
            holder.deleteButton.setOnClickListener(v -> {
                ListingManager.getInstance().removeListing(listing);
                listings.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, listings.size());
            });

            holder.archiveButton.setVisibility(View.VISIBLE);
            holder.archiveButton.setOnClickListener(v -> {
                if (isArchived) {
                    ArchivedListingsManager.getInstance().removeSavedListing(listing);
                    ListingManager.getInstance().addListing(listing);
                    holder.archiveButton.setText("Archive");
                    notifyItemChanged(holder.getAdapterPosition());
                }
                else {
                    ArchivedListingsManager.getInstance().addSavedListing(listing);
                    ListingManager.getInstance().removeListing(listing);
                    holder.archiveButton.setText("Unarchive");
                    notifyItemChanged(holder.getAdapterPosition());
                }
                if (context instanceof UserInfoActivity) {
                    ((UserInfoActivity) context).refreshListings();
                }
            });
        }

    }
    

    @Override
    public int getItemCount() {
        return listings.size();
    }

   

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        TextView description;
        TextView price;

        Button saveButton;
        Button archiveButton;
        Button deleteButton;
        Button messageButton;
        ImageView imageView;

        public ViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.listing_title);
            description = itemView.findViewById(R.id.listing_description);
            price = itemView.findViewById(R.id.listing_price);
            imageView = itemView.findViewById(R.id.image);
            saveButton = itemView.findViewById(R.id.save_button);
            archiveButton = itemView.findViewById(R.id.archive_button);
            messageButton = itemView.findViewById(R.id.message_button);
            deleteButton = itemView.findViewById(R.id.delete_button);
        }
    }

    public void updateListings(List<Listing> changes) {
        this.listings.clear();
        this.listings.addAll(changes);
        notifyDataSetChanged();
    }
}
