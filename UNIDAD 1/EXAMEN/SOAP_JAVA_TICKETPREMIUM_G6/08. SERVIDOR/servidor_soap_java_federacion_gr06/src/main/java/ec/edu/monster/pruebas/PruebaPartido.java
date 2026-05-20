package ec.edu.monster.pruebas;

import ec.edu.monster.modelo.Partido;
import ec.edu.monster.servicio.PartidoService;
import java.util.List;

/**
 * Prueba la capa de servicio de partidos (PartidoService).
 *
 * Verifica:
 *   - Que se listen partidos disponibles (FECHA >= NOW()).
 *   - Que el seed tenga >= 5 partidos (rubrica 0.5 pt).
 *   - Imprime cada partido para inspeccion visual.
 */
public class PruebaPartido {

    public static void main(String[] args) {
        System.out.println("================================================================");
        System.out.println(" PRUEBA 3/6 - PartidoService.listarDisponibles");
        System.out.println("================================================================");

        PartidoService servicio = new PartidoService();
        List<Partido> partidos = servicio.listarDisponibles();

        System.out.printf("%n  Total partidos disponibles: %d%n%n", partidos.size());
        System.out.printf("  %-6s %-25s %-25s %-22s %s%n",
                "CODIGO", "LOCAL", "VISITA", "FECHA", "LUGAR");
        System.out.println("  --------------------------------------------------------------"
                         + "----------------------------------------");
        for (Partido p : partidos) {
            System.out.printf("  %-6d %-25s %-25s %-22s %s%n",
                    p.getCodigo(), p.getEquipoLocal(), p.getEquipoVisita(),
                    p.getFecha(), p.getLugar());
        }

        System.out.println("================================================================");
        if (partidos.size() >= 5) {
            System.out.println(" RESULTADO: PASS  (rubrica >=5 cumplida)");
        } else {
            System.out.println(" RESULTADO: FAIL  (seed insuficiente: " + partidos.size() + " < 5)");
            System.exit(1);
        }
    }
}
