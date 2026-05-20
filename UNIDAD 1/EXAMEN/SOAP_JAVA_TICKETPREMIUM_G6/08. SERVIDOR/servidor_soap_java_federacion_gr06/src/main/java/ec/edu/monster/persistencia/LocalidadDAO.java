package ec.edu.monster.persistencia;

import ec.edu.monster.modelo.Localidad;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/** Acceso a la tabla LOCALIDAD_PARTIDO. */
public class LocalidadDAO {

    private static final Logger LOG = Logger.getLogger(LocalidadDAO.class.getName());

    /** Lista las localidades de un partido con DISPONIBILIDAD > 0. */
    public List<Localidad> listarDisponiblesPorPartido(int codigoPartido) {
        List<Localidad> localidades = new ArrayList<>();
        String sql =
            "SELECT ID, CODIGO_PARTIDO, CODIGO_LOCALIDAD, DISPONIBILIDAD, PRECIO " +
            "  FROM LOCALIDAD_PARTIDO " +
            " WHERE CODIGO_PARTIDO = ? AND DISPONIBILIDAD > 0 " +
            " ORDER BY PRECIO ASC";

        Connection cn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            cn = ConexionBD.conectar();
            ps = cn.prepareStatement(sql);
            ps.setInt(1, codigoPartido);
            rs = ps.executeQuery();
            while (rs.next()) {
                Localidad l = new Localidad();
                l.setId(rs.getInt("ID"));
                l.setCodigoPartido(rs.getInt("CODIGO_PARTIDO"));
                l.setCodigoLocalidad(rs.getString("CODIGO_LOCALIDAD"));
                l.setDisponibilidad(rs.getInt("DISPONIBILIDAD"));
                l.setPrecio(rs.getBigDecimal("PRECIO"));
                localidades.add(l);
            }
        } catch (SQLException e) {
            LOG.log(Level.SEVERE, "Error listando localidades del partido " + codigoPartido, e);
        } finally {
            ConexionBD.desconectar(rs);
            ConexionBD.desconectar(ps);
            ConexionBD.desconectar(cn);
        }
        return localidades;
    }

    /** Busca una localidad concreta de un partido por su codigo (GENERAL, TRIBUNA, ...). */
    public Localidad buscar(int codigoPartido, String codigoLocalidad) {
        String sql =
            "SELECT ID, CODIGO_PARTIDO, CODIGO_LOCALIDAD, DISPONIBILIDAD, PRECIO " +
            "  FROM LOCALIDAD_PARTIDO " +
            " WHERE CODIGO_PARTIDO = ? AND CODIGO_LOCALIDAD = ?";

        Connection cn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            cn = ConexionBD.conectar();
            ps = cn.prepareStatement(sql);
            ps.setInt(1, codigoPartido);
            ps.setString(2, codigoLocalidad);
            rs = ps.executeQuery();
            if (rs.next()) {
                Localidad l = new Localidad();
                l.setId(rs.getInt("ID"));
                l.setCodigoPartido(rs.getInt("CODIGO_PARTIDO"));
                l.setCodigoLocalidad(rs.getString("CODIGO_LOCALIDAD"));
                l.setDisponibilidad(rs.getInt("DISPONIBILIDAD"));
                l.setPrecio(rs.getBigDecimal("PRECIO"));
                return l;
            }
        } catch (SQLException e) {
            LOG.log(Level.SEVERE, "Error buscando localidad", e);
        } finally {
            ConexionBD.desconectar(rs);
            ConexionBD.desconectar(ps);
            ConexionBD.desconectar(cn);
        }
        return null;
    }

    /**
     * Disminuye la disponibilidad de la localidad. Debe ejecutarse dentro de la
     * misma conexion/transaccion que el registro de la factura, por eso recibe
     * la Connection ya abierta.
     */
    public boolean disminuirDisponibilidad(Connection cn, int codigoPartido,
                                           String codigoLocalidad, int cantidad)
            throws SQLException {
        String sql =
            "UPDATE LOCALIDAD_PARTIDO " +
            "   SET DISPONIBILIDAD = DISPONIBILIDAD - ? " +
            " WHERE CODIGO_PARTIDO = ? AND CODIGO_LOCALIDAD = ? " +
            "   AND DISPONIBILIDAD >= ?";
        try (PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, cantidad);
            ps.setInt(2, codigoPartido);
            ps.setString(3, codigoLocalidad);
            ps.setInt(4, cantidad);
            return ps.executeUpdate() > 0;
        }
    }
}
