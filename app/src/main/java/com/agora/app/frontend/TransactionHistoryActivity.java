package com.agora.app.frontend;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.agora.app.R;
import com.agora.app.backend.base.User;
import com.agora.app.backend.base.Listing;

import java.util.List;
import java.util.Objects;

/**
 * @class TransactionHistoryActivity
 * @brief Activity for a user's transaction history
 */
public class TransactionHistoryActivity extends AppCompatActivity {

    private ListingView view;

    private User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_history);
        Objects.requireNonNull(getSupportActionBar()).hide();
        currentUser = getIntent().getSerializableExtra("userobject", User.class);
        Button buttonUserInfo = findViewById(R.id.buttonUserInfo);
        buttonUserInfo.setOnClickListener(v -> {
            Intent intent = new Intent(TransactionHistoryActivity.this, UserInfoActivity.class);
            intent.putExtra("username", currentUser.getUsername());
            intent.putExtra("userObj", currentUser);
            startActivity(intent);
        });

        List<Listing> archivedListings = ArchivedListingsManager.getInstance().getArchivedListings();
        RecyclerView recyclerView = findViewById(R.id.archived_listings);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        view = new ListingView(archivedListings, false);
        recyclerView.setAdapter(view);
    }
}