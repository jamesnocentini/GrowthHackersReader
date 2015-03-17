package com.workshop.growthhackersreader;

import android.app.Presentation;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Database;
import com.couchbase.lite.Document;
import com.couchbase.lite.Emitter;
import com.couchbase.lite.Mapper;
import com.couchbase.lite.Query;

import java.util.HashMap;
import java.util.Map;

public class Article {

    public static final String VIEW_NAME = "article_view";
    public static final String TYPE = "article";
    private Database database;
    private Document sourceDocument;

    private String title;               // link_2/_text
    private String topic;               // topics_link/_text
    private String thumbnail;           // footer_profile_icons_image_list
    private String author;              // link_3/_text
    private String summary;             // text_list_1 & text_2
    private int likes;

    public Article(Database database) {
        this.database = database;
    }

    public static Query findAllArticle(Database database) {
        com.couchbase.lite.View view = database.getView(VIEW_NAME);
        if (view.getMap() == null) {
            Mapper map = new Mapper() {
                @Override
                public void map(Map<String, Object> document, Emitter emitter) {
                    emitter.emit(document.get("title"), document);
                }
            };
            view.setMap(map, "3");
        }

        Query query = view.createQuery();

        return query;
    }

    public Article() {

    }

    public static void createArticle(Database database, String title, String topic, String thumbnail, String author, String summary) {
        Document doc = database.createDocument();
        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put("title", title);
        properties.put("topic", topic);
        properties.put("thumbnail", thumbnail);
        properties.put("author", author);
        properties.put("summary", summary);
        properties.put("likes", 0);
        try {
            doc.putProperties(properties);
        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
        }
    }

    public static Article from(Document document) {
        Article article = new Article(document.getDatabase());
        article.setTitle((String) document.getProperty("title"));
        article.setTopic((String) document.getProperty("topic"));
        article.setThumbnail((String) document.getProperty("thumbnail"));
        article.setAuthor((String) document.getProperty("author"));
        article.setSummary((String) document.getProperty("summary"));
        if (document.getProperty("likes") != null) {
            article.setLikes((int) document.getProperty("likes"));
        } else {
            article.setLikes(0);
        }
        article.setSourceDocument(document);
        return article;
    }

    public void save() {
        Map<String, Object> properties = new HashMap<String, Object>();
        Document document;
        if (sourceDocument == null) {
            document = database.createDocument();
        } else {
            document = sourceDocument;
            properties.putAll(sourceDocument.getProperties());
        }
        properties.put("likes", likes);
        try {
            document.putProperties(properties);
        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
        }
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public Document getSourceDocument() {
        return sourceDocument;
    }

    public void setSourceDocument(Document sourceDocument) {
        this.sourceDocument = sourceDocument;
    }

    public int getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }
}
