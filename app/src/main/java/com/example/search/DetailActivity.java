package com.example.search;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class DetailActivity extends AppCompatActivity {

    private TextView titleTextView;
    private TextView descriptionTextView;
    private TextView idTextView;
    private Button saveButton;
    private Button deleteButton;
    private SearchItem currentItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        // Enable back button in action bar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Chi tiết");
        }

        // Initialize views
        titleTextView = findViewById(R.id.detailTitleTextView);
        descriptionTextView = findViewById(R.id.detailDescriptionTextView);
        idTextView = findViewById(R.id.detailIdTextView);
        saveButton = findViewById(R.id.saveButton);
        deleteButton = findViewById(R.id.deleteButton);

        // Get item ID from intent
        int itemId = getIntent().getIntExtra("ITEM_ID", -1);

        if (itemId != -1) {
            // First check if this item is already saved
            SearchItem savedItem = FileUtils.loadItem(this, itemId);

            if (savedItem != null) {
                // Item is already saved, load from file
                currentItem = savedItem;
                displayItemDetails(currentItem);
                updateButtonStates(true);
            } else {
                // Item is not saved, load from sample data
                loadItemDetails(itemId);
                updateButtonStates(false);
            }
        } else {
            Toast.makeText(this, "Không tìm thấy thông tin", Toast.LENGTH_SHORT).show();
            finish();
        }

        // Set up button click listeners
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveCurrentItem();
            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteCurrentItem();
            }
        });
    }

    private void loadItemDetails(int itemId) {
        // In a real app, you would fetch item details from a database or API
        // For this example, we're creating dummy data
        currentItem = null;

        // This is where you would normally query your database
        // For demonstration, we're creating a dummy item based on the ID
        switch (itemId) {
            case 1:
                currentItem = new SearchItem(1, "Hướng dẫn Android Studio", "Hướng dẫn cơ bản về sử dụng Android Studio");
                break;
            case 2:
                currentItem = new SearchItem(2, "Lập trình Java căn bản", "Những kiến thức cơ bản về ngôn ngữ lập trình Java");
                break;
            // Add more cases for other items
            default:
                currentItem = new SearchItem(itemId, "Item #" + itemId, "Chi tiết về item #" + itemId);
                break;
        }

        // Display item details
        if (currentItem != null) {
            displayItemDetails(currentItem);
        }
    }

    private void displayItemDetails(SearchItem item) {
        titleTextView.setText(item.getTitle());
        descriptionTextView.setText(item.getDescription());
        idTextView.setText("ID: " + item.getId());
    }

    private void updateButtonStates(boolean isSaved) {
        if (isSaved) {
            saveButton.setEnabled(false);
            saveButton.setText("Đã lưu");
            deleteButton.setEnabled(true);
        } else {
            saveButton.setEnabled(true);
            saveButton.setText("Lưu");
            deleteButton.setEnabled(false);
        }
    }

    private void saveCurrentItem() {
        if (currentItem != null) {
            boolean success = FileUtils.saveItem(this, currentItem);
            if (success) {
                Toast.makeText(this, "Đã lưu thành công", Toast.LENGTH_SHORT).show();
                updateButtonStates(true);
            } else {
                Toast.makeText(this, "Lưu thất bại", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void deleteCurrentItem() {
        if (currentItem != null) {
            boolean success = FileUtils.deleteItem(this, currentItem.getId());
            if (success) {
                Toast.makeText(this, "Đã xóa thành công", Toast.LENGTH_SHORT).show();
                updateButtonStates(false);
            } else {
                Toast.makeText(this, "Xóa thất bại", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            // Handle the back button press
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}