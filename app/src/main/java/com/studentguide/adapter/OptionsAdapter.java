package com.studentguide.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.studentguide.R;
import com.studentguide.databinding.RawOptionLayoutBinding;
import com.studentguide.fragment.QueAnsFragment;
import com.studentguide.home.QuestionAnsActivity;
import com.studentguide.models.ModelOptions;
import com.studentguide.utils.Logger;

import java.util.List;

public class OptionsAdapter extends RecyclerView.Adapter<OptionsAdapter.ViewHolder> {

    Context context;
    List<ModelOptions> MOL;
    QuestionAnsActivity activity;

    //
//    private int count=0;


    public OptionsAdapter(Context context, List<ModelOptions> MOL, QuestionAnsActivity activity) {
        this.context = context;
        this.MOL = MOL;
        this.activity = activity;
    }

    @NonNull
    @Override
    public OptionsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.raw_option_layout, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull OptionsAdapter.ViewHolder holder, int position) {
        SharedPreferences ANS = activity.getSharedPreferences("QuizAns",Context.MODE_PRIVATE);
        String answer = ANS.getString("answer","OP");


            ModelOptions model = MOL.get(position);
            holder.binding.tvOptions.setText(model.getOption());
            holder.binding.opCard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String optionText = holder.binding.tvOptions.getText().toString();
                    //
                    if(answer.equals(optionText)){
                        activity.score++;
                        SharedPreferences ANS = PreferenceManager.getDefaultSharedPreferences(context);
                        SharedPreferences.Editor editor = ANS.edit();
                        editor.putInt("score",activity.score);
                        editor.apply();
                        Logger.d(String.valueOf(activity.score));
                    }
                    else{
                        if(activity.score>0){
                            activity.score--;
                        }
                    }
                    Logger.d(String.valueOf(activity.score));
                }
            });
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