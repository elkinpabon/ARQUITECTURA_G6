package com.example.eurekabank_soap_java.modelo;

/** Respuesta de negocio {exito, mensaje, saldo}. */
public class Resultado {
    private boolean exito;
    private String mensaje;
    private double saldo;

    public Resultado() { }
    public Resultado(boolean exito, String mensaje) {
        this.exito = exito; this.mensaje = mensaje;
    }

    public boolean isExito() { return exito; }
    public void setExito(boolean v) { this.exito = v; }
    public String getMensaje() { return mensaje; }
    public void setMensaje(String v) { this.mensaje = v; }
    public double getSaldo() { return saldo; }
    public void setSaldo(double v) { this.saldo = v; }
}
