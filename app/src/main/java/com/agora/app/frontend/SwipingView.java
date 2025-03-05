package com.agora.app.frontend;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.agora.app.backend.base.Listing;
import com.yuyakaido.android.cardstackview.CardStackView;
import com.yuyakaido.android.cardstackview.CardStackListener;
import java.util.List;
import android.view.ViewGroup;
import android.view.LayoutInflater;
import android.widget.TextView;

import com.agora.app.R;

public class SwipingView extends RecyclerView.Adapter<SwipingView.ListingViewHolder> {
    private List<Listing> listings;

    public SwipingView(List<Listing> listings) {
        this.listings = listings;
    }

    @Override
    public int getItemCount() {
        return listings.size();
    }

    @NonNull
    @Override
    public ListingViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.card_listing, parent, false);
        return new ListingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ListingViewHolder holder, int position) {
        Listing listing = listings.get(position);
        holder.bind(listing);
    }

    public class ListingViewHolder extends RecyclerView.ViewHolder {
        private TextView cardTitle;
        public ListingViewHolder(View itemView) {
            super(itemView);
            cardTitle = itemView.findViewById(R.id.listing_title);
        }
        public void bind(Listing listing) {
            cardTitle.setText(listing.getTitle());
        }
    }

}
