package com.studentguide.home;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import android.os.Bundle;
import android.view.View;

import com.studentguide.R;
import com.studentguide.databinding.ActivityCurrencyBinding;
import com.studentguide.fragment.CoinsFragment;
import com.studentguide.fragment.NotesFragment;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class CurrencyActivity extends AppCompatActivity {

    ActivityCurrencyBinding binding;

    private MyActivityAdapter activityAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_currency);
        ButterKnife.bind(this);

        initView();

        setUpViewPager();
    }


    private void initView() {
        binding.toolbar.txtTitle.setText(R.string._currency);
    }

    private void setUpViewPager() {
        activityAdapter = new MyActivityAdapter(getSupportFragmentManager());
        activityAdapter.addFragment(new CoinsFragment());
        activityAdapter.addFragment(new NotesFragment());

        binding.customVP.setAdapter(activityAdapter);
        binding.customVP.setPagingEnabled(false);
    }

    @OnClick(R.id.iv_back)
    public void goToBack() {
        onBackPressed();
    }

    @OnClick(R.id.ll_coin)
    public void selectCoins() {
        binding.tvCoin.setTextColor(getResources().getColor(R.color.color_012169));
        binding.tvNotes.setTextColor(getResources().getColor(R.color.color_939597));
        binding.viewCoin.setVisibility(View.VISIBLE);
        binding.viewNote.setVisibility(View.INVISIBLE);
        binding.customVP.setCurrentItem(0);
    }

    @OnClick(R.id.ll_notes)
    public void selectNotes() {
        binding.tvCoin.setTextColor(getResources().getColor(R.color.color_939597));
        binding.tvNotes.setTextColor(getResources().getColor(R.color.color_012169));
        binding.viewCoin.setVisibility(View.INVISIBLE);
        binding.viewNote.setVisibility(View.VISIBLE);
        binding.customVP.setCurrentItem(1);
    }

    //adapter for viewpager
    class MyActivityAdapter extends FragmentPagerAdapter {

        private final List<Fragment> mFragmentList = new ArrayList<>();

        public MyActivityAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment) {
            mFragmentList.add(fragment);
        }
    }

}
