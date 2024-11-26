package UAS_PBO_NEW.model;

public class Guru {
    private int id_guru;
    private String NIP;
    private String Nama;
    private int id_user;
    private String username; // Tambahkan atribut username
    private String password; // Tambahkan atribut password

    public Guru(int id_guru, String NIP, String Nama, int id_user, String username, String password) {
        this.id_guru = id_guru;
        this.NIP = NIP;
        this.Nama = Nama;
        this.id_user = id_user;
        this.username = username;
        this.password = password;
    }

    // Getters
    public int getidguru() { return id_guru; }
    public String getNIP() { return NIP; }
    public String getNama() { return Nama; }
    public int getiduser() { return id_user; }
    public String getUsername() { return username; } // Getter untuk username
    public String getPassword() { return password; } // Getter untuk password

    // Setters
    public void setidguru(int id_guru) { this.id_guru = id_guru; }
    public void setNIP(String NIP) { this.NIP = NIP; }
    public void setNama(String Nama) { this.Nama = Nama; }
    public void setiduser(int id_user) { this.id_user = id_user; }
    public void setUsername(String username) { this.username = username; } // Setter untuk username
    public void setPassword(String password) { this.password = password; } // Setter untuk password
}
