package com.codepath.gangstagram;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import static android.support.v7.app.ActionBar.DISPLAY_HOME_AS_UP;
import static android.support.v7.app.ActionBar.DISPLAY_SHOW_CUSTOM;
import static android.support.v7.app.ActionBar.DISPLAY_SHOW_HOME;
import static android.support.v7.app.ActionBar.DISPLAY_SHOW_TITLE;

public class FeedActivity extends AppCompatActivity {

    private InstagramApi instApi;
    private FeedViewAdapter adapter;
    final private List<FeedItem> items = new ArrayList<>();
    private SwipeRefreshLayout swipeContainer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);

        ActionBar mActionBar = getSupportActionBar();
        mActionBar.setDisplayOptions(DISPLAY_SHOW_TITLE | DISPLAY_SHOW_CUSTOM | DISPLAY_SHOW_HOME);
//        mActionBar.setDisplayOptions(DISPLAY_HOME_AS_UP | DISPLAY_SHOW_CUSTOM | DISPLAY_SHOW_HOME | DISPLAY_SHOW_TITLE);

        this.instApi = new InstagramApi();

        this.swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);

        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadFeedItems();
            }
        });
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        loadFeedItems();

        this.adapter = new FeedViewAdapter(this, items);
        ListView lvFeed = (ListView) findViewById(R.id.lvFeed);
        lvFeed.setAdapter(this.adapter);



    }

    private void loadFeedItems() {
        instApi.getPopularImages(new InstagramApi.Callback() {
            @Override
            public void onSuccess(List<FeedItem> items) {
//                FeedActivity.this.items.clear();
//                FeedActivity.this.items.addAll(items);

                adapter.clear();
                adapter.addAll(items);
//                FeedActivity.this.adapter.notifyDataSetChanged();
                swipeContainer.setRefreshing(false);
            }

            @Override
            public void onFailure(int statusCode, String error, Throwable t) {
                //TODO: display error
                Log.e(FeedActivity.class.getSimpleName(), "Failed to retrieve images. " + error, t);
                swipeContainer.setRefreshing(false);
            }
        });
    }
}
