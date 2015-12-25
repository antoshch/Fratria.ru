package com.example.worker.fratriaru;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxStatus;
import com.androidquery.util.AQUtility;
import com.androidquery.util.XmlDom;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ActivityMain extends Activity implements SwipeRefreshLayout.OnRefreshListener{

    private AQuery aq;
    private Activity activity;
    private RecyclerView gridView;
    private StaggeredGridLayoutManager mLayoutManager;
    private AdapterMain adapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ArrayList<ClassItem> items = new ArrayList<ClassItem>();

    private final String[] FEEDS = new String[]{"http://fratria.ru/rss/"};
    private DateFormat formatter = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zz", Locale.ENGLISH);

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activity = this;
        aq = new AQuery(activity);
        AQUtility.setDebug(true);

        swipeRefreshLayout = new SwipeRefreshLayout(activity);
        swipeRefreshLayout.setOnRefreshListener(this);


        gridView = new RecyclerView(activity);
        gridView.setHasFixedSize(true);
        mLayoutManager = new StaggeredGridLayoutManager(1,StaggeredGridLayoutManager.VERTICAL);
        gridView.setLayoutManager(mLayoutManager);
        gridView.setItemAnimator(new DefaultItemAnimator());

        swipeRefreshLayout.addView(gridView);
        getWindow().setContentView(swipeRefreshLayout);

        adapter = new AdapterMain(activity,items);
        gridView.setAdapter(adapter);

        getFeeds();

    }

    public void getFeeds() {
        items.clear();
        for(String feed:FEEDS){
            request(feed);
        }
    }

    public void request(String url) {
        aq.ajax(url, XmlDom.class, this, "onRequest");
        swipeRefreshLayout.setRefreshing(true);
    }

    public void onRequest(String url,XmlDom xml, AjaxStatus status) {
        if (status.getCode()==200) {
            String logo = "";
            try {
                logo = xml.tags("url").get(0).text();
            }
            catch (Exception e) {
                e.printStackTrace();
            }

            List<XmlDom> xmlItems = xml.tags("item");

            for(XmlDom xmlItem: xmlItems){
                ClassItem item = new ClassItem();

                // Получаем описание
                String description = xmlItem.tag("description").text();
                item.setDescription(description);

                //Получаем лого
                item.setLogo(logo);

                // Получаем автора
                try {
                    item.setAuthor(xmlItem.tag("author").text());
                }
                catch (Exception e) {
                }

                //Получаем заголовки
                item.setTitle(xmlItem.tag("title").text());

                // Получаем ссылку
                item.setLink(xmlItem.tag("link").text());

                //Получаем дату публикации
                String pubDate = xmlItem.tag("pubDate").text();
                Date date = new Date();
                try {
                    date = formatter.parse(pubDate);
                }
                catch (Exception e) {
                    AQUtility.debug("errorParsingDate",e.toString());
                }
                item.setDate(date);

                //Получаем изображение
                String src = "";

                try {

                    src = new XmlDom(xmlItem.toString()).tag("enclosure").attr("url");
                    if (src.startsWith("//") ) {
                        src = "http:"+src;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                item.setImg(src);


                //
                items.add(item);
            }
            Collections.sort(items, new Comparator<ClassItem>() {
                public int compare(ClassItem o1, ClassItem o2) {
                    if (o1.getDate() == null || o2.getDate() == null)
                        return 0;
                    return o2.getDate().compareTo(o1.getDate());
                }
            });

        }
        adapter.notifyDataSetChanged();
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onBackPressed (){
        //AlertDialog to exit the app.
    }

    protected void onDestroy(){

        super.onDestroy();

        if(isTaskRoot()){

            //clean the file cache with advance option
            long triggerSize = 6000000; //starts cleaning when cache size is larger than 3M
            long targetSize = 5000000;      //remove the least recently used files until cache size is less than 2M
            AQUtility.cleanCacheAsync(this, triggerSize, targetSize);
        }

    }

    @Override
    public void onRefresh() {
        getFeeds();
    }
}