Growth Hackers Reader Android Tutorial
============

### Goal

Build your first Couchbase Mobile app in just a few minutes! Take an existing Android application
and add data persistence along with offline support!

# ![Application Architecture](https://raw.githubusercontent.com/couchbaselabs/mini-hacks/master/kitchen-sync/topology.png "Typical Couchbase Mobile Architecture")

### Setup

 - Clone this repo, or download the .zip folder.
 - Make sure you are on the latest Android Studio, that's the 1.0 version.
 - Launch Android Studio, choose 'Import Project...' and select the `android` folder.
 - Verify your environment is working by debugging the app on your Android device.

 	In this tutorial, we learn how to use the new Recycler View api that was added to Android Lollipop 5.0 and
 	Couchbase Lite to fetch news articles from the Growth Hackers website. Articles are stored on the cloud in Sync Gateway at 
 	`http://178.62.81.153:4984/growthhackers`

 ### Tutorial

 1. In `Application.java`, we already have a Couchbase Lite database set up in the `initDatabase()` method.
 Let's set up the pull replication to get the articles from Sync Gateway. Create a `setupSync` method:
 
 ```java
 private void setupSync() {
 ```

 2. We need to keep a reference to our replication object, add this property to the Application class and the SYNC_URL_HTTP
 constant
 ```java
 private static final String SYNC_URL_HTTP = "http://178.62.81.153:4984/growthhackers";
 private Replication pull;
 ```


 3. In the `setupSync` method, instantiate the pull replication and start it:
 ```java
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
 ```
 
 Now when you run it, the app will get the latest articles from Sync Gateway.
 But we still need to display them so let's do that with the new Recycler View api.

 4. In `FeedAdapter.java`, we set up the necessary code to display the new documents stored in Couchbase Lite.
 Here we're using a Live Query to display new updates as they arrive. That's great to 
 display data in real-time and provide a good user experience. The code so far is essentially setting up this live query
 and making sure to reload the Recycler View when new documents are detected. But if you run the app, you will see empty rows.
 Let's add data to those rows, add the following code to the `onBindViewHolder` method: 
 ```java
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
 ```
 
 Notice we use [Picasso](https://github.com/square/picasso) which is a great library for loading images from the internet. Get familiar with it, it's a great tool!
 
 Run the app and now you should see the list of articles. Click on the title and the row expands to display the summary text.
 Notice there is a heart on the right hand side of the row but nothing happends when you click on it.
 Let's focus on building this out.

 5. In the `onClick` listener for the heart icon (in `FeedAdapter.java`), let's add some animation to make the heart spin and  change color. This informs the user that this action is being processed. Add a new method called `updateHeartButton` as      follows:

 ```java
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
 ```
 
 6. And call in the `onClick` method like so:
 
 ```java
 @Override
 public void onClick(View v) {
     final int viewId = v.getId();
     if (viewId == R.id.buttonLike) {
         ArticleViewHolder holder = (ArticleViewHolder) v.getTag();
         updateHeartButton(holder);
     }
 }
 ```
 
 7. Notice that there is a counter as well to display the number of likes. We want to store this information in Couchbase Lite  and have it synched to other users. So let's update the `onClick` method to save the increase the number of likes by one:
 
 ```java
@Override
public void onClick(View v) {
    final int viewId = v.getId();
    if (viewId == R.id.buttonLike) {
        ArticleViewHolder holder = (ArticleViewHolder) v.getTag();
        Article article = Article.from((Document) getItem(holder.getPosition()));
        article.setLikes(article.getLikes() + 1);
        article.save();
        updateLikesCounter(holder, Integer.toString(article.getLikes()), true);
        updateHeartButton(holder);
    }
}
 ```
 
 8. Finally we need to had some animation code to update the number of likes on the UI. In the `onClick` method, add this   line below the call to the `updateHeartButton` method:
 
 ```java
 holder.tsLikesCounter.setText(Integer.toString(article.getLikes()));
 ```
 
  Run the app and start liking articles. You can quit and restart the app, everything is still there as it's being stored
  locally in Couchbase Lite.
 
 9. Now we want to share likes with other users. We need to use a push replication for that to send
  new data to Sync Gateway and will do all the heavy lifting of sending this to all users.
  Back in Application.java, add a pushReplication property:
 
 ```java
 private Replication push;
 ```
 
 10. Change the code in the `setupSync` method to be as follows:
 
 ```java
URL url;
try {
    url = new URL(SYNC_URL_HTTP);
} catch (MalformedURLException e) {
    Log.e(Application.TAG, "Sync URL is invalid, setting up sync failed");
    return;
}
push = database.createPushReplication(url);
push.setContinuous(true);
pull = database.createPullReplication(url);
pull.setContinuous(true);
pull.start();
push.start();
 ```
 
 10. Now run the app on two devices, you can like an article, see the counter increment by one and see it being updated on the other device.
 
That's it! You can always ask questions on our Gitter channel at [http://gitter.im/couchbase/mobile](http://gitter.im/couchbase/mobile)
