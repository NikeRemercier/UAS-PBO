package UAS_PBO_NEW.gui.role;

import javax.swing.*;
import java.awt.*;
import UAS_PBO_NEW.LoginGUI; // Impor untuk kembali ke LoginGUI
import UAS_PBO_NEW.gui.JadwalManagementGUI;
import UAS_PBO_NEW.gui.GuruManagementGUI;
import UAS_PBO_NEW.gui.KelasManagementGUI;
import UAS_PBO_NEW.gui.MapelManagementGUI;
import UAS_PBO_NEW.gui.SiswaManagementGUI;
import UAS_PBO_NEW.gui.UserManagementGUI;
import UAS_PBO_NEW.gui.NilaiManagementGUI;


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

        JPanel buttonPanel = new JPanel(new GridLayout(8, 1, 10, 10));
        JButton kelolaUserButton = new JButton("Kelola User");
        kelolaUserButton.addActionListener(e -> new UserManagementGUI().setVisible(true));
        JButton kelolaMapelButton = new JButton("Kelola Mapel");
        kelolaMapelButton.addActionListener(e -> new MapelManagementGUI().setVisible(true));
        JButton kelolaKelasButton = new JButton("Kelola Kelas");
        kelolaKelasButton.addActionListener(e -> new KelasManagementGUI().setVisible(true));
        JButton kelolaGuruButton = new JButton("Kelola Guru");
        kelolaGuruButton.addActionListener(e -> new GuruManagementGUI().setVisible(true));
        JButton kelolaSiswaButton = new JButton("Kelola Siswa");
        kelolaSiswaButton.addActionListener(e -> new SiswaManagementGUI().setVisible(true));
        JButton kelolaJadwalButton = new JButton("Kelola Jadwal");
        kelolaJadwalButton.addActionListener(e -> new JadwalManagementGUI().setVisible(true));
        JButton kelolaNilaiButton = new JButton("Kelola Nilai");
        kelolaNilaiButton.addActionListener(e -> new NilaiManagementGUI().setVisible(true));


        
        JButton logoutButton = new JButton("Logout");
        logoutButton.addActionListener(e -> logout());

        buttonPanel.add(kelolaUserButton);
        buttonPanel.add(kelolaMapelButton);
        buttonPanel.add(kelolaKelasButton);
        buttonPanel.add(kelolaGuruButton);
        buttonPanel.add(kelolaSiswaButton);
        buttonPanel.add(kelolaJadwalButton);
        buttonPanel.add(kelolaNilaiButton);
        buttonPanel.add(logoutButton);

        add(buttonPanel, BorderLayout.CENTER);
    }

    private void logout() {
        dispose(); // Tutup jendela admin
        new LoginGUI().setVisible(true); // Kembali ke tampilan login
    }
}
