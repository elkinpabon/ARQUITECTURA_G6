package com.example.eurekabank_soap_java.modelo;

import java.util.ArrayList;
import java.util.List;

/** Estado de sesión compartido (singleton simple). */
public class Sesion {
    private static final Sesion INSTANCIA = new Sesion();
    public static Sesion get() { return INSTANCIA; }
    private Sesion() { }

    private String usuario;
    private boolean admin;
    private String clienteAsignado = "";
    private List<CuentaResumen> cuentas = new ArrayList<>();

    public String getUsuario() { return usuario; }
    public void setUsuario(String v) { this.usuario = v; }
    public boolean isAdmin() { return admin; }
    public void setAdmin(boolean v) { this.admin = v; }
    public String getClienteAsignado() { return clienteAsignado; }
    public void setClienteAsignado(String v) { this.clienteAsignado = v == null ? "" : v; }
    public List<CuentaResumen> getCuentas() { return cuentas; }
    public void setCuentas(List<CuentaResumen> v) {
        this.cuentas = v == null ? new ArrayList<CuentaResumen>() : v;
    }

    public boolean cuentaPropia(String codigo) {
        if (codigo == null) return false;
        for (CuentaResumen c : cuentas)
            if (codigo.equals(c.getCodigoCuenta())) return true;
        return false;
    }

    public void limpiar() {
        usuario = null; admin = false; clienteAsignado = "";
        cuentas = new ArrayList<>();
    }
}
