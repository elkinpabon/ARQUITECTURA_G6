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

    // -------------------------------------------------------------------------
    //  CRUD admin
    // -------------------------------------------------------------------------

    /** Inserta una localidad nueva. Devuelve el ID generado, o -1 si fallo. */
    public int insertar(int codigoPartido, String codigoLocalidad,
                        int disponibilidad, java.math.BigDecimal precio) {
        String sql = "INSERT INTO LOCALIDAD_PARTIDO (CODIGO_PARTIDO, CODIGO_LOCALIDAD, DISPONIBILIDAD, PRECIO) VALUES (?, ?, ?, ?)";
        Connection cn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            cn = ConexionBD.conectar();
            ps = cn.prepareStatement(sql, java.sql.Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, codigoPartido);
            ps.setString(2, codigoLocalidad);
            ps.setInt(3, disponibilidad);
            ps.setBigDecimal(4, precio);
            if (ps.executeUpdate() == 0) return -1;
            rs = ps.getGeneratedKeys();
            return rs.next() ? rs.getInt(1) : -1;
        } catch (SQLException e) {
            LOG.log(Level.SEVERE, "Error insertando localidad", e);
            return -1;
        } finally {
            ConexionBD.desconectar(rs);
            ConexionBD.desconectar(ps);
            ConexionBD.desconectar(cn);
        }
    }

    public boolean actualizar(int id, int disponibilidad, java.math.BigDecimal precio) {
        String sql = "UPDATE LOCALIDAD_PARTIDO SET DISPONIBILIDAD=?, PRECIO=? WHERE ID=?";
        Connection cn = null;
        PreparedStatement ps = null;
        try {
            cn = ConexionBD.conectar();
            ps = cn.prepareStatement(sql);
            ps.setInt(1, disponibilidad);
            ps.setBigDecimal(2, precio);
            ps.setInt(3, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            LOG.log(Level.SEVERE, "Error actualizando localidad " + id, e);
            return false;
        } finally {
            ConexionBD.desconectar(ps);
            ConexionBD.desconectar(cn);
        }
    }

    /** Elimina una localidad por su ID. La operacion falla si tiene ventas (FK DETALLE_FACTURA). */
    public boolean eliminar(int id) {
        String sql = "DELETE FROM LOCALIDAD_PARTIDO WHERE ID=?";
        Connection cn = null;
        PreparedStatement ps = null;
        try {
            cn = ConexionBD.conectar();
            ps = cn.prepareStatement(sql);
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            LOG.log(Level.SEVERE, "Error eliminando localidad " + id, e);
            return false;
        } finally {
            ConexionBD.desconectar(ps);
            ConexionBD.desconectar(cn);
        }
    }

    public boolean tieneVentas(int idLocalidad) {
        String sql =
            "SELECT COUNT(*) FROM DETALLE_FACTURA d " +
            "  JOIN LOCALIDAD_PARTIDO l " +
            "    ON l.CODIGO_PARTIDO = d.CODIGO_PARTIDO " +
            "   AND l.CODIGO_LOCALIDAD = d.LOCALIDAD " +
            " WHERE l.ID = ?";
        Connection cn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            cn = ConexionBD.conectar();
            ps = cn.prepareStatement(sql);
            ps.setInt(1, idLocalidad);
            rs = ps.executeQuery();
            return rs.next() && rs.getInt(1) > 0;
        } catch (SQLException e) {
            LOG.log(Level.WARNING, "Error chequeando ventas de localidad " + idLocalidad, e);
            return false;
        } finally {
            ConexionBD.desconectar(rs);
            ConexionBD.desconectar(ps);
            ConexionBD.desconectar(cn);
        }
    }

    /** Lista TODAS las localidades de un partido (independiente de la disponibilidad). */
    public List<Localidad> listarTodasPorPartido(int codigoPartido) {
        List<Localidad> out = new ArrayList<>();
        String sql =
            "SELECT ID, CODIGO_PARTIDO, CODIGO_LOCALIDAD, DISPONIBILIDAD, PRECIO " +
            "  FROM LOCALIDAD_PARTIDO WHERE CODIGO_PARTIDO = ? ORDER BY CODIGO_LOCALIDAD";
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
                out.add(l);
            }
        } catch (SQLException e) {
            LOG.log(Level.SEVERE, "Error listando todas las localidades del partido " + codigoPartido, e);
        } finally {
            ConexionBD.desconectar(rs);
            ConexionBD.desconectar(ps);
            ConexionBD.desconectar(cn);
        }
        return out;
    }
}
