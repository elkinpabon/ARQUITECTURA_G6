package ec.edu.monster.persistencia;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Conexion centralizada a MySQL. La configuracion se resuelve en este orden:
 *  1) Variables de entorno (TICKETPREMIUM_DB_HOST, _PORT, _NAME, _USER, _PASSWORD).
 *  2) Archivo /db.properties dentro del classpath.
 *  3) Valores por defecto (localhost:3306 / ticketpremiumDB / root / admin2002).
 *
 * Esto hace que el WAR sea portable: el mismo artefacto funciona en cualquier
 * maquina cambiando solo las variables de entorno del servidor de aplicaciones.
 */
public final class ConexionBD {

    private static final Logger LOG = Logger.getLogger(ConexionBD.class.getName());

    private static final String URL;
    private static final String USUARIO;
    private static final String CLAVE;

    static {
        Properties props = cargarProperties();

        String host = resolver("TICKETPREMIUM_DB_HOST",     props, "db.host",     "localhost");
        String port = resolver("TICKETPREMIUM_DB_PORT",     props, "db.port",     "3306");
        String db   = resolver("TICKETPREMIUM_DB_NAME",     props, "db.name",     "ticketpremiumDB");
        USUARIO     = resolver("TICKETPREMIUM_DB_USER",     props, "db.user",     "root");
        CLAVE       = resolver("TICKETPREMIUM_DB_PASSWORD", props, "db.password", "admin2002");

        URL = "jdbc:mysql://" + host + ":" + port + "/" + db
            + "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            LOG.info("ConexionBD lista. URL=" + URL + "  user=" + USUARIO);
        } catch (ClassNotFoundException e) {
            LOG.log(Level.SEVERE, "Driver MySQL no encontrado", e);
            throw new ExceptionInInitializerError(e);
        }
    }

    private static Properties cargarProperties() {
        Properties p = new Properties();
        try (InputStream in = ConexionBD.class.getResourceAsStream("/db.properties")) {
            if (in != null) {
                p.load(in);
            } else {
                LOG.warning("db.properties no encontrado en classpath, usando defaults / env vars.");
            }
        } catch (IOException e) {
            LOG.log(Level.WARNING, "No se pudo leer db.properties", e);
        }
        return p;
    }

    private static String resolver(String envVar, Properties props, String key, String def) {
        String env = System.getenv(envVar);
        if (env != null && !env.isBlank()) return env;
        String prop = props.getProperty(key);
        if (prop != null && !prop.isBlank()) return prop;
        return def;
    }

    private ConexionBD() { }

    public static Connection conectar() throws SQLException {
        Connection cn = DriverManager.getConnection(URL, USUARIO, CLAVE);
        LOG.fine("Conexion abierta.");
        return cn;
    }

    public static void desconectar(Connection cn) {
        if (cn != null) {
            try { cn.close(); }
            catch (SQLException e) { LOG.log(Level.WARNING, "Error cerrando conexion", e); }
        }
    }

    public static void desconectar(Statement st) {
        if (st != null) {
            try { st.close(); }
            catch (SQLException e) { LOG.log(Level.WARNING, "Error cerrando statement", e); }
        }
    }

    public static void desconectar(ResultSet rs) {
        if (rs != null) {
            try { rs.close(); }
            catch (SQLException e) { LOG.log(Level.WARNING, "Error cerrando resultset", e); }
        }
    }
}
