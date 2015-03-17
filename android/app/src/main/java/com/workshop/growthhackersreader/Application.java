package com.workshop.growthhackersreader;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Database;
import com.couchbase.lite.Manager;
import com.couchbase.lite.android.AndroidContext;
import com.couchbase.lite.util.Log;

import java.io.IOException;

public class Application extends android.app.Application {

    public static final String TAG = "GrowthHackersReader";
    private static final String DATABASE_NAME = "workshop";

    private Manager manager;
    private Database database;

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
    }

    public Database getDatabase() {
        return database;
    }

    public void setDatabase(Database database) {
        this.database = database;
    }
}
