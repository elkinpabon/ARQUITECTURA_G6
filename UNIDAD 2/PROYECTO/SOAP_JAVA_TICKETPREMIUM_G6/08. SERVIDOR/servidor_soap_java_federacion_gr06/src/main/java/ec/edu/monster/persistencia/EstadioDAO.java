package ec.edu.monster.persistencia;

import ec.edu.monster.modelo.Estadio;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/** Acceso a la tabla ESTADIO. */
public class EstadioDAO {

    private static final Logger LOG = Logger.getLogger(EstadioDAO.class.getName());

    public List<Estadio> listar() {
        List<Estadio> out = new ArrayList<>();
        String sql = "SELECT ID_ESTADIO, NOMBRE_OFICIAL, NOMBRE_FIFA, CIUDAD, PAIS, CAPACIDAD " +
                     "  FROM ESTADIO ORDER BY NOMBRE_OFICIAL";
        Connection cn = null; PreparedStatement ps = null; ResultSet rs = null;
        try {
            cn = ConexionBD.conectar();
            ps = cn.prepareStatement(sql);
            rs = ps.executeQuery();
            while (rs.next()) {
                out.add(new Estadio(
                    rs.getInt("ID_ESTADIO"),
                    rs.getString("NOMBRE_OFICIAL"),
                    rs.getString("NOMBRE_FIFA"),
                    rs.getString("CIUDAD"),
                    rs.getString("PAIS"),
                    rs.getInt("CAPACIDAD")));
            }
        } catch (SQLException e) {
            LOG.log(Level.SEVERE, "Error listando estadios", e);
        } finally {
            ConexionBD.desconectar(rs); ConexionBD.desconectar(ps); ConexionBD.desconectar(cn);
        }
        return out;
    }

    public boolean existe(int idEstadio) {
        String sql = "SELECT 1 FROM ESTADIO WHERE ID_ESTADIO = ?";
        Connection cn = null; PreparedStatement ps = null; ResultSet rs = null;
        try {
            cn = ConexionBD.conectar();
            ps = cn.prepareStatement(sql);
            ps.setInt(1, idEstadio);
            rs = ps.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            LOG.log(Level.WARNING, "Error verificando estadio " + idEstadio, e);
            return false;
        } finally {
            ConexionBD.desconectar(rs); ConexionBD.desconectar(ps); ConexionBD.desconectar(cn);
        }
    }
}
