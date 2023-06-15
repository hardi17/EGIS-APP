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

import com.studentguide.R;
import com.studentguide.databinding.RawOptionLayoutBinding;
import com.studentguide.home.QuestionAnsActivity;
import com.studentguide.models.ModelOptions;
import com.studentguide.utils.Logger;
import com.studentguide.utils.MyPref;

import java.util.List;

public class OptionsAdapter extends RecyclerView.Adapter<OptionsAdapter.ViewHolder> {

    Context context;
    List<ModelOptions> MOL;
    String ans;
    QuestionAnsActivity activity;
    int _currentScore;
    private int selected_position = -1;

    public OptionsAdapter(Context context, List<ModelOptions> MOL, QuestionAnsActivity activity, String ans) {
        this.context = context;
        this.MOL = MOL;
        this.activity = activity;
        this.ans = ans;
    }

    @NonNull
    @Override
    public OptionsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.raw_option_layout, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull OptionsAdapter.ViewHolder holder, int position) {
        if (position == 0) {
            holder.binding.tvOptionID.setText("A");
        } else if (position == 1) {
            holder.binding.tvOptionID.setText("B");
        } else if (position == 2) {
            holder.binding.tvOptionID.setText("C");
        } else {
            holder.binding.tvOptionID.setText("D");
        }
    /*SharedPreferences ANS = activity.getSharedPreferences("QuizAns", Context.MODE_PRIVATE);
    String answer = ANS.getString("answer", "OP");
    int queNo = ANS.getInt("queNo", 1);*/
        _currentScore = activity.score;
        String answer = ans;
        if (selected_position == position) {
            holder.binding.llBg.setBackgroundResource(R.drawable.graybg);
        } else {
            holder.binding.llBg.setBackgroundResource(R.drawable.white_bg);
        }
        ModelOptions model = MOL.get(position);
        holder.binding.tvOptions.setText(model.getOption());
        holder.binding.opCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (selected_position == position) {
                    selected_position = -1;
                    notifyDataSetChanged();
                    return;
                }
                selected_position = position;
                notifyDataSetChanged();
                String optionText = holder.binding.tvOptions.getText().toString();
                if (answer.equals(optionText)) {
                    activity.score++;
                } else {
                    activity.score = _currentScore;
                }
                Logger.d(String.valueOf(_currentScore));
                Logger.d(String.valueOf(activity.score));
                SharedPreferences ANS = PreferenceManager.getDefaultSharedPreferences(context);
                SharedPreferences.Editor editor = ANS.edit();
                editor.putInt("score", activity.score);
                editor.apply();
                new MyPref(activity).setData(MyPref.Keys.Score, activity.score);
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