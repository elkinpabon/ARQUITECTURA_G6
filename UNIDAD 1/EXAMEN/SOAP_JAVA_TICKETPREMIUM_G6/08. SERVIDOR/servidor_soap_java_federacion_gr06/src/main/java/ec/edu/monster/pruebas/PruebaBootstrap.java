package ec.edu.monster.pruebas;

import ec.edu.monster.persistencia.BootstrapEngine;
import ec.edu.monster.persistencia.ConexionBD;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * Prueba el bootstrap automatico:
 *   1) DROPEA la base ticketpremiumDB.
 *   2) Invoca BootstrapEngine.ejecutar() (la misma logica que dispara Payara
 *      via BootstrapBD al desplegar).
 *   3) Verifica que la BD existe de nuevo, con las 5 tablas y la semilla esperada.
 *
 *  OJO: borra y recrea la BD. No correr en produccion.
 */
public class PruebaBootstrap {

    public static void main(String[] args) throws Exception {
        System.out.println("================================================================");
        System.out.println(" PRUEBA EXTRA - BootstrapEngine (drop + auto-recreate)");
        System.out.println("================================================================");

        dropearBD();
        System.out.println("[OK] ticketpremiumDB eliminada.");

        System.out.println("\nInvocando BootstrapEngine.ejecutar() ...");
        BootstrapEngine.ejecutar();
        System.out.println("[OK] Bootstrap ejecutado.");

        System.out.println("\nVerificando reconstruccion ...");
        int fallos = 0;
        fallos += verificarConteo("USUARIO",           4);
        fallos += verificarConteo("PARTIDO_FUTBOL",    5);
        fallos += verificarConteo("LOCALIDAD_PARTIDO", 20);
        fallos += verificarConteo("FACTURA",           5);   // demo data
        fallos += verificarConteo("DETALLE_FACTURA",   5);   // demo data

        // Bootstrap idempotente: correrlo otra vez no debe duplicar nada
        System.out.println("\nProbando idempotencia (corriendo bootstrap por 2a vez) ...");
        ec.edu.monster.persistencia.BootstrapEngine.ejecutar();
        fallos += verificarConteo("USUARIO",           4);
        fallos += verificarConteo("PARTIDO_FUTBOL",    5);
        fallos += verificarConteo("LOCALIDAD_PARTIDO", 20);
        fallos += verificarConteo("FACTURA",           5);
        fallos += verificarConteo("DETALLE_FACTURA",   5);

        System.out.println("================================================================");
        System.out.println(" RESULTADO: " + (fallos == 0 ? "PASS" : "FAIL (" + fallos + ")"));
        if (fallos != 0) System.exit(1);
    }

    private static void dropearBD() throws Exception {
        String host = orElse(System.getenv("TICKETPREMIUM_DB_HOST"), "localhost");
        String port = orElse(System.getenv("TICKETPREMIUM_DB_PORT"), "3306");
        String user = orElse(System.getenv("TICKETPREMIUM_DB_USER"), "root");
        String pass = orElse(System.getenv("TICKETPREMIUM_DB_PASSWORD"), "admin2002");
        String url  = "jdbc:mysql://" + host + ":" + port + "/"
                    + "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
        try (Connection cn = DriverManager.getConnection(url, user, pass);
             Statement st = cn.createStatement()) {
            st.execute("DROP DATABASE IF EXISTS ticketpremiumDB");
        }
    }

    private static int verificarConteo(String tabla, int esperado) {
        try (Connection cn = ConexionBD.conectar();
             Statement st = cn.createStatement();
             ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM " + tabla)) {
            rs.next();
            int real = rs.getInt(1);
            boolean ok = real == esperado;
            System.out.printf("    [%s] %-20s esperado=%d  real=%d%n",
                    ok ? "OK  " : "FAIL", tabla, esperado, real);
            return ok ? 0 : 1;
        } catch (Exception e) {
            System.out.printf("    [FAIL] %-20s excepcion: %s%n", tabla, e.getMessage());
            return 1;
        }
    }

    private static String orElse(String a, String b) {
        return (a != null && !a.isBlank()) ? a : b;
    }
}
