package com.alqdees.bookapp.Filter;

import android.widget.Filter;

import com.alqdees.bookapp.AdapterPdfUser;
import com.alqdees.bookapp.ModelPdf;

import java.util.ArrayList;
import java.util.Locale;

public class FilterPdfUser extends Filter {

    ArrayList<ModelPdf> filterList;
    AdapterPdfUser adapterPdfUser;
    public  FilterPdfUser(ArrayList<ModelPdf> filterList,AdapterPdfUser adapterPdfUser){
        this.filterList = filterList;
        this.adapterPdfUser = adapterPdfUser;
    }

    @Override
    protected FilterResults performFiltering(CharSequence charSequence) {
        FilterResults filterResults = new FilterResults();
        if (charSequence!= null || charSequence.length() > 0 ){
            charSequence = charSequence.toString().toUpperCase();
            ArrayList<ModelPdf> filterModels = new ArrayList<>();
            for (int i = 0;i<filterList.size();i++){
                if (filterList.get(i).getTitle().toUpperCase().contains(charSequence)){
                    filterModels.add(filterList.get(i));
                }
            }
            filterResults.count = filterModels.size();
            filterResults.values = filterModels;
        }
        else {
            filterResults.count = filterList.size();
            filterResults.values = filterList;
        }
        return filterResults;
    }

    @Override
    protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
        adapterPdfUser.pdfArrayList = (ArrayList<ModelPdf>) filterResults.values;
        adapterPdfUser.notifyDataSetChanged();
    }
}
