package com.example.search;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class DetailActivity extends AppCompatActivity {

    private EditText titleEditText;
    private EditText descriptionEditText;
    private TextView idTextView;
    private Button saveButton;
    private Button deleteButton;
    private Button editButton;
    private SearchItem currentItem;
    private boolean isEditMode = false;

    private SearchItemDatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Chi tiết");
        }

        initializeViews();
        dbHelper = new SearchItemDatabaseHelper(this);

        int itemId = getIntent().getIntExtra("ITEM_ID", -1);

        if (itemId != -1) {
            SearchItem savedItem = dbHelper.getItemById(itemId);

            if (savedItem != null) {
                currentItem = savedItem;
                displayItemDetails(currentItem);
                updateButtonStates(true, false);
            } else {
                Toast.makeText(this, "Không tìm thấy thông tin", Toast.LENGTH_SHORT).show();
                finish();
            }
        } else {
            Toast.makeText(this, "Không tìm thấy thông tin", Toast.LENGTH_SHORT).show();
            finish();
        }

        setupButtonClickListeners();
    }

    private void initializeViews() {
        titleEditText = findViewById(R.id.titleEditText);
        descriptionEditText = findViewById(R.id.descriptionEditText);
        idTextView = findViewById(R.id.detailIdTextView);
        saveButton = findViewById(R.id.saveButton);
        deleteButton = findViewById(R.id.deleteButton);
        editButton = findViewById(R.id.editButton);
    }

    private void setupButtonClickListeners() {
        editButton.setOnClickListener(v -> toggleEditMode());
        saveButton.setOnClickListener(v -> saveCurrentItem());
        deleteButton.setOnClickListener(v -> showDeleteConfirmDialog());
    }

    private void toggleEditMode() {
        isEditMode = !isEditMode;
        titleEditText.setEnabled(isEditMode);
        descriptionEditText.setEnabled(isEditMode);

        if (isEditMode) {
            editButton.setText("Hủy");
            saveButton.setEnabled(true);
            saveButton.setText("Lưu thay đổi");
            titleEditText.requestFocus();
        } else {
            editButton.setText("Sửa");
            saveButton.setText("Đã lưu");
            saveButton.setEnabled(false);
            // Khôi phục dữ liệu gốc nếu hủy
            if (currentItem != null) {
                displayItemDetails(currentItem);
            }
        }
    }

    private void displayItemDetails(SearchItem item) {
        titleEditText.setText(item.getTitle());
        descriptionEditText.setText(item.getDescription());
        idTextView.setText("ID: " + item.getId());

        // Đặt chỉ đọc ban đầu
        titleEditText.setEnabled(false);
        descriptionEditText.setEnabled(false);
    }

    private void updateButtonStates(boolean isSaved, boolean isEditMode) {
        if (isEditMode) {
            editButton.setText("Hủy");
            saveButton.setEnabled(true);
            saveButton.setText("Lưu thay đổi");
        } else {
            editButton.setText("Sửa");
            saveButton.setEnabled(false);
            saveButton.setText(isSaved ? "Đã lưu" : "Lưu");
        }
        deleteButton.setEnabled(isSaved);
        this.isEditMode = isEditMode;
    }

    private void saveCurrentItem() {
        if (currentItem != null && isEditMode) {
            String newTitle = titleEditText.getText().toString().trim();
            String newDescription = descriptionEditText.getText().toString().trim();

            if (newTitle.isEmpty()) {
                Toast.makeText(this, "Tiêu đề không được để trống", Toast.LENGTH_SHORT).show();
                return;
            }

            // Cập nhật thông tin item
            currentItem.setTitle(newTitle);
            currentItem.setDescription(newDescription);

            // Lưu vào database
            int rowsUpdated = dbHelper.updateItem(currentItem);
            if (rowsUpdated > 0) {
                Toast.makeText(this, "Đã cập nhật thành công", Toast.LENGTH_SHORT).show();
                toggleEditMode(); // Thoát chế độ chỉnh sửa
                updateButtonStates(true, false);
            } else {
                Toast.makeText(this, "Cập nhật thất bại", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void showDeleteConfirmDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Xác nhận xóa")
                .setMessage("Bạn có chắc chắn muốn xóa item này?")
                .setPositiveButton("Xóa", (dialog, which) -> deleteCurrentItem())
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void deleteCurrentItem() {
        if (currentItem != null) {
            int rowsDeleted = dbHelper.deleteItem(currentItem.getId());
            if (rowsDeleted > 0) {
                Toast.makeText(this, "Đã xóa thành công", Toast.LENGTH_SHORT).show();
                finish(); // Quay về màn hình trước
            } else {
                Toast.makeText(this, "Xóa thất bại", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (isEditMode) {
            // Nếu đang trong chế độ chỉnh sửa, hỏi người dùng có muốn hủy không
            new AlertDialog.Builder(this)
                    .setTitle("Hủy chỉnh sửa")
                    .setMessage("Bạn có muốn hủy các thay đổi chưa lưu?")
                    .setPositiveButton("Hủy thay đổi", (dialog, which) -> {
                        toggleEditMode();
                        super.onBackPressed();
                    })
                    .setNegativeButton("Tiếp tục sửa", null)
                    .show();
        } else {
            super.onBackPressed();
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