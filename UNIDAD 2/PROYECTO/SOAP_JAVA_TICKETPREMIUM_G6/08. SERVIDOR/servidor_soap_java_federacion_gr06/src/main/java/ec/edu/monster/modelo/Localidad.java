package ec.edu.monster.modelo;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * DTO de una categoria de boleto (Cat 1-4) para un partido, con su precio en USD
 * y la disponibilidad (stock) actual.
 */
public class Localidad implements Serializable {
    private int id;
    private int codigoPartido;
    private String categoria;      // CAT1 | CAT2 | CAT3 | CAT4
    private int disponibilidad;
    private BigDecimal precio;     // en USD

    public Localidad() { }

    public Localidad(int id, int codigoPartido, String categoria,
                     int disponibilidad, BigDecimal precio) {
        this.id = id;
        this.codigoPartido = codigoPartido;
        this.categoria = categoria;
        this.disponibilidad = disponibilidad;
        this.precio = precio;
    }

    public int getId()                   { return id; }
    public void setId(int id)            { this.id = id; }
    public int getCodigoPartido()        { return codigoPartido; }
    public void setCodigoPartido(int v)  { this.codigoPartido = v; }
    public String getCategoria()         { return categoria; }
    public void setCategoria(String v)   { this.categoria = v; }
    public int getDisponibilidad()       { return disponibilidad; }
    public void setDisponibilidad(int v) { this.disponibilidad = v; }
    public BigDecimal getPrecio()        { return precio; }
    public void setPrecio(BigDecimal v)  { this.precio = v; }
}
