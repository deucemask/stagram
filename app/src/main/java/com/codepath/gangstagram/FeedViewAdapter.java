package com.codepath.gangstagram;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.text.NumberFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Created by dmaskev on 10/11/15.
 */
public class FeedViewAdapter extends ArrayAdapter<FeedItem> {

    private Map<Integer, Map<Integer, View>> viewCache = new HashMap<>();

    public FeedViewAdapter(Context context, List<FeedItem> objects) {
        super(context, 0, objects);
//        super(context, android.R.layout.simple_list_item_1, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.feed_item, parent, false);
        }

        ImageView ivAvatar = (ImageView) getCachedViewById(convertView, R.id.ivAvatar, position);
        ImageView ivPhoto = (ImageView) getCachedViewById(convertView, R.id.ivPhoto, position);
        TextView tvCaption = (TextView) getCachedViewById(convertView, R.id.tvCaption, position);
        TextView tvDate = (TextView) getCachedViewById(convertView, R.id.tvDate, position);
        TextView tvLikes = (TextView) getCachedViewById(convertView, R.id.tvLikes, position);
        TextView tvUserName = (TextView) getCachedViewById(convertView, R.id.tvUserName, position);
        TextView tvUserNameBottom = (TextView) getCachedViewById(convertView, R.id.tvUserNameBottom, position);

        FeedItem item = getItem(position);

        ivAvatar.setImageResource(0);
        Picasso.with(getContext()).load(item.user.avatar).transform(new RoundTransformation(60, 4))
                .resizeDimen(R.dimen.avatar_size, R.dimen.avatar_size)
                .centerCrop().into(ivAvatar);

        ivPhoto.setImageResource(0);
        Picasso.with(getContext()).load(item.imageUrl).into(ivPhoto);

        tvCaption.setText(item.caption);
        tvDate.setText(formatRelDate(item.getRelDate()));
        if(item.likesCnt != null) {
            tvLikes.setText(NumberFormat.getInstance().format(item.likesCnt));
        }

        tvUserName.setText(item.user.name);
        tvUserNameBottom.setText(item.user.name);


        return convertView;
    }

    private View getCachedViewById(View convertView, int viewId, int position) {
        Map<Integer, View> views = (viewCache.containsKey(position) ? viewCache.get(position) : new HashMap<Integer, View>());
        View view = views.get(viewId);

        if(view == null) {
            view = convertView.findViewById(viewId);
            views.put(viewId, view);
            viewCache.put(position, views);
        }

        return view;
    }

    //TODO: move this to helper class
    private String formatRelDate(long milliesPassed) {
        int sec = 1000;
        int min = 60 * sec;
        int hour = 60 * min;
        int day = 24 * hour;
        int week = 7 * day;

        int delim = sec;
        String unit = "s";
        if(milliesPassed >= week) {
            delim = week;
            unit = "w";
        } else if(milliesPassed >= day) {
            delim = day;
            unit = "d";
        } else if(milliesPassed >= hour) {
            delim = hour;
            unit = "h";
        } else if(milliesPassed >= min) {
            delim = min;
            unit = "m";
        }

        int v = Double.valueOf(Math.ceil(milliesPassed / delim)).intValue();
        return v + unit;
    }
}
