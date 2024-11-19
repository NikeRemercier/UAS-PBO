package UAS_PBO_NEW.model;

public class User {
    private int id_user; 
    private String username; 
    private String password; 
    private String role; 

    public User(int id_user, String username, String password, String role) {
        this.id_user = id_user;
        this.username = username;
        this.password = password;
        this.role = role;
    }

    // Getters
    public int getIdUser() { return id_user; }
    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public String getRole() { return role; }

    // Setters
    public void setIdUser(int id_user) { this.id_user = id_user; }
    public void setUsername(String username) { this.username = username; }
    public void setPassword(String password) { this.password = password; }
    public void setRole(String role) { this.role = role; }
}
