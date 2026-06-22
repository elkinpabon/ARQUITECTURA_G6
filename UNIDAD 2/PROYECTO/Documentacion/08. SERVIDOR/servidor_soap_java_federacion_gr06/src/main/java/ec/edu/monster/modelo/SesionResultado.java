package ec.edu.monster.modelo;

import java.io.Serializable;

/** Respuesta de la operacion iniciarSesion. */
public class SesionResultado implements Serializable {
    private boolean exito;
    private String mensaje;
    private Usuario usuario;   // poblado cuando exito==true

    public SesionResultado() { }

    public SesionResultado(boolean exito, String mensaje, Usuario usuario) {
        this.exito = exito;
        this.mensaje = mensaje;
        this.usuario = usuario;
    }

    public static SesionResultado ok(Usuario u) {
        return new SesionResultado(true, "Bienvenido " + u.getNombre(), u);
    }

    public static SesionResultado error(String mensaje) {
        return new SesionResultado(false, mensaje, null);
    }

    public boolean isExito()              { return exito; }
    public void setExito(boolean exito)   { this.exito = exito; }
    public String getMensaje()            { return mensaje; }
    public void setMensaje(String m)      { this.mensaje = m; }
    public Usuario getUsuario()           { return usuario; }
    public void setUsuario(Usuario u)     { this.usuario = u; }
}
