package com.studentguide.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.res.ColorStateList;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;


import com.studentguide.R;
import com.studentguide.databinding.RawTravelLayoutBinding;
import com.studentguide.listener.OnRefreshTravelOptionListener;

import java.util.ArrayList;

public class TravelOptionAdapter extends RecyclerView.Adapter<TravelOptionAdapter.ViewHolder> {

    private Activity activity;
    private ArrayList<String> lsOption = new ArrayList<String>();
    private int selected_position = -1;
    OnRefreshTravelOptionListener listener;


    public TravelOptionAdapter(Activity activity, ArrayList<String> lsOption, OnRefreshTravelOptionListener listener) {
        this.activity = activity;
        this.lsOption = lsOption;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.raw_travel_layout, parent, false);
        return new ViewHolder(itemView);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.binding.tvTitle.setText(lsOption.get(position));
        holder.binding.tvTitle.setTextColor(R.color.color_012169);

        if (selected_position == position) {
            holder.binding.rlBg.setBackgroundResource(R.drawable.blue_bg_layout);
            holder.binding.tvTitle.setTextColor(R.color.white_color);
            holder.binding.ivImage.setImageTintList(ColorStateList.valueOf(R.color.white_color));
        } else {
            holder.binding.rlBg.setBackgroundResource(R.drawable.white_bg_layout);
            holder.binding.tvTitle.setTextColor(R.color.color_01194F);
            holder.binding.ivImage.setImageTintList(ColorStateList.valueOf(R.color.color_01194F));
        }

        holder.binding.tvTitle.setOnClickListener(v -> {
            if(selected_position==position){
                selected_position=-1;
                notifyDataSetChanged();
                return;
            }
            selected_position = position;
            notifyDataSetChanged();
            listener.onRefreshRoutes(lsOption.get(position));
        });
    }

    @Override
    public int getItemCount() {
        return lsOption.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        RawTravelLayoutBinding binding;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = DataBindingUtil.bind(itemView);
        }
    }
}
