package com.alqdees.bookapp.Filter;

import android.widget.Filter;
import com.alqdees.bookapp.AdapterPdfAdmin;
import com.alqdees.bookapp.ModelPdf;
import java.util.ArrayList;

public class FilterPdfAdmin extends Filter {
    ArrayList<ModelPdf> filterList;
    AdapterPdfAdmin adapterPdfAdmin;
    public FilterPdfAdmin(ArrayList<ModelPdf> filterList, AdapterPdfAdmin adapterPdfAdmin){
        this.filterList=filterList;
        this.adapterPdfAdmin = adapterPdfAdmin;
    }


    @Override
    protected FilterResults performFiltering(CharSequence charSequence) {
        FilterResults result = new FilterResults();
        if (charSequence != null && charSequence.length() > 0){
            charSequence = charSequence.toString().toUpperCase();
            ArrayList<ModelPdf> filteredModel = new ArrayList<>();
            for (int i = 0;i<filterList.size();i++){
                if (filterList.get(i).getTitle().toUpperCase().contains(charSequence)){
                    filteredModel.add(filterList.get(i));

                }
            }
            result.count = filteredModel.size();
            result.values = filteredModel;
        }
        else
            result.count = filterList.size();
            result.values = filterList;

        return result;
    }

    @Override
    protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
        adapterPdfAdmin.pdfArrayList = (ArrayList<ModelPdf>)filterResults.values;
        adapterPdfAdmin.notifyDataSetChanged();
    }
}
