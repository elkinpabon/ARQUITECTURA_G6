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

    // -------------------------------------------------------------------------
    //  CRUD admin
    // -------------------------------------------------------------------------

    /** Inserta un partido nuevo. Devuelve el CODIGO generado, o -1 si fallo. */
    public int insertar(String equipoLocal, String equipoVisita, Timestamp fecha, String lugar) {
        String sql = "INSERT INTO PARTIDO_FUTBOL (EQUIPO_LOCAL, EQUIPO_VISITA, FECHA, LUGAR) VALUES (?, ?, ?, ?)";
        Connection cn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            cn = ConexionBD.conectar();
            ps = cn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, equipoLocal);
            ps.setString(2, equipoVisita);
            ps.setTimestamp(3, fecha);
            ps.setString(4, lugar);
            if (ps.executeUpdate() == 0) return -1;
            rs = ps.getGeneratedKeys();
            return rs.next() ? rs.getInt(1) : -1;
        } catch (SQLException e) {
            LOG.log(Level.SEVERE, "Error insertando partido", e);
            return -1;
        } finally {
            ConexionBD.desconectar(rs);
            ConexionBD.desconectar(ps);
            ConexionBD.desconectar(cn);
        }
    }

    public boolean actualizar(int codigo, String equipoLocal, String equipoVisita,
                              Timestamp fecha, String lugar) {
        String sql = "UPDATE PARTIDO_FUTBOL SET EQUIPO_LOCAL=?, EQUIPO_VISITA=?, FECHA=?, LUGAR=? WHERE CODIGO=?";
        Connection cn = null;
        PreparedStatement ps = null;
        try {
            cn = ConexionBD.conectar();
            ps = cn.prepareStatement(sql);
            ps.setString(1, equipoLocal);
            ps.setString(2, equipoVisita);
            ps.setTimestamp(3, fecha);
            ps.setString(4, lugar);
            ps.setInt(5, codigo);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            LOG.log(Level.SEVERE, "Error actualizando partido " + codigo, e);
            return false;
        } finally {
            ConexionBD.desconectar(ps);
            ConexionBD.desconectar(cn);
        }
    }

    /** Elimina un partido. Falla si tiene ventas o localidades asociadas. */
    public boolean eliminar(int codigo) {
        String sql = "DELETE FROM PARTIDO_FUTBOL WHERE CODIGO=?";
        Connection cn = null;
        PreparedStatement ps = null;
        try {
            cn = ConexionBD.conectar();
            ps = cn.prepareStatement(sql);
            ps.setInt(1, codigo);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            LOG.log(Level.SEVERE, "Error eliminando partido " + codigo, e);
            return false;
        } finally {
            ConexionBD.desconectar(ps);
            ConexionBD.desconectar(cn);
        }
    }

    public boolean tieneLocalidades(int codigoPartido) {
        return contar("SELECT COUNT(*) FROM LOCALIDAD_PARTIDO WHERE CODIGO_PARTIDO=?", codigoPartido) > 0;
    }

    public boolean tieneVentas(int codigoPartido) {
        return contar("SELECT COUNT(*) FROM DETALLE_FACTURA WHERE CODIGO_PARTIDO=?", codigoPartido) > 0;
    }

    private int contar(String sql, int arg) {
        Connection cn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
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
            ConexionBD.desconectar(rs);
            ConexionBD.desconectar(ps);
            ConexionBD.desconectar(cn);
        }
    }
}
