package ec.edu.monster.pruebas;

import ec.edu.monster.modelo.ResumenLocalidad;
import ec.edu.monster.servicio.ReporteService;
import java.util.List;

/**
 * Prueba la capa de servicio de reportes (ReporteService).
 *
 * Verifica:
 *   - Que el reporte "Resumen de Ventas de un Partido" se ejecute sin error.
 *   - Imprime el reporte para el partido 1 con formato de tabla.
 *
 * Notas:
 *   - Si nunca se ha ejecutado PruebaVenta, el reporte puede devolver lista vacia
 *     (eso es correcto: no hay ventas registradas todavia).
 *   - Para datos reales corre primero PruebaVenta.
 */
public class PruebaReporte {

    public static void main(String[] args) {
        System.out.println("================================================================");
        System.out.println(" PRUEBA 6/6 - ReporteService.resumenVentasPorPartido");
        System.out.println("================================================================");

        ReporteService servicio = new ReporteService();

        for (int codigoPartido = 1; codigoPartido <= 5; codigoPartido++) {
            List<ResumenLocalidad> filas = servicio.resumenVentasPorPartido(codigoPartido);
            System.out.printf("%n  Partido %d - %d filas en el reporte:%n",
                    codigoPartido, filas.size());

            if (filas.isEmpty()) {
                System.out.println("    (sin ventas registradas todavia)");
                continue;
            }

            System.out.printf("    %-15s %-10s %-15s%n", "LOCALIDAD", "VENDIDOS", "TOTAL RECAUDADO");
            int totalVendidos = 0;
            java.math.BigDecimal totalRecaudado = java.math.BigDecimal.ZERO;
            for (ResumenLocalidad r : filas) {
                System.out.printf("    %-15s %-10d %-15s%n",
                        r.getLocalidad(), r.getVendidos(), r.getTotalRecaudado());
                totalVendidos += r.getVendidos();
                totalRecaudado = totalRecaudado.add(r.getTotalRecaudado());
            }
            System.out.printf("    %-15s %-10d %-15s%n",
                    "-- TOTAL --", totalVendidos, totalRecaudado);
        }

        System.out.println("\n================================================================");
        System.out.println(" RESULTADO: PASS  (reporte ejecutado correctamente)");
    }
}
