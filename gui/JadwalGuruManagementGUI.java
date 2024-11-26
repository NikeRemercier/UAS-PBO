package UAS_PBO_NEW.gui;

import UAS_PBO_NEW.DatabaseConnection;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import UAS_PBO_NEW.gui.role.GuruGUI;

public class JadwalGuruManagementGUI extends JFrame {
    private DefaultTableModel tableModel;
    private JTable jadwalTable;

    public JadwalGuruManagementGUI() {
        setTitle("Lihat Jadwal");
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
        JPanel buttonPanel = new JPanel(new GridLayout(2, 1, 10, 10));
        JButton refreshButton = new JButton("Refresh");
        JButton kembaliButton = new JButton("Kembali");

        refreshButton.addActionListener(e -> refreshJadwal());
        kembaliButton.addActionListener(e -> kembaliKeGuruGUI());

        buttonPanel.add(refreshButton);
        buttonPanel.add(kembaliButton);
        add(buttonPanel, BorderLayout.SOUTH);

        // Muat data awal
        refreshJadwal();
    }

    private void kembaliKeGuruGUI() {
        // Menutup frame saat kembali ke halaman GuruGUI
        this.dispose();
        new GuruGUI().setVisible(true); // Membuka GuruGUI
    }

    private void refreshJadwal() {
        tableModel.setRowCount(0); // Hapus semua data di tabel

        try (Connection conn = DatabaseConnection.getConnection(); Statement stmt = conn.createStatement()) {
            String query = "SELECT Jadwal_Mapel.id_jadwal, Mapel.Mapel, Kelas.Kelas, Kelas.Tingkatan, Jadwal_Mapel.Hari, Guru.Nama "
                    +
                    "FROM Jadwal_Mapel " +
                    "JOIN Mapel ON Jadwal_Mapel.kd_mapel = Mapel.kd_mapel " +
                    "JOIN Kelas ON Jadwal_Mapel.id_kelas = Kelas.id_kelas " +
                    "JOIN Guru ON Jadwal_Mapel.id_guru = Guru.id_guru";
            ResultSet rs = stmt.executeQuery(query);

            while (rs.next()) {
                Object[] row = {
                        rs.getInt("id_jadwal"),
                        rs.getString("Hari"),
                        rs.getString("Mapel"),
                        rs.getString("Tingkatan"),
                        rs.getString("Kelas"),
                        rs.getString("Nama")
                };
                tableModel.addRow(row);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Gagal memuat data jadwal.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new JadwalGuruManagementGUI().setVisible(true));
    }

}
