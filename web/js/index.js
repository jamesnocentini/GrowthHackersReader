var localDB = new PouchDB('growthhackers');

var remoteDB = new PouchDB('http://178.62.81.153:4984/growthhackers');

localDB.sync(remoteDB, {
    live: true
}).on('change', function(change) {

}).on('error', function(err) {
    console.log(err);
});

localDB.allDocs({
    include_docs: true
}).then(function(result) {
    render(result.rows);
}).catch(function(err) {
    console.log(err)
});

function render (articles) {
  var content = '';

  for (var i = 0; i < articles.length; i++) {
      var article = articles[i].doc;
      
      var title = '<span class="card-title blue-text text-darken-2">' + article.title + '</span>';
      
      var summary = '<p>' + article.summary + '</p>';
      
      
      var card = '<div class="card"><div class="card-content grey-text text-darken-4">' + title + summary + '</div></div>';
      
      content += card;
  }

  $('#main-content').html(content);
}