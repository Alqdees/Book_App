package com.alqdees.bookapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.alqdees.bookapp.databinding.ActivitySelectBinding;

public class SelectActivity extends AppCompatActivity {
    private ActivitySelectBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySelectBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.book.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SelectActivity.this,DashboardActivity.class));
            }
        });
        binding.video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SelectActivity.this,DashboardActivity.class));

            }
        });

    }
}