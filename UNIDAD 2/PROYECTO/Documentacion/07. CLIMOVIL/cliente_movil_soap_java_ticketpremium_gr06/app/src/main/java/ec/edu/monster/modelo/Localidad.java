package ec.edu.monster.modelo;

import java.io.Serializable;
import java.math.BigDecimal;

public class Localidad implements Serializable {
    private int id;
    private int codigoPartido;
    private String categoria;      // CAT1 | CAT2 | CAT3 | CAT4
    private int disponibilidad;
    private BigDecimal precio;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getCodigoPartido() { return codigoPartido; }
    public void setCodigoPartido(int codigoPartido) { this.codigoPartido = codigoPartido; }
    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }
    public int getDisponibilidad() { return disponibilidad; }
    public void setDisponibilidad(int disponibilidad) { this.disponibilidad = disponibilidad; }
    public BigDecimal getPrecio() { return precio; }
    public void setPrecio(BigDecimal precio) { this.precio = precio; }

    /** Alias de compatibilidad con codigo previo. */
    public String getCodigoLocalidad() { return categoria; }
    public void setCodigoLocalidad(String categoria) { this.categoria = categoria; }
}
