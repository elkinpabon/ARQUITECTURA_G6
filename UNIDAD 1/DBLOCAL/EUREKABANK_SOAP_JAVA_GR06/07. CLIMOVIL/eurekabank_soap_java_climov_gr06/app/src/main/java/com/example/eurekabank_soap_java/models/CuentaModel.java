package com.example.eurekabank_soap_java.models;

public class CuentaModel {
    private String cuenta;
    private double monto;

    // Getters
    public String getCuenta() { return cuenta; }
    public double getMonto() { return monto; }

    // Setters
    public void setCuenta(String cuenta) { this.cuenta = cuenta; }
    public void setMonto(double monto) { this.monto = monto; }
}