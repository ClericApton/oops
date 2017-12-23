package cybersecuritybase.courseproject1.database;

import cybersecuritybase.courseproject1.domain.Message;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class MessageDao {
    
    public Connection connect() {
        String url = "jdbc:sqlite:db/passwords.sqlite";
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(url);
        } catch (SQLException e) {
            System.out.println("Exception: " + e.getMessage());
        }
        return conn;
    }
    
    public void save(Message message) {
        String query = "INSERT INTO message (content) VALUES (?)";
        try (Connection conn = this.connect()) {
            PreparedStatement pstmt = conn.prepareStatement(query);
            
            pstmt.setString(1, message.getContent());
            pstmt.executeUpdate();
            pstmt.close();
        } catch (SQLException e) {
            System.out.println("Exception: " + e.getMessage());
        }
    }
    
    public List<Message> findAll() {
        String query = "SELECT * FROM message ORDER BY datetime(timestamp)";

        List<Message> messages = new ArrayList<>();
        ResultSet rs = null;
        
        try (Connection conn = this.connect()) {
            Statement stmt = conn.createStatement();
            rs = stmt.executeQuery(query);
            while(rs.next()) {
                Integer id = rs.getInt("id");
                Timestamp timestamp = Timestamp.valueOf(rs.getString("timestamp"));
                String content = rs.getString("content");
                Message message = new Message(id, timestamp, content);
                messages.add(message);
            }
            rs.close(); 
            stmt.close();
        } catch (SQLException e) {
            System.out.println("Exception: " + e.getMessage());
        }

        return messages;    
    }
    
    public Message findLatest() {
        String query = "SELECT * FROM message ORDER BY datetime(timestamp) DESC LIMIT 1";

        ResultSet rs = null;
        Message message = null;
        try (Connection conn = this.connect()) {
            Statement stmt = conn.createStatement();
            rs = stmt.executeQuery(query);
            while(rs.next()) {
                Integer id = rs.getInt("id");
                Timestamp timestamp = Timestamp.valueOf(rs.getString("timestamp"));
                String content = rs.getString("content");
                message = new Message(id, timestamp, content);
            }
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            System.out.println("Exception: " + e.getMessage());
        }

        return message;    
    }
}
