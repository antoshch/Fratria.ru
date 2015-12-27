package com.example.worker.fratriaru;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class DescriptionActivity extends Activity {

    private WebView webView;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_description);

        webView = (WebView) findViewById(R.id.webView);
        // включаем поддержку JavaScript
        webView.getSettings().setJavaScriptEnabled(true);
        // указываем страницу загрузки
        Uri url = getIntent().getData();
        webView.loadUrl(url.toString());

//      Определим экземпляр MyWebViewClient
        webView.setWebViewClient(new MyWebViewClient());

//      Добавляем увеличение без кнопок через мультитач
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setDisplayZoomControls(false);

    }

    @Override
    public void onBackPressed (){

        if (webView.isFocused() && webView.canGoBack()) {
            webView.goBack();

        }else{
            super.onBackPressed();
        }

    }

    /* Учим открывать ссылки в своей программе. Для этого переопределяем класс WebViewClient
    и позволяем нашему приложению обрабатывать ссылки.*/

    private class MyWebViewClient extends WebViewClient
    {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url)
        {
            view.loadUrl(url);
            return true;
        }
    }
}