package ec.edu.monster.modelo;

import java.io.Serializable;

/** DTO de una sede/estadio del Mundial 2026. */
public class Estadio implements Serializable {
    private int idEstadio;
    private String nombreOficial;
    private String nombreFifa;
    private String ciudad;
    private String pais;
    private int capacidad;

    public Estadio() { }

    public Estadio(int idEstadio, String nombreOficial, String nombreFifa,
                   String ciudad, String pais, int capacidad) {
        this.idEstadio = idEstadio;
        this.nombreOficial = nombreOficial;
        this.nombreFifa = nombreFifa;
        this.ciudad = ciudad;
        this.pais = pais;
        this.capacidad = capacidad;
    }

    public int getIdEstadio()              { return idEstadio; }
    public void setIdEstadio(int v)        { this.idEstadio = v; }
    public String getNombreOficial()       { return nombreOficial; }
    public void setNombreOficial(String v) { this.nombreOficial = v; }
    public String getNombreFifa()          { return nombreFifa; }
    public void setNombreFifa(String v)    { this.nombreFifa = v; }
    public String getCiudad()              { return ciudad; }
    public void setCiudad(String v)        { this.ciudad = v; }
    public String getPais()                { return pais; }
    public void setPais(String v)          { this.pais = v; }
    public int getCapacidad()              { return capacidad; }
    public void setCapacidad(int v)        { this.capacidad = v; }
}
