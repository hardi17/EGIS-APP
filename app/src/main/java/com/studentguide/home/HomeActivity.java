package com.studentguide.home;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.studentguide.R;
import com.studentguide.adapter.FamousAdapter;
import com.studentguide.databinding.ActivityHomeBinding;
import com.studentguide.dilog.CustDialogLogout;
import com.studentguide.user.IntroActivity;
import com.studentguide.user.LoginActivity;
import com.studentguide.user.SignupActivity;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class HomeActivity extends AppCompatActivity {

    ActivityHomeBinding binding;
    private boolean isOpen;
    private ActionBarDrawerToggle toggle;
    FamousAdapter adapter;

    //Firebase
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_home);
        ButterKnife.bind(this);
        //
        mAuth = FirebaseAuth.getInstance();

        initView();
        SetNavigationDrawerWithStyle();
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser == null){
            startActivity(new Intent(HomeActivity.this, IntroActivity.class)
                    .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK
                            | Intent.FLAG_ACTIVITY_CLEAR_TOP
                            | Intent.FLAG_ACTIVITY_NEW_TASK));
            finish();
        }
    }

    private void initView() {
        adapter = new FamousAdapter(this, true);
        binding.viewHome.rcvFamousSpots.setLayoutManager(new LinearLayoutManager(this));
        // binding.viewHome.rcvFamousSpots.setLayoutManager(new GridLayoutManager(this,2));
        binding.viewHome.rcvFamousSpots.setAdapter(adapter);
    }

    @OnClick(R.id.iv_listView)
    public void setListView() {
        binding.viewHome.rcvFamousSpots.setLayoutManager(new LinearLayoutManager(this));
        adapter = new FamousAdapter(this, true);
        binding.viewHome.rcvFamousSpots.setAdapter(adapter);
    }

    @OnClick(R.id.iv_gridView)
    public void setGridView() {
        binding.viewHome.rcvFamousSpots.setLayoutManager(new GridLayoutManager(this, 2));
        adapter = new FamousAdapter(this, false);
        binding.viewHome.rcvFamousSpots.setAdapter(adapter);
    }

    @OnClick(R.id.ll_profile)
    public void goToProfile() {
       startActivity(new Intent(this, SignupActivity.class).putExtra("isEditProfile",true));
        binding.rightMenuHome.llProfile.setEnabled(false);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                binding.rightMenuHome.llProfile.setEnabled(true);
            }
        }, 1000);
    }

    @SuppressLint("WrongConstant")
    private void SetNavigationDrawerWithStyle() {
        toggle = new ActionBarDrawerToggle(
                this,
                binding.drawelayoutHomeactivity,
                binding.viewHome.toolbarHomeActivity,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close) {
            public void onDrawerClosdrawerLayout_homeActivityed(View view) {
                supportInvalidateOptionsMenu();
            }

            public void onDrawerOpened(View drawerView) {
                supportInvalidateOptionsMenu();
            }

            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, slideOffset);

                float moveFactor = 0;
                moveFactor = (drawerView.getWidth() * slideOffset);

                binding.rlHomeActivity.setTranslationX(moveFactor);

            }
        };
        toggle.setDrawerIndicatorEnabled(false);
        binding.drawelayoutHomeactivity.addDrawerListener(toggle);
        toggle.syncState();
        binding.drawelayoutHomeactivity.setScrimColor(getResources().getColor(android.R.color.transparent));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            binding.drawelayoutHomeactivity.setElevation(0f);
        }

        binding.drawelayoutHomeactivity.setDrawerShadow(R.mipmap.ic_launcher, GravityCompat.END);
        binding.drawelayoutHomeactivity.setDrawerShadow(R.mipmap.ic_launcher, GravityCompat.START);
        binding.drawelayoutHomeactivity.setDrawerShadow(R.mipmap.ic_launcher, GravityCompat.RELATIVE_HORIZONTAL_GRAVITY_MASK);
    }


    @OnClick(R.id.img_left_Toolbar)
    public void menu() {
        binding.drawelayoutHomeactivity.openDrawer(GravityCompat.START);

        /*if (isOpen) {
            binding.rlHomeActivity.animate().translationX(0).scaleX(1).scaleY(1).setDuration(400).start();
            isOpen = false;
        } else {
            binding.rlHomeActivity.animate().translationX(689).scaleX((float) 0.812517012).scaleY((float) 0.812517012).setDuration(400).start();
            isOpen = true;
        }*/
    }

    /*private void closeMenuIfPossible(boolean isAnimate) {
        if (isOpen) {
            if (isAnimate) {
                binding.rlHomeActivity.animate().translationX(0).scaleX(1).scaleY(1).setDuration(400).start();
            } else {
                binding.rlHomeActivity.setTranslationX(0);
                binding.rlHomeActivity.setScaleX(1);
                binding.rlHomeActivity.setScaleY(1);
            }
            isOpen = false;
        }
    }*/

    @OnClick(R.id.tv_menu_home)
    public void openHomeScreen() {
        binding.drawelayoutHomeactivity.closeDrawer(GravityCompat.START);
    }

     @OnClick(R.id.iv_close)
     public void closeMenu() {
         binding.drawelayoutHomeactivity.closeDrawer(GravityCompat.START);
     }


    @OnClick(R.id.tv_menu_travel)
    public void openTravelScreen() {
        startActivity(new Intent(this, TravelActivity.class));
        binding.rightMenuHome.tvMenuTravel.setEnabled(false);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                binding.rightMenuHome.tvMenuTravel.setEnabled(true);
            }
        }, 1000);
    }

    @OnClick(R.id.tv_menu_traffic)
    public void openTrafficeScreen() {
        startActivity(new Intent(this, TrafficSignalsWasteGuideActivity.class)
                .putExtra("isTraffic", true));
        binding.rightMenuHome.tvMenuTraffic.setEnabled(false);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                binding.rightMenuHome.tvMenuTraffic.setEnabled(true);
            }
        }, 1000);
    }

    @OnClick(R.id.tv_menu_bins)
    public void openWasteGuideScreen() {
        startActivity(new Intent(this, TrafficSignalsWasteGuideActivity.class)
                .putExtra("isWaste", true));
        binding.rightMenuHome.tvMenuBins.setEnabled(false);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                binding.rightMenuHome.tvMenuBins.setEnabled(true);
            }
        }, 1000);
    }

    @OnClick(R.id.tv_menu_currency)
    public void openCurrencyGuideScreen() {
        startActivity(new Intent(this, CurrencyActivity.class));
        binding.rightMenuHome.tvMenuCurrency.setEnabled(false);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                binding.rightMenuHome.tvMenuCurrency.setEnabled(true);
            }
        }, 1000);
    }

    @OnClick(R.id.tv_menu_help)
    public void openHelpScreen() {
        startActivity(new Intent(this, HelpActivity.class));
        binding.rightMenuHome.tvMenuHelp.setEnabled(false);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                binding.rightMenuHome.tvMenuHelp.setEnabled(true);
            }
        }, 1000);
    }

    @OnClick(R.id.tv_menu_logout)
    public void logout() {
        new CustDialogLogout(HomeActivity.this, () ->{
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(HomeActivity.this, LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK
                    | Intent.FLAG_ACTIVITY_CLEAR_TOP
                    | Intent.FLAG_ACTIVITY_NEW_TASK));
        }).show();
    }
}
