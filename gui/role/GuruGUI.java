package UAS_PBO_NEW.gui.role;

import javax.swing.*;
import java.awt.*;
import UAS_PBO_NEW.LoginGUI; // Import to go back to LoginGUI
import UAS_PBO_NEW.gui.JadwalGuruManagementGUI;
import UAS_PBO_NEW.gui.NilaiGuruManagementGUI;

public class GuruGUI extends JFrame {

    public GuruGUI() {
        setTitle("Dashboard Guru");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JLabel titleLabel = new JLabel("Dashboard Guru", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        add(titleLabel, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel(new GridLayout(3, 1, 10, 10));
        JButton kelolaJadwalButton = new JButton("Lihat Jadwal");
        kelolaJadwalButton.addActionListener(e -> new JadwalGuruManagementGUI().setVisible(true));
        JButton kelolaNilaiButton = new JButton("Kelola Nilai");
        kelolaNilaiButton.addActionListener(e -> new NilaiGuruManagementGUI().setVisible(true));

        
        JButton logoutButton = new JButton("Logout");
        logoutButton.addActionListener(e -> logout());

        buttonPanel.add(kelolaJadwalButton);
        buttonPanel.add(kelolaNilaiButton);
        buttonPanel.add(logoutButton);

        add(buttonPanel, BorderLayout.CENTER);
    }

    private void logout() {
        dispose(); // Close Guru's window
        new LoginGUI().setVisible(true); // Go back to the login screen
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            GuruGUI guruGUI = new GuruGUI();
            guruGUI.setVisible(true);
        });
    }
}
