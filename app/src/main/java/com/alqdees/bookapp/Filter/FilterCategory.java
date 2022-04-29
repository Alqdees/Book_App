package com.alqdees.bookapp.Filter;

import android.widget.Filter;
import com.alqdees.bookapp.AdapterCategory;
import com.alqdees.bookapp.ModelCategory;
import java.util.ArrayList;

public class FilterCategory extends Filter {
    ArrayList<ModelCategory> filterList;
    AdapterCategory adapterCategory;
    public FilterCategory(ArrayList<ModelCategory> filterList,AdapterCategory adapterCategory){
        this.filterList=filterList;
        this.adapterCategory = adapterCategory;
    }


    @Override
    protected FilterResults performFiltering(CharSequence charSequence) {
        FilterResults results = new FilterResults();
        if (charSequence != null && charSequence.length() > 0){
            charSequence = charSequence.toString().toUpperCase();
            ArrayList<ModelCategory> filteredModel = new ArrayList<>();
            for (int i = 0;i<filterList.size();i++){
                if (filterList.get(i).getCategory().toUpperCase().contains(charSequence)){
                    filteredModel.add(filterList.get(i));

                }
            }
            results.count = filteredModel.size();
            results.values = filteredModel;
        }
        else {
            results.count = filterList.size();
            results.values = filterList;
        }
        return results;
    }

    @Override
    protected void publishResults(CharSequence charSequence, FilterResults filterResults)  {
        adapterCategory.categoryArrayList = (ArrayList<ModelCategory>)filterResults.values;
        adapterCategory.notifyDataSetChanged();
    }
}
