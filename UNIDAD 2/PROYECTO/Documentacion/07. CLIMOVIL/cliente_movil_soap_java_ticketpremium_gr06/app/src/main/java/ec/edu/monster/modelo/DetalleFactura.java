package ec.edu.monster.modelo;

import java.io.Serializable;
import java.math.BigDecimal;

public class DetalleFactura implements Serializable {
    private int idDetalle;
    private int idFactura;
    private int codigoPartido;
    private int idSeccion;
    private String categoria;          // CAT1..CAT4
    private String fila;               // ej F12
    private String asientos;           // ej "12,13"
    private int cantidad;
    private BigDecimal precioUnitario;
    private BigDecimal total;
    private String descripcionPartido; // "Mexico vs Sudafrica"

    public int getIdDetalle() { return idDetalle; }
    public void setIdDetalle(int idDetalle) { this.idDetalle = idDetalle; }
    public int getIdFactura() { return idFactura; }
    public void setIdFactura(int idFactura) { this.idFactura = idFactura; }
    public int getCodigoPartido() { return codigoPartido; }
    public void setCodigoPartido(int codigoPartido) { this.codigoPartido = codigoPartido; }
    public int getIdSeccion() { return idSeccion; }
    public void setIdSeccion(int idSeccion) { this.idSeccion = idSeccion; }
    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }
    public String getFila() { return fila; }
    public void setFila(String fila) { this.fila = fila; }
    public String getAsientos() { return asientos; }
    public void setAsientos(String asientos) { this.asientos = asientos; }
    public int getCantidad() { return cantidad; }
    public void setCantidad(int cantidad) { this.cantidad = cantidad; }
    public BigDecimal getPrecioUnitario() { return precioUnitario; }
    public void setPrecioUnitario(BigDecimal precioUnitario) { this.precioUnitario = precioUnitario; }
    public BigDecimal getTotal() { return total; }
    public void setTotal(BigDecimal total) { this.total = total; }
    public String getDescripcionPartido() { return descripcionPartido; }
    public void setDescripcionPartido(String descripcionPartido) { this.descripcionPartido = descripcionPartido; }
}
