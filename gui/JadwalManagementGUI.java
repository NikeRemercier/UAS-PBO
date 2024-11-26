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

public class JadwalManagementGUI extends JFrame {
    private DefaultTableModel tableModel;
    private JTable jadwalTable;

    public JadwalManagementGUI() {
        setTitle("Kelola Jadwal");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        tableModel = new DefaultTableModel(new Object[] { "No", "Hari", "Mapel", "Tingkat", "Kelas", "Guru" }, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Tabel tidak bisa diedit langsung
            }
        };

        jadwalTable = new JTable(tableModel);
        // Menambahkan renderer untuk alignment tengah
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);

        // Terapkan renderer ke semua kolom
        for (int i = 0; i < jadwalTable.getColumnCount(); i++) {
            jadwalTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        JScrollPane scrollPane = new JScrollPane(jadwalTable);
        add(scrollPane, BorderLayout.CENTER);

        // Tombol aksi
        JPanel buttonPanel = new JPanel(new GridLayout(5, 1, 10, 10));
        JButton tambahButton = new JButton("Tambah Jadwal");
        JButton editButton = new JButton("Edit Jadwal");
        JButton hapusButton = new JButton("Hapus Jadwal");
        JButton refreshButton = new JButton("Refresh");
        JButton kembaliButton = new JButton("Kembali");

        tambahButton.addActionListener(e -> tambahJadwal());
        editButton.addActionListener(e -> editJadwal());
        hapusButton.addActionListener(e -> hapusJadwal());
        refreshButton.addActionListener(e -> refreshJadwal());
        kembaliButton.addActionListener(e -> kembali());

        buttonPanel.add(tambahButton);
        buttonPanel.add(editButton);
        buttonPanel.add(hapusButton);
        buttonPanel.add(refreshButton);
        buttonPanel.add(kembaliButton);
        add(buttonPanel, BorderLayout.EAST);

        // Muat data awal
        refreshJadwal();
    }

    private int getNextAvailableId() throws SQLException {
        String query = "SELECT id_jadwal FROM Jadwal_Mapel ORDER BY id_jadwal";
        List<Integer> existingIds = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                existingIds.add(rs.getInt("id_jadwal"));
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

    private void tambahJadwal() {
        JPanel panel = new JPanel(new GridLayout(4, 2, 10, 10));

        JComboBox<String> hariDropdown = new JComboBox<>(
                new String[] { "Pilih Hari", "Senin", "Selasa", "Rabu", "Kamis", "Jumat" });
        JComboBox<String> mapelDropdown = new JComboBox<>();
        JComboBox<String> kelasDropdown = new JComboBox<>();
        JComboBox<String> guruDropdown = new JComboBox<>();

        try (Connection conn = DatabaseConnection.getConnection(); Statement stmt = conn.createStatement()) {
            // Isi dropdown mapel
            ResultSet rsMapel = stmt.executeQuery("SELECT kd_mapel, mapel FROM Mapel");
            mapelDropdown.addItem("Pilih Mapel");
            while (rsMapel.next()) {
                mapelDropdown.addItem(rsMapel.getString("kd_mapel") + " - " + rsMapel.getString("mapel"));
            }

            // Isi dropdown kelas
            ResultSet rsKelas = stmt.executeQuery("SELECT id_kelas, kelas, tingkatan FROM Kelas");
            kelasDropdown.addItem("Pilih Kelas");
            while (rsKelas.next()) {
                kelasDropdown.addItem(rsKelas.getString("id_kelas") + " - " + rsKelas.getString("tingkatan") + " - "
                        + rsKelas.getString("kelas"));
            }

            // Isi dropdown guru
            ResultSet rsGuru = stmt.executeQuery("SELECT id_guru, nama FROM Guru");
            guruDropdown.addItem("Pilih Guru");
            while (rsGuru.next()) {
                guruDropdown.addItem(rsGuru.getString("id_guru") + " - " + rsGuru.getString("nama"));
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Gagal memuat data dropdown.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        panel.add(new JLabel("Hari:"));
        panel.add(hariDropdown);
        panel.add(new JLabel("Mapel:"));
        panel.add(mapelDropdown);
        panel.add(new JLabel("Kelas:"));
        panel.add(kelasDropdown);
        panel.add(new JLabel("Guru:"));
        panel.add(guruDropdown);

        int result = JOptionPane.showConfirmDialog(this, panel, "Tambah Jadwal", JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
            String selectedHari = (String) hariDropdown.getSelectedItem();
            String selectedMapel = (String) mapelDropdown.getSelectedItem();
            String selectedKelas = (String) kelasDropdown.getSelectedItem();
            String selectedGuru = (String) guruDropdown.getSelectedItem();

            if (selectedHari.equals("Pilih Hari") || selectedMapel == null || selectedKelas == null
                    || selectedGuru == null ||
                    selectedMapel.equals("Pilih Mapel") || selectedKelas.equals("Pilih Kelas")
                    || selectedGuru.equals("Pilih Guru")) {
                JOptionPane.showMessageDialog(this, "Semua field harus dipilih!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String kdMapel = selectedMapel.split(" - ")[0];
            String idKelas = selectedKelas.split(" - ")[0];
            String idGuru = selectedGuru.split(" - ")[0];

            try (Connection conn = DatabaseConnection.getConnection(); Statement stmt = conn.createStatement()) {
                int nextId = getNextAvailableId(); // Dapatkan ID baru

                String query = "INSERT INTO Jadwal_mapel (id_jadwal, hari, kd_mapel, id_kelas, id_guru) VALUES (" +
                        nextId + ", '" + selectedHari + "', '" + kdMapel + "', '" + idKelas + "', '" + idGuru + "')";
                stmt.executeUpdate(query);

                JOptionPane.showMessageDialog(this, "Jadwal berhasil ditambahkan!");
                refreshJadwal();
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Gagal menambahkan jadwal.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void editJadwal() {
        int selectedRow = jadwalTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Pilih jadwal yang ingin diedit.", "Peringatan",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Ambil ID jadwal dari kolom pertama
        int idJadwal = (int) jadwalTable.getValueAt(selectedRow, 0);

        JPanel panel = new JPanel(new GridLayout(4, 2, 10, 10));

        // Dropdown untuk data baru
        JComboBox<String> hariDropdown = new JComboBox<>(
                new String[] { "Pilih Hari", "Senin", "Selasa", "Rabu", "Kamis", "Jumat" });
        JComboBox<String> mapelDropdown = new JComboBox<>();
        JComboBox<String> kelasDropdown = new JComboBox<>();
        JComboBox<String> guruDropdown = new JComboBox<>();

        try (Connection conn = DatabaseConnection.getConnection(); Statement stmt = conn.createStatement()) {
            // Isi dropdown mapel
            ResultSet rsMapel = stmt.executeQuery("SELECT kd_mapel, mapel FROM Mapel");
            mapelDropdown.addItem("Pilih Mapel");
            while (rsMapel.next()) {
                mapelDropdown.addItem(rsMapel.getString("kd_mapel") + " - " + rsMapel.getString("mapel"));
            }

            // Isi dropdown kelas
            ResultSet rsKelas = stmt.executeQuery("SELECT id_kelas, kelas, tingkatan FROM Kelas");
            kelasDropdown.addItem("Pilih Kelas");
            while (rsKelas.next()) {
                kelasDropdown.addItem(rsKelas.getString("id_kelas") + " - " + rsKelas.getString("tingkatan") + " - "
                        + rsKelas.getString("kelas"));
            }

            // Isi dropdown guru
            ResultSet rsGuru = stmt.executeQuery("SELECT id_guru, nama FROM Guru");
            guruDropdown.addItem("Pilih Guru");
            while (rsGuru.next()) {
                guruDropdown.addItem(rsGuru.getString("id_guru") + " - " + rsGuru.getString("nama"));
            }

            // Ambil data jadwal yang dipilih
            String query = "SELECT hari, kd_mapel, id_kelas, id_guru FROM Jadwal_Mapel WHERE id_jadwal = " + idJadwal;
            ResultSet rsJadwal = stmt.executeQuery(query);
            if (rsJadwal.next()) {
                // Set nilai untuk hari
                hariDropdown.setSelectedItem(rsJadwal.getString("hari"));

                // Pilih mapel
                String selectedMapel = rsJadwal.getString("kd_mapel");
                for (int i = 0; i < mapelDropdown.getItemCount(); i++) {
                    if (mapelDropdown.getItemAt(i).startsWith(selectedMapel)) {
                        mapelDropdown.setSelectedIndex(i);
                        break;
                    }
                }

                // Pilih kelas
                String selectedKelas = rsJadwal.getString("id_kelas");
                for (int i = 0; i < kelasDropdown.getItemCount(); i++) {
                    if (kelasDropdown.getItemAt(i).startsWith(selectedKelas)) {
                        kelasDropdown.setSelectedIndex(i);
                        break;
                    }
                }

                // Pilih guru
                String selectedGuru = rsJadwal.getString("id_guru");
                for (int i = 0; i < guruDropdown.getItemCount(); i++) {
                    if (guruDropdown.getItemAt(i).startsWith(selectedGuru)) {
                        guruDropdown.setSelectedIndex(i);
                        break;
                    }
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Gagal memuat data jadwal.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Menambahkan komponen ke panel
        panel.add(new JLabel("Hari:"));
        panel.add(hariDropdown);
        panel.add(new JLabel("Mapel:"));
        panel.add(mapelDropdown);
        panel.add(new JLabel("Kelas:"));
        panel.add(kelasDropdown);
        panel.add(new JLabel("Guru:"));
        panel.add(guruDropdown);

        int result = JOptionPane.showConfirmDialog(this, panel, "Edit Jadwal", JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
            String selectedHari = (String) hariDropdown.getSelectedItem();
            String selectedMapel = (String) mapelDropdown.getSelectedItem();
            String selectedKelas = (String) kelasDropdown.getSelectedItem();
            String selectedGuru = (String) guruDropdown.getSelectedItem();

            if (selectedHari.equals("Pilih Hari") || selectedMapel == null || selectedKelas == null
                    || selectedGuru == null ||
                    selectedMapel.equals("Pilih Mapel") || selectedKelas.equals("Pilih Kelas")
                    || selectedGuru.equals("Pilih Guru")) {
                JOptionPane.showMessageDialog(this, "Semua field harus dipilih!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String kdMapel = selectedMapel.split(" - ")[0];
            String idKelas = selectedKelas.split(" - ")[0];
            String idGuru = selectedGuru.split(" - ")[0];

            try (Connection conn = DatabaseConnection.getConnection();
                    PreparedStatement stmt = conn.prepareStatement(
                            "UPDATE Jadwal_Mapel SET hari = ?, kd_mapel = ?, id_kelas = ?, id_guru = ? WHERE id_jadwal = ?")) {
                stmt.setString(1, selectedHari);
                stmt.setString(2, kdMapel);
                stmt.setString(3, idKelas);
                stmt.setString(4, idGuru);
                stmt.setInt(5, idJadwal);

                int rowsAffected = stmt.executeUpdate();
                if (rowsAffected > 0) {
                    JOptionPane.showMessageDialog(this, "Jadwal berhasil diperbarui!");
                    refreshJadwal();
                } else {
                    JOptionPane.showMessageDialog(this, "Gagal memperbarui jadwal.", "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Gagal memperbarui jadwal.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void hapusJadwal() {
        int selectedRow = jadwalTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Pilih jadwal yang ingin dihapus.", "Peringatan",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Apakah Anda yakin ingin menghapus jadwal ini?", "Konfirmasi",
                JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            // Ambil nilai id_jadwal dari kolom pertama (No)
            int idJadwal = (int) jadwalTable.getValueAt(selectedRow, 0); // Kolom pertama di tabel

            try (Connection conn = DatabaseConnection.getConnection();
                    PreparedStatement stmt = conn.prepareStatement("DELETE FROM Jadwal_mapel WHERE id_jadwal = ?")) {
                stmt.setInt(1, idJadwal); // Masukkan id_jadwal ke query
                int rowsAffected = stmt.executeUpdate();
                if (rowsAffected > 0) {
                    JOptionPane.showMessageDialog(this, "Jadwal berhasil dihapus!");
                    refreshJadwal(); // Refresh tabel setelah penghapusan
                } else {
                    JOptionPane.showMessageDialog(this, "Jadwal tidak ditemukan atau gagal dihapus.", "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Gagal menghapus jadwal.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void refreshJadwal() {
        tableModel.setRowCount(0); // Hapus semua data di tabel

        try (Connection conn = DatabaseConnection.getConnection(); Statement stmt = conn.createStatement()) {
            String query = "SELECT Jadwal_Mapel.id_jadwal, Mapel.Mapel, Kelas.Kelas, Kelas.Tingkatan, Jadwal_Mapel.Hari, Guru.Nama "
                    +
                    "FROM Jadwal_Mapel " +
                    "JOIN Mapel ON Jadwal_Mapel.Kd_Mapel = Mapel.Kd_Mapel " +
                    "JOIN Kelas ON Jadwal_Mapel.Id_Kelas = Kelas.Id_Kelas " +
                    "JOIN Guru ON Jadwal_Mapel.id_guru = Guru.id_guru";
            try (ResultSet rs = stmt.executeQuery(query)) {
                int nomor = 1;
                while (rs.next()) {
                    String hari = rs.getString("Hari");
                    String mapel = rs.getString("Mapel");
                    String tingkatan = rs.getString("Tingkatan");
                    String kelas = rs.getString("Kelas");
                    String guru = rs.getString("Nama");

                    tableModel.addRow(new Object[] { nomor++, hari, mapel, tingkatan, kelas, guru });
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Gagal memuat data jadwal.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void kembali() {
        dispose();
        new AdminGUI().setVisible(true); // Kembali ke halaman admin
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new JadwalManagementGUI().setVisible(true));
    }
}
