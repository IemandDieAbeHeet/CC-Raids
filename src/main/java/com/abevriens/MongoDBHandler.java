package com.abevriens;

import static com.mongodb.client.model.Filters.eq;

import org.bson.Document;
import org.bson.conversions.Bson;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Projections;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class MongoDBHandler {

    MongoCollection<Document> collection;
    String poep;

    public boolean connect(String connectionUri) {
        try(MongoClient mongoClient = MongoClients.create(connectionUri)) {
            MongoDatabase database = mongoClient.getDatabase("CockCityData");
            collection = database.getCollection("cockcityraids");

            poep = test();
            return true;
        }
    }

    public String findPlayer(int _factionId) {
        Bson projectionFields = Projections.fields(
                Projections.include("lol", "poep"),
                Projections.excludeId());
        Document doc = collection.find(eq("factionId", _factionId))
                .projection(projectionFields)
                .first();

        return doc.toJson();
    }
}
