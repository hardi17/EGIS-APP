package com.studentguide.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.studentguide.R;
import com.studentguide.databinding.RawOptionLayoutBinding;
import com.studentguide.models.ModelOptions;

import java.util.List;

public class OptionsAdapter extends RecyclerView.Adapter<OptionsAdapter.ViewHolder> {

    Context context;
    List<ModelOptions> MOL;

    public OptionsAdapter(Context context, List<ModelOptions> MOL) {
        this.context = context;
        this.MOL = MOL;
    }

    @NonNull
    @Override
    public OptionsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.raw_option_layout, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull OptionsAdapter.ViewHolder holder, int position) {
            ModelOptions model = MOL.get(position);
            holder.binding.tvOptions.setText(model.getOption());
    }

    @Override
    public int getItemCount() {
        return MOL.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        RawOptionLayoutBinding binding;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = DataBindingUtil.bind(itemView);
        }
    }
}