package UAS_PBO_NEW.gui;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.util.ArrayList;

import UAS_PBO_NEW.DatabaseConnection;
import UAS_PBO_NEW.dao.KelasDAO;
import UAS_PBO_NEW.model.Kelas;

public class KelasManagementGUI extends JFrame {
    private DefaultTableModel tableModel;
    private JTable kelasTable;
    private ArrayList<Kelas> kelasList; // List untuk menyimpan data kelas
    private KelasDAO kelasDAO; // DAO untuk akses data kelas
    private int nextId = 1; // ID otomatis untuk pengguna baru

    public KelasManagementGUI() {
        kelasList = new ArrayList<>(); // Inisialisasi daftar pengguna
        kelasDAO = new KelasDAO(DatabaseConnection.getConnection()); // Inisialisasi UserDAO

        setTitle("Kelola Kelas");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Modifikasi bagian tabel untuk menambah kolom "Nama"
        tableModel = new DefaultTableModel(new Object[] { "No","Nama Kelas", "Tingkatan" }, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Semua sel tabel menjadi non-editable
            }
        };

        kelasTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(kelasTable);
        add(scrollPane, BorderLayout.CENTER);

        // Atur text alignment di tabel
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        kelasTable.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        kelasTable.getColumnModel().getColumn(1).setCellRenderer(centerRenderer);
        kelasTable.getColumnModel().getColumn(2).setCellRenderer(centerRenderer);

        // Panel tombol
        JPanel buttonPanel = new JPanel(new GridLayout(5, 1, 10, 10));
        JButton tambahKelasButton = new JButton("Tambah Kelas");
        JButton editKelasButton = new JButton("Edit Kelas");
        JButton hapusKelasButton = new JButton("Hapus Kelas");
        JButton lihatKelasButton = new JButton("Refresh Kelas");
        JButton kembaliButton = new JButton("Kembali");

        // Tambahkan action listener untuk masing-masing tombol
        tambahKelasButton.addActionListener(e -> tambahKelas());
        editKelasButton.addActionListener(e -> editKelas());
        hapusKelasButton.addActionListener(e -> hapusKelas());
        lihatKelasButton.addActionListener(e -> refreshKelas());
        kembaliButton.addActionListener(e -> kembali());

        buttonPanel.add(tambahKelasButton);
        buttonPanel.add(editKelasButton);
        buttonPanel.add(hapusKelasButton);
        buttonPanel.add(lihatKelasButton);
        buttonPanel.add(kembaliButton);
        add(buttonPanel, BorderLayout.EAST);
    }

    private void tambahKelas() {
        JPanel panel = new JPanel(new GridLayout(2, 2, 10, 10));
        JTextField kelasField = new JTextField();
        JTextField tingkatanField = new JTextField();

        panel.add(new JLabel("Kelas:"));
        panel.add(kelasField);
        panel.add(new JLabel("Tingkatan:"));
        panel.add(tingkatanField);

        int result = JOptionPane.showConfirmDialog(this, panel, "Tambah Kelas", JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
            try {
                String kelas = kelasField.getText().trim();
                int tingkatan = Integer.parseInt(tingkatanField.getText().trim());
                Kelas newKelas = new Kelas(nextId++, kelas, tingkatan); // ID akan di-set otomatis
                if (kelasDAO.addKelas(newKelas)) {
                    JOptionPane.showMessageDialog(this, "Kelas berhasil ditambahkan!");
                    refreshKelas();
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Gagal menambahkan kelas.", "Error", JOptionPane.ERROR_MESSAGE);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Tingkatan harus berupa angka.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void editKelas() {
        int selectedRow = kelasTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Pilih kelas yang ingin diedit.", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Kelas kelas = kelasList.get(selectedRow);
        JPanel panel = new JPanel(new GridLayout(2, 2, 10, 10));
        JTextField kelasField = new JTextField(kelas.getKelas());
        JTextField tingkatanField = new JTextField(String.valueOf(kelas.getTingkatan()));

        panel.add(new JLabel("Kelas:"));
        panel.add(kelasField);
        panel.add(new JLabel("Tingkatan:"));
        panel.add(tingkatanField);

        int result = JOptionPane.showConfirmDialog(this, panel, "Edit Pengguna", JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
            try {
                kelas.setKelas(kelasField.getText().trim());
                kelas.setTingkatan(Integer.parseInt(tingkatanField.getText().trim()));
                if (kelasDAO.updateKelas(kelas)) {
                    JOptionPane.showMessageDialog(this, "Kelas berhasil diperbarui!");
                    refreshKelas();
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Gagal memperbarui kelas.", "Error", JOptionPane.ERROR_MESSAGE);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Tingkatan harus berupa angka.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void hapusKelas() {
        int selectedRow = kelasTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Pilih kelas yang ingin dihapus.", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Kelas kelas = kelasList.get(selectedRow);
        int confirm = JOptionPane.showConfirmDialog(this, "Apakah Anda yakin ingin menghapus kelas ini?", "Konfirmasi", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                if (kelasDAO.deleteKelas(kelas.getIdKelas())) {
                    JOptionPane.showMessageDialog(this, "Kelas berhasil dihapus!");
                    refreshKelas();
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Gagal menghapus kelas.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void refreshKelas() {
        kelasList.clear();
        try {
            kelasList.addAll(kelasDAO.getAllKelass());
            updateTable(); // Perbarui tabel dengan data terbaru
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Gagal memuat data dari database!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateTable() {
        tableModel.setRowCount(0);
        for (int i = 0; i < kelasList.size(); i++) {
            Kelas kelas = kelasList.get(i);
            tableModel.addRow(new Object[]{i + 1, kelas.getKelas(), kelas.getTingkatan()});
        }
    }

    private void kembali() {
        dispose();
        new AdminGUI().setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new KelasManagementGUI().setVisible(true));
    }
}
