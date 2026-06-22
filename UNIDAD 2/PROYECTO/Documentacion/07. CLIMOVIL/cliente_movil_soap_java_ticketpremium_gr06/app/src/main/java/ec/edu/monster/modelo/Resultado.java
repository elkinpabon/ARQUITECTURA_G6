package ec.edu.monster.modelo;

import java.io.Serializable;

public class Resultado implements Serializable {
    private boolean exito;
    private String mensaje;
    private Factura factura;

    public boolean isExito() { return exito; }
    public void setExito(boolean exito) { this.exito = exito; }
    public String getMensaje() { return mensaje; }
    public void setMensaje(String mensaje) { this.mensaje = mensaje; }
    public Factura getFactura() { return factura; }
    public void setFactura(Factura factura) { this.factura = factura; }
}
