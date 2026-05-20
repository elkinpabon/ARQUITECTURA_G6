package ec.edu.monster.persistencia;

import ec.edu.monster.modelo.Factura;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/** Acceso a las tablas FACTURA y DETALLE_FACTURA. */
public class FacturaDAO {

    private static final Logger LOG = Logger.getLogger(FacturaDAO.class.getName());
    private static final BigDecimal TASA_IVA = new BigDecimal("0.15");

    /**
     * Registra cabecera y un detalle dentro de una transaccion. Tambien descuenta
     * la disponibilidad de la localidad usando el DAO correspondiente.
     *
     * Retorna la Factura completa (con id) si todo va bien, o null si la
     * disponibilidad no alcanzo y la transaccion fue revertida.
     */
    public Factura registrarVentaSimple(int idUsuario, int codigoPartido,
                                        String codigoLocalidad, int cantidad,
                                        BigDecimal precioUnitario,
                                        LocalidadDAO localidadDAO) {

        BigDecimal subtotal = precioUnitario.multiply(BigDecimal.valueOf(cantidad));
        BigDecimal iva      = subtotal.multiply(TASA_IVA).setScale(2, java.math.RoundingMode.HALF_UP);
        BigDecimal total    = subtotal.add(iva);

        Connection cn = null;
        try {
            cn = ConexionBD.conectar();
            cn.setAutoCommit(false);

            // 1) Disminuir disponibilidad (falla si no alcanza)
            boolean ok = localidadDAO.disminuirDisponibilidad(cn, codigoPartido, codigoLocalidad, cantidad);
            if (!ok) {
                cn.rollback();
                return null;
            }

            // 2) Insertar cabecera FACTURA
            Timestamp ahora = new Timestamp(System.currentTimeMillis());
            int idFactura;
            String sqlF =
                "INSERT INTO FACTURA (ID_USUARIO, FECHA, SUBTOTAL, IVA, TOTAL) " +
                "VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement ps = cn.prepareStatement(sqlF, Statement.RETURN_GENERATED_KEYS)) {
                ps.setInt(1, idUsuario);
                ps.setTimestamp(2, ahora);
                ps.setBigDecimal(3, subtotal);
                ps.setBigDecimal(4, iva);
                ps.setBigDecimal(5, total);
                ps.executeUpdate();
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    rs.next();
                    idFactura = rs.getInt(1);
                }
            }

            // 3) Insertar DETALLE
            String sqlD =
                "INSERT INTO DETALLE_FACTURA " +
                "(ID_FACTURA, CODIGO_PARTIDO, LOCALIDAD, CANTIDAD, PRECIO_UNITARIO, TOTAL) " +
                "VALUES (?, ?, ?, ?, ?, ?)";
            try (PreparedStatement ps = cn.prepareStatement(sqlD)) {
                ps.setInt(1, idFactura);
                ps.setInt(2, codigoPartido);
                ps.setString(3, codigoLocalidad);
                ps.setInt(4, cantidad);
                ps.setBigDecimal(5, precioUnitario);
                ps.setBigDecimal(6, subtotal);
                ps.executeUpdate();
            }

            cn.commit();
            return new Factura(idFactura, idUsuario, String.valueOf(ahora), subtotal, iva, total);

        } catch (SQLException e) {
            LOG.log(Level.SEVERE, "Error registrando venta", e);
            if (cn != null) {
                try { cn.rollback(); } catch (SQLException ex) { /* nada */ }
            }
            return null;
        } finally {
            if (cn != null) {
                try { cn.setAutoCommit(true); } catch (SQLException ex) { /* nada */ }
            }
            ConexionBD.desconectar(cn);
        }
    }

    /** Historial de facturas de un usuario, ordenado por fecha desc. */
    public List<Factura> listarPorUsuario(int idUsuario) {
        List<Factura> out = new ArrayList<>();
        String sql =
            "SELECT ID_FACTURA, ID_USUARIO, FECHA, SUBTOTAL, IVA, TOTAL " +
            "  FROM FACTURA WHERE ID_USUARIO = ? ORDER BY FECHA DESC";
        Connection cn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            cn = ConexionBD.conectar();
            ps = cn.prepareStatement(sql);
            ps.setInt(1, idUsuario);
            rs = ps.executeQuery();
            while (rs.next()) {
                out.add(new Factura(
                    rs.getInt("ID_FACTURA"),
                    rs.getInt("ID_USUARIO"),
                    String.valueOf(rs.getTimestamp("FECHA")),
                    rs.getBigDecimal("SUBTOTAL"),
                    rs.getBigDecimal("IVA"),
                    rs.getBigDecimal("TOTAL")));
            }
        } catch (SQLException e) {
            LOG.log(Level.SEVERE, "Error listando facturas del usuario " + idUsuario, e);
        } finally {
            ConexionBD.desconectar(rs);
            ConexionBD.desconectar(ps);
            ConexionBD.desconectar(cn);
        }
        return out;
    }
}
