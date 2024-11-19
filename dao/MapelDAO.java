package UAS_PBO_NEW.dao;

import UAS_PBO_NEW.model.Mapel;
import java.sql.*;
import java.util.List;

import java.util.ArrayList;

public class MapelDAO {
    private Connection connection;

    public MapelDAO(Connection connection) {
        this.connection = connection;
    }
    
    // Metode untuk mengambil semua data user dari database
    public List<Mapel> getAllMapels() throws SQLException {
        List<Mapel> mapelList = new ArrayList<>();
        String query = "SELECT * FROM mapel";

        try (Statement stmt = connection.createStatement();
                ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                int Kd_Mapel = rs.getInt("Kd_Mapel");
                String Mapel = rs.getString("Mapel");
            
                // Konstruktor User haruss mendukung parameter nama
                Mapel mapel = new Mapel(Kd_Mapel, Mapel);
                mapelList.add(mapel);
            }
        }

        return mapelList;
    }

    public int getNextAvailableId() throws SQLException {
        List<Integer> existingIds = new ArrayList<>();
        String query = "SELECT Kd_Mapel FROM mapel ORDER BY Kd_Mapel";
    
        // Ambil semua ID yang ada dari database
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
    
            while (rs.next()) {
                existingIds.add(rs.getInt("Kd_Mapel"));
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
    public boolean addMapel(Mapel mapel) throws SQLException {
        int nextId = getNextAvailableId(); // Dapatkan ID berikutnya menggunakan perulangan
    
        String query = "INSERT INTO mapel (Kd_Mapel, Mapel) VALUES (?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, nextId); // Tetapkan ID secara manual
            statement.setString(2, mapel.getMapel());
            return statement.executeUpdate() > 0;
        }
    }
    

    // Metode untuk mengambil user berdasarkan ID
    public Mapel getMapelById(int Kd_Mapel) throws SQLException {
        String query = "SELECT * FROM mapel WHERE Kd_Mapel = ?";
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setInt(1, Kd_Mapel);
        ResultSet rs = statement.executeQuery();
        if (rs.next()) {
            return new Mapel(
                rs.getInt("Kd_Mapel"),
                rs.getString("Mapel")
            );
        }
        return null;
    }

    // Method to update a user's details
    public boolean updateMapel(Mapel mapel) throws SQLException {
        String query = "UPDATE mapel SET Mapel = ? WHERE Kd_Mapel = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, mapel.getMapel());
            statement.setInt(2, mapel.getKdMapel());
            return statement.executeUpdate() > 0;
        }
    }

    // Method to delete a user by their ID
    public boolean deleteMapel(int Kd_Mapel) throws SQLException {
        String query = "DELETE FROM mapel WHERE Kd_Mapel = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, Kd_Mapel);
            return statement.executeUpdate() > 0;
        }
    }
}
