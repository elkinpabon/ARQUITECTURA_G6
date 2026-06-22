package ec.edu.monster.modelo;

import java.io.Serializable;
import java.math.BigDecimal;

/** Linea de detalle de una factura (un item del carrito ya comprado). */
public class DetalleFactura implements Serializable {
    private int idDetalle;
    private int idFactura;
    private int codigoPartido;
    private int idSeccion;
    private String categoria;       // CAT1..CAT4
    private String fila;            // ej F12
    private String asientos;        // ej "12,13"
    private int cantidad;
    private BigDecimal precioUnitario;
    private BigDecimal total;
    private String descripcionPartido; // opcional: "Mexico vs Sudafrica" (consultas con JOIN)

    public DetalleFactura() { }

    public DetalleFactura(int idDetalle, int idFactura, int codigoPartido, int idSeccion,
                          String categoria, String fila, String asientos, int cantidad,
                          BigDecimal precioUnitario, BigDecimal total) {
        this.idDetalle = idDetalle;
        this.idFactura = idFactura;
        this.codigoPartido = codigoPartido;
        this.idSeccion = idSeccion;
        this.categoria = categoria;
        this.fila = fila;
        this.asientos = asientos;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
        this.total = total;
    }

    public int getIdDetalle()             { return idDetalle; }
    public void setIdDetalle(int v)       { this.idDetalle = v; }
    public int getIdFactura()             { return idFactura; }
    public void setIdFactura(int v)       { this.idFactura = v; }
    public int getCodigoPartido()         { return codigoPartido; }
    public void setCodigoPartido(int v)   { this.codigoPartido = v; }
    public int getIdSeccion()             { return idSeccion; }
    public void setIdSeccion(int v)       { this.idSeccion = v; }
    public String getCategoria()          { return categoria; }
    public void setCategoria(String v)    { this.categoria = v; }
    public String getFila()               { return fila; }
    public void setFila(String v)         { this.fila = v; }
    public String getAsientos()           { return asientos; }
    public void setAsientos(String v)     { this.asientos = v; }
    public int getCantidad()              { return cantidad; }
    public void setCantidad(int v)        { this.cantidad = v; }
    public BigDecimal getPrecioUnitario() { return precioUnitario; }
    public void setPrecioUnitario(BigDecimal v) { this.precioUnitario = v; }
    public BigDecimal getTotal()          { return total; }
    public void setTotal(BigDecimal v)    { this.total = v; }
    public String getDescripcionPartido()       { return descripcionPartido; }
    public void setDescripcionPartido(String v) { this.descripcionPartido = v; }
}
