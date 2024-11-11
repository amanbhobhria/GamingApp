package com.example.k4mediaassingment;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.webkit.WebSettings;
import android.webkit.WebViewClient;

import com.example.k4mediaassingment.databinding.ActivityMainBinding;
import com.example.k4mediaassingment.databinding.ActivityWebViewBinding;

public class WebViewActivity extends AppCompatActivity {

    private ActivityWebViewBinding binding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityWebViewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getSupportActionBar().hide();

        WebSettings webSettings = binding.webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        binding.webView.getSettings().setJavaScriptEnabled(true);
        binding.webView.getSettings().setAllowFileAccess(true); // Allows access to files
        binding.webView.getSettings().setAllowFileAccessFromFileURLs(true); // Allows access from file URLs
        binding.webView.getSettings().setAllowUniversalAccessFromFileURLs(true); // Allows all content from file URLs
        binding.webView.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE);
        binding.webView.setWebViewClient(new WebViewClient());



        String gameUrl = getIntent().getStringExtra("gameUrl");

        loadGame(gameUrl);
        Log.d("MainActivity11", "Game URL: " + gameUrl);
        }





    private void loadGame(String url) {
        binding.webView.loadUrl(url);
    }


}