var localDB = new PouchDB('growthhackers');

var remoteDB = new PouchDB('http://178.62.81.153:4984/growthhackers');

localDB.sync(remoteDB, {
    live: true
}).on('change', function(change) {
  fetch();
}).on('error', function(err) {
    console.log(err);
});

function fetch() {
  localDB.allDocs({
      include_docs: true
  }).then(function(result) {
      render(result.rows);
  }).catch(function(err) {
      console.log(err)
  });
}

function render (articles) {
  var pageContent = '';

  for (var i = 0; i < articles.length; i++) {
      var article = articles[i].doc;
      
      var title = '<span class="card-title blue-text text-darken-2 truncate">' + article.title + '</span>';
      
      var summary = '<p>' + article.summary + '</p>';
      
      var cardContent = '<div class="card-content grey-text text-darken-4">' + title + summary + '</div>';

      var like = '<i class="mdi-action-favorite-outline" id="' + article._id + '"></i> ' + article.likes;

      var topic = '<span class="right-align badge blue darken-4 blue-grey-text text-lighten-5">' + article.topic + '</span>';

      var cardFooter = '<div class="card-action">' + like + topic + '</div>';

      var card = '<div class="card">' + cardContent + cardFooter + '</div>';
      
      pageContent += card;
  }

  $('#main-content').html(pageContent);
}

$('#main-content').on('click', '.mdi-action-favorite-outline', function click () {
  $(this).toggleClass('mdi-action-favorite-outline mdi-action-favorite red-text');

  var articleId = $(this).attr('id');

  localDB.get(articleId).then(function(doc) {
    doc.likes++;
    localDB.put(doc);
  });
});

fetch();