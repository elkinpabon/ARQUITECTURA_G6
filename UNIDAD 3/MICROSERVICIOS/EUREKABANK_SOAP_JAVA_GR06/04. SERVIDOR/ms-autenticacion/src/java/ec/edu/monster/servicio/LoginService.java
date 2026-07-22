package ec.edu.monster.servicio;

import ec.edu.monster.modelo.Usuario;
import ec.edu.monster.persistencia.UsuarioDAO;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LoginService {

    private static final Logger LOG = Logger.getLogger(LoginService.class.getName());
    private final UsuarioDAO usuarioDAO = new UsuarioDAO();

    public boolean login(String usuario, String clave) {
        if (usuario == null || usuario.isBlank() || clave == null || clave.isBlank()) {
            return false;
        }
        try {
            Usuario u = usuarioDAO.buscarPorUsuario(usuario.trim());
            if (u == null) {
                return false;
            }
            if (!"ACTIVO".equalsIgnoreCase(u.getEstado())) {
                return false;
            }
            String hash = SeguridadUtil.sha1(clave);
            return hash.equalsIgnoreCase(u.getClave());
        } catch (SQLException e) {
            LOG.log(Level.SEVERE, "Error autenticando usuario " + usuario, e);
            return false;
        }
    }

    public String clienteDe(String usuario) {
        if (usuario == null || usuario.isBlank()) {
            return "";
        }
        try {
            Usuario u = usuarioDAO.buscarPorUsuario(usuario.trim());
            if (u == null || u.getClienteCodigo() == null) {
                return "";
            }
            return u.getClienteCodigo();
        } catch (SQLException e) {
            LOG.log(Level.SEVERE, "Error obteniendo cliente del usuario " + usuario, e);
            return "";
        }
    }
}
