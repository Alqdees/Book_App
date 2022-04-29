package com.alqdees.bookapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.alqdees.bookapp.databinding.ActivityPdfAddBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashMap;

public class PdfAddActivity extends AppCompatActivity {
    private ActivityPdfAddBinding binding;
    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;
    private ArrayList<String> categoryTitleArrayList,categoryIdArrayList;
    private static final int PDF_PICK_CODE =1000;
    private Uri pdfUri = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPdfAddBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        firebaseAuth = FirebaseAuth.getInstance();
        loadPdfCategories();
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("أنتظر...");
        progressDialog.setCanceledOnTouchOutside(false);

        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        binding.attachment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pdfPickIntent();
            }
        });

        binding.categoryTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                categoryPickDialog();
            }
        });

        binding.uploadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateData();
            }
        });

    }
    private String title = "",description = "";
    private void validateData() {
        title = binding.titleEt.getText().toString().trim();
        description = binding.description.getText().toString().trim();
//        categoryPick = binding.categoryTv.getText().toString().trim();
        if (TextUtils.isEmpty(title)){
            Toast.makeText(PdfAddActivity.this, "حقل العنوان فارغ", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(description)){
            Toast.makeText(PdfAddActivity.this, "حقل الوصف فارغ", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(selectedCategoryTitle)){
            Toast.makeText(PdfAddActivity.this, "أختر فئة للكتاب", Toast.LENGTH_LONG).show();
        }
        else if (pdfUri == null){
            Toast.makeText(PdfAddActivity.this, "أختر الكتاب", Toast.LENGTH_LONG).show();
        }
        else {
            uploadPdfToStorage();
        }

    }

    private void uploadPdfToStorage() {
        //uploadPdf
//        Log.d(TAG,"uploadPdfToStorage: uploading to storage...");
        progressDialog.setMessage("جاري تحميل الملف...");
        progressDialog.show();
//        long timestamp = System.currentTimeMillis();
        long timestamp = System.currentTimeMillis();

        String filePath = "Books/" + timestamp;
        StorageReference storageReference = FirebaseStorage.getInstance().getReference(filePath);
        storageReference.putFile(pdfUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                Log.d(TAG,"onSuccess: PDF upload to storage");
//                Log.d(TAG,"onSuccess: getting pdf url");
                Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                while(!uriTask.isSuccessful());
                    String uploadedPdfUrl = ""+uriTask.getResult();
                    uploadPdfInfoToDb(uploadedPdfUrl,timestamp);

            }
        })
        .addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
//              Log.d(TAG,"onFailure: PDF upload failed due to "+e.getMessage());
                Toast.makeText(PdfAddActivity.this, "فشل التحميل...", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void uploadPdfInfoToDb(String uploadedPdfUrl, long timestamp) {
//        Log.d(TAG,"loadPdfCategories: Loading pdf categories...");
        progressDialog.setMessage("جاري تحميل معلومات الملف...");
        String uid = firebaseAuth.getUid();
        HashMap<String,Object> hashMap = new HashMap<>();
        hashMap.put("uid",""+uid);
        hashMap.put("id",""+timestamp);
        hashMap.put("title",""+title);
        hashMap.put("description",""+description);
        hashMap.put("categoryId",""+selectedCategoryId);
        hashMap.put("url",""+uploadedPdfUrl);
        hashMap.put("timestamp",""+timestamp);
        hashMap.put("viewCount",""+0);
        hashMap.put("downloadsCount",""+0);

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Books");
        ref.child(""+timestamp).setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                progressDialog.dismiss();
                Toast.makeText(PdfAddActivity.this, "تم التحميل  بنجاح..", Toast.LENGTH_LONG).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(PdfAddActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void loadPdfCategories() {
//        Log.d(TAG,"loadPdfCategories: تحميل الملف");
        categoryTitleArrayList = new ArrayList<>();
        categoryIdArrayList = new ArrayList<>();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Categories");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
               categoryTitleArrayList.clear();
               categoryIdArrayList.clear();
                for (DataSnapshot ds : snapshot.getChildren()){
                 String categoryId = ""+ds.child("id").getValue();
                 String categoryTitle = ""+ds.child("category").getValue();
                 categoryTitleArrayList.add(categoryTitle);
                 categoryIdArrayList.add(categoryId);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private String selectedCategoryId,selectedCategoryTitle;

    private void categoryPickDialog() {
//        Log.d(TAG,"categoryPickDialog : showing category pick dialog");
        String[] categoriesArray = new String[categoryTitleArrayList.size()];
        for (int a = 0; a < categoryTitleArrayList.size() ; a++){
            categoriesArray[a] = categoryTitleArrayList.get(a);
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("أختر فئة الكتاب").setItems(categoriesArray, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
               selectedCategoryTitle = categoryTitleArrayList.get(i);
               selectedCategoryId = categoryIdArrayList.get(i);
                binding.categoryTv.setText(selectedCategoryTitle);
//                Log.d(TAG,"Selected category: "+selectedCategoryId+" " + selectedCategoryTitle);
            }
        }).show();
    }

    private void pdfPickIntent() {
//        Log.d(TAG,"pdfPickIntent: start pdf pick intent");
        Intent intent = new Intent();
        intent.setType("application/pdf");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"اختر ملف PDF"),PDF_PICK_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK){
            if (requestCode == PDF_PICK_CODE){
//                Log.d(TAG,"onActivityResult: PDF PICKED");
                pdfUri = data.getData();
//                Log.d(TAG,"onActivityResult URI: "+pdfUri);

            }
        }
        else {
//            Log.d(TAG,"onActivityResult: cancelled picking pdf");
            Toast.makeText(this, "اللغاء استيراد الملف", Toast.LENGTH_SHORT).show();
        }
    }
}