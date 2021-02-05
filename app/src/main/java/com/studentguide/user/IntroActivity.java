package com.studentguide.user;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.studentguide.R;
import com.studentguide.databinding.ActivityIntroBinding;
import com.studentguide.databinding.RowStartupViewpagerBinding;
import com.studentguide.home.HomeActivity;

import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class IntroActivity extends AppCompatActivity {

    ActivityIntroBinding binding;

    private ImageSlideAdapter
            imageSlideAdapter;

    private ImageView[] dots;

    private int dotsCount = 0;

    Unbinder unbinder;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAuth = FirebaseAuth.getInstance();


        binding = DataBindingUtil.setContentView(this, R.layout.activity_intro);
        unbinder = ButterKnife.bind(this);

        setupViewPager();
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            startActivity(new Intent(IntroActivity.this, HomeActivity.class)
                    .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK
                            | Intent.FLAG_ACTIVITY_CLEAR_TOP
                            | Intent.FLAG_ACTIVITY_NEW_TASK));
            finish();
        }
    }

    private void setupViewPager() {
        setViewPagerIndicator(3);
        imageSlideAdapter = new ImageSlideAdapter(this);
        binding.viewPager.setAdapter(imageSlideAdapter);
    }

    private void setViewPagerIndicator(int size) {
        binding.llVpIndicator.removeAllViews();
        dotsCount = size;
        dots = new ImageView[size];

        for (int i = 0; i < size; i++) {
            dots[i] = new ImageView(this);
            dots[i].setImageDrawable(getResources().getDrawable(R.drawable.gray_round));

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );

            params.setMargins(4, 0, 4, 0);

            binding.llVpIndicator.addView(dots[i], params);
        }

        dots[0].setImageDrawable(getResources().getDrawable(R.drawable.blue_round));


        binding.viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int position) {
                if (dotsCount > 1) {
                    for (int j = 0; j < dotsCount; j++) {
                        dots[j].setImageDrawable(getResources().getDrawable(R.drawable.gray_round));
                    }
                    dots[position].setImageDrawable(getResources().getDrawable(R.drawable.blue_round));
                }

                if (position == 2) {
                    binding.txtSkip.setVisibility(View.GONE);
                    binding.btnLoginSignup.setVisibility(View.VISIBLE);
                    binding.llVpIndicator.setVisibility(View.GONE);
                } else {
                    binding.llVpIndicator.setVisibility(View.VISIBLE);
                    binding.txtSkip.setVisibility(View.VISIBLE);
                    binding.btnLoginSignup.setVisibility(View.GONE);
                }
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
    }

    public class ImageSlideAdapter extends PagerAdapter {

        private Context context;
        private LayoutInflater inflater;

        public ImageSlideAdapter(Context context) {
            this.context = context;
            inflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return dotsCount;
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;

        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public Object instantiateItem(ViewGroup view, final int position) {

            RowStartupViewpagerBinding rowStartupViewpagerBinding;
            rowStartupViewpagerBinding = DataBindingUtil.inflate(inflater, R.layout.row_startup_viewpager, view, false);
            if (position == 0) {
                rowStartupViewpagerBinding.txtRowstartupViewpagerMaintext.setText("11111 Lorem Ipsum Lorem Ipsum Lorem Ipsum");
            } else if (position == 1) {
                rowStartupViewpagerBinding.txtRowstartupViewpagerMaintext.setText("2222 Lorem Ipsum Lorem Ipsum Lorem Ipsum");
            } else if (position == 2) {
                rowStartupViewpagerBinding.txtRowstartupViewpagerMaintext.setText("3333 Lorem Ipsum Lorem Ipsum Lorem Ipsum");
            }
            view.addView(rowStartupViewpagerBinding.getRoot());
            return rowStartupViewpagerBinding.getRoot();

        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view.equals(object);
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (unbinder != null)
            unbinder.unbind();
    }

    @OnClick(R.id.tv_login)
    public void redirectLoginFromSignIn() {
        startActivity(new Intent(this, LoginActivity.class));
        finish();
        binding.tvLogin.setEnabled(false);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                binding.tvLogin.setEnabled(true);
            }
        }, 1000);
    }

    @OnClick(R.id.txt_skip)
    public void redirectToLoginSs() {
        startActivity(new Intent(this, LoginActivity.class));
        finish();
        binding.txtSkip.setEnabled(false);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                binding.txtSkip.setEnabled(true);
            }
        }, 1000);
    }

    @OnClick(R.id.tv_signup)
    public void redirectSignup() {
        startActivity(new Intent(this, SignupActivity.class));
        finish();
    }

}
