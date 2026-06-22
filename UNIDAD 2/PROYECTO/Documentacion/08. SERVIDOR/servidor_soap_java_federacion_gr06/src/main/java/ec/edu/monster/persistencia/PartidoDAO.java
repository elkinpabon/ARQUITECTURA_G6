package ec.edu.monster.persistencia;

import ec.edu.monster.modelo.Partido;
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

/** Acceso a la tabla PARTIDO_FUTBOL (con JOIN a SELECCION y ESTADIO). */
public class PartidoDAO {

    private static final Logger LOG = Logger.getLogger(PartidoDAO.class.getName());

    // SELECT base con los nombres ya resueltos por JOIN.
    private static final String SELECT_BASE =
        "SELECT p.CODIGO, p.ID_LOCAL, p.ID_VISITA, p.ID_ESTADIO, p.FECHA, p.GRUPO, " +
        "       sl.NOMBRE AS LOCAL_NOM, sv.NOMBRE AS VISITA_NOM, " +
        "       e.NOMBRE_OFICIAL AS ESTADIO_NOM, e.CIUDAD, e.PAIS " +
        "  FROM PARTIDO_FUTBOL p " +
        "  JOIN SELECCION sl ON sl.ID_SELECCION = p.ID_LOCAL " +
        "  JOIN SELECCION sv ON sv.ID_SELECCION = p.ID_VISITA " +
        "  JOIN ESTADIO   e  ON e.ID_ESTADIO   = p.ID_ESTADIO ";

    /**
     * Catalogo de venta: todos los partidos del torneo, ordenados por fecha.
     * (Como un marketplace tipo StubHub, se siguen ofertando aunque la fecha de
     * inicio ya haya pasado; antes se filtraba por FECHA >= NOW(), lo que ocultaba
     * los partidos inaugurales durante el propio Mundial y rompia la tienda/mashup.)
     */
    public List<Partido> listarDisponibles() {
        List<Partido> out = new ArrayList<>();
        String sql = SELECT_BASE + " ORDER BY p.FECHA ASC";
        Connection cn = null; PreparedStatement ps = null; ResultSet rs = null;
        try {
            cn = ConexionBD.conectar();
            ps = cn.prepareStatement(sql);
            rs = ps.executeQuery();
            while (rs.next()) out.add(map(rs));
        } catch (SQLException e) {
            LOG.log(Level.SEVERE, "Error listando partidos disponibles", e);
        } finally {
            ConexionBD.desconectar(rs); ConexionBD.desconectar(ps); ConexionBD.desconectar(cn);
        }
        return out;
    }

    /** Todos los partidos (admin), ordenados por grupo y fecha. */
    public List<Partido> listarTodos() {
        List<Partido> out = new ArrayList<>();
        String sql = SELECT_BASE + " ORDER BY p.GRUPO, p.FECHA ASC";
        Connection cn = null; PreparedStatement ps = null; ResultSet rs = null;
        try {
            cn = ConexionBD.conectar();
            ps = cn.prepareStatement(sql);
            rs = ps.executeQuery();
            while (rs.next()) out.add(map(rs));
        } catch (SQLException e) {
            LOG.log(Level.SEVERE, "Error listando todos los partidos", e);
        } finally {
            ConexionBD.desconectar(rs); ConexionBD.desconectar(ps); ConexionBD.desconectar(cn);
        }
        return out;
    }

    public Partido buscarPorCodigo(int codigo) {
        String sql = SELECT_BASE + " WHERE p.CODIGO = ?";
        Connection cn = null; PreparedStatement ps = null; ResultSet rs = null;
        try {
            cn = ConexionBD.conectar();
            ps = cn.prepareStatement(sql);
            ps.setInt(1, codigo);
            rs = ps.executeQuery();
            if (rs.next()) return map(rs);
        } catch (SQLException e) {
            LOG.log(Level.SEVERE, "Error buscando partido " + codigo, e);
        } finally {
            ConexionBD.desconectar(rs); ConexionBD.desconectar(ps); ConexionBD.desconectar(cn);
        }
        return null;
    }

    /** Solo la FECHA de un partido (para validar si ya se jugo). Null si no existe. */
    public Timestamp fechaDe(int codigo) {
        String sql = "SELECT FECHA FROM PARTIDO_FUTBOL WHERE CODIGO = ?";
        Connection cn = null; PreparedStatement ps = null; ResultSet rs = null;
        try {
            cn = ConexionBD.conectar();
            ps = cn.prepareStatement(sql);
            ps.setInt(1, codigo);
            rs = ps.executeQuery();
            if (rs.next()) return rs.getTimestamp("FECHA");
        } catch (SQLException e) {
            LOG.log(Level.SEVERE, "Error consultando fecha del partido " + codigo, e);
        } finally {
            ConexionBD.desconectar(rs); ConexionBD.desconectar(ps); ConexionBD.desconectar(cn);
        }
        return null;
    }

    // ------------------------------------------------------------------ CRUD admin

    public int insertar(int idLocal, int idVisita, int idEstadio, Timestamp fecha, String grupo) {
        String sql = "INSERT INTO PARTIDO_FUTBOL (ID_LOCAL, ID_VISITA, ID_ESTADIO, FECHA, FASE, GRUPO) " +
                     "VALUES (?, ?, ?, ?, 'GRUPOS', ?)";
        Connection cn = null; PreparedStatement ps = null; ResultSet rs = null;
        try {
            cn = ConexionBD.conectar();
            ps = cn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, idLocal);
            ps.setInt(2, idVisita);
            ps.setInt(3, idEstadio);
            ps.setTimestamp(4, fecha);
            ps.setString(5, grupo);
            if (ps.executeUpdate() == 0) return -1;
            rs = ps.getGeneratedKeys();
            return rs.next() ? rs.getInt(1) : -1;
        } catch (SQLException e) {
            LOG.log(Level.SEVERE, "Error insertando partido", e);
            return -1;
        } finally {
            ConexionBD.desconectar(rs); ConexionBD.desconectar(ps); ConexionBD.desconectar(cn);
        }
    }

    public boolean actualizar(int codigo, int idLocal, int idVisita, int idEstadio,
                              Timestamp fecha, String grupo) {
        String sql = "UPDATE PARTIDO_FUTBOL SET ID_LOCAL=?, ID_VISITA=?, ID_ESTADIO=?, FECHA=?, GRUPO=? WHERE CODIGO=?";
        Connection cn = null; PreparedStatement ps = null;
        try {
            cn = ConexionBD.conectar();
            ps = cn.prepareStatement(sql);
            ps.setInt(1, idLocal);
            ps.setInt(2, idVisita);
            ps.setInt(3, idEstadio);
            ps.setTimestamp(4, fecha);
            ps.setString(5, grupo);
            ps.setInt(6, codigo);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            LOG.log(Level.SEVERE, "Error actualizando partido " + codigo, e);
            return false;
        } finally {
            ConexionBD.desconectar(ps); ConexionBD.desconectar(cn);
        }
    }

    public boolean eliminar(int codigo) {
        String sql = "DELETE FROM PARTIDO_FUTBOL WHERE CODIGO=?";
        Connection cn = null; PreparedStatement ps = null;
        try {
            cn = ConexionBD.conectar();
            ps = cn.prepareStatement(sql);
            ps.setInt(1, codigo);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            LOG.log(Level.SEVERE, "Error eliminando partido " + codigo, e);
            return false;
        } finally {
            ConexionBD.desconectar(ps); ConexionBD.desconectar(cn);
        }
    }

    public boolean tieneLocalidades(int codigoPartido) {
        return contar("SELECT COUNT(*) FROM LOCALIDAD_PARTIDO WHERE CODIGO_PARTIDO=?", codigoPartido) > 0;
    }

    public boolean tieneVentas(int codigoPartido) {
        return contar("SELECT COUNT(*) FROM DETALLE_FACTURA WHERE CODIGO_PARTIDO=?", codigoPartido) > 0;
    }

    private int contar(String sql, int arg) {
        Connection cn = null; PreparedStatement ps = null; ResultSet rs = null;
        try {
            cn = ConexionBD.conectar();
            ps = cn.prepareStatement(sql);
            ps.setInt(1, arg);
            rs = ps.executeQuery();
            return rs.next() ? rs.getInt(1) : 0;
        } catch (SQLException e) {
            LOG.log(Level.WARNING, "Error contando", e);
            return 0;
        } finally {
            ConexionBD.desconectar(rs); ConexionBD.desconectar(ps); ConexionBD.desconectar(cn);
        }
    }

    private Partido map(ResultSet rs) throws SQLException {
        Partido p = new Partido();
        p.setCodigo(rs.getInt("CODIGO"));
        p.setIdLocal(rs.getInt("ID_LOCAL"));
        p.setIdVisita(rs.getInt("ID_VISITA"));
        p.setIdEstadio(rs.getInt("ID_ESTADIO"));
        p.setEquipoLocal(rs.getString("LOCAL_NOM"));
        p.setEquipoVisita(rs.getString("VISITA_NOM"));
        p.setFecha(String.valueOf(rs.getTimestamp("FECHA")));
        p.setGrupo(rs.getString("GRUPO"));
        String estadio = rs.getString("ESTADIO_NOM");
        String ciudad  = rs.getString("CIUDAD");
        String pais    = rs.getString("PAIS");
        p.setEstadio(estadio);
        p.setCiudad(ciudad);
        p.setPais(pais);
        p.setLugar(estadio + ", " + ciudad + " (" + pais + ")");
        return p;
    }
}
