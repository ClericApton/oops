package cybersecuritybase.courseproject1.domain;

import java.sql.Timestamp;

public class Message {
    private int id;
    private Timestamp timestamp;
    private String content;
    
    public Message(String content) {
        this.content = content;
    }

    public Message(int id, Timestamp timestamp, String content) {
        this.id = id;
        this.timestamp = timestamp;
        this.content = content;
    }
        
    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Timestamp getTimestamp() {
        return this.timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public String getContent() {
        return this.content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
