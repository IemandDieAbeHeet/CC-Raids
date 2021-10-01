package com.abevriens;

import static com.mongodb.client.model.Filters.eq;
import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.MongoException;
import com.mongodb.client.model.FindOneAndReplaceOptions;
import com.mongodb.client.model.ReplaceOptions;
import com.mongodb.client.model.ReturnDocument;
import org.bson.Document;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import org.bson.conversions.Bson;
import org.bukkit.ChatColor;

public class MongoDBHandler {
    private MongoClient mongoClient;
    private MongoDatabase mongoDatabase;
    public MongoCollection<POJO_Player> playerCollection;
    public MongoCollection<POJO_Faction> factionCollection;

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
            playerCollection = mongoDatabase.getCollection("players", POJO_Player.class);
            factionCollection = mongoDatabase.getCollection("factions", POJO_Faction.class);
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

    public void insertPlayer(POJO_Player POJOPlayer) { playerCollection.insertOne(POJOPlayer); }

    public void updatePlayer(POJO_Player POJOPlayer) {
        ReplaceOptions opts = new ReplaceOptions().upsert(true);
        playerCollection.replaceOne(eq("uuid", POJOPlayer.uuid), POJOPlayer, opts);
    }

    public void insertFaction(POJO_Faction POJOFaction) {
        factionCollection.insertOne(POJOFaction);
    }
}
