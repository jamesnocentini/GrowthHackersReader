package com.workshop.growthhackersreader;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Database;
import com.couchbase.lite.Manager;
import com.couchbase.lite.android.AndroidContext;
import com.couchbase.lite.replicator.Replication;
import com.couchbase.lite.util.Log;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

public class Application extends android.app.Application {

    public static final String TAG = "GrowthHackersReader";
    private static final String DATABASE_NAME = "workshop";
    private static final String SYNC_URL_HTTP = "http://10.0.3.2:4984/growthhackers";

    private Manager manager;
    private Database database;

    private Replication pull;

    private void initDatabase() {
        Manager.enableLogging(TAG, Log.VERBOSE);
        Manager.enableLogging(Log.TAG, Log.VERBOSE);
        Manager.enableLogging(Log.TAG_SYNC_ASYNC_TASK, Log.VERBOSE);
        Manager.enableLogging(Log.TAG_SYNC, Log.VERBOSE);
        Manager.enableLogging(Log.TAG_QUERY, Log.VERBOSE);
        Manager.enableLogging(Log.TAG_VIEW, Log.VERBOSE);
        Manager.enableLogging(Log.TAG_DATABASE, Log.VERBOSE);
        try {
            manager = new Manager(new AndroidContext(getApplicationContext()), Manager.DEFAULT_OPTIONS);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            /* Create Database will return the existing database or create it if it does not
             exist. */
            database = manager.getDatabase(DATABASE_NAME);
        } catch (CouchbaseLiteException e) {
            Log.e(TAG, "Cannot get Database", e);
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(Application.TAG, "Application State: onCreate()");
        initDatabase();
        setupSync();
    }

    private void setupSync() {
        URL url;
        try {
            url = new URL(SYNC_URL_HTTP);
        } catch (MalformedURLException e) {
            Log.e(Application.TAG, "Sync URL is invalid, setting up sync failed");
            return;
        }

        pull = database.createPullReplication(url);
        pull.setContinuous(true);

        pull.start();
    }

    public Database getDatabase() {
        return database;
    }

    public void setDatabase(Database database) {
        this.database = database;
    }
}
