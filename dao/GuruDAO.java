package UAS_PBO_NEW.dao;

import UAS_PBO_NEW.model.Guru;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class GuruDAO {
    private Connection connection;

    public GuruDAO(Connection connection) {
        this.connection = connection;
    }

    // Periksa apakah NIP sudah ada di tabel Guru
    public boolean isNIPExists(String nip) throws SQLException {
        String query = "SELECT COUNT(*) FROM Guru WHERE NIP = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, nip);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0; // Jika lebih dari 0, berarti NIP sudah ada
            }
        }
        return false;
    }

    // Periksa apakah Username sudah ada di tabel User
    public boolean isUsernameExists(String username) throws SQLException {
        String query = "SELECT COUNT(*) FROM User WHERE username = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0; // Jika lebih dari 0, berarti Username sudah ada
            }
        }
        return false;
    }

    public int getNextAvailableUserId() throws SQLException {
        List<Integer> existingIds = new ArrayList<>();
        String query = "SELECT id_user FROM User ORDER BY id_user";
    
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

    public int getNextAvailableGuruId() throws SQLException {
        List<Integer> existingIds = new ArrayList<>();
        String query = "SELECT id_guru FROM Guru ORDER BY id_guru";
    
        // Ambil semua ID guru yang ada dari database
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
    
            while (rs.next()) {
                existingIds.add(rs.getInt("id_guru"));
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
    

    public int addUser(String username, String password, String role) throws SQLException {
        int newId = getNextAvailableUserId(); // ID baru adalah ID terkecil yang belum digunakan
        String query = "INSERT INTO User (id_user, username, password, role) VALUES (?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, newId);
            pstmt.setString(2, username);
            pstmt.setString(3, password);
            pstmt.setString(4, role); // Tambahkan role
            pstmt.executeUpdate();
            return newId; // Kembalikan ID yang baru ditambahkan
        } catch (SQLException e) {
            e.printStackTrace();
            return -1; // Jika gagal, kembalikan -1
        }
    }    

// Metode untuk mengambil semua data guru dari database (termasuk username dan password dari tabel user)
public List<Guru> getAllGurus() throws SQLException {
    List<Guru> guruList = new ArrayList<>();
    String query = "SELECT g.id_guru, g.NIP, g.Nama, g.id_user, u.username, u.password " +
                   "FROM guru g " +
                   "JOIN user u ON g.id_user = u.id_user " +
                   "ORDER BY g.id_guru ASC"; // Urutkan berdasarkan id_guru secara menaik
    try (Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(query)) {

        while (rs.next()) {
            int id_guru = rs.getInt("id_guru");
            String NIP = rs.getString("NIP");
            String Nama = rs.getString("Nama");
            int id_user = rs.getInt("id_user");
            String username = rs.getString("username");
            String password = rs.getString("password");

            // Buat objek Guru menggunakan data dari database
            Guru guru = new Guru(id_guru, NIP, Nama, id_user, username, password);
            guruList.add(guru);
        }
    }
    return guruList;
}

    public boolean addGuru(Guru guru) throws SQLException {
        // Mulai transaksi
        connection.setAutoCommit(false); // Matikan auto-commit
    
        try {
            // Dapatkan id_guru yang baru
            int id_guru = getNextAvailableGuruId();
    
            // Tambahkan user dengan role "guru"
            int id_user = addUser(guru.getUsername(), guru.getPassword(), "guru");
            if (id_user == -1) {
                connection.rollback(); // Jika gagal menambahkan user, rollback transaksi
                return false;
            }
    
            // Jika user berhasil ditambahkan, tambahkan data guru ke tabel guru
            String query = "INSERT INTO guru (id_guru, NIP, Nama, id_user) VALUES (?, ?, ?, ?)";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setInt(1, id_guru); // Gunakan id_guru yang baru saja dibuat
                statement.setString(2, guru.getNIP());
                statement.setString(3, guru.getNama());
                statement.setInt(4, id_user); // Gunakan id_user yang baru saja dibuat
                int rowsAffected = statement.executeUpdate();
                if (rowsAffected > 0) {
                    // Jika berhasil, commit transaksi
                    connection.commit();
                    return true;
                } else {
                    connection.rollback(); // Jika gagal, rollback transaksi
                    return false;
                }
            }
        } catch (SQLException e) {
            // Tangani jika ada error dan rollback transaksi
            e.printStackTrace();
            connection.rollback();
            return false;
        } finally {
            // Kembalikan auto-commit ke true setelah transaksi selesai
            connection.setAutoCommit(true);
        }
    }
    
    public boolean updateGuru(Guru guru) throws SQLException {
        connection.setAutoCommit(false); // Memulai transaksi
    
        try {
            // Update tabel Guru
            String queryGuru = "UPDATE Guru SET NIP = ?, Nama = ? WHERE id_guru = ?";
            try (PreparedStatement psGuru = connection.prepareStatement(queryGuru)) {
                psGuru.setString(1, guru.getNIP());
                psGuru.setString(2, guru.getNama());
                psGuru.setInt(3, guru.getidguru());
                int rowsGuru = psGuru.executeUpdate();
                if (rowsGuru == 0) {
                    connection.rollback();
                    return false;
                }
            }
    
            // Update tabel User
            String queryUser = "UPDATE User SET username = ?, password = ? WHERE id_user = ?";
            try (PreparedStatement psUser = connection.prepareStatement(queryUser)) {
                psUser.setString(1, guru.getUsername());
                psUser.setString(2, guru.getPassword());
                psUser.setInt(3, guru.getiduser());
                int rowsUser = psUser.executeUpdate();
                if (rowsUser == 0) {
                    connection.rollback();
                    return false;
                }
            }
    
            connection.commit(); // Commit jika semua berhasil
            return true;
    
        } catch (SQLException e) {
            e.printStackTrace();
            connection.rollback(); // Rollback jika ada kesalahan
            return false;
    
        } finally {
            connection.setAutoCommit(true); // Kembalikan auto-commit
        }
    }
    
        
    public boolean deleteGuru(String nip) throws SQLException {
        // Mulai transaksi
        connection.setAutoCommit(false);
    
        try {
            // Ambil id_user terkait dari NIP
            String getIdUserQuery = "SELECT id_user FROM Guru WHERE NIP = ?";
            int idUser = -1;
    
            try (PreparedStatement stmt = connection.prepareStatement(getIdUserQuery)) {
                stmt.setString(1, nip);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    idUser = rs.getInt("id_user");
                } else {
                    connection.rollback(); // Rollback jika NIP tidak ditemukan
                    return false;
                }
            }
    
            // Hapus data guru
            String deleteGuruQuery = "DELETE FROM Guru WHERE NIP = ?";
            try (PreparedStatement stmt = connection.prepareStatement(deleteGuruQuery)) {
                stmt.setString(1, nip);
                int guruRowsDeleted = stmt.executeUpdate();
                if (guruRowsDeleted == 0) {
                    connection.rollback(); // Rollback jika tidak ada baris dihapus
                    return false;
                }
            }
    
            // Hapus data user yang terkait
            String deleteUserQuery = "DELETE FROM User WHERE id_user = ?";
            try (PreparedStatement stmt = connection.prepareStatement(deleteUserQuery)) {
                stmt.setInt(1, idUser);
                int userRowsDeleted = stmt.executeUpdate();
                if (userRowsDeleted == 0) {
                    connection.rollback(); // Rollback jika tidak ada baris dihapus di User
                    return false;
                }
            }
    
            // Commit transaksi jika semua operasi berhasil
            connection.commit();
            return true;
    
        } catch (SQLException e) {
            e.printStackTrace();
            connection.rollback(); // Rollback transaksi jika ada error
            return false;
    
        } finally {
            connection.setAutoCommit(true); // Kembalikan auto-commit ke true
        }
    }    
}
