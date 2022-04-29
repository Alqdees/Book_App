package com.alqdees.bookapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.alqdees.bookapp.databinding.ActivityPdfEditBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class PdfEditActivity extends AppCompatActivity {
    private ActivityPdfEditBinding binding;
    private String bookId;
    private ProgressDialog progressDialog;
    private ArrayList<String> arrayList,arrayId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPdfEditBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        bookId = getIntent().getStringExtra("bookId");
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("أنتظر...");
        progressDialog.setCanceledOnTouchOutside(false);
        loadCategories();
        loadBookInfo();


        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        binding.categoryTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                categoryDialog();
            }
        });
        binding.updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateData();
            }
        });
    }
    private String title = "",description = "";
    private void validateData(){
        title = binding.titleEt.getText().toString().trim();
        description = binding.descriptionEt.getText().toString().trim();
        if (TextUtils.isEmpty(title)){
            Toast.makeText(PdfEditActivity.this, "حقل العنوان فارغ", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(description)){
            Toast.makeText(PdfEditActivity.this, "حقل الوصف فارغ", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(selectedId)){
            Toast.makeText(PdfEditActivity.this, "اختر فئة للكتاب", Toast.LENGTH_SHORT).show();
        }
        else {
            updatePdf();
        }
    }
    private void updatePdf(){
        progressDialog.setMessage("جاري تحديث المعلومات..");
        progressDialog.show();
        HashMap<String,Object> hashMap = new HashMap<>();
        hashMap.put("title",""+title);
        hashMap.put("description",""+description);
        hashMap.put("categoryId",""+selectedId);
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Books");
        ref.child(bookId).updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                progressDialog.dismiss();
                Toast.makeText(PdfEditActivity.this, "جاري تحديث المعلومات..", Toast.LENGTH_SHORT).show();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(PdfEditActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }



    private void loadBookInfo() {
        DatabaseReference refBook = FirebaseDatabase.getInstance().getReference("Books");
        refBook.child(bookId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                selectedId = ""+snapshot.child("categoryId").getValue();
                String description = ""+snapshot.child("description").getValue();
                String title = ""+snapshot.child("title").getValue();

                binding.titleEt.setText(title);
                binding.descriptionEt.setText(description);

                DatabaseReference refCategory = FirebaseDatabase.getInstance().getReference("Categories");
                refCategory.child(selectedId).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String category = ""+snapshot.child("category").getValue();
                        binding.categoryTv.setText(category);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private String selectedId = "",selectedTitle = "";
    private void categoryDialog(){
        String[] categoriesArray = new String[arrayList.size()];
        for (int i = 0 ; i<arrayList.size();i++){
            categoriesArray[i] = arrayList.get(i);

        }
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("أختر فئة ").setItems(categoriesArray, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                selectedId = arrayId.get(i);
                selectedTitle = arrayList.get(i);
                binding.categoryTv.setText(selectedTitle);
            }
        }).show();

    }

    private void loadCategories() {
        arrayId = new ArrayList<>();
        arrayList = new ArrayList<>();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Categories");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                arrayId.clear();
                arrayList.clear();
                for (DataSnapshot ds : snapshot.getChildren()){
                    String id = ""+ds.child("id").getValue();
                    String category = ""+ds.child("category").getValue();
                    arrayId.add(id);
                    arrayList.add(category);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}