package com.studentguide.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.studentguide.R;
import com.studentguide.databinding.RowGuideLayoutBinding;


public class TravelSignalWasteGuideAdapter extends RecyclerView.Adapter<TravelSignalWasteGuideAdapter.ViewHolder> {

    private Context context;
    private boolean isTraffic = false,
            isWaste = false,
            isNote = false,
            isCoin = false;


    public TravelSignalWasteGuideAdapter(Context context, boolean isTraffic, boolean isWaste,boolean isCoin,boolean isNote) {
        this.context = context;
        this.isTraffic = isTraffic;
        this.isWaste = isWaste;
        this.isCoin = isCoin;
        this.isNote = isNote;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_guide_layout, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        if (isTraffic) {
            holder.binding.tvTitle.setText("Road Sign");
        } else if(isWaste){
            holder.binding.tvTitle.setText("Medical Waste");
        }else if(isCoin){
            holder.binding.tvTitle.setText("1 Pound");
        } else {
            holder.binding.tvTitle.setText("10 Pound");
        }
    }

    @Override
    public int getItemCount() {
        return 5;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        RowGuideLayoutBinding binding;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = DataBindingUtil.bind(itemView);
        }
    }
}
