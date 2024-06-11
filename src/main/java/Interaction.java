import org.bson.types.ObjectId;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.chrono.ChronoLocalDateTime;
import java.util.List;

public class Interaction {
    private ObjectId id;
    private String question;
    private String answer;
    private LocalDateTime timestamp;
    private List<String> tags;

    public Interaction(String question, String answer){
        this.question = question;
        this.answer = answer;
        this.timestamp = LocalDateTime.now();
    }
    public Interaction(){
        this.question = "";
        this.answer = "answer";
        this.timestamp = LocalDateTime.now();
    }

    public Interaction(String question, String answer, List<String> tags) {
        this.question = question;
        this.answer = answer;
        this.tags = tags;
    }

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }
}
