package ec.edu.monster.modelo;

import java.io.Serializable;
import java.math.BigDecimal;

public class ComprobanteCompra implements Serializable {
    private int idFactura;
    private String codigoR;
    private String fecha;
    private String usuario;
    private String partido;
    private String localidad;
    private int cantidad;
    private BigDecimal subtotal;
    private BigDecimal iva;
    private BigDecimal total;

    public int getIdFactura() { return idFactura; }
    public void setIdFactura(int idFactura) { this.idFactura = idFactura; }
    public String getCodigoR() { return codigoR; }
    public void setCodigoR(String codigoR) { this.codigoR = codigoR; }
    public String getFecha() { return fecha; }
    public void setFecha(String fecha) { this.fecha = fecha; }
    public String getUsuario() { return usuario; }
    public void setUsuario(String usuario) { this.usuario = usuario; }
    public String getPartido() { return partido; }
    public void setPartido(String partido) { this.partido = partido; }
    public String getLocalidad() { return localidad; }
    public void setLocalidad(String localidad) { this.localidad = localidad; }
    public int getCantidad() { return cantidad; }
    public void setCantidad(int cantidad) { this.cantidad = cantidad; }
    public BigDecimal getSubtotal() { return subtotal; }
    public void setSubtotal(BigDecimal subtotal) { this.subtotal = subtotal; }
    public BigDecimal getIva() { return iva; }
    public void setIva(BigDecimal iva) { this.iva = iva; }
    public BigDecimal getTotal() { return total; }
    public void setTotal(BigDecimal total) { this.total = total; }
}
