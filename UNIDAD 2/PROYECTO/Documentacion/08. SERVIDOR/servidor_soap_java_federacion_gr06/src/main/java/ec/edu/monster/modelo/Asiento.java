package ec.edu.monster.modelo;

import java.io.Serializable;

/**
 * Estado de un asiento concreto de una seccion.
 * ESTADO: LIBRE | RESERVADO | OCUPADO. El servidor solo persiste los NO libres;
 * los que no aparecen se consideran LIBRE.
 */
public class Asiento implements Serializable {
    private int idSeccion;
    private String fila;
    private String asiento;
    private String estado;

    public Asiento() { }

    public Asiento(int idSeccion, String fila, String asiento, String estado) {
        this.idSeccion = idSeccion;
        this.fila = fila;
        this.asiento = asiento;
        this.estado = estado;
    }

    public int getIdSeccion()        { return idSeccion; }
    public void setIdSeccion(int v)  { this.idSeccion = v; }
    public String getFila()          { return fila; }
    public void setFila(String v)    { this.fila = v; }
    public String getAsiento()       { return asiento; }
    public void setAsiento(String v) { this.asiento = v; }
    public String getEstado()        { return estado; }
    public void setEstado(String v)  { this.estado = v; }
}
