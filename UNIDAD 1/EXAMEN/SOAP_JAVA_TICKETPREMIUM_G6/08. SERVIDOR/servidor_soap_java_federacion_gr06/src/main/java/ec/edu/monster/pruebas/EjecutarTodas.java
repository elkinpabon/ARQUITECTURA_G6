package ec.edu.monster.pruebas;

/**
 * Runner que ejecuta TODAS las pruebas en orden:
 *   1. PruebaConexion    -> verifica acceso a MySQL y tablas.
 *   2. PruebaSesion      -> SesionService.iniciarSesion (4 casos).
 *   3. PruebaPartido     -> PartidoService.listarDisponibles.
 *   4. PruebaLocalidad   -> LocalidadService.listarPorPartido (5 partidos).
 *   5. PruebaVenta       -> VentaService.registrarVenta + misFacturas (flujo end-to-end).
 *   6. PruebaReporte     -> ReporteService.resumenVentasPorPartido (5 partidos).
 *
 * Si alguna falla, las siguientes se omiten y el programa termina con codigo 1.
 *
 * Uso: click derecho ▸ Run File en NetBeans
 *      o:  java -cp target/classes ec.edu.monster.pruebas.EjecutarTodas
 */
public class EjecutarTodas {

    public static void main(String[] args) {
        long t0 = System.currentTimeMillis();

        System.out.println("################################################################");
        System.out.println("#                                                              #");
        System.out.println("#   BATERIA DE PRUEBAS - servidor_soap_java_federacion_gr06    #");
        System.out.println("#                                                              #");
        System.out.println("################################################################");

        ejecutar("PruebaConexion",  PruebaConexion::main);
        ejecutar("PruebaSesion",    PruebaSesion::main);
        ejecutar("PruebaPartido",   PruebaPartido::main);
        ejecutar("PruebaLocalidad", PruebaLocalidad::main);
        ejecutar("PruebaVenta",     PruebaVenta::main);
        ejecutar("PruebaReporte",   PruebaReporte::main);

        long dt = System.currentTimeMillis() - t0;
        System.out.println();
        System.out.println("################################################################");
        System.out.println("#                                                              #");
        System.out.println("#   TODAS LAS PRUEBAS PASARON en " + dt + " ms");
        System.out.println("#                                                              #");
        System.out.println("################################################################");
    }

    @FunctionalInterface
    private interface PruebaMain {
        void run(String[] args);
    }

    private static void ejecutar(String nombre, PruebaMain m) {
        System.out.println();
        System.out.println(">>> Iniciando " + nombre + " ...");
        try {
            m.run(new String[0]);
        } catch (Throwable t) {
            System.err.println("[FALLO CRITICO en " + nombre + "] " + t.getMessage());
            t.printStackTrace(System.err);
            System.exit(1);
        }
    }
}
