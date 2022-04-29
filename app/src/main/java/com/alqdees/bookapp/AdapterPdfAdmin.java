package com.alqdees.bookapp;

import static com.alqdees.bookapp.PdfDetailActivity.formatTimeStamp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.alqdees.bookapp.Filter.FilterPdfAdmin;
import com.alqdees.bookapp.databinding.RowPdfAdminBinding;
import com.github.barteksc.pdfviewer.PDFView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class AdapterPdfAdmin extends RecyclerView.Adapter<AdapterPdfAdmin.HolderPdfAdmin> implements Filterable {
    private Context context;
    public ArrayList<ModelPdf> pdfArrayList,filterList;
    private RowPdfAdminBinding binding;
    private FilterPdfAdmin filter;
    private ProgressDialog progressDialog;

    public AdapterPdfAdmin(Context context, ArrayList<ModelPdf> pdfArrayList) {
        this.context = context;
        this.pdfArrayList = pdfArrayList;
        this.filterList = pdfArrayList;
        progressDialog = new ProgressDialog(context);
        progressDialog.setTitle("أنتظر ...");
        progressDialog.setCanceledOnTouchOutside(false);
    }

    @NonNull
    @Override
    public HolderPdfAdmin onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        binding = RowPdfAdminBinding.inflate(LayoutInflater.from(context),parent,false);
        return new HolderPdfAdmin(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull HolderPdfAdmin holder, int position) {
        ModelPdf modelPdf = pdfArrayList.get(position);
        String bookId = modelPdf.getId();
        String categoryId = modelPdf.getCategoryId();
        String title = modelPdf.getTitle();
        String description = modelPdf.getDescription();
        String pdfUrl = modelPdf.getUrl();
        String timestamp = modelPdf.getTimestamp();
        /*-----اعطاء الوقت الدقيق-----*/

        loadPdfSize(modelPdf,holder);
        String format = MyApplication.formatTimestamp(Long.parseLong(timestamp));
        holder.dateTv.setText(format);

        holder.titleTv.setText(title);
        holder.descriptionTv.setText(description);
//        holder.dateTv.setText(formatTimeStamp(Long.parseLong(timestamp)));
        MyApplication.loadCategory(categoryId, holder.categoryTv);
        MyApplication.loadPdfFromUrlSinglePage(pdfUrl,holder.pdfViewers, holder.progressBar,null);
//        MyApplication.loadPdfSize(pdfUrl,binding.sizeTv);
        holder.moreBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                moreOptionDialog(modelPdf,holder);
            }
        });
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context,PdfDetailActivity.class);
                intent.putExtra("bookId",bookId);
                context.startActivity(intent);
            }
        });

    }

    private void moreOptionDialog(ModelPdf modelPdf, HolderPdfAdmin holder) {
        String bookId = modelPdf.getId();
        String bookUrl = modelPdf.getUrl();
        String bookTitle = modelPdf.getTitle();
        String[] options = {"تعديل","حذف"};
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("أختر...").setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (i == 0){
                    Intent intent = new Intent(context,PdfEditActivity.class);
                    intent.putExtra("bookId",bookId);
                    context.startActivity(intent);

                }else if (i == 1){

                    MyApplication.deleteBook(context,
                            ""+bookId,
                            ""+bookUrl
                    ,""+bookTitle);

                }
            }
        }).show();
    }

    @Override
    public int getItemCount() {
        return pdfArrayList.size();
    }

    @Override
    public Filter getFilter() {
        if (filter == null){
            filter = new FilterPdfAdmin(filterList,this);
        }
        return filter;
    }

    class HolderPdfAdmin extends RecyclerView.ViewHolder{
        PDFView pdfViewers;
        ProgressBar progressBar;
        TextView titleTv,descriptionTv,categoryTv,sizeTv,dateTv;
        ImageButton moreBtn;


        public HolderPdfAdmin(@NonNull View itemView) {
            super(itemView);
            pdfViewers= binding.pdfViewer;
            progressBar = binding.progressBsr;
            titleTv = binding.titleTv;
            descriptionTv = binding.descriptionTv;
            categoryTv = binding.categoryTv;
            sizeTv = binding.sizeTv;
            dateTv = binding.dateTv;
            moreBtn = binding.moreBtn;
        }
    }

    public static void loadPdfSize(ModelPdf modelPdf, HolderPdfAdmin holder) {
        String pdfUrl = modelPdf.getUrl();
//        StorageReference ref = FirebaseStorage.getInstance().getReferenceFromUrl(pdfUrl);
        StorageReference ref = FirebaseStorage.getInstance().getReferenceFromUrl(pdfUrl);
        ref.getMetadata().addOnSuccessListener(new OnSuccessListener<StorageMetadata>() {
            @Override
            public void onSuccess(StorageMetadata storageMetadata) {
                double bytes = storageMetadata.getSizeBytes();
                /*تحويل بين الوحدات */
                double kb = bytes/1024;
                double mb = kb/1024;
                if (mb >= 1){
                    holder.sizeTv.setText(String.format("%.2f",mb)+"MB");
                }
                else if (kb >= 1){
                    holder.sizeTv.setText(String.format("%.2f",kb)+"KB");
                }
                else {
                    holder.sizeTv.setText(String.format("%.2f",bytes)+"bytes");
                }
            }
        }).addOnFailureListener(new OnFailureListener() {

            @Override
            public void onFailure(@NonNull Exception e) {
//                Toast.makeText(context, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
