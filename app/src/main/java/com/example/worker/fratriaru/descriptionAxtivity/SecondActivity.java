package com.example.worker.fratriaru.descriptionAxtivity;

import android.app.Activity;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ListView;

import com.example.worker.fratriaru.R;
import com.example.worker.fratriaru.mainActivity.HtmlToPlainText;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.safety.Whitelist;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;

public class SecondActivity extends Activity {

    public Elements storyTitle;
    public Elements storyText;
    public Elements content;

    ArrayList<Item> data = new ArrayList<Item>();

    private ListView lv;
    private MyAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.second_activity);

        lv = (ListView) this.findViewById(R.id.listView);

        new NewThread().execute();

        adapter = new MyAdapter(this, data);
    }

    public class NewThread extends AsyncTask<String, Void, String> {

        @Override
        protected  String doInBackground(String... arg) {
            data.clear();
            for (int i = 1; i <= 2; i++) {
                final String url = "http://fratria.ru/cgi-bin/MainNews/index.cgi?line_id=0&page=" + i;

                Document doc;

                try {
                    doc = Jsoup.connect(url).get();
                    content = doc.select(".c1-post");


                } catch (IOException e) {
                    e.printStackTrace();
                }

                for (org.jsoup.nodes.Element contents : content) {
                    Item item = new Item();

                    storyTitle = contents.select("h2");
                    storyText = contents.select(".c1-post-data");

                    item.setHeader(storyTitle.text());
                    item.setDescription(storyText.text());
                    data.add(item);
                }
            }
            return  null;
        }

        @Override
        protected void onPostExecute(String result) {
            lv.setAdapter(adapter);
        }
    }
}