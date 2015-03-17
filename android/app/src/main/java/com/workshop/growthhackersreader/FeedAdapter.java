package com.workshop.growthhackersreader;

import android.app.Activity;
import android.content.Context;
import android.jamiltz.com.growthhackersreader.R;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.couchbase.lite.Document;
import com.couchbase.lite.LiveQuery;
import com.couchbase.lite.QueryEnumerator;
import com.squareup.picasso.Picasso;


public class FeedAdapter extends RecyclerView.Adapter<ArticleViewHolder> implements View.OnClickListener {

    private Context context;
    private LiveQuery query;
    private QueryEnumerator enumerator;

    public FeedAdapter(Context context, LiveQuery query) {
        this.context = context;
        this.query = query;
        query.addChangeListener(new LiveQuery.ChangeListener() {
            @Override
            public void changed(final LiveQuery.ChangeEvent event) {
                ((Activity) FeedAdapter.this.context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        enumerator = event.getRows();
                        notifyDataSetChanged();
                    }
                });
            }
        });
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
                .centerCrop()
                .resize(56, 56)
                .transform(new RoundedTransformation())
                .into(articleViewHolder.imageThumbnail);
        articleViewHolder.summaryView.setText(article.getSummary());

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
            holder.buttonLike.setImageResource(R.drawable.ic_heart_red);
            Article article = Article.from((Document) getItem(holder.getPosition()));
        }
    }
}
