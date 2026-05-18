package com.example.eurekabank_soap_java.models;

public class MovimientoModel {
    private String codigoCuenta;
    private String codigoEmpleado;
    private String codigoTipoMovimiento;
    private String descTipoMovimiento;
    private String cuentaReferencia;
    private String fechaMovimiento;
    private double importeMovimiento;
    private int numeroMovimiento;

    // Getters
    public String getCodigoCuenta() { return codigoCuenta; }
    public String getCodigoEmpleado() { return codigoEmpleado; }
    public String getCodigoTipoMovimiento() { return codigoTipoMovimiento; }
    public String getDescTipoMovimiento() { return descTipoMovimiento; }
    public String getCuentaReferencia() { return cuentaReferencia; }
    public String getFechaMovimiento() { return fechaMovimiento; }
    public double getImporteMovimiento() { return importeMovimiento; }
    public int getNumeroMovimiento() { return numeroMovimiento; }

    // Setters
    public void setCodigoCuenta(String codigoCuenta) { this.codigoCuenta = codigoCuenta; }
    public void setCodigoEmpleado(String codigoEmpleado) { this.codigoEmpleado = codigoEmpleado; }
    public void setCodigoTipoMovimiento(String codigoTipoMovimiento) { this.codigoTipoMovimiento = codigoTipoMovimiento; }
    public void setDescTipoMovimiento(String descTipoMovimiento) { this.descTipoMovimiento = descTipoMovimiento; }
    public void setCuentaReferencia(String cuentaReferencia) { this.cuentaReferencia = cuentaReferencia; }
    public void setFechaMovimiento(String fechaMovimiento) { this.fechaMovimiento = fechaMovimiento; }
    public void setImporteMovimiento(double importeMovimiento) { this.importeMovimiento = importeMovimiento; }
    public void setNumeroMovimiento(int numeroMovimiento) { this.numeroMovimiento = numeroMovimiento; }
}
