package com.example.worker.fratriaru;

import android.app.Activity;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ListView;

import com.example.worker.fratriaru.jsoup.HtmlToPlainText;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.safety.Whitelist;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;

public class SecondActivity extends Activity {

    // благодоря этому классу мы будем разбирать данные на куски
    public String post;
    public Elements post2;
    public Elements storyTitle;
    public Elements storyText;
    public Elements comments;

    private static final String userAgent = "Mozilla/5.0 (jsoup)";
    private static final int timeout = 5 * 1000;

    // то в чем будем хранить данные пока не передадим адаптеру

    ArrayList<Item> data = new ArrayList<Item>();

    // List view
    private ListView lv;
    private MyAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.second_activity);

        // определение данных
        lv = (ListView) this.findViewById(R.id.listView);

        // запрос к нашему отдельному поток на выборку данных
        new NewThread().execute();

        // Добавляем данные для ListView
        adapter = new MyAdapter(this, data);

    }

    // внутрений класс который делает запросы
    public class NewThread extends AsyncTask<String, Void, String> {

        // Метод выполняющий запрос в фоне, в версиях выше 4 андроида, запросы в главном потоке выполнять
        // нельзя, поэтому все что вам нужно выполнять - выносите в отдельный тред
        @Override
        protected  String doInBackground(String... arg) {
            Uri urls = getIntent().getData();
            String url = urls.toString();

            HtmlToPlainText formatter = new HtmlToPlainText();
            // класс который захватывает страницу
            Document doc;

            try {
                // определяем откуда будем воровать данные
                doc = Jsoup.connect(url).userAgent(userAgent).timeout(timeout).get();

                // задаем с какого места, я выбрал заголовке статей

                storyTitle = doc.select("title");
                storyText = doc.select(".content").select(".c1-post-data");
                comments = doc.select(".content").select(".comments");

//                storyTitle = doc.select("title");
//                storyText = doc.select(".c1-post-data");
//                comments = doc.select(".comments");

                String comm = Jsoup.clean(storyText.toString(), Whitelist.basic());
//                Document doc1 = Jsoup.parse(scomments);
                data.add(new Item(storyTitle.text(), storyText.html(), comments.html()));

            } catch (IOException e) {
                e.printStackTrace();
            }
            // ничего не возвращаем потому что я так захотел)
            return  null;
         }

        @Override
        protected void onPostExecute(String result) {
            // после запроса обновляем листвью
            lv.setAdapter(adapter);
        }
    }
}