package com.example.xspace;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.bson.conversions.Bson;
import com.mongodb.client.model.Filters;

public class LoginDB {

    private static final String DATABASE_NAME = "UserDatabase";
    private static final String COLLECTION_NAME = "UserCollection";

    private MongoClient mongoClient;
    private MongoDatabase database;
    private MongoCollection<Document> collection;

    public LoginDB(Login login) {
        mongoClient = MongoClients.create("mongodb+srv://<username>:<password>@cluster0.mongodb.net/?retryWrites=true&w=majority");
        database = mongoClient.getDatabase(DATABASE_NAME);
        collection = database.getCollection(COLLECTION_NAME);
    }

    public boolean insertData(String username, String password, String email) {
        Document document = new Document("USERNAME", username)
                .append("PASSWORD", password)
                .append("EMAIL", email);
        collection.insertOne(document);
        return true; // insertion is successful if no exception is thrown
    }

    public boolean checkEmailExists(String email) {
        Bson filter = Filters.eq("EMAIL", email);
        long count = collection.countDocuments(filter);
        return count > 0;
    }

    public boolean validateUser(String username, String password) {
        Bson filter = Filters.and(Filters.eq("USERNAME", username), Filters.eq("PASSWORD", password));
        long count = collection.countDocuments(filter);
        return count > 0;
    }

    public void close() {
        mongoClient.close();
    }
}


