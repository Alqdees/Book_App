package com.alqdees.bookapp;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.alqdees.bookapp.databinding.ActivityProfileBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileActivity extends AppCompatActivity {
    private ActivityProfileBinding binding;

    private FirebaseAuth auth;
    private String email,name,uid;
    private Long timestamp;
    private Intent intent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        auth = FirebaseAuth.getInstance();
        loadUserInfo();
        intent = new Intent(ProfileActivity.this,EditProfilActivity.class);
        binding.editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (name.isEmpty() || email.isEmpty()){
                    return;
                }else {
                    intent.putExtra("name", name);
                    intent.putExtra("email", email);
                    intent.putExtra("timestamp", timestamp);
                    intent.putExtra("uid", uid);
                    startActivity(intent);
                }
            }
        });
    }

    private void loadUserInfo() {
        DatabaseReference reference = FirebaseDatabase
                .getInstance().
                getReferenceFromUrl
                        ("https://book-app-8ad03-default-rtdb.firebaseio.com/users");

        reference.child(auth.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                 email = (String) snapshot.child("email").getValue();
                 name = (String) snapshot.child("name").getValue();
                uid = (String) snapshot.child("uid").getValue();
                 timestamp = snapshot.child("timestamp").getValue(Long.class);

//                String formatDate = MyApplication.formatTimestamp(timestamp);
                binding.emailTv.setText(email);
                binding.nameTv.setText(name);
//                binding.Member.setText(formatDate);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                Toast.makeText(ProfileActivity.this, ""+error.getMessage(), Toast.LENGTH_SHORT).show();


            }
        });

    }
}