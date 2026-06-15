package ec.edu.monster.modelo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Cabecera de la factura (= un carrito de compra). Soporta pago a CONTADO o a
 * CREDITO; en credito lleva entrada, monto financiado, numero de cuotas, tasa
 * mensual y la tabla de amortizacion (lista de cuotas). Tambien puede llevar el
 * detalle de items comprados (poblado en consultas con JOIN).
 */
public class Factura implements Serializable {
    private int idFactura;
    private int idUsuario;
    private String usuarioNombre;     // poblado en consultas con JOIN
    private String fecha;             // yyyy-MM-dd HH:mm:ss
    private BigDecimal subtotal;
    private BigDecimal iva;
    private BigDecimal total;
    private String moneda;            // USD

    // --- pago ---
    private String tipoPago;          // CONTADO | CREDITO
    private BigDecimal entrada;       // abono inicial (credito)
    private BigDecimal montoFinanciado;
    private int numCuotas;
    private BigDecimal tasaInteres;   // mensual, ej 0.0200

    private List<DetalleFactura> detalles = new ArrayList<>();
    private List<Cuota> amortizacion = new ArrayList<>();

    public Factura() { }

    /** Constructor compacto para listados simples (CONTADO por defecto). */
    public Factura(int idFactura, int idUsuario, String usuarioNombre, String fecha,
                   BigDecimal subtotal, BigDecimal iva, BigDecimal total) {
        this.idFactura = idFactura;
        this.idUsuario = idUsuario;
        this.usuarioNombre = usuarioNombre;
        this.fecha = fecha;
        this.subtotal = subtotal;
        this.iva = iva;
        this.total = total;
        this.moneda = "USD";
        this.tipoPago = "CONTADO";
    }

    public int getIdFactura()              { return idFactura; }
    public void setIdFactura(int v)        { this.idFactura = v; }
    public int getIdUsuario()              { return idUsuario; }
    public void setIdUsuario(int v)        { this.idUsuario = v; }
    public String getUsuarioNombre()       { return usuarioNombre; }
    public void setUsuarioNombre(String v) { this.usuarioNombre = v; }
    public String getFecha()               { return fecha; }
    public void setFecha(String v)         { this.fecha = v; }
    public BigDecimal getSubtotal()        { return subtotal; }
    public void setSubtotal(BigDecimal v)  { this.subtotal = v; }
    public BigDecimal getIva()             { return iva; }
    public void setIva(BigDecimal v)       { this.iva = v; }
    public BigDecimal getTotal()           { return total; }
    public void setTotal(BigDecimal v)     { this.total = v; }
    public String getMoneda()              { return moneda; }
    public void setMoneda(String v)        { this.moneda = v; }
    public String getTipoPago()            { return tipoPago; }
    public void setTipoPago(String v)      { this.tipoPago = v; }
    public BigDecimal getEntrada()         { return entrada; }
    public void setEntrada(BigDecimal v)   { this.entrada = v; }
    public BigDecimal getMontoFinanciado() { return montoFinanciado; }
    public void setMontoFinanciado(BigDecimal v) { this.montoFinanciado = v; }
    public int getNumCuotas()              { return numCuotas; }
    public void setNumCuotas(int v)        { this.numCuotas = v; }
    public BigDecimal getTasaInteres()     { return tasaInteres; }
    public void setTasaInteres(BigDecimal v) { this.tasaInteres = v; }
    public List<DetalleFactura> getDetalles()        { return detalles; }
    public void setDetalles(List<DetalleFactura> v)  { this.detalles = v; }
    public List<Cuota> getAmortizacion()             { return amortizacion; }
    public void setAmortizacion(List<Cuota> v)       { this.amortizacion = v; }
}
