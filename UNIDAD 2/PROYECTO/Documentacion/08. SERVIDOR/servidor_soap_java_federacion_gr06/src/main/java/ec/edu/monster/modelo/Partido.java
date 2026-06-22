package ec.edu.monster.modelo;

import java.io.Serializable;

/**
 * DTO de un partido del Mundial 2026.
 * Las selecciones y la sede vienen normalizadas (FK), pero el DTO expone tambien
 * los nombres ya resueltos (equipoLocal/equipoVisita/lugar) para los clientes.
 */
public class Partido implements Serializable {
    private int codigo;
    private int idLocal;
    private int idVisita;
    private int idEstadio;
    private String equipoLocal;    // nombre de la seleccion local
    private String equipoVisita;   // nombre de la seleccion visitante
    private String fecha;          // yyyy-MM-dd HH:mm:ss
    private String grupo;          // A..L
    private String estadio;        // nombre oficial del estadio
    private String ciudad;
    private String pais;
    private String lugar;          // "Estadio X, Ciudad (Pais)" (compatibilidad)

    public Partido() { }

    public int getCodigo()               { return codigo; }
    public void setCodigo(int v)         { this.codigo = v; }
    public int getIdLocal()              { return idLocal; }
    public void setIdLocal(int v)        { this.idLocal = v; }
    public int getIdVisita()             { return idVisita; }
    public void setIdVisita(int v)       { this.idVisita = v; }
    public int getIdEstadio()            { return idEstadio; }
    public void setIdEstadio(int v)      { this.idEstadio = v; }
    public String getEquipoLocal()       { return equipoLocal; }
    public void setEquipoLocal(String v) { this.equipoLocal = v; }
    public String getEquipoVisita()      { return equipoVisita; }
    public void setEquipoVisita(String v){ this.equipoVisita = v; }
    public String getFecha()             { return fecha; }
    public void setFecha(String v)       { this.fecha = v; }
    public String getGrupo()             { return grupo; }
    public void setGrupo(String v)       { this.grupo = v; }
    public String getEstadio()           { return estadio; }
    public void setEstadio(String v)     { this.estadio = v; }
    public String getCiudad()            { return ciudad; }
    public void setCiudad(String v)      { this.ciudad = v; }
    public String getPais()              { return pais; }
    public void setPais(String v)        { this.pais = v; }
    public String getLugar()             { return lugar; }
    public void setLugar(String v)       { this.lugar = v; }
}
