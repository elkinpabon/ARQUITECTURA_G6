package com.example.eurekabank_soap_java.modelo;

/** Movimiento con detalle de conversión opcional. */
public class Movimiento {
    private int numero;
    private String fecha;
    private String tipoCodigo;
    private String tipoDescripcion;
    private double importe;
    private String cuentaReferencia;
    private String monedaOrigen;   // null = sin conversión
    private Double importeOrigen;
    private Double tasaAplicada;

    public int getNumero() { return numero; }
    public void setNumero(int v) { this.numero = v; }
    public String getFecha() { return fecha; }
    public void setFecha(String v) { this.fecha = v; }
    public String getTipoCodigo() { return tipoCodigo; }
    public void setTipoCodigo(String v) { this.tipoCodigo = v; }
    public String getTipoDescripcion() { return tipoDescripcion; }
    public void setTipoDescripcion(String v) { this.tipoDescripcion = v; }
    public double getImporte() { return importe; }
    public void setImporte(double v) { this.importe = v; }
    public String getCuentaReferencia() { return cuentaReferencia; }
    public void setCuentaReferencia(String v) { this.cuentaReferencia = v; }
    public String getMonedaOrigen() { return monedaOrigen; }
    public void setMonedaOrigen(String v) { this.monedaOrigen = v; }
    public Double getImporteOrigen() { return importeOrigen; }
    public void setImporteOrigen(Double v) { this.importeOrigen = v; }
    public Double getTasaAplicada() { return tasaAplicada; }
    public void setTasaAplicada(Double v) { this.tasaAplicada = v; }

    public boolean tieneConversion() {
        return monedaOrigen != null && !monedaOrigen.trim().isEmpty();
    }
}
