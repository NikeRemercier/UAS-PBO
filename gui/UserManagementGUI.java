package UAS_PBO_NEW.gui;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.util.ArrayList;

import UAS_PBO_NEW.DatabaseConnection;
import UAS_PBO_NEW.dao.UserDAO;
import UAS_PBO_NEW.gui.role.AdminGUI;
import UAS_PBO_NEW.model.User; // Pastikan path ini sesuai dengan lokasi kelas User

public class UserManagementGUI extends JFrame {
    private DefaultTableModel tableModel;
    private JTable userTable;
    private ArrayList<User> userList; // List untuk menyimpan data pengguna
    private UserDAO userDAO; // DAO untuk akses data pengguna
    private int nextId = 1; // ID otomatis untuk pengguna baru

    public UserManagementGUI() {
        userList = new ArrayList<>(); // Inisialisasi daftar pengguna
        userDAO = new UserDAO(DatabaseConnection.getConnection()); // Inisialisasi UserDAO

        setTitle("Kelola Pengguna");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Modifikasi bagian tabel untuk menambah kolom "Nama"
        tableModel = new DefaultTableModel(new Object[] { "No","Username", "Password", "Role" }, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Semua sel tabel menjadi non-editable
            }
        };

        userTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(userTable);
        add(scrollPane, BorderLayout.CENTER);

        // Atur text alignment di tabel
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        userTable.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        userTable.getColumnModel().getColumn(1).setCellRenderer(centerRenderer);
        userTable.getColumnModel().getColumn(2).setCellRenderer(centerRenderer);
        userTable.getColumnModel().getColumn(3).setCellRenderer(centerRenderer);

        // Panel tombol
        JPanel buttonPanel = new JPanel(new GridLayout(5, 1, 10, 10));
        JButton tambahUserButton = new JButton("Tambah Pengguna");
        JButton editUserButton = new JButton("Edit Pengguna");
        JButton hapusUserButton = new JButton("Hapus Pengguna");
        JButton lihatUserButton = new JButton("Refresh Pengguna");
        JButton kembaliButton = new JButton("Kembali");

        // Tambahkan action listener untuk masing-masing tombol
        tambahUserButton.addActionListener(e -> tambahUser());
        editUserButton.addActionListener(e -> editUser());
        hapusUserButton.addActionListener(e -> hapusUser());
        lihatUserButton.addActionListener(e -> refreshUser());
        kembaliButton.addActionListener(e -> kembali());

        buttonPanel.add(tambahUserButton);
        buttonPanel.add(editUserButton);
        buttonPanel.add(hapusUserButton);
        buttonPanel.add(lihatUserButton);
        buttonPanel.add(kembaliButton);
        add(buttonPanel, BorderLayout.EAST);
    }

    private void tambahUser() {
        JPanel panel = new JPanel(new GridLayout(3, 2, 10, 10));
        JTextField usernameField = new JTextField();
        JPasswordField passwordField = new JPasswordField();
        String[] roles = { "Pilih Role", "siswa", "guru", "admin" };
        JComboBox<String> roleComboBox = new JComboBox<>(roles);

        panel.add(new JLabel("Username:"));
        panel.add(usernameField);
        panel.add(new JLabel("Password:"));
        panel.add(passwordField);
        panel.add(new JLabel("Role:"));
        panel.add(roleComboBox);

    
        int result = JOptionPane.showConfirmDialog(this, panel, "Tambah Pengguna", JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE);
    
        if (result == JOptionPane.OK_OPTION) {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());
            String role = (String) roleComboBox.getSelectedItem();
    
            if (!username.isEmpty() && !password.isEmpty() && !role.equals("Pilih Role")) {
                try {
                    User user = new User(nextId++, username, password, role);
                    if (userDAO.addUser(user)) {
                        userList.add(user);
                        updateTable();
                        JOptionPane.showMessageDialog(this, "Pengguna " + username + " telah ditambahkan ke database.");
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(this, "Gagal menambahkan data ke database!", "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Semua field harus diisi dan role harus dipilih!", "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void editUser() {
        int selectedRow = userTable.getSelectedRow();
    
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Pilih pengguna yang ingin diedit.", "Peringatan",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
    
        User user = userList.get(selectedRow);
    
        JPanel panel = new JPanel(new GridLayout(3, 2, 10, 10));
        JTextField usernameField = new JTextField(user.getUsername());
        JPasswordField passwordField = new JPasswordField(user.getPassword());
        String[] roles = { "siswa", "guru", "admin" };
        JComboBox<String> roleComboBox = new JComboBox<>(roles);
        roleComboBox.setSelectedItem(user.getRole());
    
        panel.add(new JLabel("Username:"));
        panel.add(usernameField);
        panel.add(new JLabel("Password:"));
        panel.add(passwordField);
        panel.add(new JLabel("Role:"));
        panel.add(roleComboBox);
    
        int result = JOptionPane.showConfirmDialog(this, panel, "Edit Pengguna", JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE);
    
        if (result == JOptionPane.OK_OPTION) {
            String usernameBaru = usernameField.getText();
            String passwordBaru = new String(passwordField.getPassword());
            String roleBaru = (String) roleComboBox.getSelectedItem();
    
            if (!usernameBaru.trim().isEmpty() && !passwordBaru.trim().isEmpty() && roleBaru != null) {
                try {
                    user.setUsername(usernameBaru);
                    user.setPassword(passwordBaru);
                    user.setRole(roleBaru);
    
                    if (userDAO.updateUser(user)) {
                        updateTable();
                        JOptionPane.showMessageDialog(this, "Data pengguna telah diedit.");
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(this, "Gagal mengedit data di database!", "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Semua field harus diisi!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }    

    private void hapusUser() {
        int selectedRow = userTable.getSelectedRow();

        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Pilih pengguna yang ingin dihapus.", "Peringatan",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        User user = userList.get(selectedRow);
        int confirm = JOptionPane.showConfirmDialog(this, "Apakah Anda yakin ingin menghapus pengguna ini?",
                "Konfirmasi",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                if (userDAO.deleteUser(user.getIdUser())) {
                    userList.remove(user);
                    updateTable();
                    JOptionPane.showMessageDialog(this, "Pengguna " + user.getUsername() + " telah dihapus.");
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Gagal menghapus data dari database!", "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void refreshUser() {
        userList.clear();
        try {
            userList.addAll(userDAO.getAllUsers());
            updateTable(); // Perbarui tabel dengan data terbaru
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Gagal memuat data dari database!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
        
    private void updateTable() {
        // Hapus semua data yang ada di tabel
        tableModel.setRowCount(0);
    
        // Tambahkan data pengguna dari `userList` ke tabel
        for (int i = 0; i < userList.size(); i++) {
            User user = userList.get(i);
            tableModel.addRow(new Object[] {
                    i + 1,  // Nomor urut
                    user.getUsername(),  // Username
                    user.getPassword(),  // Password
                    user.getRole()  // Role
            });
        }
    }
        
    private void kembali() {
        dispose();
        new AdminGUI().setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new UserManagementGUI().setVisible(true));
    }
}
