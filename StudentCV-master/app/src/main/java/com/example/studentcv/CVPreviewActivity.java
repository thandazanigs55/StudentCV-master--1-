package com.example.studentcv;

import android.os.Bundle;
import android.webkit.WebView;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ShareCompat;

public class CVPreviewActivity extends AppCompatActivity {

    private WebView webView;
    private String cvData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cv_preview);

        webView = findViewById(R.id.webView);
        Button shareButton = findViewById(R.id.shareButton);

        cvData = getIntent().getStringExtra("cvData");

        // Load the CV data into WebView
        webView.loadDataWithBaseURL(null, cvData, "text/html", "UTF-8", null);

        // Setup share functionality
        shareButton.setOnClickListener(v -> shareCV());
    }

    private void shareCV() {
        ShareCompat.IntentBuilder.from(this)
                .setType("text/html")
                .setText(cvData)
                .setSubject("My CV")
                .startChooser();
    }
}