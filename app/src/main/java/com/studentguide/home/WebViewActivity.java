package com.studentguide.home;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.database.DatabaseUtils;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.studentguide.R;
import com.studentguide.databinding.ActivityWebViewBinding;

import butterknife.OnClick;

public class WebViewActivity extends AppCompatActivity {

    ActivityWebViewBinding binding;

    private String openFrom= "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding  = DataBindingUtil.setContentView(this,R.layout.activity_web_view);

        initView();
    }

    private void initView() {
        if(getIntent().getExtras() != null) {
            openFrom = getIntent().getStringExtra("openFrom");

            switch (openFrom) {
                case "About":
                    binding.toolbar.txtTitle.setText(R.string.aboutus);
                    sendUrl(getString(R.string.aboutus));
                    break;
                case "Terms":
                    binding.toolbar.txtTitle.setText(R.string.terms_condition);
                    sendUrl(getString(R.string.terms_condition));
                    break;
                case "Privacy":
                    binding.toolbar.txtTitle.setText(R.string.privacy_policy);
                    sendUrl(getString(R.string.privacy_policy));
                    break;
            }
        }
    }

    private void sendUrl(String str) {
      /*  if (AppClass.networkConnectivity.isNetworkAvailable()) {
            binding.wvWebViewActivity.setWebViewClient(new myWebClient());
            String url = null;
            if (str.equalsIgnoreCase(getString(R.string.aboutus))) {
                url = WebAPI.ABOUT_APP;
            } else if (str.equalsIgnoreCase(getString(R.string.terms_condition))) {
                url = WebAPI.TERMS_CONDITION;
            } else {
                url = WebAPI.PRIVACY_POLICY;
            }
            binding.wvWebViewActivity.loadUrl(url);
            binding.wvWebViewActivity.getSettings().setSupportZoom(true);
        } else {
            showSnackbar(getResources().getString(R.string.nonetwork));
        }*/
    }

    @OnClick(R.id.iv_back)
    public void goToBack(){
        onBackPressed();
    }

    private class myWebClient extends WebViewClient {
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            binding.progressbarWebview.setVisibility(View.GONE);
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            binding.progressbarWebview.setVisibility(View.VISIBLE);
            view.loadUrl(url);
            return true;
        }
    }
}
