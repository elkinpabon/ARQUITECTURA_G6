package ec.edu.monster.modelo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class Factura implements Serializable {
    private int idFactura;
    private int idUsuario;
    private String usuarioNombre;
    private String fecha;
    private BigDecimal subtotal;
    private BigDecimal iva;
    private BigDecimal total;
    private String moneda;             // USD

    // Pago
    private String tipoPago;           // CONTADO | CREDITO
    private BigDecimal entrada;        // abono inicial (credito)
    private BigDecimal montoFinanciado;
    private int numCuotas;
    private BigDecimal tasaInteres;    // mensual, ej 0.0200

    private final List<DetalleFactura> detalles = new ArrayList<>();
    private final List<Cuota> amortizacion = new ArrayList<>();

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
    public String getMoneda() { return moneda; }
    public void setMoneda(String moneda) { this.moneda = moneda; }
    public String getTipoPago() { return tipoPago; }
    public void setTipoPago(String tipoPago) { this.tipoPago = tipoPago; }
    public BigDecimal getEntrada() { return entrada; }
    public void setEntrada(BigDecimal entrada) { this.entrada = entrada; }
    public BigDecimal getMontoFinanciado() { return montoFinanciado; }
    public void setMontoFinanciado(BigDecimal montoFinanciado) { this.montoFinanciado = montoFinanciado; }
    public int getNumCuotas() { return numCuotas; }
    public void setNumCuotas(int numCuotas) { this.numCuotas = numCuotas; }
    public BigDecimal getTasaInteres() { return tasaInteres; }
    public void setTasaInteres(BigDecimal tasaInteres) { this.tasaInteres = tasaInteres; }
    public List<DetalleFactura> getDetalles() { return detalles; }
    public List<Cuota> getAmortizacion() { return amortizacion; }

    public boolean esCredito() { return "CREDITO".equalsIgnoreCase(tipoPago); }
}
