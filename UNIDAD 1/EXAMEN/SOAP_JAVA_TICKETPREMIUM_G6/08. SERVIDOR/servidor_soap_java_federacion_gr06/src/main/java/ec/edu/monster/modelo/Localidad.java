package ec.edu.monster.modelo;

import java.io.Serializable;
import java.math.BigDecimal;

/** DTO de la disponibilidad de una localidad en un partido. */
public class Localidad implements Serializable {
    private int id;
    private int codigoPartido;
    private String codigoLocalidad;
    private int disponibilidad;
    private BigDecimal precio;

    public Localidad() { }

    public Localidad(int id, int codigoPartido, String codigoLocalidad,
                     int disponibilidad, BigDecimal precio) {
        this.id = id;
        this.codigoPartido = codigoPartido;
        this.codigoLocalidad = codigoLocalidad;
        this.disponibilidad = disponibilidad;
        this.precio = precio;
    }

    public int getId()                       { return id; }
    public void setId(int id)                { this.id = id; }
    public int getCodigoPartido()            { return codigoPartido; }
    public void setCodigoPartido(int v)      { this.codigoPartido = v; }
    public String getCodigoLocalidad()       { return codigoLocalidad; }
    public void setCodigoLocalidad(String v) { this.codigoLocalidad = v; }
    public int getDisponibilidad()           { return disponibilidad; }
    public void setDisponibilidad(int v)     { this.disponibilidad = v; }
    public BigDecimal getPrecio()            { return precio; }
    public void setPrecio(BigDecimal precio) { this.precio = precio; }
}
