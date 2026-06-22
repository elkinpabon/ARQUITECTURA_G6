package ec.edu.monster.modelo;

import java.io.Serializable;

public class SesionResultado implements Serializable {
    private boolean exito;
    private String mensaje;
    private Usuario usuario;

    public boolean isExito() { return exito; }
    public void setExito(boolean exito) { this.exito = exito; }
    public String getMensaje() { return mensaje; }
    public void setMensaje(String mensaje) { this.mensaje = mensaje; }
    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }
}
