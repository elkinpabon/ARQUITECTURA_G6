package ec.edu.monster.persistencia;

import ec.edu.monster.modelo.Seccion;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/** Acceso a la tabla SECCION (secciones fisicas de cada categoria). */
public class SeccionDAO {

    private static final Logger LOG = Logger.getLogger(SeccionDAO.class.getName());

    /** Secciones de una localidad (categoria) concreta. */
    public List<Seccion> listarPorLocalidad(int idLocalidad) {
        List<Seccion> out = new ArrayList<>();
        String sql = "SELECT ID_SECCION, ID_LOCALIDAD, CODIGO_SECCION, NUM_FILAS, ASIENTOS_POR_FILA " +
                     "  FROM SECCION WHERE ID_LOCALIDAD = ? ORDER BY CODIGO_SECCION";
        Connection cn = null; PreparedStatement ps = null; ResultSet rs = null;
        try {
            cn = ConexionBD.conectar();
            ps = cn.prepareStatement(sql);
            ps.setInt(1, idLocalidad);
            rs = ps.executeQuery();
            while (rs.next()) out.add(map(rs));
        } catch (SQLException e) {
            LOG.log(Level.SEVERE, "Error listando secciones de localidad " + idLocalidad, e);
        } finally {
            ConexionBD.desconectar(rs); ConexionBD.desconectar(ps); ConexionBD.desconectar(cn);
        }
        return out;
    }

    /** Todas las secciones de un partido (con su categoria via JOIN localidad). */
    public List<Seccion> listarPorPartido(int codigoPartido) {
        List<Seccion> out = new ArrayList<>();
        String sql = "SELECT s.ID_SECCION, s.ID_LOCALIDAD, s.CODIGO_SECCION, s.NUM_FILAS, s.ASIENTOS_POR_FILA " +
                     "  FROM SECCION s JOIN LOCALIDAD_PARTIDO lp ON lp.ID = s.ID_LOCALIDAD " +
                     " WHERE lp.CODIGO_PARTIDO = ? ORDER BY lp.PRECIO DESC, s.CODIGO_SECCION";
        Connection cn = null; PreparedStatement ps = null; ResultSet rs = null;
        try {
            cn = ConexionBD.conectar();
            ps = cn.prepareStatement(sql);
            ps.setInt(1, codigoPartido);
            rs = ps.executeQuery();
            while (rs.next()) out.add(map(rs));
        } catch (SQLException e) {
            LOG.log(Level.SEVERE, "Error listando secciones del partido " + codigoPartido, e);
        } finally {
            ConexionBD.desconectar(rs); ConexionBD.desconectar(ps); ConexionBD.desconectar(cn);
        }
        return out;
    }

    private Seccion map(ResultSet rs) throws SQLException {
        return new Seccion(
            rs.getInt("ID_SECCION"),
            rs.getInt("ID_LOCALIDAD"),
            rs.getString("CODIGO_SECCION"),
            rs.getInt("NUM_FILAS"),
            rs.getInt("ASIENTOS_POR_FILA"));
    }
}
