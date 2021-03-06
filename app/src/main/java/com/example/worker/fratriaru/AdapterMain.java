package com.example.worker.fratriaru;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.VideoView;

import com.androidquery.AQuery;
import com.example.worker.fratriaru.descriptionAxtivityWebwiev.DescriptionActivity;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

public class AdapterMain extends RecyclerView.Adapter<AdapterMain.ViewHolder> {

    private ArrayList<ClassItem> data;
    private AQuery aq;
    private Activity activity;
    private DateFormat formatter = new SimpleDateFormat("dd.MM.yyyy" + " в " + "HH:mm", Locale.getDefault());

    public AdapterMain(Activity activity, ArrayList<ClassItem> data) {
        this.activity = activity;
        this.data = data;
        aq = new AQuery(activity);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private CardView cardView;
        private ImageView stgvImageView;
        private ImageView userAva;
        private TextView siteurl;
        private TextView userFullname;
        private TextView articleTitle;
        private TextView category;
        private at.markushi.ui.CircleButton share;

        public ViewHolder(View holderView) {
            super(holderView);
            cardView = (CardView) holderView.findViewById(R.id.card_view);
            stgvImageView = (ImageView) holderView.findViewById(R.id.stgvImageView);
            userAva = (ImageView) holderView.findViewById(R.id.userAva);
            siteurl = (TextView) holderView.findViewById(R.id.siteurl);
            userFullname = (TextView) holderView.findViewById(R.id.userFullname);
            articleTitle = (TextView) holderView.findViewById(R.id.articleTitle);
            category = (TextView) holderView.findViewById(R.id.category);
            share = (at.markushi.ui.CircleButton) holderView.findViewById(R.id.button0);
        }
    }

    @Override
    public AdapterMain.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                     int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card, parent, false);

        ViewHolder vh = new ViewHolder(v);

        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {

        ClassItem item = data.get(i);
        aq.id(viewHolder.articleTitle).text(item.getTitle());
        aq.id(viewHolder.category).text(item.getCategories());
        aq.id(viewHolder.siteurl).text(item.getLink());
        aq.id(viewHolder.userAva).image(item.getLogo());
        String author = ""+item.getAuthor();
        if (author.equals("null")) author="";
        aq.id(viewHolder.userFullname).text(author + " " + formatter.format(item.getDate()));
        aq.id(viewHolder.stgvImageView).clear();
        if (TextUtils.equals(item.getImg(),""))
            aq.id(viewHolder.stgvImageView).gone();
        else {

            /**
             * Set the image of an ImageView.
             *
             * @param url The image url.
             * @param memCache Use memory cache.
             * @param fileCache Use file cache.
             * @param targetWidth Target width for down sampling when reading large images. 0 = no downsampling.
             * @param fallbackId Fallback image if result is network fetch and image convert failed. 0 = no fallback.
             * @param preset Default image to show before real image loaded. null = no preset.
             * @param animId Apply this animation when image is loaded. 0 = no animation. Also accept AQuery.FADE_IN as a predefined 500ms fade in animation.
             * @return self
             *
             * @see testImage7
             *
             */

            aq.id(viewHolder.stgvImageView).visible().image(item.getImg(), true, true, 640, 0, null, AQuery.FADE_IN, AQuery.RATIO_PRESERVE);
        }
        //
        final String url = item.getLink();
        aq.id(viewHolder.cardView).clicked(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Intent i = new Intent(activity, DescriptionActivity.class);
                    i.setData(Uri.parse(url));
                    activity.startActivity(i);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        aq.id(viewHolder.share).clicked(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, url);
                sendIntent.setType("text/plain");
                activity.startActivity(Intent.createChooser(sendIntent,  "Поделиться через:"));
            }
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }
}