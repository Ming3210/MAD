package com.example.search;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements SearchItemClickListener {

    private EditText searchEditText;
    private RecyclerView recyclerView;
    private SearchAdapter searchAdapter;
    private ProgressBar progressBar;
    private TextView resultsCountTextView;
    private FloatingActionButton addButton;

    private List<SearchItem> allItems = new ArrayList<>();
    private List<SearchItem> filteredItems = new ArrayList<>();

    private SearchItemDatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        searchEditText = findViewById(R.id.searchEditText);
        recyclerView = findViewById(R.id.recyclerView);
        progressBar = findViewById(R.id.progressBar);
        resultsCountTextView = findViewById(R.id.resultsCountTextView);
        addButton = findViewById(R.id.addButton);

        dbHelper = new SearchItemDatabaseHelper(this);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        searchAdapter = new SearchAdapter(filteredItems);
        searchAdapter.setOnItemClickListener(this);
        recyclerView.setAdapter(searchAdapter);

        allItems = dbHelper.getAllSavedItems(this);
        performSearch("");

        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                performSearch(s.toString().trim().toLowerCase());
            }
        });

        addButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddItemActivity.class);
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        allItems = dbHelper.getAllSavedItems(this); // Refresh on return
        performSearch(searchEditText.getText().toString().trim().toLowerCase());
    }

    @Override
    public void onItemClick(SearchItem item) {
        Toast.makeText(this, "Đã chọn: " + item.getTitle(), Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, DetailActivity.class);
        intent.putExtra("ITEM_ID", item.getId());
        startActivity(intent);
    }

    private void performSearch(final String searchTerm) {
        progressBar.setVisibility(View.VISIBLE);
        recyclerView.postDelayed(() -> {
            filteredItems.clear();
            for (SearchItem item : allItems) {
                if (searchTerm.isEmpty() ||
                        item.getTitle().toLowerCase().contains(searchTerm) ||
                        item.getDescription().toLowerCase().contains(searchTerm)) {
                    filteredItems.add(item);
                }
            }

            resultsCountTextView.setVisibility(View.VISIBLE);
            if (searchTerm.isEmpty()) {
                resultsCountTextView.setText("Tất cả kết quả (" + filteredItems.size() + ")");
            } else {
                resultsCountTextView.setText(filteredItems.size() + " kết quả cho \"" + searchTerm + "\"");
            }

            searchAdapter.notifyDataSetChanged();
            progressBar.setVisibility(View.GONE);
        }, 300);
    }
}
