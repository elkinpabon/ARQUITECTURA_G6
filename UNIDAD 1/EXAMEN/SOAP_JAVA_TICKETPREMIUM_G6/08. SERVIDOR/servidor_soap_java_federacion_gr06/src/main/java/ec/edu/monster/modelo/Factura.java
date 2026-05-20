package ec.edu.monster.modelo;

import java.io.Serializable;
import java.math.BigDecimal;

/** Cabecera de la factura de venta de boletos. */
public class Factura implements Serializable {
    private int idFactura;
    private int idUsuario;
    private String fecha;     // formato ISO yyyy-MM-dd HH:mm:ss
    private BigDecimal subtotal;
    private BigDecimal iva;
    private BigDecimal total;

    public Factura() { }

    public Factura(int idFactura, int idUsuario, String fecha, BigDecimal subtotal,
                   BigDecimal iva, BigDecimal total) {
        this.idFactura = idFactura;
        this.idUsuario = idUsuario;
        this.fecha = fecha;
        this.subtotal = subtotal;
        this.iva = iva;
        this.total = total;
    }

    public int getIdFactura()                 { return idFactura; }
    public void setIdFactura(int v)           { this.idFactura = v; }
    public int getIdUsuario()                 { return idUsuario; }
    public void setIdUsuario(int v)           { this.idUsuario = v; }
    public String getFecha()                  { return fecha; }
    public void setFecha(String fecha)        { this.fecha = fecha; }
    public BigDecimal getSubtotal()           { return subtotal; }
    public void setSubtotal(BigDecimal v)     { this.subtotal = v; }
    public BigDecimal getIva()                { return iva; }
    public void setIva(BigDecimal iva)        { this.iva = iva; }
    public BigDecimal getTotal()              { return total; }
    public void setTotal(BigDecimal total)    { this.total = total; }
}
