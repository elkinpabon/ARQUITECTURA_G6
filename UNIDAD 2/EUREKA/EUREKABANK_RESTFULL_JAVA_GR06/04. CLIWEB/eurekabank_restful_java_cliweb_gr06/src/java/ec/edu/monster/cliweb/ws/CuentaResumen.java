package ec.edu.monster.cliweb.ws;

/** Resumen de cuenta (mapeado desde JSON). */
public class CuentaResumen {
    private String codigoCuenta;
    private String moneda;
    private double saldo;
    private String estado;
    private String codigoCliente;
    private String nombreCliente;

    public String getCodigoCuenta() { return codigoCuenta; }
    public void setCodigoCuenta(String v) { this.codigoCuenta = v; }
    public String getMoneda() { return moneda; }
    public void setMoneda(String v) { this.moneda = v; }
    public double getSaldo() { return saldo; }
    public void setSaldo(double v) { this.saldo = v; }
    public String getEstado() { return estado; }
    public void setEstado(String v) { this.estado = v; }
    public String getCodigoCliente() { return codigoCliente; }
    public void setCodigoCliente(String v) { this.codigoCliente = v; }
    public String getNombreCliente() { return nombreCliente; }
    public void setNombreCliente(String v) { this.nombreCliente = v; }
}
