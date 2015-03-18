var localDB = new PouchDB('growthhackers');

var remoteDB = new PouchDB('http://localhost:4984/growthhackers');

localDB.sync(remoteDB, {
    live: true
}).on('change', function(change) {

}).on('error', function(err) {
    console.log(err);
});

localDB.allDocs({
    include_docs: true
}).then(function(result) {
    console.log(result)
}).catch(function(err) {
    console.log(err)
});