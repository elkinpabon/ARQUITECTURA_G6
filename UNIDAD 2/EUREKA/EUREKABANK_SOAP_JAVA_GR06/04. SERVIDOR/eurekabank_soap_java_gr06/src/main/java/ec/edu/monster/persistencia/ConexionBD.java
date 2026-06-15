package ec.edu.monster.persistencia;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Unica clase responsable de la conexion del servidor con la base de datos
 * MySQL local `eurekabank`. Centraliza el conectar() y el desconectar() para
 * que ningun otro componente abra conexiones por su cuenta.
 *
 * Regla del proyecto: SIEMPRE conectar al usar y desconectar al terminar.
 */
public final class ConexionBD {

    private static final Logger LOG = Logger.getLogger(ConexionBD.class.getName());

    // Datos de conexion al MySQL local (entorno GR06).
    private static final String URL =
            "jdbc:mysql://localhost:3306/eurekabank"
            + "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    private static final String USUARIO = "root";
    private static final String CLAVE = "admin2002";

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            LOG.log(Level.SEVERE, "Driver MySQL no encontrado", e);
            throw new ExceptionInInitializerError(e);
        }
    }

    private ConexionBD() {
        // Clase de utilidad: no se instancia.
    }

    /** Abre y devuelve una nueva conexion a la base de datos. */
    public static Connection conectar() throws SQLException {
        Connection cn = DriverManager.getConnection(URL, USUARIO, CLAVE);
        LOG.fine("Conexion a eurekabank abierta.");
        return cn;
    }

    /** Cierra la conexion si esta abierta (uso obligatorio al terminar). */
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

    /** Cierra un Statement de forma segura. */
    public static void desconectar(Statement st) {
        if (st != null) {
            try {
                st.close();
            } catch (SQLException e) {
                LOG.log(Level.WARNING, "Error al cerrar el statement", e);
            }
        }
    }

    /** Cierra un ResultSet de forma segura. */
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
