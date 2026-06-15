package ec.edu.monster.modelo;

import java.io.Serializable;

public class Seccion implements Serializable {
    private int idSeccion;
    private int idLocalidad;
    private String codigoSeccion;
    private int numFilas;
    private int asientosPorFila;

    public int getIdSeccion() { return idSeccion; }
    public void setIdSeccion(int idSeccion) { this.idSeccion = idSeccion; }
    public int getIdLocalidad() { return idLocalidad; }
    public void setIdLocalidad(int idLocalidad) { this.idLocalidad = idLocalidad; }
    public String getCodigoSeccion() { return codigoSeccion; }
    public void setCodigoSeccion(String codigoSeccion) { this.codigoSeccion = codigoSeccion; }
    public int getNumFilas() { return numFilas; }
    public void setNumFilas(int numFilas) { this.numFilas = numFilas; }
    public int getAsientosPorFila() { return asientosPorFila; }
    public void setAsientosPorFila(int asientosPorFila) { this.asientosPorFila = asientosPorFila; }

    @Override public String toString() {
        return codigoSeccion + "  (" + numFilas + " filas x " + asientosPorFila + " asientos)";
    }
}
