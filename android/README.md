Growth Hackers Reader Android Tutorial
============

### Goal

Build your first Couchbase Mobile app in just a few minutes! Take an existing Android application
and add data persistence along with offline support!

# ![Application Architecture](https://raw.githubusercontent.com/couchbaselabs/mini-hacks/master/kitchen-sync/topology.png "Typical Couchbase Mobile Architecture")

### Setup

 - Clone this repo, or download the .zip folder.
 - Make sure you are on the latest Android Studio in the `Stable` channel.
 - Launch Android Studio, choose 'Import Project...' and select the `android` folder.
 - Verify your environment is working by debugging the app on your Android device.

 	In this tutorial, we learn how to use the new Recycler View api that was added to Android Lollipop 5.0 and
 	CouchbaseLite to fetch news articles from the Growth Hackers website. Articles are storing on the cloud in Sync Gateway at 
 	`http://localhost:4984/growthhackers`

 ### Tutorial

 1. In Application.java, we already have a Couchbase Lite database set up in the `initDatabase()` method.
 Let's set up the replications to get the articles from Sync Gateway. Create a `setupSync` method:
 
 ```java
 private void setupSync() {
 ```

 2. We need to keep a reference to our replication object, add this property to the Application class and the SYNC_URL_HTTP
 constant
 ```java
 private static final String SYNC_URL_HTTP = "http://10.0.3.2:4984/growthhackers";
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
 
 Now when you run the app, the app will get the latest articles from Sync Gateway.
 But we still need to display them so let's do that with the new Recycler View api.

 4. In `FeedAdapter.java`, we set up the necessary code to display new documents stored in Couchbase Lite.
 Here we're using a Live Query to display new items as they arrive. That's a great feature of Couchbase Lite to 
 display data in real-time and provide a good user experience. The code so far is essentially setting up this live query
 and making sure to reload the Recycler View when new documents are detected. If you run the app, you will notice empty rows.
 Let's add data to those rows in the `onBindViewHolder` method:
 ```java
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
 ```
 
 Notice we use [Picasso](https://github.com/square/picasso) which is a great library for loading images from the internet. Get familiar with it, it's a great tool!
 
 Run the app and you see the list of articles. When you touch the title, the row expands to display the summary text.
 Notice there is a heart on the right hand side of the row but nothing happends when you touch.
 Let's focus on building this out.

 5. In the click listener for the heart icon, let's add some animation to make the heart spin and change color.
 This informs the user that this action was successful.
 ```java
 
	if (viewId == R.id.buttonLike) {
            ArticleViewHolder holder = (ArticleViewHolder) v.getTag();
            holder.buttonLike.setImageResource(R.drawable.ic_heart_red);
            return;
        }
```

 6. Notice that there is a counter as well to display the number of likes. We want to store this information in
 Couchbase Lite and have it synced to other users. In the same method add the following code to save update the document
 in Couchbase Lite.
 ```java
    Article article = Article.from((Document) getItem(holder.getPosition()));
    article.setCount...
    // code needed
    ```

 7. Finally we need to had some UI code to update the counter with a slick animation:
 ```java
        code needed
 ```

  Run the app and start liking articles. You can quit and restart the app, everything is still there as it's being stored
  locally in Couchbase Lite.

 8. Now we want to share likes with other users. We need to use a push replication for that to send
 new data to Sync Gateway and will do all the heavy lifting of sending this to all users.
 Back in Application.java, add a pushReplication property:
 ```java
 Replication pushReplication;
 ```

 9. Change the code in the `setupSync` method to be as follows:
 ```java
 
    URL url;
        try {
            url = new URL(SYNC_URL_HTTP);
        } catch (MalformedURLException e) {
            Log.e(Application.TAG, "Sync URL is invalid, setting up sync failed");
            return;
        }

        pull = database.createPullReplication(url);
        push = database.createPushReplication(url);

        pull.setContinuous(true);
        push.setContinuous(true);

        pull.addChangeListener(getReplicationChangeListener());
        push.addChangeListener(getReplicationChangeListener());
```

 10. Now run the app on two devices, like an article on one and see it being updated on the other.

 
That's it! You can always ask questions on our Gitter channerl at [http://gitter.im/couchbase/mobile](http://gitter.im/couchbase/mobile)
