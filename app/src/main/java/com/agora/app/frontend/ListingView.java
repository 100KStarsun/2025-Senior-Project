package com.agora.app.frontend;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.agora.app.R;
import com.agora.app.backend.base.Listing;
import android.widget.LinearLayout;
import androidx.annotation.NonNull;
import java.util.List;
import android.content.Intent;

/**
 * @class ListingView
 * @brief Adapter for displaying listings that a user has posted.
 */
public class ListingView extends RecyclerView.Adapter<ListingView.ViewHolder> {

    private List<Listing> listings;

    public ListingView(List<Listing> listings) {
        this.listings = listings;
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
    
        // Set click listener for each listing card
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), ExpandedListingActivity.class);
            intent.putExtra("title", listing.getTitle());
            intent.putExtra("description", listing.getDescription());
            intent.putExtra("price", listing.getPrice());
            intent.putExtra("image", R.drawable.ic_placeholder); 
            intent.putExtra("tags", listing.getTags());
            v.getContext().startActivity(intent);
        });
    }
    

    @Override
    public int getItemCount() {
        return listings.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        TextView description;
        TextView price;
        //LinearLayout expandedView; // The expanded view to show more details

        public ViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.listing_title);
            description = itemView.findViewById(R.id.listing_description);
            price = itemView.findViewById(R.id.listing_price);
            //tag = itemView.findViewById(R.id.listing_)
            //expandedView = itemView.findViewById(R.id.expanded_view); // Reference to expanded view

            // Initially hide the expanded view
            //expandedView.setVisibility(View.GONE);
        }
    }
}
