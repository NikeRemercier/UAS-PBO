package UAS_PBO_NEW;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import UAS_PBO_NEW.gui.role.AdminGUI;
import UAS_PBO_NEW.gui.role.GuruGUI; // Import GuruGUI

public class LoginGUI extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;

    public LoginGUI() {
        setTitle("Login Sistem Akademik");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(true); // Mengaktifkan perubahan ukuran jendela

        // Menggunakan layout BorderLayout
        setLayout(new BorderLayout());

        // Judul
        JLabel titleLabel = new JLabel("Sistem Akademik Login", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 22));
        titleLabel.setForeground(new Color(51, 102, 255)); // Warna judul
        add(titleLabel, BorderLayout.NORTH);

        // Panel untuk form login
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20)); // Margin panel

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10); // Padding antar elemen
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Username Label dan Field
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("Username:"), gbc);

        gbc.gridx = 1;
        usernameField = new JTextField(15);
        gbc.weightx = 1.0; // Membuat field lebih fleksibel
        panel.add(usernameField, gbc);

        // Password Label dan Field
        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(new JLabel("Password:"), gbc);

        gbc.gridx = 1;
        passwordField = new JPasswordField(15);
        gbc.weightx = 1.0; // Membuat field lebih fleksibel
        panel.add(passwordField, gbc);

        // Tombol Login
        gbc.gridx = 1;
        gbc.gridy = 2;
        JButton loginButton = new JButton("Login");
        loginButton.setPreferredSize(new Dimension(100, 30)); // Ukuran tombol
        loginButton.addActionListener(new LoginAction());
        panel.add(loginButton, gbc);

        add(panel, BorderLayout.CENTER);

        // Menambahkan footer
        JLabel footerLabel = new JLabel("© 2024 Sistem Akademik", JLabel.CENTER);
        footerLabel.setForeground(Color.GRAY);
        add(footerLabel, BorderLayout.SOUTH);
    }

    private class LoginAction implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());

            try (Connection conn = DatabaseConnection.getConnection()) {
                String query = "SELECT role FROM user WHERE username=? AND password=?";
                PreparedStatement stmt = conn.prepareStatement(query);
                stmt.setString(1, username);
                stmt.setString(2, password);
                ResultSet rs = stmt.executeQuery();

                if (rs.next()) {
                    String role = rs.getString("role");
                    switch (role) {
                        case "admin":
                            new AdminGUI().setVisible(true);
                            break;
                        case "guru":
                            new GuruGUI().setVisible(true); // Navigate to GuruGUI if role is guru
                            break;
                        default:
                            JOptionPane.showMessageDialog(null, "Role tidak dikenal.");
                            break;
                    }
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(null, "Username atau password salah.");
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginGUI().setVisible(true));
    }
}
