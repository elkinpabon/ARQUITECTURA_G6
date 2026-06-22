package ec.edu.monster.persistencia;

import ec.edu.monster.modelo.Seleccion;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/** Acceso a la tabla SELECCION. */
public class SeleccionDAO {

    private static final Logger LOG = Logger.getLogger(SeleccionDAO.class.getName());

    public List<Seleccion> listar() {
        List<Seleccion> out = new ArrayList<>();
        String sql = "SELECT ID_SELECCION, NOMBRE, GRUPO FROM SELECCION ORDER BY GRUPO, NOMBRE";
        Connection cn = null; PreparedStatement ps = null; ResultSet rs = null;
        try {
            cn = ConexionBD.conectar();
            ps = cn.prepareStatement(sql);
            rs = ps.executeQuery();
            while (rs.next()) {
                out.add(new Seleccion(
                    rs.getInt("ID_SELECCION"),
                    rs.getString("NOMBRE"),
                    rs.getString("GRUPO")));
            }
        } catch (SQLException e) {
            LOG.log(Level.SEVERE, "Error listando selecciones", e);
        } finally {
            ConexionBD.desconectar(rs); ConexionBD.desconectar(ps); ConexionBD.desconectar(cn);
        }
        return out;
    }

    public boolean existe(int idSeleccion) {
        String sql = "SELECT 1 FROM SELECCION WHERE ID_SELECCION = ?";
        Connection cn = null; PreparedStatement ps = null; ResultSet rs = null;
        try {
            cn = ConexionBD.conectar();
            ps = cn.prepareStatement(sql);
            ps.setInt(1, idSeleccion);
            rs = ps.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            LOG.log(Level.WARNING, "Error verificando seleccion " + idSeleccion, e);
            return false;
        } finally {
            ConexionBD.desconectar(rs); ConexionBD.desconectar(ps); ConexionBD.desconectar(cn);
        }
    }
}
