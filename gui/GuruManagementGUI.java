package UAS_PBO_NEW.gui;

import UAS_PBO_NEW.dao.GuruDAO;
import UAS_PBO_NEW.model.Guru;
import UAS_PBO_NEW.DatabaseConnection;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.sql.Statement;
import java.sql.Connection;

public class GuruManagementGUI extends JFrame {
    private DefaultTableModel tableModel;
    private JTable guruTable;
    private List<Guru> guruList;
    private GuruDAO guruDAO;

    public GuruManagementGUI() {
        guruDAO = new GuruDAO(DatabaseConnection.getConnection());
        guruList = new ArrayList<>();

        setTitle("Kelola Guru");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Tabel untuk menampilkan data guru
        tableModel = new DefaultTableModel(new Object[] { "No", "NIP", "Nama", "Username", "Password" }, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Tabel tidak bisa diedit langsung
            }
        };

        guruTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(guruTable);
        add(scrollPane, BorderLayout.CENTER);

        // Atur alignment kolom
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        guruTable.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        guruTable.getColumnModel().getColumn(1).setCellRenderer(centerRenderer);
        guruTable.getColumnModel().getColumn(2).setCellRenderer(centerRenderer);
        guruTable.getColumnModel().getColumn(3).setCellRenderer(centerRenderer);
        guruTable.getColumnModel().getColumn(4).setCellRenderer(centerRenderer);


        // Tombol aksi
        JPanel buttonPanel = new JPanel(new GridLayout(5, 1, 10, 10));
        JButton tambahButton = new JButton("Tambah Guru");
        JButton editButton = new JButton("Edit Guru");
        JButton hapusButton = new JButton("Hapus Guru");
        JButton refreshButton = new JButton("Refresh");
        JButton kembaliButton = new JButton("Kembali");

        // Tambahkan listener untuk tombol
        tambahButton.addActionListener(e -> tambahGuru());
        editButton.addActionListener(e -> editGuru());
        hapusButton.addActionListener(e -> hapusGuru());
        refreshButton.addActionListener(e -> refreshGuru());
        kembaliButton.addActionListener(e -> kembali());

        buttonPanel.add(tambahButton);
        buttonPanel.add(editButton);
        buttonPanel.add(hapusButton);
        buttonPanel.add(refreshButton);
        buttonPanel.add(kembaliButton);
        add(buttonPanel, BorderLayout.EAST);

        // Muat data awal
        refreshGuru();
    }

    private void tambahGuru() {
        JPanel panel = new JPanel(new GridLayout(4, 2, 10, 10));
        JTextField nipField = new JTextField();
        JTextField namaField = new JTextField();
        JTextField usernameField = new JTextField();
        JPasswordField passwordField = new JPasswordField();
    
        panel.add(new JLabel("NIP:"));
        panel.add(nipField);
        panel.add(new JLabel("Nama:"));
        panel.add(namaField);
        panel.add(new JLabel("Username:"));
        panel.add(usernameField);
        panel.add(new JLabel("Password:"));
        panel.add(passwordField);
    
        int result = JOptionPane.showConfirmDialog(this, panel, "Tambah Guru", JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
            String nip = nipField.getText().trim();
            String nama = namaField.getText().trim();
            String username = usernameField.getText().trim();
            String password = new String(passwordField.getPassword()).trim();
    
            if (nip.isEmpty() || nama.isEmpty() || username.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Semua field harus diisi!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
    
            try {
                // Periksa apakah NIP sudah ada di database
                if (guruDAO.isNIPExists(nip)) {
                    JOptionPane.showMessageDialog(this, "NIP sudah digunakan!", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
    
                // Periksa apakah Username sudah ada di database
                if (guruDAO.isUsernameExists(username)) {
                    JOptionPane.showMessageDialog(this, "Username sudah digunakan!", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
    
                Guru newGuru = new Guru(nip, nama, 0, username, password);
                if (guruDAO.addGuru(newGuru)) {
                    JOptionPane.showMessageDialog(this, "Guru berhasil ditambahkan!");
                    refreshGuru();
                } else {
                    JOptionPane.showMessageDialog(this, "Gagal menambahkan guru.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Terjadi kesalahan saat menambahkan guru.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
            
    private void editGuru() {
        int selectedRow = guruTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Pilih guru yang ingin diedit.", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Ambil data guru yang dipilih
        Guru selectedGuru = guruList.get(selectedRow);
        JPanel panel = new JPanel(new GridLayout(4, 2, 10, 10));
        JTextField nipField = new JTextField(selectedGuru.getNIP());
        JTextField namaField = new JTextField(selectedGuru.getNama());
        JTextField usernameField = new JTextField(selectedGuru.getUsername());
        JPasswordField passwordField = new JPasswordField(selectedGuru.getPassword());

        panel.add(new JLabel("NIP:"));
        panel.add(nipField);
        panel.add(new JLabel("Nama:"));
        panel.add(namaField);
        panel.add(new JLabel("Username:"));
        panel.add(usernameField);
        panel.add(new JLabel("Password:"));
        panel.add(passwordField);

        int result = JOptionPane.showConfirmDialog(this, panel, "Edit Guru", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
            String nip = nipField.getText().trim();
            String nama = namaField.getText().trim();
            String username = usernameField.getText().trim();
            String password = new String(passwordField.getPassword()).trim();

            if (nip.isEmpty() || nama.isEmpty() || username.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Semua field harus diisi!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                // Perbarui data guru
                selectedGuru.setNIP(nip);
                selectedGuru.setNama(nama);
                selectedGuru.setUsername(username);
                selectedGuru.setPassword(password);

                // Panggil DAO untuk memperbarui
                if (guruDAO.updateGuru(selectedGuru)) {
                    JOptionPane.showMessageDialog(this, "Guru berhasil diperbarui!");
                    refreshGuru(); // Perbarui tampilan tabel
                } else {
                    JOptionPane.showMessageDialog(this, "Gagal memperbarui guru.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Terjadi kesalahan saat memperbarui data guru.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
        
    private void hapusGuru() {
        int selectedRow = guruTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Pilih guru yang ingin dihapus.", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }
    
        Guru selectedGuru = guruList.get(selectedRow);
        int confirm = JOptionPane.showConfirmDialog(this, "Apakah Anda yakin ingin menghapus guru ini?", "Konfirmasi", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                // Call deleteGuru method from DAO
                if (guruDAO.deleteGuru(selectedGuru.getNIP())) {
                    JOptionPane.showMessageDialog(this, "Guru berhasil dihapus!");
                    refreshGuru();
                } else {
                    JOptionPane.showMessageDialog(this, "Gagal menghapus guru.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Terjadi kesalahan saat menghapus data.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }    
    
    private void refreshGuru() {
        guruList.clear(); // Kosongkan daftar guru
        try (Connection conn = DatabaseConnection.getConnection()) {
            if (conn != null) {
                String query = "SELECT Guru.NIP, Guru.Nama, User.username, User.password "
                             + "FROM Guru "
                             + "JOIN User ON Guru.id_user = User.id_user";
                try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
                    while (rs.next()) {
                        String nip = rs.getString("NIP");
                        String nama = rs.getString("Nama");
                        String username = rs.getString("username");
                        String password = rs.getString("password");
    
                        guruList.add(new Guru(nip, nama, 0, username, password)); // Tambahkan ke daftar guru
                    }
                }
                updateTable(); // Perbarui tampilan tabel
            } else {
                JOptionPane.showMessageDialog(this, "Koneksi gagal ke database.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Gagal memuat data guru.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
        
    private void updateTable() {
        tableModel.setRowCount(0); // Hapus semua baris dari tabel
        
        int nomor = 1; // Nomor urut
        for (Guru guru : guruList) {
            tableModel.addRow(new Object[] {
                nomor++, 
                guru.getNIP(), 
                guru.getNama(), 
                guru.getUsername(), 
                guru.getPassword()
            });
        }
    }    
            
    private void kembali() {
        dispose();
        new AdminGUI().setVisible(true); // Navigasi ke Admin GUI
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new GuruManagementGUI().setVisible(true));
    }
}
