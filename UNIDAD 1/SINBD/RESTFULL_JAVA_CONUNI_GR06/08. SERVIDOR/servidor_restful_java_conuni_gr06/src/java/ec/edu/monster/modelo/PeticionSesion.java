package ec.edu.monster.modelo;

/**
 * DTO de entrada para POST /api/sesion. Mapea el JSON {usuario, contrasena}.
 */
public class PeticionSesion {

    private String usuario;
    private String contrasena;

    public PeticionSesion() {
    }

    public PeticionSesion(String usuario, String contrasena) {
        this.usuario = usuario;
        this.contrasena = contrasena;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getContrasena() {
        return contrasena;
    }

    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }
}
