package UAS_PBO_NEW.gui;

import javax.swing.*;
import java.awt.*;
import UAS_PBO_NEW.LoginGUI; // Impor untuk kembali ke LoginGUI

public class AdminGUI extends JFrame {
    public AdminGUI() {
        setTitle("Dashboard Admin");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JLabel titleLabel = new JLabel("Dashboard Admin", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        add(titleLabel, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel(new GridLayout(6, 1, 10, 10));
        JButton kelolaUserButton = new JButton("Kelola User");
        kelolaUserButton.addActionListener(e -> new UserManagementGUI().setVisible(true));
        JButton kelolaMapelButton = new JButton("Kelola Mapel");
        kelolaMapelButton.addActionListener(e -> new MapelManagementGUI().setVisible(true));
        JButton kelolaKelasButton = new JButton("Kelola Kelas");
        kelolaKelasButton.addActionListener(e -> new KelasManagementGUI().setVisible(true));
        JButton kelolaGuruButton = new JButton("Kelola Guru");
        kelolaGuruButton.addActionListener(e -> new GuruManagementGUI().setVisible(true));



        
        JButton logoutButton = new JButton("Logout");
        logoutButton.addActionListener(e -> logout());

        buttonPanel.add(kelolaUserButton);
        buttonPanel.add(kelolaMapelButton);
        buttonPanel.add(kelolaKelasButton);
        buttonPanel.add(kelolaGuruButton);
        buttonPanel.add(logoutButton);

        add(buttonPanel, BorderLayout.CENTER);
    }

    private void logout() {
        dispose(); // Tutup jendela admin
        new LoginGUI().setVisible(true); // Kembali ke tampilan login
    }
}
