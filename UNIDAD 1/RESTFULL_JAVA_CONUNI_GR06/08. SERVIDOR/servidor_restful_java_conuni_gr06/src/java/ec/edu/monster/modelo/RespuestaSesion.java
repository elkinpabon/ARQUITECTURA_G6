package ec.edu.monster.modelo;

/**
 * DTO de respuesta para POST /api/sesion. Solo expone si la credencial es valida
 * y un mensaje opcional. No emite tokens — la sesion la administra cada cliente.
 */
public class RespuestaSesion {

    private boolean valido;
    private String mensaje;

    public RespuestaSesion() {
    }

    public RespuestaSesion(boolean valido, String mensaje) {
        this.valido = valido;
        this.mensaje = mensaje;
    }

    public boolean isValido() {
        return valido;
    }

    public void setValido(boolean valido) {
        this.valido = valido;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }
}
