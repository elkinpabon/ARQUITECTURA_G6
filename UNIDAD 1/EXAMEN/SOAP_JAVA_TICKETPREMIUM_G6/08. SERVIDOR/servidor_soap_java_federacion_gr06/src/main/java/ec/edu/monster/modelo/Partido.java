package ec.edu.monster.modelo;

import java.io.Serializable;

/** DTO de un partido de futbol. */
public class Partido implements Serializable {
    private int codigo;
    private String equipoLocal;
    private String equipoVisita;
    private String fecha;   // formato ISO: yyyy-MM-dd HH:mm:ss
    private String lugar;

    public Partido() { }

    public Partido(int codigo, String equipoLocal, String equipoVisita,
                   String fecha, String lugar) {
        this.codigo = codigo;
        this.equipoLocal = equipoLocal;
        this.equipoVisita = equipoVisita;
        this.fecha = fecha;
        this.lugar = lugar;
    }

    public int getCodigo()             { return codigo; }
    public void setCodigo(int codigo)  { this.codigo = codigo; }
    public String getEquipoLocal()     { return equipoLocal; }
    public void setEquipoLocal(String v) { this.equipoLocal = v; }
    public String getEquipoVisita()    { return equipoVisita; }
    public void setEquipoVisita(String v){ this.equipoVisita = v; }
    public String getFecha()           { return fecha; }
    public void setFecha(String fecha) { this.fecha = fecha; }
    public String getLugar()           { return lugar; }
    public void setLugar(String lugar) { this.lugar = lugar; }
}
