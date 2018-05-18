package com.example.adama.twittercloneapp;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.tweetcomposer.ComposerActivity;
import com.twitter.sdk.android.tweetcomposer.TweetComposer;
import com.twitter.sdk.android.tweetui.SearchTimeline;
import com.twitter.sdk.android.tweetui.TweetActionBarView;
import com.twitter.sdk.android.tweetui.TweetTimelineRecyclerViewAdapter;
import com.twitter.sdk.android.tweetui.UserTimeline;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.rcvMain) RecyclerView rcvMain;
    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.prbMain) ProgressBar prbMain;
    @BindView(R.id.fabCreate) FloatingActionButton fabCreate;

    UserTimeline userTimeline;
    TweetTimelineRecyclerViewAdapter adapterUser;
    TweetTimelineRecyclerViewAdapter adapterSearch;
    SearchTimeline searchTimeline;
    SearchView searchView = null;
    String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        username = getIntent().getStringExtra("username");
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(null);

        rcvMain.setLayoutManager(new LinearLayoutManager(this));

        getUserTimeLine(username);

    }

    @OnClick(R.id.fabCreate) public void fabCreateClicked(){
        final TwitterSession session = TwitterCore.getInstance().getSessionManager().getActiveSession();
        final Intent intent = new ComposerActivity.Builder(MainActivity.this)
                .session(session)
                .createIntent();
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_main, menu);

        final MenuItem item = menu.findItem(R.id.action_search);

        searchView = (SearchView) item.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                item.collapseActionView();
                searchView.clearFocus();

                setAdapterBySearch(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        switch (id){
            case R.id.action_refresh:
                getUserTimeLine(username);
                break;
            case R.id.action_disconnect:
                finish();
                break;
            default:
                break;
        }

        return true;

    }

    public void getUserTimeLine(String username){
        toolbar.setTitle("@"+username);

        userTimeline = new UserTimeline.Builder()
                .screenName(username)
                .build();

        adapterUser = new TweetTimelineRecyclerViewAdapter.Builder(this)
                .setTimeline(userTimeline)
                .setViewStyle(R.style.tw__TweetLightWithActionsStyle)
                .build();

        rcvMain.setAdapter(adapterUser);
    }

    public void setAdapterBySearch(String query){
        toolbar.setTitle(query);

        searchTimeline = new SearchTimeline.Builder()
                .query(query)
                .build();


        adapterSearch = new TweetTimelineRecyclerViewAdapter.Builder(this)
                .setTimeline(searchTimeline)
                .setViewStyle(R.style.tw__TweetLightWithActionsStyle)
                .build();


        rcvMain.setAdapter(adapterSearch);
    }


    @Override
    public void onBackPressed() {
        if(!searchView.isIconified()){
            searchView.onActionViewCollapsed();
        }else {
            super.onBackPressed();
        }
    }
}
