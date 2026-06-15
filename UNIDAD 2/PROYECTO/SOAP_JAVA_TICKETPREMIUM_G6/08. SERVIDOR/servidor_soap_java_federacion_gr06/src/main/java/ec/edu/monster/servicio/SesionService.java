package ec.edu.monster.servicio;

import ec.edu.monster.modelo.SesionResultado;
import ec.edu.monster.modelo.Usuario;
import ec.edu.monster.persistencia.UsuarioDAO;

/** Logica de autenticacion. */
public class SesionService {

    private final UsuarioDAO usuarioDAO = new UsuarioDAO();

    public SesionResultado iniciarSesion(String usuario, String contrasena) {
        if (usuario == null || usuario.isBlank() ||
            contrasena == null || contrasena.isBlank()) {
            return SesionResultado.error("Usuario y contrasena son obligatorios.");
        }
        Usuario u = usuarioDAO.autenticar(usuario.trim(), contrasena);
        if (u == null) {
            return SesionResultado.error("Credenciales invalidas.");
        }
        return SesionResultado.ok(u);
    }
}
