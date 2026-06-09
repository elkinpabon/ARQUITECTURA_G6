package com.example.eurekabank_soap_java.modelo;

/** Resumen de cliente (para el admin). */
public class ClienteResumen {
    private String codigo;
    private String dni;
    private String nombre;

    public String getCodigo() { return codigo; }
    public void setCodigo(String v) { this.codigo = v; }
    public String getDni() { return dni; }
    public void setDni(String v) { this.dni = v; }
    public String getNombre() { return nombre; }
    public void setNombre(String v) { this.nombre = v; }

    @Override public String toString() {
        return codigo + " · DNI " + dni + " · " + nombre;
    }
}
