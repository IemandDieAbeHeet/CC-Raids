package com.abevriens;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.MongoException;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.ReplaceOptions;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.bukkit.ChatColor;

import static com.mongodb.client.model.Filters.eq;
import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

public class MongoDBHandler {
    private MongoClient mongoClient;
    public MongoCollection<POJO_Player> playerCollection;
    public MongoCollection<POJO_Faction> factionCollection;

    public void connect(String _connectionUri) {
        ConnectionString connectionString = new ConnectionString(_connectionUri);

        CodecRegistry pojoCodecRegistry = fromProviders(PojoCodecProvider.builder().automatic(true).build());

        CodecRegistry codecRegistry = fromRegistries(MongoClientSettings.getDefaultCodecRegistry(), pojoCodecRegistry);

        MongoClientSettings clientSettings = MongoClientSettings.builder()
                .applyConnectionString(connectionString)
                .codecRegistry(codecRegistry)
                .build();

        try {
            mongoClient = MongoClients.create(clientSettings);
            MongoDatabase mongoDatabase = mongoClient.getDatabase("CrackCityData");
            playerCollection = mongoDatabase.getCollection("players", POJO_Player.class);
            factionCollection = mongoDatabase.getCollection("factions", POJO_Faction.class);
            CrackCityRaids.instance.getLogger().info(ChatColor.GREEN + "Connected to MongoDB");
        } catch(MongoException e) {
            CrackCityRaids.instance.getLogger().info(ChatColor.RED + e.getMessage());
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

    public void deleteFaction(String factionName) { factionCollection.deleteOne(eq("factionName", factionName)); }

    public void insertFaction(POJO_Faction POJOFaction) {
        factionCollection.insertOne(POJOFaction);
    }

    public void updateFaction(POJO_Faction POJOFaction) {
        ReplaceOptions opts = new ReplaceOptions().upsert(true);
        factionCollection.replaceOne(eq("factionName", POJOFaction.factionName), POJOFaction, opts);
    }
}
