package com.workshop.growthhackersreader;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.jamiltz.com.growthhackersreader.R;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextSwitcher;
import android.widget.TextView;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Expandable RecyclerView cells - inspired from https://gist.github.com/ZkHaider/9bf0e1d7b8a2736fd676
 */
public class ArticleViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    private int originalHeight = 0;
    private boolean isViewExpanded = false;

    @InjectView(R.id.imageThumbnail)
    ImageView imageThumbnail;

    @InjectView(R.id.labelTopic)
    TextView labelTopic;

    @InjectView(R.id.labelTitle)
    TextView labelTitle;

    @InjectView(R.id.summaryView)
    TextView summaryView;

    @InjectView(R.id.tsLikesCounter)
    TextSwitcher tsLikesCounter;

    @InjectView(R.id.buttonLike)
    ImageButton buttonLike;

    public ArticleViewHolder(View itemView) {
        super(itemView);

        ButterKnife.inject(this, itemView);

        itemView.setOnClickListener(this);
        buttonLike.setOnClickListener(this);

        if (isViewExpanded == false) {
            summaryView.setVisibility(View.GONE);
            summaryView.setEnabled(false);
        }
    }

    @Override
    public void onClick(final View v) {

        final int viewId = v.getId();
        if (viewId == R.id.buttonLike) {
            ArticleViewHolder holder = (ArticleViewHolder) v.getTag();
            holder.buttonLike.setImageResource(R.drawable.ic_heart_red);
            return;
        }

        // if the originalHeight is 0 then find the height of the View being used
        // This would be the height of the cardview
        if (originalHeight == 0) {
            originalHeight = v.getHeight();
        }

        // Declare the ValueAnimator object
        ValueAnimator valueAnimator;
        if (!isViewExpanded) {
            summaryView.setVisibility(View.VISIBLE);
            summaryView.setEnabled(true);
            isViewExpanded = true;
            valueAnimator = ValueAnimator.ofInt(originalHeight, originalHeight + (int) (originalHeight * 2.0));
        } else {
            isViewExpanded = false;
            valueAnimator = ValueAnimator.ofInt(originalHeight + (int) (originalHeight * 2.0), originalHeight);

            Animation a = new AlphaAnimation(1.00f, 0.00f); // Fade out

            a.setDuration(200);
            // Set a listener to the animation and configure onAnimationEnd
            a.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    summaryView.setVisibility(View.INVISIBLE);
                    summaryView.setEnabled(false);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });

            // Set the animation on the custom view
            summaryView.startAnimation(a);
        }
        valueAnimator.setDuration(200);
        valueAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                Integer value = (Integer) animation.getAnimatedValue();
                v.getLayoutParams().height = value.intValue();
                v.requestLayout();
            }
        });
        valueAnimator.start();
    }
}
