package ec.edu.monster.modelo;

import java.io.Serializable;

public class Asiento implements Serializable {
    private int idSeccion;
    private String fila;      // ej "F3"
    private String asiento;   // ej "7"
    private String estado;    // RESERVADO | OCUPADO

    public int getIdSeccion() { return idSeccion; }
    public void setIdSeccion(int idSeccion) { this.idSeccion = idSeccion; }
    public String getFila() { return fila; }
    public void setFila(String fila) { this.fila = fila; }
    public String getAsiento() { return asiento; }
    public void setAsiento(String asiento) { this.asiento = asiento; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
}
