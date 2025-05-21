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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        searchEditText = findViewById(R.id.searchEditText);
        recyclerView = findViewById(R.id.recyclerView);
        progressBar = findViewById(R.id.progressBar);
        resultsCountTextView = findViewById(R.id.resultsCountTextView);
        addButton = findViewById(R.id.addButton);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        searchAdapter = new SearchAdapter(filteredItems);
        searchAdapter.setOnItemClickListener(this);
        recyclerView.setAdapter(searchAdapter);

        allItems = FileUtils.getAllSavedItems(this); // ✅ Load from storage
        performSearch(""); // Display all saved items

        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                String searchTerm = s.toString().trim().toLowerCase();
                performSearch(searchTerm);
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
        allItems = FileUtils.getAllSavedItems(this); // Refresh on return
        performSearch(searchEditText.getText().toString().trim().toLowerCase());
    }


    @Override
    public void onItemClick(SearchItem item) {
        Toast.makeText(this, "Đã chọn: " + item.getTitle(), Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, DetailActivity.class);
        intent.putExtra("ITEM_ID", item.getId());
        startActivity(intent);
    }

    private void initSampleData() {
        allItems.add(new SearchItem(1, "Hướng dẫn Android Studio", "Hướng dẫn cơ bản về sử dụng Android Studio"));
        allItems.add(new SearchItem(2, "Lập trình Java căn bản", "Những kiến thức cơ bản về ngôn ngữ lập trình Java"));
        allItems.add(new SearchItem(3, "Thiết kế giao diện Android", "Hướng dẫn thiết kế UI/UX cho ứng dụng Android"));
        allItems.add(new SearchItem(4, "RecyclerView trong Android", "Cách sử dụng RecyclerView để hiển thị danh sách"));
        allItems.add(new SearchItem(5, "SQLite Database", "Sử dụng SQLite để lưu trữ dữ liệu trong ứng dụng Android"));
        allItems.add(new SearchItem(6, "Android Fragments", "Làm việc với Fragments trong ứng dụng Android"));
        allItems.add(new SearchItem(7, "Networking trong Android", "Thực hiện các yêu cầu mạng với Retrofit và Volley"));
        allItems.add(new SearchItem(8, "Xử lý sự kiện trong Android", "Cách xử lý các sự kiện người dùng trong Android"));
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
