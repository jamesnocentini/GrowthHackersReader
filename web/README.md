#Growth Hackers Reader Web Tutorial

### Goal

Build your first Couchbase Web app in just a few minutes! Take an existing Web application
and add data persistence along with offline support!

# ![Application Architecture](https://raw.githubusercontent.com/couchbaselabs/mini-hacks/master/kitchen-sync/topology.png "Typical Couchbase Mobile Architecture")

### Setup

 - Clone this repo, or download the .zip folder.
 - Open it in your favorite editor ([Sublime Text](http://www.sublimetext.com/3) is perfect for that)

 	In this tutorial, we learn how to use Materialize, a material-design UI, Import.IO and
 	Couchbase to read offline news articles from the Growth Hackers website and synchronise likes across all the connected devices. Articles are storing on the cloud in Sync Gateway at 
 	`http://localhost:4984/growthhackers`

 ### Tutorial

* In the web folder, create a simple HTML page:
 
 ```
<!DOCTYPE html>
<html>
  <head>
    <meta charset="utf-8" />
    <title>Women Who Code workshop</title>
  </head>
  <body>
    
  </body>
</html>
 ```

* Add Materialize stylesheet inside the head
 ```
 <link rel="stylesheet" href="css/materialize.css" />
 ```


* We need to tell to the browser that this page is optimised for mobiles (it avoids use to zoom to read our page). For that, we add this meta to the head
 ```
 <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no" />
 ```
 
 
* We start to work the UI with the header. Thanks to Materialize, we already have many elements. 
 
 ```
 <nav>
      <div class="nav-wrapper">
        <a href="#" class="brand-logo center">Main title</a>
      </div>
 </nav>
 ```
 
 
* To be as close as possible to the Growth Hackers UI guidelines, we need to change the color of the header. The list of colors classes of Materialize is available [here](http://materializecss.com/color.html).
 We need to add it to the ```<div class="nav-wrapper">```.
 
 
* Now we finished the header, let's create the wrapper of the page content:
```
<div class="row">
      <div class="col s12"></div>
</div>
```
 
 
* we add the items that will represent the submitted links. To follow the Material Design guidelines, the [card UI element](http://materializecss.com/cards.html) is the best fit for this need. We have to add them inside ```<div class="col s12 m6"></div>```:
```
<div class="card">
  <div class="card-content grey-text text-darken-4">
    <span class="card-title blue-text text-darken-2">Card Title</span>
    <p>I am a very simple card. I am good at containing small bits of information.</p>
  </div>
  <div class="card-action">
    <a href="#">This is a link</a>
  </div>
</div>
```
Don't forget to add color classes to have the right UI fit.


* Now that we finished the boilerplate of our UI, we can start to add the database where the Growth Hacker articles will be stored.
Just before ```</body>```, add the PouchDB script (which is the local database):
```
<script src="js/pouchdb.js"></script>
```


* We have to add jQuery, to help us writing less boilerplate JavaScript code, just after the pouchdb line in ```</body>```:
```
<script src="js/jquery.js></script>
```


* Now we have all our JavaScript dependencies added in our page, we are going to create our own JavaScript file where all the logic will be found. Create a empty file named ```index.js``` in the js folder.


* We have to create a local database and connect it in realtime with our Couchbase server (in our JS file):

```
var localDB  = new PouchDB('growthhackers');
var remoteDB = new PouchDB('http://178.62.81.153:4984/growthhackers');

localDB.sync(remoteDB, {
    live: true
});
```

* Add an **id** attribute to the ```<div class="col s12"></div>``` to identify it easily. Set **main-content** as a value.


* Let's go back to our JavaScript file. We need to fetch the articles from the database:

```
localDB.allDocs({
    include_docs: true
}).then(function(result) {
    alert('There are' + result.rows.length + ' articles');
}).catch(function(err) {
    alert('Sorry, there is a problem. Please refresh');
});
```

* Instead of showing a message, it will be much better to display the articles. So let's create a function that we will call it render:

```
function render(articles) {
    // We are going to bind data to the view here
}
```

* Inside of this function, we will iterate each article to render them:

```
var pageContent = '';

for (var i = 0; i < articles.length; i++) {
    var article = articles[i].doc;
    
    var title = '<span class="card-title blue-text text-darken-2">' + article.title + '</span>';
    
    var summary = '<p>' + article.summary + '</p>';
    
    var cardContent = '<div class="card-content grey-text text-darken-4">' + title + summary + '</div>';
    
    var card = '<div class="card">' + cardContent + '</div>';
    
    pageContent += card;
}

$('#main-content').html(pageContent);
```


* Now we replace the message by this render:

```
then(function(result) {
    render(result.rows);
}
```


* Refresh and houray, we have the articles displayed!
We want now to add the category of each article. Thanks to Materialize, we just need to add this element after ```card-content```:

```
var topic = '<span class="right-align badge">' + article.topic + '</span>';

var cardFooter = '<div class="card-action">' + topic + '</div>';

var card = '<div class="card">' + cardContent + cardFooter + '</div>';

```


* Next step, we will add a heart icon that allow us to display the number of likes and let us like the article too. We will add that icon in the ```card-action``` element:

```
var like = '<i id="' + article._id + '" class="mdi-action-favorite-outline"></i> ' + article.likes;
var cardFooter = '<div class="card-action">' + like + topic + '</div>';
```
You can change the heart icon by replacing the ```mdi-action-favorite-outline``` class with any of [this list](http://materializecss.com/icons.html).


* We are going to create a listener of click events on any heart icon. For that, add the next function in the end of your JavaScript code:

```
$('#main-content').on('click', '.mdi-action-favourite-outline', function click () {
    //My code
});
```


* Now we need to switch the icon when a click is triggered by the user:

```
$(this).toggleClass('mdi-action-favorite-outline mdi-action-favorite');
```
```toggleClass``` is a method of jQuery that allows to remove/add classes automatically of the selected element based on the list of classes when it's called.


* Next step, we need to increment the number of likes in the database. After the toggle, we need to get the article id and fetch it to be able to update it:

```
var articleId = $(this).attr('id');

localDB.get(articleId);
```


* Because the call to the database is asynchronous, we will update the number of likes in the callback:

```
localDB.get(articleId).then(function(doc) {
    doc.likes++;
    localDB.put(doc);
});
```


* We're close to the end! You remember the ```localDB.allDocs``` call in the beginning of your JS file ? Wrap it in a function named fetch and call it in the end of your file:

```
fetch();
```

* Final step. Set ```fetch``` as a listener to the live changes:

```
localDB.sync(remoteDB, {
    live: true
}).on('change', function(change) {
  fetch();
}).on('error', function(err) {
    console.log(err);
});
```


* Refresh the page and you'll have in realtime, the new likes of each article ! Enjoy :smiley: