package ec.edu.monster.modelo;

import java.io.Serializable;

/** Respuesta generica de operaciones (registrarVenta, etc.). */
public class Resultado implements Serializable {
    private boolean exito;
    private String mensaje;
    private Factura factura;   // poblada cuando exito==true y la operacion crea factura

    public Resultado() { }

    public Resultado(boolean exito, String mensaje) {
        this.exito = exito;
        this.mensaje = mensaje;
    }

    public Resultado(boolean exito, String mensaje, Factura factura) {
        this.exito = exito;
        this.mensaje = mensaje;
        this.factura = factura;
    }

    public static Resultado ok(String mensaje, Factura f) {
        return new Resultado(true, mensaje, f);
    }

    public static Resultado error(String mensaje) {
        return new Resultado(false, mensaje);
    }

    public boolean isExito()              { return exito; }
    public void setExito(boolean exito)   { this.exito = exito; }
    public String getMensaje()            { return mensaje; }
    public void setMensaje(String mensaje){ this.mensaje = mensaje; }
    public Factura getFactura()           { return factura; }
    public void setFactura(Factura f)     { this.factura = f; }
}
