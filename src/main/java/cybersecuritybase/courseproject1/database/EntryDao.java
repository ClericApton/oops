package cybersecuritybase.courseproject1.database;

import cybersecuritybase.courseproject1.domain.Entry;
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
public class EntryDao {
    
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
    
    public void save(Entry entry) {
        String query = "INSERT INTO entry (user, service, username, password) VALUES (?, ?, ?, ?)";
        try (Connection conn = this.connect()) {
            PreparedStatement pstmt = conn.prepareStatement(query);
            
            pstmt.setString(1, entry.getUser());
            pstmt.setString(2, entry.getService());
            pstmt.setString(3, entry.getUsername());
            pstmt.setString(4, entry.getPassword());
            pstmt.executeUpdate();
            pstmt.close();
        } catch (SQLException e) {
            System.out.println("Exception: " + e.getMessage());
        }
    }
    
    public List<Entry> findBySearchString(String givenUser, String search) {
        String query = "SELECT * FROM entry WHERE user = '" + givenUser + "' AND service LIKE '%" + search + "%' ORDER BY service";
        // SQL injection: ' OR 1=1 OR '
        List<Entry> entries = new ArrayList<>();
        ResultSet rs = null;
        
        try (Connection conn = this.connect()) {
            Statement stmt = conn.createStatement();
            rs = stmt.executeQuery(query);
            while(rs.next()) {
                Integer id = rs.getInt("id");
                String user = rs.getString("user");
                String username = rs.getString("username");
                String service = rs.getString("service");
                String password = rs.getString("password");
                Entry entry = new Entry(id, user, service, username, password);
                entries.add(entry);
            }
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            System.out.println("Exception: " + e.getMessage());
        }

        return entries;    
    }
    
    public void deleteAll(String user) {
        String query = "DELETE FROM entry WHERE user = ?";
        try (Connection conn = this.connect()) {
            PreparedStatement pstmt = conn.prepareStatement(query);
            
            pstmt.setString(1, user);
            pstmt.executeUpdate();
            pstmt.close();
        } catch (SQLException e) {
            System.out.println("Exception: " + e.getMessage());
        }        
    }
}
