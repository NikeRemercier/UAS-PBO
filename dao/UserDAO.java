package UAS_PBO_NEW.dao;

import UAS_PBO_NEW.model.User;
import java.sql.*;
import java.util.List;

import java.util.ArrayList;

public class UserDAO {
    private Connection connection;

    public UserDAO(Connection connection) {
        this.connection = connection;
    }
    
    // Metode untuk mengambil semua data user dari database
    public List<User> getAllUsers() throws SQLException {
        List<User> userList = new ArrayList<>();
        String query = "SELECT * FROM user";

        try (Statement stmt = connection.createStatement();
                ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                int id_user = rs.getInt("id_user");
                String username = rs.getString("username");
                String password = rs.getString("password");
                String role = rs.getString("role");

                // Konstruktor User harus mendukung parameter nama
                User user = new User(id_user, username, password, role);
                userList.add(user);
            }
        }

        return userList;
    }

    public int getNextAvailableId() throws SQLException {
        List<Integer> existingIds = new ArrayList<>();
        String query = "SELECT id_user FROM user ORDER BY id_user";
    
        // Ambil semua ID yang ada dari database
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
    
            while (rs.next()) {
                existingIds.add(rs.getInt("id_user"));
            }
        }
    
        // Cari ID terkecil yang belum digunakan
        int nextId = 1; // Mulai dari 1
        for (int id : existingIds) {
            if (id == nextId) {
                nextId++; // Jika ID ada, naik ke angka berikutnya
            } else {
                break; // Jika ada celah, gunakan angka tersebut
            }
        }
    
        return nextId;
    }
    
    
    // Method to add a new user to the database
    public boolean addUser(User user) throws SQLException {
        int nextId = getNextAvailableId(); // Dapatkan ID berikutnya menggunakan perulangan
    
        String query = "INSERT INTO user (id_user, username, password, role) VALUES (?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, nextId); // Tetapkan ID secara manual
            statement.setString(2, user.getUsername());
            statement.setString(3, user.getPassword());
            statement.setString(4, user.getRole());
            return statement.executeUpdate() > 0;
        }
    }
    

    // Metode untuk mengambil user berdasarkan ID
    public User getUserById(int id_user) throws SQLException {
        String query = "SELECT * FROM user WHERE id_user = ?";
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setInt(1, id_user);
        ResultSet rs = statement.executeQuery();
        if (rs.next()) {
            return new User(
                rs.getInt("id_user"),
                rs.getString("username"),
                rs.getString("password"),
                rs.getString("role")
            );
        }
        return null;
    }

    // Method to update a user's details
    public boolean updateUser(User user) throws SQLException {
        String query = "UPDATE user SET username = ?, password = ?, role = ? WHERE id_user = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, user.getUsername());
            statement.setString(2, user.getPassword());
            statement.setString(3, user.getRole());
            statement.setInt(4, user.getIdUser());
            return statement.executeUpdate() > 0;
        }
    }

    // Method to delete a user by their ID
    public boolean deleteUser(int id_user) throws SQLException {
        String query = "DELETE FROM user WHERE id_user = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, id_user);
            return statement.executeUpdate() > 0;
        }
    }
}
