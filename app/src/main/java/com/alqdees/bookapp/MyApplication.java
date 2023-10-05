package com.alqdees.bookapp;

import static com.alqdees.bookapp.Constants.MAX_BYTES_PDF;

import android.app.Application;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnErrorListener;
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener;
import com.github.barteksc.pdfviewer.listener.OnPageErrorListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

public class MyApplication extends Application {
    private Context context;


    @Override
    public void onCreate() {
        super.onCreate();
    }
    public static final String formatTimestamp(long timestamp){
        Calendar calendar = Calendar.getInstance(Locale.ENGLISH);
        calendar.setTimeInMillis(timestamp);
        String date = DateFormat.format("dd/MM/yyyy",calendar).toString();

        return date;
    }


    public static void deleteBook(Context context, String bookId, String bookUrl, String bookTitle) {

        ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setTitle("أنتظر...");
        progressDialog.setMessage(bookTitle+"جاري الحذف ");
        progressDialog.show();
        StorageReference ref = FirebaseStorage.getInstance().getReferenceFromUrl(bookUrl);
        ref.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Book");
                reference.child(bookId).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        progressDialog.dismiss();
                        Toast.makeText(context, "تم الحذف ...", Toast.LENGTH_SHORT).show();
                    }
                })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                progressDialog.dismiss();
                                Toast.makeText(context, ""+e.getMessage(), Toast.LENGTH_SHORT).show();

                            }
                        });
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(context, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
    public static void loadPdfSize(String pdfUrl,TextView sizeTv) {

        StorageReference ref;
        ref = FirebaseStorage.getInstance().getReferenceFromUrl(pdfUrl);
        ref.getMetadata().addOnSuccessListener(new OnSuccessListener<StorageMetadata>() {
            @Override
            public void onSuccess(StorageMetadata storageMetadata) {
                double bytes = storageMetadata.getSizeBytes();
                /*تحويل بين الوحدات */
                double kb = bytes/1024;
                double mb = kb/1024;
                if (mb >= 1){
                    sizeTv.setText(String.format("%.2f",mb)+"MB");
                }
                else if (kb >= 1){
                    sizeTv.setText(String.format("%.2f",kb)+"KB");
                }
                else {
                    sizeTv.setText(String.format("%.2f",bytes)+"bytes");
                }
            }
        }).addOnFailureListener(new OnFailureListener() {

            @Override
            public void onFailure(@NonNull Exception e) {
//                Toast.makeText(context, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    public static void loadPdfFromUrlSinglePage(String pdfUrl, PDFView pdfViewer,
                                                ProgressBar progressBar,TextView pagesTv) {

        StorageReference ref = FirebaseStorage.getInstance().getReferenceFromUrl(pdfUrl);
        ref.getBytes(MAX_BYTES_PDF).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                pdfViewer
                        .fromBytes(bytes)
                        .pages(0)
                        .spacing(0)
                        .swipeHorizontal(false)
                        .enableSwipe(false)
                        .onError(new OnErrorListener() {

                            @Override
                            public void onError(Throwable t) {

                                progressBar.setVisibility(View.INVISIBLE);
                            }
                        })
                        .onPageError(new OnPageErrorListener() {

                            @Override
                            public void onPageError(int page, Throwable t) {

                               progressBar.setVisibility(View.INVISIBLE);

                            }
                        }).onLoad(new OnLoadCompleteListener() {

                    @Override
                    public void loadComplete(int nbPages) {

                        progressBar.setVisibility(View.INVISIBLE);

                        if (pagesTv != null) {

                            pagesTv.setText(String.valueOf(nbPages));

                        }
                    }
                })
                        .load();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
               progressBar.setVisibility(View.INVISIBLE);
            }
        });
    }


    public static void loadCategory(String categoryId ,TextView categoryTv) {

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Category");

        ref.child(categoryId).addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                String category = ""+snapshot.child("category").getValue();

                categoryTv.setText(category);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public static void incrementBookViewCount(String bookId){

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Book");

        ref.child(bookId).addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                String viewCount = snapshot.child("viewCount").getValue().toString();

                if (viewCount.equals("") || viewCount.equals("null")){

                     viewCount = "0";
                }
                    long newViewCount = Long.parseLong(viewCount) + 1;

                    HashMap<String, Object> hashMap = new HashMap<>();

                    hashMap.put("viewCount", ""+newViewCount);

                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Book");

                    reference.child(bookId).updateChildren(hashMap);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public static void downloadBook(Context context,String bookId,String bookTitle,String bookUrl){

        String name = bookTitle + ".pdf";

        ProgressDialog progressDialog = new ProgressDialog(context);

        progressDialog.setTitle("انتظر..");

        progressDialog.setMessage(" جاري التحميل... "+ name);

        progressDialog.setCanceledOnTouchOutside(true);

        progressDialog.show();

        StorageReference storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(bookUrl);

        storageReference.getBytes(MAX_BYTES_PDF).addOnSuccessListener(new OnSuccessListener<byte[]>() {

            @RequiresApi(api = Build.VERSION_CODES.R)

            @Override
            public void onSuccess(byte[] bytes) {

                Toast.makeText(context, "جاري التحميل...", Toast.LENGTH_SHORT).show();

                saveDownloadBook(context,progressDialog,bytes,name,bookId);

            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        progressDialog.dismiss();

                        Toast.makeText(context,
                                "خطأ في التنزيل"+e.getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                });

    }

    @RequiresApi(api = Build.VERSION_CODES.R)
    private static void saveDownloadBook(Context context, ProgressDialog progressDialog,
                                         byte[] bytes, String name, String bookId) {
        try{
            File downloadsFolder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);

            String filePath = downloadsFolder.getPath() + "/" + name;

            FileOutputStream out = new FileOutputStream(filePath);

            out.write(bytes);

            out.close();

            Toast.makeText(context, "تم الحفظ في المستندات", Toast.LENGTH_SHORT).show();

            progressDialog.dismiss();

            incrementBookDownloadCount(bookId);

        }catch (Exception e){

            Toast.makeText(context, ""+e.getMessage(), Toast.LENGTH_SHORT).show();

            progressDialog.dismiss();

        }

    }

    private static void incrementBookDownloadCount(String bookId) {

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Book");
        ref.child(bookId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String downloadsCount = ""+snapshot.child("downloadsCount").getValue();
                if (downloadsCount.equals("")||downloadsCount.equals("null")){
                    downloadsCount = "0";
                }
                int newDownloadsCount = Integer.parseInt(downloadsCount) + 1;
                HashMap<String,Object> hashMap =new HashMap<>();
                hashMap.put("downloadsCount",""+newDownloadsCount);
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Book");
                reference.child(bookId).updateChildren(hashMap)
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                            }
                        });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

//    public static void loadPdfPageCount(Context context,String pdfUrl,TextView pageTv){
//        StorageReference storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(pdfUrl);
//        storageReference.getBytes(MAX_BYTES_PDF).addOnSuccessListener(new OnSuccessListener<byte[]>() {
//            @Override
//            public void onSuccess(byte[] bytes) {
//
//                PDFView pdfView = new PDFView(context,null);
//                pdfView.fromBytes(bytes)
//                        .onLoad(new OnLoadCompleteListener() {
//                            @Override
//                            public void loadComplete(int nbPages) {
//                                pageTv.setText(String.valueOf(nbPages));
//                            }
//                        });
//            }
//        })
//                .addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception e) {
//
//
//            }
//        });
//    }


    public static void addToFavorite(Context context,String bookId){

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        if (firebaseAuth.getCurrentUser() == null){
            Toast.makeText(context, "لم تسجل الدخول", Toast.LENGTH_SHORT).show();
        }
        else {
            long timestamp = System.currentTimeMillis();
            HashMap<String,Object> hashMap = new HashMap<>();
            hashMap.put("bookId",""+bookId);
            hashMap.put("timestamp",""+timestamp);

            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users");
            ref.child(firebaseAuth.getUid()).child("Favorites").child(bookId).setValue(hashMap)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(context, "تمت الاضافة الى المفضلة", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(context, "لم تتم الاضافة", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    public static void removeFromFavorite(Context context,String bookId){


        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        if (firebaseAuth.getCurrentUser() == null){
            Toast.makeText(context, "لم تسجل الدخول", Toast.LENGTH_SHORT).show();
        }
        else {

            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users");
            ref.child(firebaseAuth.getUid()).child("Favorites").child(bookId).removeValue()
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(context, "تم الحذف من المفضلة ", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(context, "لم تحذف ", Toast.LENGTH_SHORT).show();
                        }
                    });
        }

    }
}
//binding.favoriteBtn
//        .setCompoundDrawablesRelativeWithIntrinsicBounds(0,R.drawable.ic_favorite_white,0,0);
//        binding.favoriteBtn.setText("أضافة الى المفضلة");