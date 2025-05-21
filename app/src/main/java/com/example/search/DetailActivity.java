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

    private SearchItemDatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Chi tiết");
        }

        titleTextView = findViewById(R.id.detailTitleTextView);
        descriptionTextView = findViewById(R.id.detailDescriptionTextView);
        idTextView = findViewById(R.id.detailIdTextView);
        saveButton = findViewById(R.id.saveButton);
        deleteButton = findViewById(R.id.deleteButton);

        dbHelper = new SearchItemDatabaseHelper(this);

        int itemId = getIntent().getIntExtra("ITEM_ID", -1);

        if (itemId != -1) {
            SearchItem savedItem = dbHelper.getItemById(itemId);

            if (savedItem != null) {
                currentItem = savedItem;
                displayItemDetails(currentItem);
                updateButtonStates(true);
            } else {
                loadItemDetails(itemId);
                updateButtonStates(false);
            }
        } else {
            Toast.makeText(this, "Không tìm thấy thông tin", Toast.LENGTH_SHORT).show();
            finish();
        }

        saveButton.setOnClickListener(v -> saveCurrentItem());

        deleteButton.setOnClickListener(v -> deleteCurrentItem());
    }

    private void loadItemDetails(int itemId) {
        // Dữ liệu mẫu - có thể thay bằng API hoặc source khác
        switch (itemId) {
            case 1:
                currentItem = new SearchItem(1, "Hướng dẫn Android Studio", "Hướng dẫn cơ bản về sử dụng Android Studio");
                break;
            case 2:
                currentItem = new SearchItem(2, "Lập trình Java căn bản", "Những kiến thức cơ bản về ngôn ngữ lập trình Java");
                break;
            default:
                currentItem = new SearchItem(itemId, "Item #" + itemId, "Chi tiết về item #" + itemId);
                break;
        }

        displayItemDetails(currentItem);
    }

    private void displayItemDetails(SearchItem item) {
        titleTextView.setText(item.getTitle());
        descriptionTextView.setText(item.getDescription());
        idTextView.setText("ID: " + item.getId());
    }

    private void updateButtonStates(boolean isSaved) {
        saveButton.setEnabled(!isSaved);
        saveButton.setText(isSaved ? "Đã lưu" : "Lưu");
        deleteButton.setEnabled(isSaved);
    }

    private void saveCurrentItem() {
        if (currentItem != null) {
            long resultId = dbHelper.insertItem(currentItem);
            if (resultId != -1) {
                Toast.makeText(this, "Đã lưu thành công", Toast.LENGTH_SHORT).show();
                updateButtonStates(true);
                currentItem = dbHelper.getItemById((int) resultId); // Lấy lại với ID thực tế nếu cần
            } else {
                Toast.makeText(this, "Lưu thất bại", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void deleteCurrentItem() {
        if (currentItem != null) {
            int rowsDeleted = dbHelper.deleteItem(currentItem.getId());
            if (rowsDeleted > 0) {
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
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
