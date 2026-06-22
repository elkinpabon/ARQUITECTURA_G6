package ec.edu.monster.persistencia;

import ec.edu.monster.modelo.Localidad;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/** Acceso a la tabla LOCALIDAD_PARTIDO (categorias Cat 1-4). */
public class LocalidadDAO {

    private static final Logger LOG = Logger.getLogger(LocalidadDAO.class.getName());

    public List<Localidad> listarDisponiblesPorPartido(int codigoPartido) {
        List<Localidad> out = new ArrayList<>();
        String sql =
            "SELECT ID, CODIGO_PARTIDO, CATEGORIA, DISPONIBILIDAD, PRECIO " +
            "  FROM LOCALIDAD_PARTIDO " +
            " WHERE CODIGO_PARTIDO = ? AND DISPONIBILIDAD > 0 " +
            " ORDER BY PRECIO DESC";
        Connection cn = null; PreparedStatement ps = null; ResultSet rs = null;
        try {
            cn = ConexionBD.conectar();
            ps = cn.prepareStatement(sql);
            ps.setInt(1, codigoPartido);
            rs = ps.executeQuery();
            while (rs.next()) out.add(map(rs));
        } catch (SQLException e) {
            LOG.log(Level.SEVERE, "Error listando localidades del partido " + codigoPartido, e);
        } finally {
            ConexionBD.desconectar(rs); ConexionBD.desconectar(ps); ConexionBD.desconectar(cn);
        }
        return out;
    }

    public List<Localidad> listarTodasPorPartido(int codigoPartido) {
        List<Localidad> out = new ArrayList<>();
        String sql =
            "SELECT ID, CODIGO_PARTIDO, CATEGORIA, DISPONIBILIDAD, PRECIO " +
            "  FROM LOCALIDAD_PARTIDO WHERE CODIGO_PARTIDO = ? ORDER BY PRECIO DESC";
        Connection cn = null; PreparedStatement ps = null; ResultSet rs = null;
        try {
            cn = ConexionBD.conectar();
            ps = cn.prepareStatement(sql);
            ps.setInt(1, codigoPartido);
            rs = ps.executeQuery();
            while (rs.next()) out.add(map(rs));
        } catch (SQLException e) {
            LOG.log(Level.SEVERE, "Error listando todas las localidades del partido " + codigoPartido, e);
        } finally {
            ConexionBD.desconectar(rs); ConexionBD.desconectar(ps); ConexionBD.desconectar(cn);
        }
        return out;
    }

    /** Busca una categoria concreta de un partido. */
    public Localidad buscar(int codigoPartido, String categoria) {
        String sql =
            "SELECT ID, CODIGO_PARTIDO, CATEGORIA, DISPONIBILIDAD, PRECIO " +
            "  FROM LOCALIDAD_PARTIDO WHERE CODIGO_PARTIDO = ? AND CATEGORIA = ?";
        Connection cn = null; PreparedStatement ps = null; ResultSet rs = null;
        try {
            cn = ConexionBD.conectar();
            ps = cn.prepareStatement(sql);
            ps.setInt(1, codigoPartido);
            ps.setString(2, categoria);
            rs = ps.executeQuery();
            if (rs.next()) return map(rs);
        } catch (SQLException e) {
            LOG.log(Level.SEVERE, "Error buscando localidad", e);
        } finally {
            ConexionBD.desconectar(rs); ConexionBD.desconectar(ps); ConexionBD.desconectar(cn);
        }
        return null;
    }

    /**
     * Resuelve la localidad (categoria) a la que pertenece una seccion. Usa la
     * conexion de la transaccion en curso. Util para la compra por carrito.
     */
    public Localidad buscarPorSeccion(Connection cn, int idSeccion) throws SQLException {
        String sql =
            "SELECT lp.ID, lp.CODIGO_PARTIDO, lp.CATEGORIA, lp.DISPONIBILIDAD, lp.PRECIO " +
            "  FROM SECCION s JOIN LOCALIDAD_PARTIDO lp ON lp.ID = s.ID_LOCALIDAD " +
            " WHERE s.ID_SECCION = ?";
        try (PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, idSeccion);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return map(rs);
            }
        }
        return null;
    }

    /** Disminuye el stock de una localidad por su ID (transaccional). */
    public boolean disminuirDisponibilidadPorId(Connection cn, int idLocalidad, int cantidad)
            throws SQLException {
        String sql =
            "UPDATE LOCALIDAD_PARTIDO SET DISPONIBILIDAD = DISPONIBILIDAD - ? " +
            " WHERE ID = ? AND DISPONIBILIDAD >= ?";
        try (PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, cantidad);
            ps.setInt(2, idLocalidad);
            ps.setInt(3, cantidad);
            return ps.executeUpdate() > 0;
        }
    }

    // ------------------------------------------------------------------ CRUD admin

    public int insertar(int codigoPartido, String categoria, int disponibilidad, BigDecimal precio) {
        String sql = "INSERT INTO LOCALIDAD_PARTIDO (CODIGO_PARTIDO, CATEGORIA, DISPONIBILIDAD, PRECIO) VALUES (?, ?, ?, ?)";
        Connection cn = null; PreparedStatement ps = null; ResultSet rs = null;
        try {
            cn = ConexionBD.conectar();
            ps = cn.prepareStatement(sql, java.sql.Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, codigoPartido);
            ps.setString(2, categoria);
            ps.setInt(3, disponibilidad);
            ps.setBigDecimal(4, precio);
            if (ps.executeUpdate() == 0) return -1;
            rs = ps.getGeneratedKeys();
            return rs.next() ? rs.getInt(1) : -1;
        } catch (SQLException e) {
            LOG.log(Level.SEVERE, "Error insertando localidad", e);
            return -1;
        } finally {
            ConexionBD.desconectar(rs); ConexionBD.desconectar(ps); ConexionBD.desconectar(cn);
        }
    }

    public boolean actualizar(int id, int disponibilidad, BigDecimal precio) {
        String sql = "UPDATE LOCALIDAD_PARTIDO SET DISPONIBILIDAD=?, PRECIO=? WHERE ID=?";
        Connection cn = null; PreparedStatement ps = null;
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
            ConexionBD.desconectar(ps); ConexionBD.desconectar(cn);
        }
    }

    public boolean eliminar(int id) {
        String sql = "DELETE FROM LOCALIDAD_PARTIDO WHERE ID=?";
        Connection cn = null; PreparedStatement ps = null;
        try {
            cn = ConexionBD.conectar();
            ps = cn.prepareStatement(sql);
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            LOG.log(Level.SEVERE, "Error eliminando localidad " + id, e);
            return false;
        } finally {
            ConexionBD.desconectar(ps); ConexionBD.desconectar(cn);
        }
    }

    /** Una localidad tiene ventas si alguna de sus secciones aparece en DETALLE_FACTURA. */
    public boolean tieneVentas(int idLocalidad) {
        String sql =
            "SELECT COUNT(*) FROM DETALLE_FACTURA d " +
            "  JOIN SECCION s ON s.ID_SECCION = d.ID_SECCION " +
            " WHERE s.ID_LOCALIDAD = ?";
        Connection cn = null; PreparedStatement ps = null; ResultSet rs = null;
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
            ConexionBD.desconectar(rs); ConexionBD.desconectar(ps); ConexionBD.desconectar(cn);
        }
    }

    private Localidad map(ResultSet rs) throws SQLException {
        Localidad l = new Localidad();
        l.setId(rs.getInt("ID"));
        l.setCodigoPartido(rs.getInt("CODIGO_PARTIDO"));
        l.setCategoria(rs.getString("CATEGORIA"));
        l.setDisponibilidad(rs.getInt("DISPONIBILIDAD"));
        l.setPrecio(rs.getBigDecimal("PRECIO"));
        return l;
    }
}
