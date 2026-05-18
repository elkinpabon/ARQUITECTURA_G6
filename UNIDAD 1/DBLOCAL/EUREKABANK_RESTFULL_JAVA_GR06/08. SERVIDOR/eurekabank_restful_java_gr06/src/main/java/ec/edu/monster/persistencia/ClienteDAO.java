package ec.edu.monster.persistencia;

import ec.edu.monster.modelo.ClienteResumen;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/** Acceso a datos de la tabla `cliente`. */
public class ClienteDAO {

    /** Lista todos los clientes registrados (solo lectura). */
    public List<ClienteResumen> listarTodos() throws SQLException {
        String sql = "SELECT chr_cliecodigo, chr_cliedni, "
                   + "vch_clienombre, vch_cliepaterno, vch_cliematerno "
                   + "FROM cliente ORDER BY vch_cliepaterno, vch_cliematerno, vch_clienombre";
        List<ClienteResumen> lista = new ArrayList<>();
        Connection cn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            cn = ConexionBD.conectar();
            ps = cn.prepareStatement(sql);
            rs = ps.executeQuery();
            while (rs.next()) {
                ClienteResumen c = new ClienteResumen();
                c.setCodigo(rs.getString("chr_cliecodigo"));
                c.setDni(rs.getString("chr_cliedni"));
                c.setNombre((rs.getString("vch_clienombre") + " "
                        + rs.getString("vch_cliepaterno") + " "
                        + rs.getString("vch_cliematerno")).trim());
                lista.add(c);
            }
            return lista;
        } finally {
            ConexionBD.desconectar(rs);
            ConexionBD.desconectar(ps);
            ConexionBD.desconectar(cn);
        }
    }

    /**
     * Inserta un cliente con código autogenerado (MAX+1, 5 dígitos).
     * @return el código del cliente creado.
     */
    public String insertar(String paterno, String materno, String nombre,
                           String dni, String ciudad, String direccion,
                           String telefono, String email) throws SQLException {
        Connection cn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            cn = ConexionBD.conectar();
            String codigo;
            try (PreparedStatement pm = cn.prepareStatement(
                    "SELECT LPAD(COALESCE(MAX(CAST(chr_cliecodigo AS UNSIGNED)),0)+1,5,'0') "
                    + "FROM cliente");
                 ResultSet rm = pm.executeQuery()) {
                rm.next();
                codigo = rm.getString(1);
            }
            String sql = "INSERT INTO cliente (chr_cliecodigo, vch_cliepaterno, "
                       + "vch_cliematerno, vch_clienombre, chr_cliedni, vch_clieciudad, "
                       + "vch_cliedireccion, vch_clietelefono, vch_clieemail) "
                       + "VALUES (?,?,?,?,?,?,?,?,?)";
            ps = cn.prepareStatement(sql);
            ps.setString(1, codigo);
            ps.setString(2, paterno);
            ps.setString(3, materno);
            ps.setString(4, nombre);
            ps.setString(5, dni);
            ps.setString(6, ciudad);
            ps.setString(7, direccion);
            ps.setString(8, telefono);
            ps.setString(9, email);
            ps.executeUpdate();
            return codigo;
        } finally {
            ConexionBD.desconectar(rs);
            ConexionBD.desconectar(ps);
            ConexionBD.desconectar(cn);
        }
    }

    /** Verifica si existe un cliente por su código. */
    public boolean existe(String codigo) throws SQLException {
        Connection cn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            cn = ConexionBD.conectar();
            ps = cn.prepareStatement(
                    "SELECT 1 FROM cliente WHERE chr_cliecodigo = ?");
            ps.setString(1, codigo);
            rs = ps.executeQuery();
            return rs.next();
        } finally {
            ConexionBD.desconectar(rs);
            ConexionBD.desconectar(ps);
            ConexionBD.desconectar(cn);
        }
    }
}
