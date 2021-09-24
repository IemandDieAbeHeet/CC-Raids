package com.abevriens;

import static com.mongodb.client.model.Aggregates.limit;
import static com.mongodb.client.model.Filters.eq;
import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.MongoException;
import org.bson.Document;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.bson.conversions.Bson;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Projections;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class MongoDBHandler {
    private MongoClient mongoClient;
    private MongoDatabase mongoDatabase;
    private MongoCollection<Faction> factionCollection;

    public boolean connect(String _connectionUri) {
        ConnectionString connectionString = new ConnectionString(_connectionUri);

        CodecRegistry pojoCodecRegistry = fromProviders(PojoCodecProvider.builder().automatic(true).build());

        CodecRegistry codecRegistry = fromRegistries(MongoClientSettings.getDefaultCodecRegistry(), pojoCodecRegistry);

        MongoClientSettings clientSettings = MongoClientSettings.builder()
                .applyConnectionString(connectionString)
                .codecRegistry(codecRegistry)
                .build();

        try {
            mongoClient = MongoClients.create(clientSettings);
            mongoDatabase = mongoClient.getDatabase("CockCityData");
            factionCollection = mongoDatabase.getCollection("factions", Faction.class);
            CockCityRaids.instance.getLogger().info(ChatColor.GREEN + "Connected to MongoDB");
            return true;
        } catch(MongoException e) {
            CockCityRaids.instance.getLogger().info(ChatColor.RED + e.getMessage());
            return false;
        }
    }

    public void disconnect() {
        mongoClient.close();
    }

    public void insertFaction(Faction faction) {
        factionCollection.insertOne(faction);
    }

    public Faction findFaction(String _factionName) {
        return factionCollection.find(eq("factionName", _factionName)).first();
    }
}
