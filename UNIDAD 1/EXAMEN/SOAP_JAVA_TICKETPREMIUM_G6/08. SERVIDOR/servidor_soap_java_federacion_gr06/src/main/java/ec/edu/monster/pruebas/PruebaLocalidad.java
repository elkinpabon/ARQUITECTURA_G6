package ec.edu.monster.pruebas;

import ec.edu.monster.modelo.Localidad;
import ec.edu.monster.servicio.LocalidadService;
import java.util.List;

/**
 * Prueba la capa de servicio de localidades (LocalidadService).
 *
 * Verifica:
 *   - Que se listen localidades disponibles para el partido 1.
 *   - Que cada localidad tenga DISPONIBILIDAD > 0.
 *   - Que el seed total cubra los 20 registros (rubrica 0.5 pt).
 */
public class PruebaLocalidad {

    public static void main(String[] args) {
        System.out.println("================================================================");
        System.out.println(" PRUEBA 4/6 - LocalidadService.listarPorPartido");
        System.out.println("================================================================");

        LocalidadService servicio = new LocalidadService();
        int totalEnTodosLosPartidos = 0;
        int fallos = 0;

        for (int codigoPartido = 1; codigoPartido <= 5; codigoPartido++) {
            List<Localidad> locs = servicio.listarPorPartido(codigoPartido);
            totalEnTodosLosPartidos += locs.size();

            System.out.printf("%n  Partido %d - %d localidades:%n", codigoPartido, locs.size());
            System.out.printf("    %-15s %-15s %-10s%n", "LOCALIDAD", "DISPONIBILIDAD", "PRECIO");
            for (Localidad l : locs) {
                System.out.printf("    %-15s %-15d %-10s%n",
                        l.getCodigoLocalidad(), l.getDisponibilidad(), l.getPrecio());
                if (l.getDisponibilidad() <= 0) {
                    System.out.println("    [FAIL] DISPONIBILIDAD invalida (<=0)");
                    fallos++;
                }
            }
        }

        System.out.println("\n================================================================");
        System.out.println(" Total de localidades en BD: " + totalEnTodosLosPartidos);
        if (fallos == 0 && totalEnTodosLosPartidos >= 20) {
            System.out.println(" RESULTADO: PASS  (rubrica >=20 cumplida)");
        } else {
            System.out.println(" RESULTADO: FAIL");
            System.exit(1);
        }
    }
}
