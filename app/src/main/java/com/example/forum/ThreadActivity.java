package com.example.forum;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

public class ThreadActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thread);

        TextView categoryHeader = findViewById(R.id.categoryHeader);

        String categoryName = getIntent().getStringExtra("category_name");
        categoryHeader.setText(categoryName);
    }
}

