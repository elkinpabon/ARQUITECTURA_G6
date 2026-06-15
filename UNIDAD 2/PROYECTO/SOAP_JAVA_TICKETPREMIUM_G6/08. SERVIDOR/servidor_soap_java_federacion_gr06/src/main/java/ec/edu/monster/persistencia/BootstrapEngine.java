package ec.edu.monster.persistencia;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Logica pura del bootstrap (sin imports de Jakarta EE).
 * La invoca BootstrapBD (servlet listener) en deploy, y tambien PruebaBootstrap
 * para tests standalone.
 *
 *  - Si ticketpremiumDB no existe, la crea (CREATE DATABASE IF NOT EXISTS).
 *  - Crea tablas con CREATE TABLE IF NOT EXISTS.
 *  - Inserta semilla con INSERT IGNORE (idempotente).
 *
 * Script: classpath:/bootstrap.sql
 */
public final class BootstrapEngine {

    private static final Logger LOG = Logger.getLogger(BootstrapEngine.class.getName());
    private static final String SCRIPT = "/bootstrap.sql";

    private BootstrapEngine() { }

    /** Ejecuta el bootstrap. Si falla, registra y propaga (deja decidir al caller). */
    public static void ejecutar() throws IOException, SQLException {
        LOG.info("Iniciando bootstrap de ticketpremiumDB ...");
        List<String> sentencias = leerScript();

        // 1) Conexion admin (sin DB) para el CREATE DATABASE
        try (Connection cn = abrirAdmin(); Statement st = cn.createStatement()) {
            st.execute(sentencias.get(0));
            LOG.info("  [OK] CREATE DATABASE (si no existia)");
        }

        // 2) Conexion normal para el resto
        try (Connection cn = ConexionBD.conectar(); Statement st = cn.createStatement()) {
            for (int i = 1; i < sentencias.size(); i++) {
                String sql = sentencias.get(i);
                try {
                    st.execute(sql);
                } catch (SQLException e) {
                    LOG.log(Level.WARNING, "Sentencia #" + i + " fallo: " + resumen(sql), e);
                }
            }
        }

        LOG.info("Bootstrap completado: " + sentencias.size() + " sentencias (esquema + semilla nucleo).");
    }

    private static List<String> leerScript() throws IOException {
        try (InputStream in = BootstrapEngine.class.getResourceAsStream(SCRIPT)) {
            if (in == null) {
                throw new IOException("No se encontro " + SCRIPT + " en el classpath.");
            }
            StringBuilder sb = new StringBuilder();
            try (BufferedReader br = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))) {
                String linea;
                while ((linea = br.readLine()) != null) {
                    String trim = linea.trim();
                    if (trim.isEmpty() || trim.startsWith("--")) continue;
                    sb.append(linea).append('\n');
                }
            }
            List<String> out = new ArrayList<>();
            for (String s : sb.toString().split(";")) {
                String t = s.trim();
                if (!t.isEmpty()) out.add(t);
            }
            return out;
        }
    }

    private static Connection abrirAdmin() throws SQLException {
        String host = orElse(System.getenv("TICKETPREMIUM_DB_HOST"), "3.239.254.34");
        String port = orElse(System.getenv("TICKETPREMIUM_DB_PORT"), "3306");
        String user = orElse(System.getenv("TICKETPREMIUM_DB_USER"), "admin");
        String pass = orElse(System.getenv("TICKETPREMIUM_DB_PASSWORD"), "SqlAmazon2026!");
        String url  = "jdbc:mysql://" + host + ":" + port + "/"
                    + "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
        return DriverManager.getConnection(url, user, pass);
    }

    private static String orElse(String a, String b) {
        return (a != null && !a.isBlank()) ? a : b;
    }

    private static String resumen(String sql) {
        String s = sql.replaceAll("\\s+", " ").trim();
        return s.length() > 80 ? s.substring(0, 80) + "..." : s;
    }
}
