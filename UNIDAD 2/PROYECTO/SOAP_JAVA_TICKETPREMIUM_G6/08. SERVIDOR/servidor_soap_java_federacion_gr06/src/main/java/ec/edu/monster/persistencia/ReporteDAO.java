package ec.edu.monster.persistencia;

import ec.edu.monster.modelo.ResumenLocalidad;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/** Consultas agregadas para reportes. */
public class ReporteDAO {

    private static final Logger LOG = Logger.getLogger(ReporteDAO.class.getName());

    /**
     * Reporte "Resumen de Ventas de un Partido": agrupa los DETALLE_FACTURA por
     * CATEGORIA (Cat 1-4) para un partido dado.
     */
    public List<ResumenLocalidad> resumenVentasPorPartido(int codigoPartido) {
        List<ResumenLocalidad> filas = new ArrayList<>();
        String sql =
            "SELECT CATEGORIA, " +
            "       SUM(CANTIDAD) AS VENDIDOS, " +
            "       SUM(TOTAL)    AS RECAUDADO " +
            "  FROM DETALLE_FACTURA " +
            " WHERE CODIGO_PARTIDO = ? " +
            " GROUP BY CATEGORIA " +
            " ORDER BY CATEGORIA";
        Connection cn = null; PreparedStatement ps = null; ResultSet rs = null;
        try {
            cn = ConexionBD.conectar();
            ps = cn.prepareStatement(sql);
            ps.setInt(1, codigoPartido);
            rs = ps.executeQuery();
            while (rs.next()) {
                filas.add(new ResumenLocalidad(
                    rs.getString("CATEGORIA"),
                    rs.getInt("VENDIDOS"),
                    rs.getBigDecimal("RECAUDADO")));
            }
        } catch (SQLException e) {
            LOG.log(Level.SEVERE, "Error en reporte de ventas partido " + codigoPartido, e);
        } finally {
            ConexionBD.desconectar(rs); ConexionBD.desconectar(ps); ConexionBD.desconectar(cn);
        }
        return filas;
    }
}
