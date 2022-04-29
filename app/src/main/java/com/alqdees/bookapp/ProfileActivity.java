package com.alqdees.bookapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.alqdees.bookapp.databinding.ActivityPdfEditBinding;

public class ProfileActivity extends AppCompatActivity {
    private ActivityPdfEditBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
    }
}