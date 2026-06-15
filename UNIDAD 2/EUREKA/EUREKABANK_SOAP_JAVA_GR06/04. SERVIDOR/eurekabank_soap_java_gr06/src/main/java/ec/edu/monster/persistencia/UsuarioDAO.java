package ec.edu.monster.persistencia;

import ec.edu.monster.modelo.Usuario;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/** Acceso a datos de la tabla `usuario`. */
public class UsuarioDAO {

    /**
     * Busca un usuario por su nombre de usuario.
     * Abre y cierra su propia conexion (operacion de solo lectura).
     *
     * @return el Usuario encontrado, o null si no existe.
     */
    public Usuario buscarPorUsuario(String usuario) throws SQLException {
        String sql = "SELECT chr_emplcodigo, vch_emplusuario, vch_emplclave, "
                   + "vch_emplestado, chr_cliecodigo "
                   + "FROM usuario WHERE vch_emplusuario = ?";
        Connection cn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            cn = ConexionBD.conectar();
            ps = cn.prepareStatement(sql);
            ps.setString(1, usuario);
            rs = ps.executeQuery();
            if (rs.next()) {
                Usuario u = new Usuario();
                u.setCodigoEmpleado(rs.getString("chr_emplcodigo"));
                u.setUsuario(rs.getString("vch_emplusuario"));
                u.setClave(rs.getString("vch_emplclave"));
                u.setEstado(rs.getString("vch_emplestado"));
                u.setClienteCodigo(rs.getString("chr_cliecodigo"));
                return u;
            }
            return null;
        } finally {
            ConexionBD.desconectar(rs);
            ConexionBD.desconectar(ps);
            ConexionBD.desconectar(cn);
        }
    }
}
