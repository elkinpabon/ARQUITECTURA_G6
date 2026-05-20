package ec.edu.monster.persistencia;

import ec.edu.monster.modelo.Partido;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/** Acceso a la tabla PARTIDO_FUTBOL. */
public class PartidoDAO {

    private static final Logger LOG = Logger.getLogger(PartidoDAO.class.getName());

    /** Devuelve los partidos cuya FECHA >= NOW() (disponibles para vender). */
    public List<Partido> listarDisponibles() {
        List<Partido> partidos = new ArrayList<>();
        String sql =
            "SELECT CODIGO, EQUIPO_LOCAL, EQUIPO_VISITA, FECHA, LUGAR " +
            "  FROM PARTIDO_FUTBOL " +
            " WHERE FECHA >= NOW() " +
            " ORDER BY FECHA ASC";

        Connection cn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            cn = ConexionBD.conectar();
            ps = cn.prepareStatement(sql);
            rs = ps.executeQuery();
            while (rs.next()) {
                Partido p = new Partido();
                p.setCodigo(rs.getInt("CODIGO"));
                p.setEquipoLocal(rs.getString("EQUIPO_LOCAL"));
                p.setEquipoVisita(rs.getString("EQUIPO_VISITA"));
                p.setFecha(String.valueOf(rs.getTimestamp("FECHA")));
                p.setLugar(rs.getString("LUGAR"));
                partidos.add(p);
            }
        } catch (SQLException e) {
            LOG.log(Level.SEVERE, "Error listando partidos disponibles", e);
        } finally {
            ConexionBD.desconectar(rs);
            ConexionBD.desconectar(ps);
            ConexionBD.desconectar(cn);
        }
        return partidos;
    }

    /** Devuelve solo la FECHA de un partido (para validar si ya se jugo). Null si no existe. */
    public Timestamp fechaDe(int codigo) {
        String sql = "SELECT FECHA FROM PARTIDO_FUTBOL WHERE CODIGO = ?";
        Connection cn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            cn = ConexionBD.conectar();
            ps = cn.prepareStatement(sql);
            ps.setInt(1, codigo);
            rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getTimestamp("FECHA");
            }
        } catch (SQLException e) {
            LOG.log(Level.SEVERE, "Error consultando fecha del partido " + codigo, e);
        } finally {
            ConexionBD.desconectar(rs);
            ConexionBD.desconectar(ps);
            ConexionBD.desconectar(cn);
        }
        return null;
    }

    /** Busca un partido por su codigo. Devuelve null si no existe. */
    public Partido buscarPorCodigo(int codigo) {
        String sql =
            "SELECT CODIGO, EQUIPO_LOCAL, EQUIPO_VISITA, FECHA, LUGAR " +
            "  FROM PARTIDO_FUTBOL WHERE CODIGO = ?";

        Connection cn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            cn = ConexionBD.conectar();
            ps = cn.prepareStatement(sql);
            ps.setInt(1, codigo);
            rs = ps.executeQuery();
            if (rs.next()) {
                Partido p = new Partido();
                p.setCodigo(rs.getInt("CODIGO"));
                p.setEquipoLocal(rs.getString("EQUIPO_LOCAL"));
                p.setEquipoVisita(rs.getString("EQUIPO_VISITA"));
                p.setFecha(String.valueOf(rs.getTimestamp("FECHA")));
                p.setLugar(rs.getString("LUGAR"));
                return p;
            }
        } catch (SQLException e) {
            LOG.log(Level.SEVERE, "Error buscando partido " + codigo, e);
        } finally {
            ConexionBD.desconectar(rs);
            ConexionBD.desconectar(ps);
            ConexionBD.desconectar(cn);
        }
        return null;
    }
}
