package UAS_PBO_NEW.gui;

import UAS_PBO_NEW.DatabaseConnection;
import UAS_PBO_NEW.gui.role.AdminGUI;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import javax.swing.table.DefaultTableCellRenderer;

public class SiswaManagementGUI extends JFrame {
    private DefaultTableModel tableModel;
    private JTable siswaTable;

    public SiswaManagementGUI() {
        setTitle("Kelola Siswa");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        tableModel = new DefaultTableModel(new Object[]{"No", "Nama", "NIS", "Kelas", "Tingkatan"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Tabel tidak bisa diedit langsung
            }
        };

        siswaTable = new JTable(tableModel);
        
        // Menambahkan renderer untuk alignment tengah
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);

        // Terapkan renderer ke semua kolom
        for (int i = 0; i < siswaTable.getColumnCount(); i++) {
            siswaTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        JScrollPane scrollPane = new JScrollPane(siswaTable);
        add(scrollPane, BorderLayout.CENTER);

        // Tombol aksi
        JPanel buttonPanel = new JPanel(new GridLayout(5, 1, 10, 10));
        JButton tambahButton = new JButton("Tambah Siswa");
        JButton editButton = new JButton("Edit Siswa");
        JButton hapusButton = new JButton("Hapus Siswa");
        JButton refreshButton = new JButton("Refresh");
        JButton kembaliButton = new JButton("Kembali");

        tambahButton.addActionListener(e -> tambahSiswa());
        editButton.addActionListener(e -> editSiswa());
        hapusButton.addActionListener(e -> hapusSiswa());
        refreshButton.addActionListener(e -> refreshSiswa());
        kembaliButton.addActionListener(e -> kembali());

        buttonPanel.add(tambahButton);
        buttonPanel.add(editButton);
        buttonPanel.add(hapusButton);
        buttonPanel.add(refreshButton);
        buttonPanel.add(kembaliButton);
        add(buttonPanel, BorderLayout.EAST);

        // Muat data awal
        refreshSiswa();
    }

    // Menambahkan metode untuk mendapatkan ID berikutnya
    private int getNextAvailableId() throws SQLException {
        String query = "SELECT id_siswa FROM Siswa ORDER BY id_siswa";
        List<Integer> existingIds = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                existingIds.add(rs.getInt("id_siswa"));
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

    private void tambahSiswa() {
        JPanel panel = new JPanel(new GridLayout(3, 2, 10, 10));

        JTextField namaField = new JTextField();
        JTextField nisField = new JTextField();
        JComboBox<String> kelasDropdown = new JComboBox<>();

        try (Connection conn = DatabaseConnection.getConnection(); Statement stmt = conn.createStatement()) {
            // Isi dropdown kelas
            ResultSet rsKelas = stmt.executeQuery("SELECT id_kelas, kelas, tingkatan FROM Kelas");
            kelasDropdown.addItem("Pilih Kelas");
            while (rsKelas.next()) {
                kelasDropdown.addItem(rsKelas.getString("id_kelas") + " - " + rsKelas.getString("tingkatan") + " - " + rsKelas.getString("kelas"));
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Gagal memuat data dropdown.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        panel.add(new JLabel("Nama:"));
        panel.add(namaField);
        panel.add(new JLabel("NISN:"));
        panel.add(nisField);
        panel.add(new JLabel("Kelas:"));
        panel.add(kelasDropdown);

        int result = JOptionPane.showConfirmDialog(this, panel, "Tambah Siswa", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
            String nama = namaField.getText();
            String nis = nisField.getText();
            String selectedKelas = (String) kelasDropdown.getSelectedItem();

            if (nama.isEmpty() || nis.isEmpty() || selectedKelas.equals("Pilih Kelas")) {
                JOptionPane.showMessageDialog(this, "Semua field harus diisi!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Memisahkan id_kelas dari string kelas yang dipilih
            String idKelas = selectedKelas.split(" - ")[0];

            try {
                // Mendapatkan ID berikutnya
                int nextId = getNextAvailableId();

                // Menyisipkan data siswa baru dengan ID yang diperoleh
                String query = "INSERT INTO Siswa (id_siswa, nama, nisn, id_kelas) VALUES (?, ?, ?, ?)";
                try (Connection conn = DatabaseConnection.getConnection();
                     PreparedStatement pstmt = conn.prepareStatement(query)) {

                    pstmt.setInt(1, nextId);
                    pstmt.setString(2, nama);
                    pstmt.setString(3, nis);
                    pstmt.setString(4, idKelas);

                    pstmt.executeUpdate();

                    JOptionPane.showMessageDialog(this, "Siswa berhasil ditambahkan!");
                    refreshSiswa();
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Gagal menambahkan siswa.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void editSiswa() {
        int selectedRow = siswaTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Pilih siswa yang ingin diedit.", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }
    
        // Ambil ID siswa dari kolom pertama
        int idSiswa = (int) siswaTable.getValueAt(selectedRow, 0);
    
        // Panel input
        JPanel panel = new JPanel(new GridLayout(4, 2, 10, 10));
    
        JTextField namaField = new JTextField();
        JTextField nisField = new JTextField();
        JComboBox<String> kelasDropdown = new JComboBox<>();
    
        try (Connection conn = DatabaseConnection.getConnection(); Statement stmt = conn.createStatement()) {
            // Isi dropdown kelas dengan format "id_kelas - tingkatan - kelas"
            ResultSet rsKelas = stmt.executeQuery("SELECT id_kelas, kelas, tingkatan FROM Kelas");
            kelasDropdown.addItem("Pilih Kelas");
            while (rsKelas.next()) {
                kelasDropdown.addItem(rsKelas.getString("id_kelas") + " - " + rsKelas.getString("tingkatan") + " - " + rsKelas.getString("kelas"));
            }
    
            // Ambil data siswa yang dipilih
            String query = "SELECT nama, nisn, id_kelas FROM Siswa WHERE id_siswa = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                pstmt.setInt(1, idSiswa);
                try (ResultSet rsSiswa = pstmt.executeQuery()) {
                    if (rsSiswa.next()) {
                        namaField.setText(rsSiswa.getString("nama"));
                        nisField.setText(rsSiswa.getString("nisn"));
    
                        String selectedKelas = rsSiswa.getString("id_kelas");
                        for (int i = 0; i < kelasDropdown.getItemCount(); i++) {
                            String item = kelasDropdown.getItemAt(i);
                            if (item.startsWith(selectedKelas + " - ")) { // Pastikan format cocok
                                kelasDropdown.setSelectedIndex(i);
                                break;
                            }
                        }
                    }
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Gagal memuat data siswa.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
    
        // Menambahkan komponen ke panel
        panel.add(new JLabel("Nama:"));
        panel.add(namaField);
        panel.add(new JLabel("NISN:"));
        panel.add(nisField);
        panel.add(new JLabel("Kelas:"));
        panel.add(kelasDropdown);
    
        // Menampilkan dialog edit
        int result = JOptionPane.showConfirmDialog(this, panel, "Edit Siswa", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
            String nama = namaField.getText().trim();
            String nis = nisField.getText().trim();
            String selectedKelas = (String) kelasDropdown.getSelectedItem();
    
            // Validasi input
            if (nama.isEmpty() || nis.isEmpty() || selectedKelas.equals("Pilih Kelas")) {
                JOptionPane.showMessageDialog(this, "Semua field harus diisi!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
    
            // Memisahkan id_kelas dari string kelas yang dipilih
            String idKelas = selectedKelas.split(" - ")[0];
    
            try (Connection conn = DatabaseConnection.getConnection()) {
                String updateQuery = "UPDATE Siswa SET nama = ?, nisn = ?, id_kelas = ? WHERE id_siswa = ?";
                try (PreparedStatement pstmt = conn.prepareStatement(updateQuery)) {
                    pstmt.setString(1, nama);
                    pstmt.setString(2, nis);
                    pstmt.setString(3, idKelas);
                    pstmt.setInt(4, idSiswa);
    
                    int rowsUpdated = pstmt.executeUpdate();
                    if (rowsUpdated > 0) {
                        JOptionPane.showMessageDialog(this, "Siswa berhasil diperbarui!");
                        refreshSiswa();
                    } else {
                        JOptionPane.showMessageDialog(this, "Gagal memperbarui siswa.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Gagal memperbarui siswa.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
        
    private void hapusSiswa() {
        int selectedRow = siswaTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Pilih siswa yang ingin dihapus.", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }
    
        int idSiswa = (int) siswaTable.getValueAt(selectedRow, 0);
    
        // Menampilkan konfirmasi sebelum menghapus
        int confirm = JOptionPane.showConfirmDialog(this, "Apakah Anda yakin ingin menghapus siswa ini?", "Konfirmasi Hapus", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm == JOptionPane.YES_OPTION) {
            try (Connection conn = DatabaseConnection.getConnection()) {
                String query = "DELETE FROM Siswa WHERE id_siswa = ?";
                try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                    pstmt.setInt(1, idSiswa);
                    pstmt.executeUpdate();
                    JOptionPane.showMessageDialog(this, "Siswa berhasil dihapus!");
                    refreshSiswa();
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Gagal menghapus siswa.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void refreshSiswa() {
        tableModel.setRowCount(0); // Clear table
    
        try (Connection conn = DatabaseConnection.getConnection(); 
             Statement stmt = conn.createStatement()) {
    
            String query = "SELECT S.id_siswa, S.nama, S.nisn, K.kelas, K.tingkatan " +
                           "FROM Siswa S " +
                           "JOIN Kelas K ON S.id_kelas = K.id_kelas";
            
            try (ResultSet rs = stmt.executeQuery(query)) {
                int nomor = 1; // Menambahkan nomor urut baris
                while (rs.next()) {
                    String nama = rs.getString("nama");
                    String nisn = rs.getString("nisn");
                    String kelas = rs.getString("kelas");
                    String tingkatan = rs.getString("tingkatan");
    
                    // Menambahkan data siswa ke dalam tabel
                    tableModel.addRow(new Object[]{nomor++, nama, nisn, kelas, tingkatan});
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Gagal memuat data siswa.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void kembali() {
        dispose(); // Menutup jendela ini
        new AdminGUI().setVisible(true); // Kembali ke halaman admin

    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new SiswaManagementGUI().setVisible(true));
    }
}
