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
import java.util.ArrayList;

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

    String imagePath = listing.getImagePath();

    Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
        if (bitmap != null) {
            // Set the decoded image to the ImageView (optional: use it in the item view if needed)
            holder.imageView.setImageBitmap(bitmap); // Assuming there's an ImageView in the ViewHolder to show the image
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
    }
    

    @Override
    public int getItemCount() {
        return listings.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        TextView description;
        TextView price;
        ImageView imageView;
  

        public ViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.listing_title);
            description = itemView.findViewById(R.id.listing_description);
            price = itemView.findViewById(R.id.listing_price);
            imageView = itemView.findViewById(R.id.image);
   
        }
    }
}
