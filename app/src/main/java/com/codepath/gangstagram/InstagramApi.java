package com.codepath.gangstagram;

import android.util.Log;

import com.codepath.gangstagram.FeedItem.User;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import cz.msebera.android.httpclient.Header;

/**
 * Created by dmaskev on 10/11/15.
 */
public class InstagramApi {

    private AsyncHttpClient api;
    //TODO: move this to config file or manifest file
    private static final String INSTA_CLIENT_ID = "3e7118514309496998c71c04c8501c53";
    private static final String INSTA_URL_GET_POPULAR = "https://api.instagram.com/v1/media/popular";
    private static final String INSTA_API_CALL_GET_POPULAR = INSTA_URL_GET_POPULAR + "?client_id=" + INSTA_CLIENT_ID;

    public interface Callback {
        void onSuccess(List<FeedItem> items);
        void onFailure(int statusCode, String error, Throwable t);
    }


    public InstagramApi() {
        this.api = new AsyncHttpClient();
    }

    public void getPopularImages(final Callback callback) {
        this.api.get(INSTA_API_CALL_GET_POPULAR, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d(this.getClass().getSimpleName(), String.format("[%d] Response for API call %s : %s", statusCode, getRequestURI(), response));


                List<FeedItem> items = createFeedItems(response);
                callback.onSuccess(items);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.e(this.getClass().getSimpleName(), String.format("[%d] Failed on API call %s : %s", statusCode, getRequestURI(), errorResponse));
                callback.onFailure(statusCode, errorResponse.toString(), throwable);
            }
        });
    }

    //TODO: move this function to separate class
    private List<FeedItem> createFeedItems(JSONObject json) {
        List<FeedItem> res = Collections.emptyList();
        try {
            JSONArray data = json.getJSONArray("data");
            res = new ArrayList<>(data.length());
            for(int i = 0; i < data.length(); i ++) {
                JSONObject item = data.getJSONObject(i);
                String type = item.getString("type");
                if(type == null || !type.equals("image")) {
                    continue;
                }

                FeedItem fitem = createSingleFeedItem(item);
                if(fitem != null && fitem.imageUrl != null && !fitem.imageUrl.isEmpty()) {
                    res.add(fitem);
                }

            }

            //                {
//                    FeedItem item = new FeedItem();
//                    item.caption = "caption 1";
//                    item.date = new Date(System.currentTimeMillis() - 60*60*1000);
//                    item.imageUrl = "http://resizing.flixster.com/irlsXK0tD2GGP7YDhi0m5JV8d3s=/180x267/dkpu1ddg7pbsk.cloudfront.net/movie/11/19/11/11191103_ori.jpg";
//                    item.likesCnt = "88";
////                    item.imageHeight = "100";
//                    item.user = new User("user1", "https://encrypted-tbn1.gstatic.com/images?q=tbn:ANd9GcSPpC5MKpl4uavBK89qwxwAHfmLUnIVL1YgQ1WoBgFmeVVRmmmsJw");
//
//                    items.add(item);
//                }
//
//                {
//                    FeedItem item = new FeedItem();
//                    item.caption = "caption 2";
//                    item.date = new Date(System.currentTimeMillis() - 60*1000);
//                    item.imageUrl = "http://resizing.flixster.com/YoiBoNqEY7Y20GBhdQSy8q98uuo=/180x267/dkpu1ddg7pbsk.cloudfront.net/movie/11/19/12/11191226_ori.jpg";
//                    item.likesCnt = "88";
//
////                    item.imageHeight = "400";
//                    item.user = new User("user2", "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQRG38VK4-kYeMdF1fQ6RbhfDoUjbE8smcnFrvJrVg5n1xwP4tN");
//
//                    items.add(item);
//                }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return res;
    }

    private FeedItem createSingleFeedItem(JSONObject item) {

        FeedItem fitem = new FeedItem();
        try {
            JSONObject user = item.getJSONObject("user");
            fitem.user = new User(user.getString("username"), user.getString("profile_picture"));
            JSONObject likesObj = item.getJSONObject("likes");
            if(likesObj != null) {
                String count = likesObj.getString("count");
                if(count != null && !count.equals("")) {
                    fitem.likesCnt = Long.valueOf(count);
                }

            }
            JSONObject caption = item.getJSONObject("caption");
            if(caption != null) {
                fitem.caption = caption.getString("text");
            }

            fitem.date = formatDate(item.getString("created_time"));
            JSONObject images = item.getJSONObject("images");
            if(images != null) {
                JSONObject standardResolution = images.getJSONObject("standard_resolution");
                if(standardResolution != null) {
                    fitem.imageUrl = standardResolution.getString("url");
                }
            }

        } catch (JSONException e) {
            Log.e(this.getClass().getSimpleName(), "Failed to parse json item. " + e.getMessage() + ". JSON: " + item, e);
            fitem = null;
        }
        return fitem;
    }

    private Date formatDate(String seconds) {
        Date date = null;
        try {
            long millis = Long.valueOf(seconds) * 1000;
            date = new Date(millis);
        } catch(Exception e) {
            date = new Date();
            Log.e(this.getClass().getSimpleName(), "Failed to formatDate from seconds value " + seconds, e);
        }

        return date;
    }


}
