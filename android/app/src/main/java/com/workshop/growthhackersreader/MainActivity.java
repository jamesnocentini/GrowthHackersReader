package com.workshop.growthhackersreader;

import android.jamiltz.com.growthhackersreader.R;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.LiveQuery;

import butterknife.ButterKnife;
import butterknife.InjectView;


public class MainActivity extends ActionBarActivity {

    @InjectView(R.id.rvFeed)
    RecyclerView rvFeed;

    private FeedAdapter feedAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setupFeed();
    }

    private void setupFeed() {
        LiveQuery query = Article.findAllArticle(((Application) getApplication()).getDatabase()).toLiveQuery();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rvFeed.setLayoutManager(linearLayoutManager);
        feedAdapter = new FeedAdapter(this, query);
        rvFeed.setAdapter(feedAdapter);
        try {
            query.run();
        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            createNewArticle();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void createNewArticle() {
        Article.createArticle(((Application) getApplication()).getDatabase(), "A random title",
                "Press",
                "https://secure.gravatar.com/avatar/3334de1f544b64f1d1b3383098d14892?s=40&d=https%3A%2F%2Fsecure.gravatar.com%2Favatar%2Fad516503a11cd5ca435acc9bb6523536%3Fs%3D40&r=G", "James", "A random description");
    }
}
