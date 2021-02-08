package com.studentguide.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.studentguide.R;
import com.studentguide.databinding.RowGuideLayoutBinding;
import com.studentguide.models.ModelTrafficSignal;
import com.studentguide.models.ModelWasteManagement;

import java.util.List;


public class TravelSignalWasteGuideAdapter extends RecyclerView.Adapter<TravelSignalWasteGuideAdapter.ViewHolder> {

    private Context context;
    private boolean isTraffic = false,
            isWaste = false,
            isNote = false,
            isCoin = false;

    List<ModelTrafficSignal> MTS;
    List<ModelWasteManagement> MWM;


    public TravelSignalWasteGuideAdapter(Context context, boolean isTraffic, boolean isWaste, boolean isNote, boolean isCoin, List<ModelTrafficSignal> MTS, List<ModelWasteManagement> MWM) {
        this.context = context;
        this.isTraffic = isTraffic;
        this.isWaste = isWaste;
        this.isNote = isNote;
        this.isCoin = isCoin;
        this.MTS = MTS;
        this.MWM = MWM;
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
            ModelTrafficSignal model = MTS.get(position);
            holder.binding.tvTitle.setText(MTS.get(position).getSignalTitle());
            holder.binding.tvDetail.setText(MTS.get(position).getSignalDesc());
            Glide.with(context).load(MTS.get(position).getSignalImage()).into(holder.binding.ivGuideImage);
        } else if(isWaste){
            holder.binding.tvTitle.setText(MWM.get(position).getBinName());
            holder.binding.tvDetail.setText(MWM.get(position).getBinDesc());
            Glide.with(context).load(MWM.get(position).getBinImage()).into(holder.binding.ivGuideImage);
        }else if(isCoin){
            holder.binding.tvTitle.setText("1 Pound");
        } else {
            holder.binding.tvTitle.setText("10 Pound");
        }
    }

    @Override
    public int getItemCount() {
        if(isTraffic){
            return MTS.size();
        }
        else{
            return MWM.size();
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        RowGuideLayoutBinding binding;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = DataBindingUtil.bind(itemView);
        }
    }
}
