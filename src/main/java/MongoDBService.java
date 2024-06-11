import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

public class MongoDBService {
    private static final String CONNECTION_STRING = "mongodb://localhost:27017";
    private static final String DATABASE_NAME = "grammarTutor";
    private static final String COLLECTION_NAME = "interactions";

    private MongoCollection<Document> collection;

    public MongoDBService() {
        MongoClient mongoClient = MongoClients.create(CONNECTION_STRING);
        MongoDatabase database = mongoClient.getDatabase(DATABASE_NAME);
        this.collection = database.getCollection(COLLECTION_NAME);
    }

    public void saveInteraction(Interaction interaction) {
        Document doc = new Document("question", interaction.getQuestion())
                .append("answer", interaction.getAnswer())
                .append("timestamp", interaction.getTimestamp());
        collection.insertOne(doc);
    }

    public void getAllInteractions() {
        // Implement method to retrieve and display all interactions
    }



}
