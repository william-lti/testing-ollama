import org.bson.types.ObjectId;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.chrono.ChronoLocalDateTime;

public class Interaction {
    private ObjectId id;
    private String question;
    private String answer;
    private LocalDateTime timestamp;

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
}
