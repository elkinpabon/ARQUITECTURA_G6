package ec.edu.monster.persistencia;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Unica clase responsable de la conexion de los microservicios con la base de datos
 * MySQL `eurekabank`. Centraliza el conectar() y el desconectar().
 */
public final class ConexionBD {

    private static final Logger LOG = Logger.getLogger(ConexionBD.class.getName());

    private static final String URL =
            "jdbc:mysql://3.239.254.34:3306/eurekasopajava"
            + "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    private static final String USUARIO = "admin";
    private static final String CLAVE = "SqlAmazon2026!";

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            LOG.log(Level.SEVERE, "Driver MySQL no encontrado", e);
            throw new ExceptionInInitializerError(e);
        }
    }

    private ConexionBD() {
    }

    public static Connection conectar() throws SQLException {
        Connection cn = DriverManager.getConnection(URL, USUARIO, CLAVE);
        LOG.fine("Conexion a eurekabank abierta.");
        return cn;
    }

    public static void desconectar(Connection cn) {
        if (cn != null) {
            try {
                cn.close();
                LOG.fine("Conexion a eurekabank cerrada.");
            } catch (SQLException e) {
                LOG.log(Level.WARNING, "Error al cerrar la conexion", e);
            }
        }
    }

    public static void desconectar(Statement st) {
        if (st != null) {
            try {
                st.close();
            } catch (SQLException e) {
                LOG.log(Level.WARNING, "Error al cerrar el statement", e);
            }
        }
    }

    public static void desconectar(ResultSet rs) {
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException e) {
                LOG.log(Level.WARNING, "Error al cerrar el resultset", e);
            }
        }
    }
}
