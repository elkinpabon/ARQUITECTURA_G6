package ec.edu.monster.modelo;

import java.io.Serializable;
import java.math.BigDecimal;

public class Factura implements Serializable {
    private int idFactura;
    private int idUsuario;
    private String usuarioNombre;
    private String fecha;
    private BigDecimal subtotal;
    private BigDecimal iva;
    private BigDecimal total;

    public int getIdFactura() { return idFactura; }
    public void setIdFactura(int idFactura) { this.idFactura = idFactura; }
    public int getIdUsuario() { return idUsuario; }
    public void setIdUsuario(int idUsuario) { this.idUsuario = idUsuario; }
    public String getUsuarioNombre() { return usuarioNombre; }
    public void setUsuarioNombre(String v) { this.usuarioNombre = v; }
    public String getFecha() { return fecha; }
    public void setFecha(String fecha) { this.fecha = fecha; }
    public BigDecimal getSubtotal() { return subtotal; }
    public void setSubtotal(BigDecimal subtotal) { this.subtotal = subtotal; }
    public BigDecimal getIva() { return iva; }
    public void setIva(BigDecimal iva) { this.iva = iva; }
    public BigDecimal getTotal() { return total; }
    public void setTotal(BigDecimal total) { this.total = total; }
}
