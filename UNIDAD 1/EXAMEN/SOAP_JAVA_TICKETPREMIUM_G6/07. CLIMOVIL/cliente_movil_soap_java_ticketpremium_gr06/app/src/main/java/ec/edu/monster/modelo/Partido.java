package ec.edu.monster.modelo;

import java.io.Serializable;

public class Partido implements Serializable {
    private int codigo;
    private String equipoLocal;
    private String equipoVisita;
    private String fecha;
    private String lugar;

    public int getCodigo() { return codigo; }
    public void setCodigo(int codigo) { this.codigo = codigo; }
    public String getEquipoLocal() { return equipoLocal; }
    public void setEquipoLocal(String equipoLocal) { this.equipoLocal = equipoLocal; }
    public String getEquipoVisita() { return equipoVisita; }
    public void setEquipoVisita(String equipoVisita) { this.equipoVisita = equipoVisita; }
    public String getFecha() { return fecha; }
    public void setFecha(String fecha) { this.fecha = fecha; }
    public String getLugar() { return lugar; }
    public void setLugar(String lugar) { this.lugar = lugar; }

    @Override public String toString() {
        return "[" + codigo + "] " + equipoLocal + " vs " + equipoVisita + " - " + fecha;
    }
}
