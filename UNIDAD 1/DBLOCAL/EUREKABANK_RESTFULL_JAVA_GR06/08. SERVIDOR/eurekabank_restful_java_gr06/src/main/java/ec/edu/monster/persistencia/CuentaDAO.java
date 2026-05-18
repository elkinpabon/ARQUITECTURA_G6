package ec.edu.monster.persistencia;

import ec.edu.monster.modelo.CuentaModel;
import ec.edu.monster.modelo.CuentaResumen;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/** Acceso a datos de la tabla `cuenta`. */
public class CuentaDAO {

    /**
     * Lee una cuenta bloqueando la fila (SELECT ... FOR UPDATE).
     * Debe ejecutarse DENTRO de la transaccion (recibe la conexion).
     */
    public CuentaModel obtenerParaActualizar(Connection cn, String codigoCuenta) throws SQLException {
        String sql = "SELECT chr_cuencodigo, chr_monecodigo, dec_cuensaldo, vch_cuenestado, "
                   + "int_cuencontmov, chr_cuenclave "
                   + "FROM cuenta WHERE chr_cuencodigo = ? FOR UPDATE";
        try (PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, codigoCuenta);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    CuentaModel c = new CuentaModel();
                    c.setChrCuenCodigo(rs.getString("chr_cuencodigo"));
                    c.setChrMoneCodigo(rs.getString("chr_monecodigo"));
                    c.setDecCuenSaldo(rs.getDouble("dec_cuensaldo"));
                    c.setVchCuenEstado(rs.getString("vch_cuenestado"));
                    c.setIntCuenContMov(rs.getInt("int_cuencontmov"));
                    c.setChrCuenClave(rs.getString("chr_cuenclave"));
                    return c;
                }
                return null;
            }
        }
    }

    /**
     * Actualiza saldo (+/- delta) e incrementa el contador de movimientos.
     * Debe ejecutarse DENTRO de la transaccion (recibe la conexion).
     */
    public int actualizarSaldo(Connection cn, String codigoCuenta, double delta) throws SQLException {
        String sql = "UPDATE cuenta "
                   + "SET dec_cuensaldo = dec_cuensaldo + ?, "
                   + "int_cuencontmov = int_cuencontmov + 1 "
                   + "WHERE chr_cuencodigo = ?";
        try (PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setDouble(1, delta);
            ps.setString(2, codigoCuenta);
            return ps.executeUpdate();
        }
    }

    /**
     * Consulta una cuenta (solo lectura). Abre y cierra su propia conexion.
     */
    public CuentaModel obtenerPorCodigo(String codigoCuenta) throws SQLException {
        String sql = "SELECT chr_cuencodigo, chr_monecodigo, dec_cuensaldo, vch_cuenestado, "
                   + "int_cuencontmov, chr_cuenclave "
                   + "FROM cuenta WHERE chr_cuencodigo = ?";
        Connection cn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            cn = ConexionBD.conectar();
            ps = cn.prepareStatement(sql);
            ps.setString(1, codigoCuenta);
            rs = ps.executeQuery();
            if (rs.next()) {
                CuentaModel c = new CuentaModel();
                c.setChrCuenCodigo(rs.getString("chr_cuencodigo"));
                c.setChrMoneCodigo(rs.getString("chr_monecodigo"));
                c.setDecCuenSaldo(rs.getDouble("dec_cuensaldo"));
                c.setVchCuenEstado(rs.getString("vch_cuenestado"));
                c.setIntCuenContMov(rs.getInt("int_cuencontmov"));
                c.setChrCuenClave(rs.getString("chr_cuenclave"));
                return c;
            }
            return null;
        } finally {
            ConexionBD.desconectar(rs);
            ConexionBD.desconectar(ps);
            ConexionBD.desconectar(cn);
        }
    }

    /**
     * Lista las cuentas de un cliente. El criterio puede ser el codigo del
     * cliente (chr_cliecodigo) o su DNI (chr_cliedni). Solo lectura.
     */
    public List<CuentaResumen> listarPorCliente(String criterio) throws SQLException {
        String sql = "SELECT cu.chr_cuencodigo, cu.chr_monecodigo, cu.dec_cuensaldo, "
                   + "cu.vch_cuenestado, cl.chr_cliecodigo, "
                   + "cl.vch_cliepaterno, cl.vch_cliematerno, cl.vch_clienombre "
                   + "FROM cuenta cu "
                   + "JOIN cliente cl ON cl.chr_cliecodigo = cu.chr_cliecodigo "
                   + "WHERE cl.chr_cliecodigo = ? OR cl.chr_cliedni = ? "
                   + "ORDER BY cu.chr_cuencodigo";
        List<CuentaResumen> lista = new ArrayList<>();
        Connection cn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            cn = ConexionBD.conectar();
            ps = cn.prepareStatement(sql);
            ps.setString(1, criterio);
            ps.setString(2, criterio);
            rs = ps.executeQuery();
            while (rs.next()) {
                CuentaResumen r = new CuentaResumen();
                r.setCodigoCuenta(rs.getString("chr_cuencodigo"));
                r.setMoneda(rs.getString("chr_monecodigo"));
                r.setSaldo(rs.getDouble("dec_cuensaldo"));
                r.setEstado(rs.getString("vch_cuenestado"));
                r.setCodigoCliente(rs.getString("chr_cliecodigo"));
                r.setNombreCliente((rs.getString("vch_clienombre") + " "
                        + rs.getString("vch_cliepaterno") + " "
                        + rs.getString("vch_cliematerno")).trim());
                lista.add(r);
            }
            return lista;
        } finally {
            ConexionBD.desconectar(rs);
            ConexionBD.desconectar(ps);
            ConexionBD.desconectar(cn);
        }
    }

    /**
     * Crea una cuenta para un cliente con código autogenerado (MAX+1, 8 dígitos),
     * saldo inicial 0, estado ACTIVO. Sucursal 001, empleado 0001, clave 123456.
     * @return el código de la cuenta creada.
     */
    public String insertar(String clienteCodigo, String moneda) throws SQLException {
        Connection cn = null;
        PreparedStatement ps = null;
        try {
            cn = ConexionBD.conectar();
            String codigo;
            try (PreparedStatement pm = cn.prepareStatement(
                    "SELECT LPAD(COALESCE(MAX(CAST(chr_cuencodigo AS UNSIGNED)),0)+1,8,'0') "
                    + "FROM cuenta");
                 ResultSet rm = pm.executeQuery()) {
                rm.next();
                codigo = rm.getString(1);
            }
            String sql = "INSERT INTO cuenta (chr_cuencodigo, chr_monecodigo, "
                       + "chr_sucucodigo, chr_emplcreacuenta, chr_cliecodigo, "
                       + "dec_cuensaldo, dtt_cuenfechacreacion, vch_cuenestado, "
                       + "int_cuencontmov, chr_cuenclave) "
                       + "VALUES (?,?, '001','0001', ?, 0.00, CURDATE(), 'ACTIVO', 0, '123456')";
            ps = cn.prepareStatement(sql);
            ps.setString(1, codigo);
            ps.setString(2, moneda);
            ps.setString(3, clienteCodigo);
            ps.executeUpdate();
            return codigo;
        } finally {
            ConexionBD.desconectar(ps);
            ConexionBD.desconectar(cn);
        }
    }

    /** Devuelve true si la cuenta existe. */
    public boolean existe(String codigoCuenta) throws SQLException {
        Connection cn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            cn = ConexionBD.conectar();
            ps = cn.prepareStatement("SELECT 1 FROM cuenta WHERE chr_cuencodigo = ?");
            ps.setString(1, codigoCuenta);
            rs = ps.executeQuery();
            return rs.next();
        } finally {
            ConexionBD.desconectar(rs);
            ConexionBD.desconectar(ps);
            ConexionBD.desconectar(cn);
        }
    }

    /**
     * Elimina una cuenta y sus movimientos en UNA transacción.
     * @return número de cuentas borradas (0 si no existía).
     */
    public int eliminar(String codigoCuenta) throws SQLException {
        Connection cn = null;
        try {
            cn = ConexionBD.conectar();
            cn.setAutoCommit(false);
            try (PreparedStatement pm = cn.prepareStatement(
                    "DELETE FROM movimiento WHERE chr_cuencodigo = ?")) {
                pm.setString(1, codigoCuenta);
                pm.executeUpdate();
            }
            int filas;
            try (PreparedStatement pc = cn.prepareStatement(
                    "DELETE FROM cuenta WHERE chr_cuencodigo = ?")) {
                pc.setString(1, codigoCuenta);
                filas = pc.executeUpdate();
            }
            cn.commit();
            return filas;
        } catch (SQLException e) {
            if (cn != null) {
                try { cn.rollback(); } catch (SQLException ignore) { }
            }
            throw e;
        } finally {
            if (cn != null) {
                try { cn.setAutoCommit(true); } catch (SQLException ignore) { }
            }
            ConexionBD.desconectar(cn);
        }
    }
}
