package com.workshop.growthhackersreader;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.jamiltz.com.growthhackersreader.R;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Database;
import com.couchbase.lite.Document;
import com.couchbase.lite.DocumentChange;
import com.couchbase.lite.LiveQuery;
import com.couchbase.lite.QueryEnumerator;
import com.couchbase.lite.QueryOptions;
import com.couchbase.lite.QueryRow;
import com.squareup.picasso.Picasso;


public class FeedAdapter extends RecyclerView.Adapter<ArticleViewHolder> implements View.OnClickListener {
    private static final DecelerateInterpolator DECELERATE_INTERPOLATOR = new DecelerateInterpolator();
    private static final AccelerateInterpolator ACCELERATE_INTERPOLATOR = new AccelerateInterpolator();
    private static final OvershootInterpolator OVERSHOOT_INTERPOLATOR = new OvershootInterpolator(4);

    private Context context;
    private LiveQuery query;
    private QueryEnumerator enumerator;

    public FeedAdapter(Context context, final LiveQuery query) {
        this.context = context;
        this.query = query;
        try {
            enumerator = query.run();
        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
        }
        query.getDatabase().addChangeListener(new Database.ChangeListener() {
            @Override
            public void changed(final Database.ChangeEvent event) {
                ((Activity) FeedAdapter.this.context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (event.isExternal() == true) {
                            try {
                                enumerator = query.run();
                            } catch (CouchbaseLiteException e) {
                                e.printStackTrace();
                            }
                            for(DocumentChange change : event.getChanges()) {
                                int i = 0;
                                while (enumerator.hasNext()) {
                                    QueryRow row = enumerator.next();
                                    if (row.getDocumentId().equals(change.getDocumentId())) {
                                        notifyItemChanged(i);
                                    }
                                    i++;
                                }

                            }

                        }
                    }
                });
            }
        });
//        query.addChangeListener(new LiveQuery.ChangeListener() {
//            @Override
//            public void changed(final LiveQuery.ChangeEvent event) {
//                ((Activity) FeedAdapter.this.context).runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        if (event)
//                        enumerator = event.getRows();
//                        notifyDataSetChanged();
//                    }
//                });
//            }
//        });
    }

    @Override
    public ArticleViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        final View view = LayoutInflater.from(context).inflate(R.layout.item_article, viewGroup, false);
        ArticleViewHolder holder = new ArticleViewHolder(view);
        holder.buttonLike.setOnClickListener(this);
        return holder;
    }

    @Override
    public void onBindViewHolder(ArticleViewHolder articleViewHolder, int i) {
        // Step 4
    }

    @Override
    public int getItemCount() {
        return enumerator != null ? enumerator.getCount() : 0;
    }

    public Object getItem(int i) {
        return enumerator != null ? enumerator.getRow(i).getDocument() : null;
    }

    @Override
    public void onClick(View v) {
        final int viewId = v.getId();
        if (viewId == R.id.buttonLike) {
            // Step 6
        }
    }

    // Step 5

}
