package com.studentguide.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.studentguide.R;
import com.studentguide.databinding.RawRoutesBinding;
import com.studentguide.home.MapActivity;

import java.util.List;

public class RoutesAdapter extends RecyclerView.Adapter<RoutesAdapter.ViewHolder> {

    Context context;
    String travelOption;

    public RoutesAdapter(Context context, String travelOption){
        this.context = context;
        this.travelOption = travelOption;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.raw_routes, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

       /* if(travelOption.equals("Walking")){
            holder.binding.tvRouteName.setText(travelOption);
        }*/

        holder.binding.tvRouteName.setText(travelOption);
        holder.binding.llRouteDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(new Intent(context, MapActivity.class).putExtra("isPath",true));
            }
        });

    }


    public void setItems(String travelOption) {
        this.travelOption = travelOption;
    }

    @Override
    public int getItemCount() {
        return 3;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        RawRoutesBinding binding;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = DataBindingUtil.bind(itemView);
        }
    }
}
