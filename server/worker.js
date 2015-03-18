var request = require('request')
  , config = require('config');

var url = 'https://api.import.io/store/data/2bd31dbf-20cd-437a-bcc7-8fbc4524e8f3/_query?input/webpage/url=https%3A%2F%2Fgrowthhackers.com%2F&_user=c5b82e89-e0b8-41f9-bdd5-f13f5925b0bb&_apikey=' + config.get('IMPORT_IO_API_KEY');

request({url: url}, function(error, response, body) {
    if (!error && response.statusCode == 200) {
        var articles = JSON.parse(body);



        for (var i = 5; i < 10; i++) {

            var article = articles["results"][i];

            var obj = {};
            obj.title = article["link_2/_text"];
            obj.topic = article["topics_link/_text"];
            obj.thumbnail = article["right_image"];
            obj.author = article["link_4/_text"];
            obj.summary = article["text_list_3"];

              request.post('http://localhost:4985/growthhackers/', {body: JSON.stringify(obj)}, function(error, response, body) {
                console.log(body);
            });
        }

    }
});