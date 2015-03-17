Growth Hackers Reader Web Tutorial (In progress...)
============

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

 1. In the web folder, create a simple HTML page:
 
 ```
<!DOCTYPE html>
<html>
  <head>
    <title>Women Who Code workshop</title>
  </head>
  <body>
    
  </body>
</html>
 ```

 2. Add Materialize stylesheet inside the head
 ```
 <link rel="stylesheet" href="css/materialize.css" />
 ```


 3. We need to tell to the browser that this page is optimised for mobiles (it avoids use to zoom to read our page). For that, we add this meta to the head
 ```
 <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no" />
 ```
 
 
 4. We start to work the UI with the header. Thanks to Materialize, we already have many elements. 
 
 ```
 <nav>
      <div class="nav-wrapper">
        <a href="#" class="brand-logo center">Main title</a>
      </div>
 </nav>
 ```
 
 
 5. To be as close as possible to the Growth Hackers UI guidelines, we need to change the color of the header. The list of colors classes of Materialize is available [here](http://materializecss.com/color.html).
 We need to add it to the ```<div class="nav-wrapper">```.
 
 
6. Now we finished the header, let's create the wrapper of the page content:

```
<div class="row">
      <div class="col s12 m6"></div>
</div>
```
      

7. Now we add the items that will represent the submitted links. To follow the Material Design guidelines, the [card UI element](http://materializecss.com/cards.html) is the best fit for this need. We have to add them inside ```<div class="col s12 m6"></div>```:

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

