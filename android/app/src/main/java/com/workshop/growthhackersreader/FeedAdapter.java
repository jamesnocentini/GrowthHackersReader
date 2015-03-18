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
        Article article = Article.from((Document) getItem(i));
        articleViewHolder.labelTitle.setText(article.getTitle());
        articleViewHolder.labelTopic.setText(article.getTopic());
        Picasso.with(context)
                .load(article.getThumbnail())
                .placeholder(R.drawable.ic_person_outline_grey600_24dp)
                .centerCrop()
                .resize(56, 56)
                .transform(new RoundedTransformation())
                .into(articleViewHolder.imageThumbnail);
        articleViewHolder.summaryView.setText(article.getSummary());
        articleViewHolder.tsLikesCounter.setText(Integer.toString(article.getLikes()));
        articleViewHolder.buttonLike.setTag(articleViewHolder);
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
            ArticleViewHolder holder = (ArticleViewHolder) v.getTag();
            Article article = Article.from((Document) getItem(holder.getPosition()));
            article.setLikes(article.getLikes() + 1);
            article.save();
            updateHeartButton(holder);

            holder.tsLikesCounter.setText(Integer.toString(article.getLikes()));
        }
    }

    private void updateHeartButton(final ArticleViewHolder holder) {
        AnimatorSet animatorSet = new AnimatorSet();

        ObjectAnimator rotationAnim = ObjectAnimator.ofFloat(holder.buttonLike, "rotation", 0f, 360f);
        rotationAnim.setDuration(300);
        rotationAnim.setInterpolator(ACCELERATE_INTERPOLATOR);

        ObjectAnimator bounceAnimX = ObjectAnimator.ofFloat(holder.buttonLike, "scaleX", 0.2f, 1f);
        bounceAnimX.setDuration(300);
        bounceAnimX.setInterpolator(OVERSHOOT_INTERPOLATOR);

        ObjectAnimator bounceAnimY = ObjectAnimator.ofFloat(holder.buttonLike, "scaleY", 0.2f, 1f);
        bounceAnimY.setDuration(300);
        bounceAnimY.setInterpolator(OVERSHOOT_INTERPOLATOR);
        bounceAnimY.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                holder.buttonLike.setImageResource(R.drawable.ic_heart_red);
            }
        });

        animatorSet.play(rotationAnim);
        animatorSet.play(bounceAnimX).with(bounceAnimY).after(rotationAnim);

        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                holder.buttonLike.setImageResource(R.drawable.ic_heart_outline_grey);
            }
        });

        animatorSet.start();
    }

}
