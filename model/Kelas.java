package UAS_PBO_NEW.model;

public class Kelas {
    private int Id_Kelas; 
    private String Kelas; 
    private int Tingkatan; 

    public Kelas(int Id_Kelas, String Kelas, int Tingkatan) {
        this.Id_Kelas = Id_Kelas;
        this.Kelas = Kelas;
        this.Tingkatan = Tingkatan;
    }

    // Getters
    public int getIdKelas() { return Id_Kelas; }
    public String getKelas() { return Kelas; }
    public int getTingkatan() { return Tingkatan; }

    // Setters
    public void setIdUser(int Id_Kelas) { this.Id_Kelas = Id_Kelas; }
    public void setKelas(String Kelas) { this.Kelas = Kelas; }
    public void setTingkatan(int Tingkatan) { this.Tingkatan = Tingkatan; }
}
