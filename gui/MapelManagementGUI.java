package UAS_PBO_NEW.gui;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.ArrayList;

import UAS_PBO_NEW.DatabaseConnection;
import UAS_PBO_NEW.dao.MapelDAO;
import UAS_PBO_NEW.model.Mapel;

public class MapelManagementGUI extends JFrame {
    private DefaultTableModel tableModel;
    private JTable mapelTable;
    private ArrayList<Mapel> mapelList; // List untuk menyimpan data mapel
    private MapelDAO mapelDAO;
    private int nextId = 1; // ID otomatis untuk pengguna baru

    public MapelManagementGUI() {
        mapelList = new ArrayList<>(); // Inisialisasi daftar pengguna
        mapelDAO = new MapelDAO(DatabaseConnection.getConnection()); // Inisialisasi UserDAO

        setTitle("Kelola Mapel");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Tabel untuk menampilkan data mapel
        tableModel = new DefaultTableModel(new Object[] { "No", "Nama Mapel" }, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Non-editable
            }
        };
        mapelTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(mapelTable);
        add(scrollPane, BorderLayout.CENTER);

        // Atur text alignment di tabel
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        mapelTable.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        mapelTable.getColumnModel().getColumn(1).setCellRenderer(centerRenderer);

        // Panel tombol
        JPanel buttonPanel = new JPanel(new GridLayout(5, 1, 10, 10));
        JButton tambahButton = new JButton("Tambah Mapel");
        JButton editButton = new JButton("Edit Mapel");
        JButton hapusButton = new JButton("Hapus Mapel");
        JButton refreshButton = new JButton("Refresh Mapel");
        JButton kembaliButton = new JButton("Kembali");

        tambahButton.addActionListener(e -> tambahMapel());
        editButton.addActionListener(e -> editMapel());
        hapusButton.addActionListener(e -> hapusMapel());
        refreshButton.addActionListener(e -> refreshMapel());
        kembaliButton.addActionListener(e -> kembali());

        buttonPanel.add(tambahButton);
        buttonPanel.add(editButton);
        buttonPanel.add(hapusButton);
        buttonPanel.add(refreshButton);
        buttonPanel.add(kembaliButton);
        add(buttonPanel, BorderLayout.EAST);
    }

    private void tambahMapel() {
        JPanel panel = new JPanel(new GridLayout(1, 2, 10, 10));
        JTextField mapelField = new JTextField();

        panel.add(new JLabel("Mapel:"));
        panel.add(mapelField);

        int result = JOptionPane.showConfirmDialog(this, panel, "Tambah Mapel", JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
            try {
                String mapel = mapelField.getText().trim();
                Mapel newMapel = new Mapel(nextId++, mapel); // ID akan di-set otomatis
                if (mapelDAO.addMapel(newMapel)) {
                    JOptionPane.showMessageDialog(this, "Mapel berhasil ditambahkan!");
                    refreshMapel();
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Gagal menambahkan mapel.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void editMapel() {
        int selectedRow = mapelTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Pilih mapel yang ingin diedit.", "Peringatan",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        Mapel mapel = mapelList.get(selectedRow);
        JPanel panel = new JPanel(new GridLayout(1, 2, 10, 10));
        JTextField mapelField = new JTextField(mapel.getMapel());

        panel.add(new JLabel("Mapel:"));
        panel.add(mapelField);

        int result = JOptionPane.showConfirmDialog(this, panel, "Edit Mapel", JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
            try {
                mapel.setMapel(mapelField.getText().trim());
                if (mapelDAO.updateMapel(mapel)) {
                    JOptionPane.showMessageDialog(this, "Mapel berhasil diperbarui!");
                    refreshMapel();
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Gagal memperbarui mapel.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void hapusMapel() {
        int selectedRow = mapelTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Pilih mapel yang ingin dihapus.", "Peringatan",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        Mapel selectedMapel = mapelList.get(selectedRow);
        int confirm = JOptionPane.showConfirmDialog(this, "Apakah Anda yakin ingin menghapus mapel ini?", "Konfirmasi",
                JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                if (mapelDAO.deleteMapel(selectedMapel.getKdMapel())) {
                    JOptionPane.showMessageDialog(this, "Mapel berhasil dihapus!");
                    refreshMapel();
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Gagal menghapus mapel.", "Error", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        }
    }

    private void refreshMapel() {
        mapelList.clear();
        try {
            mapelList.addAll(mapelDAO.getAllMapels());
            updateTable();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Gagal memuat data dari database.", "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void updateTable() {
        tableModel.setRowCount(0);
        for (int i = 0; i < mapelList.size(); i++) {
            Mapel mapel = mapelList.get(i);
            tableModel.addRow(new Object[] { i + 1, mapel.getMapel() });
        }
    }

    private void kembali() {
        dispose();
        new AdminGUI().setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MapelManagementGUI().setVisible(true));
    }
}
