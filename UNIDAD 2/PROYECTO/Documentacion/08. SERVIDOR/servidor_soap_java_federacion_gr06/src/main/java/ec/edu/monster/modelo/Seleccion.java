package ec.edu.monster.modelo;

import java.io.Serializable;

/** DTO de una seleccion nacional participante (grupo A-L). */
public class Seleccion implements Serializable {
    private int idSeleccion;
    private String nombre;
    private String grupo;   // A..L

    public Seleccion() { }

    public Seleccion(int idSeleccion, String nombre, String grupo) {
        this.idSeleccion = idSeleccion;
        this.nombre = nombre;
        this.grupo = grupo;
    }

    public int getIdSeleccion()        { return idSeleccion; }
    public void setIdSeleccion(int v)  { this.idSeleccion = v; }
    public String getNombre()          { return nombre; }
    public void setNombre(String v)    { this.nombre = v; }
    public String getGrupo()           { return grupo; }
    public void setGrupo(String v)     { this.grupo = v; }
}
