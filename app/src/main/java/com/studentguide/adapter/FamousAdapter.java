package com.studentguide.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.studentguide.ParentObj;
import com.studentguide.R;
import com.studentguide.databinding.RawFamouspotLayoutGridBinding;
import com.studentguide.databinding.RowFamouspotLayoutBinding;

public class FamousAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    Context context;
    boolean isList ;

    public FamousAdapter(Context context,boolean isList) {
        this.context = context;
        this.isList = isList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(isList){
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_famouspot_layout, parent, false);
            return new ViewHolder(itemView);
        }else {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.raw_famouspot_layout_grid, parent, false);
            return new ViewGridHolder(itemView);
        }

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

    }


    @Override
    public int getItemCount() {
        return 4;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        RowFamouspotLayoutBinding binding;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = DataBindingUtil.bind(itemView);
        }
    }

    public class ViewGridHolder extends RecyclerView.ViewHolder {
        RawFamouspotLayoutGridBinding gridBinding;
        public ViewGridHolder(View itemView) {
            super(itemView);
            gridBinding = DataBindingUtil.bind(itemView);
        }
    }
}
