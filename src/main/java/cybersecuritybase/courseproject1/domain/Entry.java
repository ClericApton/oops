package cybersecuritybase.courseproject1.domain;

public class Entry {
    private int id;
    private String user;
    private String service;
    private String username;
    private String password;
    
    public Entry(String user, String service, String username, String password) {
        this.user = user;
        this.service = service;
        this.username = username;
        this.password = password;
    }
    
    public Entry(int id, String user, String service, String username, String password) {
        this.id = id;
        this.user = user;
        this.service = service;
        this.username = username;
        this.password = password;
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUser() {
        return this.user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getService() {
        return this.service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
