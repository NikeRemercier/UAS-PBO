package UAS_PBO_NEW.dao;

import UAS_PBO_NEW.model.Kelas;
import java.sql.*;
import java.util.List;

import java.util.ArrayList;

public class KelasDAO {
    private Connection connection;

    public KelasDAO(Connection connection) {
        this.connection = connection;
    }
    
    // Metode untuk mengambil semua data kelas dari database
    public List<Kelas> getAllKelass() throws SQLException {
        List<Kelas> kelasList = new ArrayList<>();
        String query = "SELECT * FROM kelas";

        try (Statement stmt = connection.createStatement();
                ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                int Id_Kelas = rs.getInt("Id_Kelas");
                String Kelas = rs.getString("Kelas");
                int Tingkatan = rs.getInt("Tingkatan");

                // Konstruktor User haruss mendukung parameter nama
                Kelas kelas = new Kelas(Id_Kelas, Kelas, Tingkatan);
                kelasList.add(kelas);
            }
        }

        return kelasList;
    }

    public int getNextAvailableId() throws SQLException {
        List<Integer> existingIds = new ArrayList<>();
        String query = "SELECT Id_Kelas FROM kelas ORDER BY Id_Kelas";
    
        // Ambil semua ID yang ada dari database
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
    
            while (rs.next()) {
                existingIds.add(rs.getInt("Id_Kelas"));
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
    public boolean addKelas(Kelas kelas) throws SQLException {
        int nextId = getNextAvailableId(); // Dapatkan ID berikutnya menggunakan perulangan
    
        String query = "INSERT INTO kelas (Id_Kelas, Kelas, Tingkatan) VALUES (?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, nextId); // Tetapkan ID secara manual
            statement.setString(2, kelas.getKelas());
            statement.setInt(3, kelas.getTingkatan());
            return statement.executeUpdate() > 0;
        }
    }
    

    // Metode untuk mengambil user berdasarkan ID
    public Kelas getKelasById(int Id_Kelas) throws SQLException {
        String query = "SELECT * FROM kelas WHERE Id_Kelas = ?";
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setInt(1, Id_Kelas);
        ResultSet rs = statement.executeQuery();
        if (rs.next()) {
            return new Kelas(
                rs.getInt("Id_Kelas"),
                rs.getString("Kelas"),
                rs.getInt("Tingkatan")
            );
        }
        return null;
    }

    // Method to update a user's details
    public boolean updateKelas(Kelas kelas) throws SQLException {
        String query = "UPDATE kelas SET Kelas = ?, Tingkatan = ? WHERE Id_Kelas = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, kelas.getKelas());
            statement.setInt(2, kelas.getTingkatan());
            statement.setInt(3, kelas.getIdKelas());
            return statement.executeUpdate() > 0;
        }
    }

    // Method to delete a user by their ID
    public boolean deleteKelas(int Id_Kelas) throws SQLException {
        String query = "DELETE FROM kelas WHERE Id_Kelas = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, Id_Kelas);
            return statement.executeUpdate() > 0;
        }
    }
}
