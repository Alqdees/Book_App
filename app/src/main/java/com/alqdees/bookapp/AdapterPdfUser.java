package com.alqdees.bookapp;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.alqdees.bookapp.Filter.FilterPdfUser;
import com.alqdees.bookapp.databinding.RowPdfUserBinding;
import com.github.barteksc.pdfviewer.PDFView;

import java.util.ArrayList;

public class AdapterPdfUser extends RecyclerView.Adapter<AdapterPdfUser.HolderPdfUser> implements Filterable {
    private Context context;
    public ArrayList<ModelPdf> pdfArrayList,filterList;
    private FilterPdfUser filter;
    private RowPdfUserBinding binding;
    public AdapterPdfUser (Context context,ArrayList<ModelPdf> pdfArrayList){
        this.context = context;
        this.pdfArrayList = pdfArrayList;
        this.filterList = pdfArrayList;

    }

    @NonNull
    @Override
    public HolderPdfUser onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        binding = RowPdfUserBinding.inflate(LayoutInflater.from(context),parent,false);
        return new HolderPdfUser(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull HolderPdfUser holder, int position) {
        ModelPdf model = pdfArrayList.get(position);
        String title = model.getTitle();
        String uid = model.getUid();
        String pdfId = model.getId();
        String description = model.getDescription();
        String pdfUrl = model.getUrl();
        String categoryId = model.getCategoryId();
        String timestamp = model.getTimestamp();

        String date = MyApplication.formatTimestamp(Long.parseLong(timestamp));
//        holder.pdfView;
        holder.dateTv.setText(date);
        holder.titleTv.setText(title);
        holder.description.setText(description);
        MyApplication.loadPdfFromUrlSinglePage(""+pdfUrl,holder.pdfView,holder.progressBar,null);
        MyApplication.loadCategory(""+categoryId,holder.categoryTv);
        MyApplication.loadPdfSize(""+pdfUrl,binding.sizeTv);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context,PdfDetailActivity.class);
                intent.putExtra("bookId",pdfId);
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return pdfArrayList.size();
    }

    @Override
    public Filter getFilter() {
        if (filter == null){
            filter = new FilterPdfUser(filterList,this);
        }
        return filter;
    }


    class HolderPdfUser extends RecyclerView.ViewHolder{
        TextView titleTv,description,categoryTv,dateTv,sizeTv;
        PDFView pdfView;
        ProgressBar progressBar;
        public HolderPdfUser(@NonNull View itemView) {
            super(itemView);
            titleTv = binding.titleTv;
            description = binding.descriptionTv;
            categoryTv = binding.categoryTv;
            dateTv = binding.dateTv;
            sizeTv = binding.sizeTv;
            pdfView = binding.pdfViewer;
            progressBar = binding.progressBsr;

        }
    }
}
