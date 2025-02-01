package org.dev.Repository;

import com.mongodb.MongoTimeoutException;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import org.bson.BsonDocument;
import org.bson.BsonInt64;
import org.bson.codecs.configuration.CodecProvider;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.bson.conversions.Bson;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.dev.entity.redditChildrenData;
import static com.mongodb.MongoClientSettings.getDefaultCodecRegistry;
import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;


@ApplicationScoped
public class RedditRepository {
    private MongoCollection<redditChildrenData> redditCollection;
    private static final String COLLECTION_NAME = "reddit";
    private MongoClient mongoClient;

    @ConfigProperty(name="quarkus.mongodb.connection-string")
    private String uri;

    public void init(@Observes StartupEvent ev){
        System.out.println("Connecting to MongoClient");
        System.out.println("Initialising CodecProvider and CodecRegistry");
        CodecProvider pojoCodecProvider = PojoCodecProvider.builder().automatic(true).build();
        CodecRegistry pojoCodecRegistry = fromRegistries(getDefaultCodecRegistry(), fromProviders(pojoCodecProvider));
        MongoClient mongoClient = MongoClients.create(uri);
        MongoDatabase database = mongoClient.getDatabase("reddit").withCodecRegistry(pojoCodecRegistry);
        this.mongoClient=mongoClient;
        this.redditCollection=database.getCollection("reddit", redditChildrenData.class);
        if(!isConnected(database)){
            System.out.println("Failed to connect to database");
        }
        System.out.println("Connected to database");
    }
    static private boolean isConnected(MongoDatabase database) {
        Bson command = new BsonDocument("ping", new BsonInt64(1));
        try {
            database.runCommand(command);
        } catch (MongoTimeoutException e) {
            return false;
        }
        return true;
    }




    //add reddit post
    public void addRedditPost(redditChildrenData redditPost){
        redditCollection.insertOne(redditPost);
    }
}
