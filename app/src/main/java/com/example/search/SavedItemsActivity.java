package com.example.search;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.util.List;

public class SavedItemsActivity extends AppCompatActivity implements SearchItemClickListener {

    private RecyclerView recyclerView;
    private SearchAdapter adapter;
    private TextView emptyTextView;
    private List<SearchItem> savedItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_items);

        // Enable back button in action bar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Các mục đã lưu");
        }

        // Initialize views
        recyclerView = findViewById(R.id.savedItemsRecyclerView);
        emptyTextView = findViewById(R.id.emptyTextView);

        // Setup RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        loadSavedItems();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Reload saved items when returning to this activity
        loadSavedItems();
    }

    private void loadSavedItems() {
        // Load all saved items
        savedItems = FileUtils.getAllSavedItems(this);

        if (savedItems.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            emptyTextView.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            emptyTextView.setVisibility(View.GONE);

            adapter = new SearchAdapter(savedItems);
            adapter.setOnItemClickListener(this);
            recyclerView.setAdapter(adapter);
        }
    }

    @Override
    public void onItemClick(SearchItem item) {
        // Open DetailActivity for the selected item
        Intent intent = new Intent(this, DetailActivity.class);
        intent.putExtra("ITEM_ID", item.getId());
        startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            // Handle the back button
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}