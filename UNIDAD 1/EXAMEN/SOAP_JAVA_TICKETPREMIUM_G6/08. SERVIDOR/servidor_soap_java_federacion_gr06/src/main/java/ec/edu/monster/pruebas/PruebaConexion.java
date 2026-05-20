package ec.edu.monster.pruebas;

import ec.edu.monster.persistencia.ConexionBD;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * Prueba la capa de conexion (ConexionBD).
 *
 * Verifica:
 *   1) Que el driver MySQL este disponible.
 *   2) Que la conexion se abra correctamente con la config (env vars,
 *      db.properties o defaults).
 *   3) Que la base ticketpremiumDB exista y tenga las 5 tablas esperadas.
 *
 * Ejecutar:  click derecho ▸ Run File  (o java ec.edu.monster.pruebas.PruebaConexion).
 */
public class PruebaConexion {

    public static void main(String[] args) {
        System.out.println("================================================================");
        System.out.println(" PRUEBA 1/6 - ConexionBD");
        System.out.println("================================================================");

        try (Connection cn = ConexionBD.conectar()) {
            System.out.println("[OK] Conexion abierta. Catalog: " + cn.getCatalog());
            System.out.println("     Driver: " + cn.getMetaData().getDriverName()
                              + " " + cn.getMetaData().getDriverVersion());
            System.out.println("     URL:    " + cn.getMetaData().getURL());

            String[] tablasEsperadas = {
                "USUARIO", "PARTIDO_FUTBOL", "LOCALIDAD_PARTIDO",
                "FACTURA", "DETALLE_FACTURA"
            };
            try (Statement st = cn.createStatement()) {
                for (String t : tablasEsperadas) {
                    try (ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM " + t)) {
                        rs.next();
                        System.out.printf("     %-20s -> %d filas%n", t, rs.getInt(1));
                    }
                }
            }

            System.out.println("[OK] Todas las tablas existen.");
            System.out.println("================================================================");
            System.out.println(" RESULTADO: PASS");
        } catch (Exception e) {
            System.err.println("[FAIL] " + e.getMessage());
            e.printStackTrace(System.err);
            System.exit(1);
        }
    }
}
