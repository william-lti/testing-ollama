import com.mongodb.client.*;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import org.bson.Document;

import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

public class MongoDBService {
    private static final String CONNECTION_STRING = "mongodb://localhost:27017";
    private static final String DATABASE_NAME = "grammarTutor";
    private static final String COLLECTION_NAME = "interactions";

    private MongoCollection<Document> collection;

    public MongoDBService() {
        MongoClient mongoClient = MongoClients.create(CONNECTION_STRING); //MongoClients is a utility class that provides static factory methods for creating MongoClient instances.
        MongoDatabase database = mongoClient.getDatabase(DATABASE_NAME);
        this.collection = database.getCollection(COLLECTION_NAME);
    }

    public void saveInteraction(Interaction interaction) {
        Document doc = new Document("question", interaction.getQuestion())
                .append("answer", interaction.getAnswer())
                .append("timestamp", interaction.getTimestamp());
        collection.insertOne(doc);
    }

    public List<Interaction> getAllInteractions() {
        List<Interaction> interactions = new ArrayList<>();
        FindIterable<Document> documents = collection.find();

        //Mapping Documents to Interaction objects
        for(Document doc : documents){
            Interaction interaction = new Interaction();
            interaction.setId(doc.getObjectId("_id"));
            interaction.setQuestion(doc.getString("question"));
            interaction.setAnswer(doc.getString("answer"));
            interaction.setTimestamp(doc.getDate("timestamp").toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDateTime());
        }

        return interactions;
    }
    public List<Interaction> getAllInteractionsByQuestion(String search) {
        List<Interaction> interactions = new ArrayList<>();
        String regexQuery = ".*" + search.toLowerCase() + ".*";
        Document query = new Document("question", new Document("$regex", regexQuery).append("$options", "i")); //Using $options field set to i for case-insensitive matching.
        FindIterable<Document> documents = collection.find(query);

        for (Document doc : documents) {
            Interaction interaction = new Interaction();
            interaction.setId(doc.getObjectId("_id"));
            interaction.setQuestion(doc.getString("question"));
            interaction.setAnswer(doc.getString("answer"));
            interaction.setTimestamp(doc.getDate("timestamp").toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDateTime());
            interactions.add(interaction);
        }

        return interactions;

    }
    public void updateInteractionWithTags(String question, List<String> tags) {
        collection.updateOne(Filters.eq("question", question), Updates.set("tags", tags));
    }

}
