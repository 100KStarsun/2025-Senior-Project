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
import java.util.List;

/**
 * @class ListingActivity
 * @brief Activity for displaying listings that a user has posted.
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
    public void onBindViewHolder(ViewHolder holder, int place) {
        Listing listing = listings.get(place);
        holder.title.setText(listing.getTitle());
        holder.description.setText(listing.getDescription());
        float price = listing.getPrice();
        holder.price.setText("$" + String.format("%.2f", price));
    }

    @Override
    public int getItemCount() {
        return listings.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        TextView description;
        TextView price;
        //TextView tag1;
        //TextView tag2;
        //TextView tag3;

        public ViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.listing_title);
            description = itemView.findViewById(R.id.listing_description);
            price = itemView.findViewById(R.id.listing_price);
            //price = itemView.findViewById(R.id.listing_price);
            //price.setText("$" + String.format("%.2f", price));  
            //tag1 = itemView.findViewById(R.id.listing_tag1);
            //tag2 = itemView.findViewById(R.id.listing_tag2);
            //tag3 = itemView.findViewById(R.id.listing_tag3);
        }
    }

}
