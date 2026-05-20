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

        // 3) Datos demo de facturas (idempotente: solo si FACTURA esta vacia)
        sembrarDatosDemoSiVacio();

        LOG.info("Bootstrap completado: " + sentencias.size() + " sentencias + datos demo.");
    }

    /**
     * Inserta facturas de demo asi el reporte y el historial muestran algo en
     * una BD recien creada. Solo siembra si la tabla FACTURA esta vacia para
     * no duplicar datos en cada arranque.
     *
     * Cada venta:
     *   - inserta FACTURA(id_usuario, fecha, subtotal, iva, total)
     *   - inserta DETALLE_FACTURA con cantidad y precios
     *   - decrementa DISPONIBILIDAD en LOCALIDAD_PARTIDO
     * Todo dentro de una transaccion.
     */
    private static void sembrarDatosDemoSiVacio() {
        try (Connection cn = ConexionBD.conectar()) {

            // Chequear si ya hay facturas
            try (Statement st = cn.createStatement();
                 ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM FACTURA")) {
                rs.next();
                if (rs.getInt(1) > 0) {
                    LOG.info("  FACTURA ya tiene datos -> omito siembra demo.");
                    return;
                }
            }

            LOG.info("  Sembrando facturas de demo ...");
            cn.setAutoCommit(false);
            try {
                // (idUsuario, codigoPartido, codigoLocalidad, cantidad, fechaCompra)
                int n = 0;
                n += sembrarVenta(cn, 2, 1, "GENERAL",     5,
                                   LocalDateTime.of(2026, 5, 10, 10, 0));
                n += sembrarVenta(cn, 3, 1, "PALCO",       2,
                                   LocalDateTime.of(2026, 5, 11, 14, 30));
                n += sembrarVenta(cn, 2, 2, "TRIBUNA",     3,
                                   LocalDateTime.of(2026, 5, 12, 18, 15));
                n += sembrarVenta(cn, 4, 3, "PREFERENCIA", 1,
                                   LocalDateTime.of(2026, 5, 13, 9, 45));
                n += sembrarVenta(cn, 3, 5, "GENERAL",     4,
                                   LocalDateTime.of(2026, 5, 14, 16, 20));
                cn.commit();
                LOG.info("  [OK] " + n + " facturas demo creadas.");
            } catch (SQLException e) {
                cn.rollback();
                throw e;
            } finally {
                cn.setAutoCommit(true);
            }

        } catch (SQLException e) {
            LOG.log(Level.WARNING, "No se pudo sembrar datos demo (no critico)", e);
        }
    }

    private static int sembrarVenta(Connection cn, int idUsuario, int codigoPartido,
                                    String codigoLocalidad, int cantidad,
                                    LocalDateTime fecha) throws SQLException {

        // 1) Leer precio actual de la localidad
        BigDecimal precio;
        try (PreparedStatement ps = cn.prepareStatement(
                "SELECT PRECIO FROM LOCALIDAD_PARTIDO WHERE CODIGO_PARTIDO=? AND CODIGO_LOCALIDAD=?")) {
            ps.setInt(1, codigoPartido);
            ps.setString(2, codigoLocalidad);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    LOG.warning("    Localidad inexistente p=" + codigoPartido + " l=" + codigoLocalidad);
                    return 0;
                }
                precio = rs.getBigDecimal("PRECIO");
            }
        }

        BigDecimal subtotal = precio.multiply(BigDecimal.valueOf(cantidad));
        BigDecimal iva      = subtotal.multiply(new BigDecimal("0.15"))
                                      .setScale(2, RoundingMode.HALF_UP);
        BigDecimal total    = subtotal.add(iva);

        // 2) Insertar FACTURA
        int idFactura;
        try (PreparedStatement ps = cn.prepareStatement(
                "INSERT INTO FACTURA (ID_USUARIO, FECHA, SUBTOTAL, IVA, TOTAL) " +
                "VALUES (?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, idUsuario);
            ps.setTimestamp(2, Timestamp.valueOf(fecha));
            ps.setBigDecimal(3, subtotal);
            ps.setBigDecimal(4, iva);
            ps.setBigDecimal(5, total);
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                rs.next();
                idFactura = rs.getInt(1);
            }
        }

        // 3) Insertar DETALLE_FACTURA
        try (PreparedStatement ps = cn.prepareStatement(
                "INSERT INTO DETALLE_FACTURA " +
                "(ID_FACTURA, CODIGO_PARTIDO, LOCALIDAD, CANTIDAD, PRECIO_UNITARIO, TOTAL) " +
                "VALUES (?, ?, ?, ?, ?, ?)")) {
            ps.setInt(1, idFactura);
            ps.setInt(2, codigoPartido);
            ps.setString(3, codigoLocalidad);
            ps.setInt(4, cantidad);
            ps.setBigDecimal(5, precio);
            ps.setBigDecimal(6, subtotal);
            ps.executeUpdate();
        }

        // 4) Descontar stock
        try (PreparedStatement ps = cn.prepareStatement(
                "UPDATE LOCALIDAD_PARTIDO SET DISPONIBILIDAD = DISPONIBILIDAD - ? " +
                "WHERE CODIGO_PARTIDO=? AND CODIGO_LOCALIDAD=? AND DISPONIBILIDAD >= ?")) {
            ps.setInt(1, cantidad);
            ps.setInt(2, codigoPartido);
            ps.setString(3, codigoLocalidad);
            ps.setInt(4, cantidad);
            ps.executeUpdate();
        }

        LOG.fine("    factura #" + idFactura + " uid=" + idUsuario
                + " partido=" + codigoPartido + " " + codigoLocalidad + " x" + cantidad
                + " total=" + total);
        return 1;
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
        String host = orElse(System.getenv("TICKETPREMIUM_DB_HOST"), "localhost");
        String port = orElse(System.getenv("TICKETPREMIUM_DB_PORT"), "3306");
        String user = orElse(System.getenv("TICKETPREMIUM_DB_USER"), "root");
        String pass = orElse(System.getenv("TICKETPREMIUM_DB_PASSWORD"), "admin2002");
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
