package ec.edu.monster.modelo;

import java.io.Serializable;
import java.math.BigDecimal;

/** Linea de detalle de una factura. */
public class DetalleFactura implements Serializable {
    private int idDetalle;
    private int idFactura;
    private int codigoPartido;
    private String localidad;
    private int cantidad;
    private BigDecimal precioUnitario;
    private BigDecimal total;

    public DetalleFactura() { }

    public DetalleFactura(int idDetalle, int idFactura, int codigoPartido,
                          String localidad, int cantidad,
                          BigDecimal precioUnitario, BigDecimal total) {
        this.idDetalle = idDetalle;
        this.idFactura = idFactura;
        this.codigoPartido = codigoPartido;
        this.localidad = localidad;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
        this.total = total;
    }

    public int getIdDetalle()                  { return idDetalle; }
    public void setIdDetalle(int v)            { this.idDetalle = v; }
    public int getIdFactura()                  { return idFactura; }
    public void setIdFactura(int v)            { this.idFactura = v; }
    public int getCodigoPartido()              { return codigoPartido; }
    public void setCodigoPartido(int v)        { this.codigoPartido = v; }
    public String getLocalidad()               { return localidad; }
    public void setLocalidad(String localidad) { this.localidad = localidad; }
    public int getCantidad()                   { return cantidad; }
    public void setCantidad(int cantidad)      { this.cantidad = cantidad; }
    public BigDecimal getPrecioUnitario()      { return precioUnitario; }
    public void setPrecioUnitario(BigDecimal v){ this.precioUnitario = v; }
    public BigDecimal getTotal()               { return total; }
    public void setTotal(BigDecimal total)     { this.total = total; }
}
