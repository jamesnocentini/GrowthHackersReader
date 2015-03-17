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
 