package ec.edu.monster.modelo;

import java.io.Serializable;

/**
 * DTO de una seccion fisica dentro de una categoria (estilo StubHub).
 * Pertenece a una LOCALIDAD_PARTIDO (categoria Cat 1-4) y define el mapa de
 * asientos (num de filas x asientos por fila) entre los que el cliente elige.
 */
public class Seccion implements Serializable {
    private int idSeccion;
    private int idLocalidad;
    private String codigoSeccion;
    private int numFilas;
    private int asientosPorFila;

    public Seccion() { }

    public Seccion(int idSeccion, int idLocalidad, String codigoSeccion,
                   int numFilas, int asientosPorFila) {
        this.idSeccion = idSeccion;
        this.idLocalidad = idLocalidad;
        this.codigoSeccion = codigoSeccion;
        this.numFilas = numFilas;
        this.asientosPorFila = asientosPorFila;
    }

    public int getIdSeccion()          { return idSeccion; }
    public void setIdSeccion(int v)    { this.idSeccion = v; }
    public int getIdLocalidad()        { return idLocalidad; }
    public void setIdLocalidad(int v)  { this.idLocalidad = v; }
    public String getCodigoSeccion()   { return codigoSeccion; }
    public void setCodigoSeccion(String v) { this.codigoSeccion = v; }
    public int getNumFilas()           { return numFilas; }
    public void setNumFilas(int v)     { this.numFilas = v; }
    public int getAsientosPorFila()    { return asientosPorFila; }
    public void setAsientosPorFila(int v) { this.asientosPorFila = v; }
}
