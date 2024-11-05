package com.example.xspace;

import android.content.Context;

import com.mongodb.client.*;
import com.mongodb.client.model.Filters;
import org.bson.Document;

public class LoginDB {
    private static final String DATABASE_NAME = "UserDatabase";
    private static final String COLLECTION_NAME = "UserCollection";

    private final MongoClient mongoClient;
    private final MongoDatabase database;
    private final MongoCollection<Document> collection;

    public LoginDB(Context context) {
        mongoClient = MongoClients.create("mongodb://localhost:27017"); // replace with your MongoDB URI
        database = mongoClient.getDatabase(DATABASE_NAME);
        collection = database.getCollection(COLLECTION_NAME);
    }

    public void close() {
        mongoClient.close();
    }

    public boolean insertData(String username, String password, String email) {
        Document document = new Document()
                .append("USERNAME", username)
                .append("PASSWORD", password)
                .append("EMAIL", email);
        collection.insertOne(document);
        return true; // assume insertion is successful
    }

    public boolean checkEmailExists(String email) {
        Document doc = collection.find(Filters.eq("EMAIL", email)).first();
        return doc != null; // email exists if doc is not null
    }

    public boolean validateUser(String username, String password) {
        Document doc = collection.find(Filters.and(
                Filters.eq("USERNAME", username),
                Filters.eq("PASSWORD", password)
        )).first();
        return doc != null; // user exists if doc is not null
    }
}
