package ec.edu.monster.persistencia;

import ec.edu.monster.modelo.Usuario;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/** Acceso a la tabla USUARIO. */
public class UsuarioDAO {

    private static final Logger LOG = Logger.getLogger(UsuarioDAO.class.getName());

    /** Valida credenciales (usuario/contrasena). Devuelve el Usuario o null. */
    public Usuario autenticar(String usuario, String contrasena) {
        String sql =
            "SELECT ID_USUARIO, USUARIO, NOMBRE, ROL " +
            "  FROM USUARIO " +
            " WHERE USUARIO = ? AND CONTRASENA = ?";

        Connection cn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            cn = ConexionBD.conectar();
            ps = cn.prepareStatement(sql);
            ps.setString(1, usuario);
            ps.setString(2, contrasena);
            rs = ps.executeQuery();
            if (rs.next()) {
                return new Usuario(
                    rs.getInt("ID_USUARIO"),
                    rs.getString("USUARIO"),
                    rs.getString("NOMBRE"),
                    rs.getString("ROL"));
            }
        } catch (SQLException e) {
            LOG.log(Level.SEVERE, "Error autenticando usuario " + usuario, e);
        } finally {
            ConexionBD.desconectar(rs);
            ConexionBD.desconectar(ps);
            ConexionBD.desconectar(cn);
        }
        return null;
    }

    /** Busca un usuario por id. Devuelve null si no existe. */
    public Usuario buscarPorId(int idUsuario) {
        String sql =
            "SELECT ID_USUARIO, USUARIO, NOMBRE, ROL FROM USUARIO WHERE ID_USUARIO = ?";
        Connection cn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            cn = ConexionBD.conectar();
            ps = cn.prepareStatement(sql);
            ps.setInt(1, idUsuario);
            rs = ps.executeQuery();
            if (rs.next()) {
                return new Usuario(
                    rs.getInt("ID_USUARIO"),
                    rs.getString("USUARIO"),
                    rs.getString("NOMBRE"),
                    rs.getString("ROL"));
            }
        } catch (SQLException e) {
            LOG.log(Level.SEVERE, "Error buscando usuario " + idUsuario, e);
        } finally {
            ConexionBD.desconectar(rs);
            ConexionBD.desconectar(ps);
            ConexionBD.desconectar(cn);
        }
        return null;
    }
}
