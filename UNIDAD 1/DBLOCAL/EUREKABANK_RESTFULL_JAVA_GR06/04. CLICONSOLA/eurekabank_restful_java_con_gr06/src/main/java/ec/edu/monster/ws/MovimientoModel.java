package ec.edu.monster.ws;

/** Movimiento (mapeado desde JSON; conversión opcional). */
public class MovimientoModel {
    private int numeroMovimiento;
    private String fechaMovimiento;
    private String codigoEmpleado;
    private String codigoTipoMovimiento;
    private String tipoDescripcion;
    private double importeMovimiento;
    private String cuentaReferencia;
    private String monedaOrigen;
    private Double importeOrigen;
    private Double tasaAplicada;

    public int getNumeroMovimiento() { return numeroMovimiento; }
    public void setNumeroMovimiento(int v) { this.numeroMovimiento = v; }
    public String getFechaMovimiento() { return fechaMovimiento; }
    public void setFechaMovimiento(String v) { this.fechaMovimiento = v; }
    public String getCodigoEmpleado() { return codigoEmpleado; }
    public void setCodigoEmpleado(String v) { this.codigoEmpleado = v; }
    public String getCodigoTipoMovimiento() { return codigoTipoMovimiento; }
    public void setCodigoTipoMovimiento(String v) { this.codigoTipoMovimiento = v; }
    public String getTipoDescripcion() { return tipoDescripcion; }
    public void setTipoDescripcion(String v) { this.tipoDescripcion = v; }
    public double getImporteMovimiento() { return importeMovimiento; }
    public void setImporteMovimiento(double v) { this.importeMovimiento = v; }
    public String getCuentaReferencia() { return cuentaReferencia; }
    public void setCuentaReferencia(String v) { this.cuentaReferencia = v; }
    public String getMonedaOrigen() { return monedaOrigen; }
    public void setMonedaOrigen(String v) { this.monedaOrigen = v; }
    public Double getImporteOrigen() { return importeOrigen; }
    public void setImporteOrigen(Double v) { this.importeOrigen = v; }
    public Double getTasaAplicada() { return tasaAplicada; }
    public void setTasaAplicada(Double v) { this.tasaAplicada = v; }
}
