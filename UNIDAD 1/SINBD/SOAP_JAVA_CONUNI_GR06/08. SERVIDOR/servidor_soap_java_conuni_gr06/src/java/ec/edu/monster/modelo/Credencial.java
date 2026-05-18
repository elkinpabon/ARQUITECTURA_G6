package ec.edu.monster.modelo;

public class Credencial {

    private final String usuario;
    private final String contrasena;

    public Credencial(String usuario, String contrasena) {
        this.usuario = usuario;
        this.contrasena = contrasena;
    }

    public String getUsuario() {
        return usuario;
    }

    public String getContrasena() {
        return contrasena;
    }

    public boolean coincide(Credencial otra) {
        if (otra == null) {
            return false;
        }
        return this.usuario != null && this.contrasena != null
                && this.usuario.equals(otra.usuario)
                && this.contrasena.equals(otra.contrasena);
    }
}
