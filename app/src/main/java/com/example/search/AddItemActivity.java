package com.example.search;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class AddItemActivity extends AppCompatActivity {

    private EditText titleEditText, descriptionEditText;
    private Button saveButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);

        titleEditText = findViewById(R.id.titleEditText);
        descriptionEditText = findViewById(R.id.descriptionEditText);
        saveButton = findViewById(R.id.saveItemButton);

        saveButton.setOnClickListener(v -> saveNewItem());
    }

    private void saveNewItem() {
        String title = titleEditText.getText().toString().trim();
        String description = descriptionEditText.getText().toString().trim();

        if (title.isEmpty() || description.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ tiêu đề và mô tả", Toast.LENGTH_SHORT).show();
            return;
        }

        // Tạo ID đơn giản dựa trên thời gian
        int id = (int) (System.currentTimeMillis() / 1000);
        SearchItem newItem = new SearchItem(id, title, description);

        boolean saved = SearchItemDatabaseHelper.saveItem(this, newItem);
        if (saved) {
            Toast.makeText(this, "Đã lưu thành công", Toast.LENGTH_SHORT).show();
            finish(); // Quay lại MainActivity
        } else {
            Toast.makeText(this, "Lỗi khi lưu", Toast.LENGTH_SHORT).show();
        }
    }
}
