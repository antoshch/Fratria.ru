package com.example.worker.fratriaru;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.safety.Whitelist;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;

public class DescriptionActivity extends Activity {

    ProgressDialog mProgressDialog;
    private WebView mWebView;

    private static final int SUCCESS = 1;
    private static final int NETWORK_ERROR = 2;
    private static final int ERROR = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_description);

        mWebView = (WebView) findViewById(R.id.webView);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.setWebChromeClient(new WebChromeClient());
        WebSettings settings = mWebView.getSettings();
        settings.setDefaultTextEncodingName("utf-8");

        new getHtml().execute();
    }

    private class getHtml extends AsyncTask<String, Void, Integer> {
        Elements tfa;
        Elements title;
        Elements comments;

        @Override
        protected void onPreExecute() {
            mProgressDialog = new ProgressDialog(DescriptionActivity.this);
//            mProgressDialog.setTitle("Fratria");
            mProgressDialog.setMessage("Загрузка...");
            mProgressDialog.setIndeterminate(false);
            mProgressDialog.show();
        }

        @Override
        protected Integer doInBackground(String... params) {
            try {

                Uri urls = getIntent().getData();
                String url = urls.toString();

                Document doc = Jsoup.connect(url)
                        .userAgent("Mozilla")
                        .cookie("auth", "token")
                        .timeout(10000)
                        .get();

                title = doc.select(".c1-post").select("span");
                tfa = doc.select(".c1-post-data");
                comments = doc.select(".content").select(".comments");


                return SUCCESS;
            } catch (UnknownHostException e) {
                Log.e("Unknown Host Exception", "Network error", e);
                return NETWORK_ERROR;
            } catch (IOException e) {
                Log.e("IO Exception", "Failed to load HTML", e);
                return ERROR;
            } catch (Exception e) {
                Log.e("Exception", "An exception occured", e);
                return ERROR;
            }

        }

        @Override
        protected void onPostExecute(Integer result) {

            if (result == 2) {
                Toast.makeText(
                        getApplicationContext(),
                        "Network connection error. Check your internet connection and try again.",
                        Toast.LENGTH_LONG).show();
            } else if (result == 3) {
                Toast.makeText(getApplicationContext(),
                        "Unknown error. Failed to load.",
                        Toast.LENGTH_LONG).show();
            } else if (result == 1) {

                String comqqq = Jsoup.clean(comments.toString(), Whitelist.basic());

                mWebView.loadDataWithBaseURL("http://fratria.ru/",
                        "<style>img{display: inline;height: auto; width: 100%;}" +
                                "iframe {display: block; max-width:100%; margin-top:10px; margin-bottom:10px;}" +
                                "</style>"
                                + "<h2>" + title.html() + "</h2>" + tfa.html() , "text/html", "en_US", null);
            }

            mProgressDialog.dismiss();
        }
    }

    @Override
    public void onBackPressed() {
        mWebView.stopLoading();
//        mWebView.removeAllViews();
        mWebView.destroy();
//        mWebView = null;
//        finish();
        super.onBackPressed();
    }
}