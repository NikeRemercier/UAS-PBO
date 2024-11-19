package UAS_PBO_NEW;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/sistem_akademik?useSSL=false";
    private static final String USER = "root";
    private static final String PASSWORD = ""; // Isi dengan password MySQL Anda jika ada

    public static Connection getConnection() {
        try {
            // Memuat driver JDBC
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("Koneksi ke database berhasil.");
            return conn;
        } catch (ClassNotFoundException e) {
            System.out.println("Driver JDBC tidak ditemukan: " + e.getMessage());
            return null;
        } catch (SQLException e) {
            System.out.println("Koneksi gagal: " + e.getMessage());
            return null;
        }
    }

    // Method main untuk uji coba koneksi
    public static void main(String[] args) {
        Connection conn = getConnection();
        if (conn != null) {
            System.out.println("Tes koneksi: Berhasil terhubung ke database.");
        } else {
            System.out.println("Tes koneksi: Gagal terhubung ke database.");
        }
    }
}
