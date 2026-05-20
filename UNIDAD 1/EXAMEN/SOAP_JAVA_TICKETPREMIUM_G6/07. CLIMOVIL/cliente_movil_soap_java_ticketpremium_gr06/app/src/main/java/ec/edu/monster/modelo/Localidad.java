package ec.edu.monster.modelo;

import java.io.Serializable;
import java.math.BigDecimal;

public class Localidad implements Serializable {
    private int id;
    private int codigoPartido;
    private String codigoLocalidad;
    private int disponibilidad;
    private BigDecimal precio;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getCodigoPartido() { return codigoPartido; }
    public void setCodigoPartido(int codigoPartido) { this.codigoPartido = codigoPartido; }
    public String getCodigoLocalidad() { return codigoLocalidad; }
    public void setCodigoLocalidad(String codigoLocalidad) { this.codigoLocalidad = codigoLocalidad; }
    public int getDisponibilidad() { return disponibilidad; }
    public void setDisponibilidad(int disponibilidad) { this.disponibilidad = disponibilidad; }
    public BigDecimal getPrecio() { return precio; }
    public void setPrecio(BigDecimal precio) { this.precio = precio; }
}
