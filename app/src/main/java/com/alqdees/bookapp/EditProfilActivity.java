package com.alqdees.bookapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.alqdees.bookapp.databinding.ActivityEditProfilBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class EditProfilActivity extends AppCompatActivity {


    private ActivityEditProfilBinding binding;
    private FirebaseAuth auth;
    private String name , email,uid;
    private String timestamp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEditProfilBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        auth = FirebaseAuth.getInstance();
        Intent intent = getIntent();
         name = intent.getStringExtra("name");
         email = intent.getStringExtra("email");
         timestamp = intent.getStringExtra("timestamp");
         timestamp = intent.getStringExtra("uid");
        binding.editMail.setText(email);
        binding.editName.setText(name);
        binding.edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditData();
            }
        });

    }

    private void EditData() {

        String studentUser = auth.getCurrentUser().getUid();
        String nameEdit = binding.editName.getText().toString();
        String EmailEdit = binding.editMail.getText().toString();
//        final DatabaseReference databaseReference = FirebaseDatabase.getInstance()
//        .getReference("Users").child(studentUser);
//        String replaceName = studentChangeFullName.getText().toString().trim();
//        String replacePhoneNumber = studentChangePhoneNum.getText().toString().trim();
//        Map<String, Object> updates = new HashMap<>();
//        updates.put("fullName", replaceName);
//        updates.put("phoneNumber", replacePhoneNumber);
//
//        databaseReference.updateChildren(updates);
//        Toast.makeText(StudentProfileEditAct.this, "Changes has been made", Toast.LENGTH_SHORT).show();
        DatabaseReference reference = FirebaseDatabase
                .getInstance().
                getReferenceFromUrl
                        ("https://book-app-8ad03-default-rtdb.firebaseio.com/users").child(studentUser);
        HashMap<String ,Object> hashMap = new HashMap<>();
        hashMap.put("name",nameEdit);
        hashMap.put("email",EmailEdit);
       reference.updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
           @Override
           public void onSuccess(Void unused) {
               Toast.makeText(EditProfilActivity.this,
                       "تم التحديث", Toast.LENGTH_SHORT).show();
               onBackPressed();
           }
       });



    }
}