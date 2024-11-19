package UAS_PBO_NEW.model;

public class Mapel {
    private int Kd_Mapel; 
    private String Mapel; 

    public Mapel(int Kd_Mapel, String Mapel) {
        this.Kd_Mapel = Kd_Mapel;
        this.Mapel = Mapel;
    }

    // Getters
    public int getKdMapel() { return Kd_Mapel; }
    public String getMapel() { return Mapel; }
    
    // Setters
    public void setKdMapel(int Kd_Mapel) { this.Kd_Mapel = Kd_Mapel; }
    public void setMapel(String Mapel) { this.Mapel = Mapel; }
}
