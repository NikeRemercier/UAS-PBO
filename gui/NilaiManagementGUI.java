package UAS_PBO_NEW.gui;

import UAS_PBO_NEW.DatabaseConnection;
import UAS_PBO_NEW.gui.role.AdminGUI;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class NilaiManagementGUI extends JFrame {
    private DefaultTableModel tableModel;
    private JTable nilaiTable;

    public NilaiManagementGUI() {
        setTitle("Kelola Nilai");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Model tabel
        tableModel = new DefaultTableModel(new Object[] { "No", "Siswa", "Mapel", "Absen", "Tugas", "UTS", "UAS" }, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Tidak dapat diedit langsung
            }
        };

        nilaiTable = new JTable(tableModel);
        // Menambahkan renderer untuk alignment tengah
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);

        // Terapkan renderer ke semua kolom
        for (int i = 0; i < nilaiTable.getColumnCount(); i++) {
            nilaiTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        JScrollPane scrollPane = new JScrollPane(nilaiTable);
        add(scrollPane, BorderLayout.CENTER);

        // Panel tombol
        JPanel buttonPanel = new JPanel(new GridLayout(5, 1, 10, 10));
        JButton tambahButton = new JButton("Tambah Nilai");
        JButton editButton = new JButton("Edit Nilai");
        JButton hapusButton = new JButton("Hapus Nilai");
        JButton refreshButton = new JButton("Refresh");
        JButton kembaliButton = new JButton("Kembali");

        tambahButton.addActionListener(e -> tambahNilai());
        editButton.addActionListener(e -> editNilai());
        hapusButton.addActionListener(e -> hapusNilai());
        refreshButton.addActionListener(e -> refreshNilai());
        kembaliButton.addActionListener(e -> kembali());

        buttonPanel.add(tambahButton);
        buttonPanel.add(editButton);
        buttonPanel.add(hapusButton);
        buttonPanel.add(refreshButton);
        buttonPanel.add(kembaliButton);
        add(buttonPanel, BorderLayout.EAST);

        // Muat data awal
        refreshNilai();
    }

    private int getNextAvailableId() throws SQLException {
        String query = "SELECT id_nilai FROM nilai ORDER BY id_nilai";
        List<Integer> existingIds = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                existingIds.add(rs.getInt("id_nilai"));
            }
        }

        int nextId = 1; // ID dimulai dari 1
        for (int id : existingIds) {
            if (id == nextId) {
                nextId++; // Jika ID sudah ada, lanjut ke angka berikutnya
            } else {
                break; // Jika ditemukan celah, gunakan angka tersebut
            }
        }

        return nextId;
    }

    private void tambahNilai() {
        JPanel panel = new JPanel(new GridLayout(6, 2, 10, 10)); // 6 baris dan 2 kolom

        JComboBox<String> siswaComboBox = new JComboBox<>();
        JComboBox<String> mapelComboBox = new JComboBox<>();
        JTextField absenField = new JTextField();
        JTextField tugasField = new JTextField();
        JTextField utsField = new JTextField();
        JTextField uasField = new JTextField();

        // Muat nama siswa ke dalam ComboBox
        try (Connection conn = DatabaseConnection.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT id_siswa, nama FROM siswa")) {
            siswaComboBox.addItem("Pilih Siswa");
            while (rs.next()) {
                siswaComboBox.addItem(rs.getInt("id_siswa") + " - " + rs.getString("nama"));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Gagal memuat data siswa.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Muat nama mapel ke dalam ComboBox
        try (Connection conn = DatabaseConnection.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT Kd_Mapel, Mapel FROM mapel")) {
            mapelComboBox.addItem("Pilih Mapel");
            while (rs.next()) {
                mapelComboBox.addItem(rs.getInt("Kd_Mapel") + " - " + rs.getString("Mapel"));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Gagal memuat data mapel.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Menambahkan label dan komponen input ke panel
        panel.add(new JLabel("Siswa:"));
        panel.add(siswaComboBox);
        panel.add(new JLabel("Mapel:"));
        panel.add(mapelComboBox);
        panel.add(new JLabel("Absen:"));
        panel.add(absenField);
        panel.add(new JLabel("Tugas:"));
        panel.add(tugasField);
        panel.add(new JLabel("UTS:"));
        panel.add(utsField);
        panel.add(new JLabel("UAS:"));
        panel.add(uasField);

        int result = JOptionPane.showConfirmDialog(this, panel, "Tambah Nilai", JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE);

        // Jika tombol OK diklik
        if (result == JOptionPane.OK_OPTION) {
            String selectedSiswa = (String) siswaComboBox.getSelectedItem();
            String selectedMapel = (String) mapelComboBox.getSelectedItem();

            // Cek apakah semua field sudah diisi
            if (selectedSiswa.equals("Pilih Siswa") || selectedMapel.equals("Pilih Mapel") ||
                    absenField.getText().isEmpty() || tugasField.getText().isEmpty() ||
                    utsField.getText().isEmpty() || uasField.getText().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Semua field harus diisi!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Proses penyimpanan data ke database
            try (Connection conn = DatabaseConnection.getConnection()) {
                int nextId = getNextAvailableId(); // Panggil metode getNextAvailableId

                String query = "INSERT INTO nilai (id_nilai, id_siswa, Kd_Mapel, Absen, Tugas, UTS, UAS) VALUES (?, ?, ?, ?, ?, ?, ?)";
                try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                    pstmt.setInt(1, nextId); // ID otomatis
                    pstmt.setInt(2, Integer.parseInt(selectedSiswa.split(" - ")[0]));
                    pstmt.setInt(3, Integer.parseInt(selectedMapel.split(" - ")[0]));
                    pstmt.setInt(4, Integer.parseInt(absenField.getText()));
                    pstmt.setInt(5, Integer.parseInt(tugasField.getText()));
                    pstmt.setInt(6, Integer.parseInt(utsField.getText()));
                    pstmt.setInt(7, Integer.parseInt(uasField.getText()));

                    pstmt.executeUpdate();
                    JOptionPane.showMessageDialog(this, "Nilai berhasil ditambahkan!");
                    refreshNilai(); // Method untuk merefresh data nilai jika diperlukan
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Gagal menambahkan nilai.", "Error", JOptionPane.ERROR_MESSAGE);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Input nilai harus berupa angka.", "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void editNilai() {
        int selectedRow = nilaiTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Pilih nilai yang ingin diedit.", "Peringatan",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int idNilai = Integer.parseInt(tableModel.getValueAt(selectedRow, 0).toString());

        JPanel panel = new JPanel(new GridLayout(6, 2, 10, 10));

        JComboBox<String> siswaComboBox = new JComboBox<>();
        JComboBox<String> mapelComboBox = new JComboBox<>();
        JTextField absenField = new JTextField();
        JTextField tugasField = new JTextField();
        JTextField utsField = new JTextField();
        JTextField uasField = new JTextField();

        // Muat data siswa ke dalam ComboBox
        try (Connection conn = DatabaseConnection.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT id_siswa, nama FROM siswa")) {
            while (rs.next()) {
                siswaComboBox.addItem(rs.getInt("id_siswa") + " - " + rs.getString("nama"));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Gagal memuat data siswa.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Muat data mapel ke dalam ComboBox
        try (Connection conn = DatabaseConnection.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT Kd_Mapel, Mapel FROM mapel")) {
            while (rs.next()) {
                mapelComboBox.addItem(rs.getInt("Kd_Mapel") + " - " + rs.getString("Mapel"));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Gagal memuat data mapel.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Muat nilai berdasarkan id_nilai
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT s.id_siswa, s.nama, m.Kd_Mapel, m.mapel, n.Absen, n.Tugas, n.UTS, n.UAS " +
                    "FROM nilai n " +
                    "JOIN siswa s ON n.id_siswa = s.id_siswa " +
                    "JOIN mapel m ON n.Kd_Mapel = m.Kd_Mapel WHERE n.id_nilai = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                pstmt.setInt(1, idNilai);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        String selectedSiswa = rs.getInt("id_siswa") + " - " + rs.getString("nama");
                        String selectedMapel = rs.getInt("Kd_Mapel") + " - " + rs.getString("mapel");

                        siswaComboBox.setSelectedItem(selectedSiswa);
                        mapelComboBox.setSelectedItem(selectedMapel);
                        absenField.setText(String.valueOf(rs.getInt("Absen")));
                        tugasField.setText(String.valueOf(rs.getInt("Tugas")));
                        utsField.setText(String.valueOf(rs.getInt("UTS")));
                        uasField.setText(String.valueOf(rs.getInt("UAS")));
                    }
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Gagal memuat data nilai.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        panel.add(new JLabel("Siswa:"));
        panel.add(siswaComboBox);
        panel.add(new JLabel("Mata Pelajaran:"));
        panel.add(mapelComboBox);
        panel.add(new JLabel("Absen:"));
        panel.add(absenField);
        panel.add(new JLabel("Tugas:"));
        panel.add(tugasField);
        panel.add(new JLabel("UTS:"));
        panel.add(utsField);
        panel.add(new JLabel("UAS:"));
        panel.add(uasField);

        int result = JOptionPane.showConfirmDialog(this, panel, "Edit Nilai", JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
            try (Connection conn = DatabaseConnection.getConnection()) {
                String query = "UPDATE nilai SET id_siswa = ?, Kd_Mapel = ?, Absen = ?, Tugas = ?, UTS = ?, UAS = ? WHERE id_nilai = ?";
                try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                    pstmt.setInt(1, Integer.parseInt(siswaComboBox.getSelectedItem().toString().split(" - ")[0]));
                    pstmt.setInt(2, Integer.parseInt(mapelComboBox.getSelectedItem().toString().split(" - ")[0]));
                    pstmt.setInt(3, Integer.parseInt(absenField.getText()));
                    pstmt.setInt(4, Integer.parseInt(tugasField.getText()));
                    pstmt.setInt(5, Integer.parseInt(utsField.getText()));
                    pstmt.setInt(6, Integer.parseInt(uasField.getText()));
                    pstmt.setInt(7, idNilai);

                    int rowsUpdated = pstmt.executeUpdate();
                    if (rowsUpdated > 0) {
                        JOptionPane.showMessageDialog(this, "Nilai berhasil diperbarui!");
                        refreshNilai();
                    } else {
                        JOptionPane.showMessageDialog(this, "Gagal memperbarui nilai.", "Error",
                                JOptionPane.ERROR_MESSAGE);
                    }
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Terjadi kesalahan saat memperbarui nilai.", "Error",
                        JOptionPane.ERROR_MESSAGE);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Input angka tidak valid.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void hapusNilai() {
        int selectedRow = nilaiTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Pilih nilai yang ingin dihapus.", "Peringatan",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int idNilai = (int) nilaiTable.getValueAt(selectedRow, 0);

        int confirm = JOptionPane.showConfirmDialog(this, "Apakah Anda yakin ingin menghapus nilai ini?",
                "Konfirmasi Hapus", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try (Connection conn = DatabaseConnection.getConnection()) {
                String query = "DELETE FROM nilai WHERE id_nilai = ?";
                try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                    pstmt.setInt(1, idNilai);
                    pstmt.executeUpdate();
                    JOptionPane.showMessageDialog(this, "Nilai berhasil dihapus!");
                    refreshNilai();
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Gagal menghapus nilai.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void refreshNilai() {
        tableModel.setRowCount(0); // Reset tabel

        String query = "SELECT n.id_nilai, s.nama, m.mapel, n.Absen, n.Tugas, n.UTS, n.UAS " +
                "FROM nilai n " +
                "JOIN siswa s ON n.id_siswa = s.id_siswa " +
                "JOIN mapel m ON n.Kd_Mapel = m.Kd_Mapel";

        try (Connection conn = DatabaseConnection.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(query)) {

            int nomor = 1;
            while (rs.next()) {
                String namaSiswa = rs.getString("nama");
                String namaMapel = rs.getString("mapel");
                int absen = rs.getInt("Absen");
                int tugas = rs.getInt("Tugas");
                int uts = rs.getInt("UTS");
                int uas = rs.getInt("UAS");

                // Tambahkan data ke tabel
                tableModel.addRow(new Object[] { nomor++, namaSiswa, namaMapel, absen, tugas, uts, uas });
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Gagal memuat data nilai.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void kembali() {
        dispose(); // Menutup jendela ini
        new AdminGUI().setVisible(true); // Kembali ke halaman admin

    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new NilaiManagementGUI().setVisible(true));
    }
}
