package ec.edu.monster.pruebas;

import ec.edu.monster.modelo.SesionResultado;
import ec.edu.monster.servicio.SesionService;

/**
 * Prueba la capa de servicio de autenticacion (SesionService).
 *
 * Casos:
 *   - Admin master:  monster / monster9   -> debe entrar con rol ADMIN.
 *   - Cliente:       josue   / admin2002  -> debe entrar con rol CLIENTE.
 *   - Credenciales invalidas              -> debe fallar con mensaje claro.
 *   - Campos vacios                       -> debe fallar antes de tocar BD.
 */
public class PruebaSesion {

    public static void main(String[] args) {
        System.out.println("================================================================");
        System.out.println(" PRUEBA 2/6 - SesionService");
        System.out.println("================================================================");

        SesionService servicio = new SesionService();
        int fail = 0;

        fail += chequear(servicio.iniciarSesion("monster", "monster9"),
                          true,  "ADMIN",   "Login admin master");
        fail += chequear(servicio.iniciarSesion("josue", "admin2002"),
                          true,  "CLIENTE", "Login cliente josue");
        fail += chequear(servicio.iniciarSesion("monster", "xxx"),
                          false, null,      "Login con clave incorrecta");
        fail += chequear(servicio.iniciarSesion("",        ""),
                          false, null,      "Login con campos vacios");

        System.out.println("================================================================");
        System.out.println(" RESULTADO: " + (fail == 0 ? "PASS" : "FAIL (" + fail + " casos)"));
        if (fail != 0) System.exit(1);
    }

    /** Imprime el resultado y devuelve 0 si paso, 1 si fallo. */
    private static int chequear(SesionResultado r, boolean exitoEsperado,
                                String rolEsperado, String caso) {
        boolean ok = (r.isExito() == exitoEsperado)
                  && (rolEsperado == null
                      || (r.getUsuario() != null && rolEsperado.equals(r.getUsuario().getRol())));

        System.out.printf("  [%s] %s%n", ok ? "OK  " : "FAIL", caso);
        System.out.printf("        exito=%b  mensaje=%s%n", r.isExito(), r.getMensaje());
        if (r.getUsuario() != null) {
            System.out.printf("        usuario: id=%d  rol=%s  nombre=%s%n",
                    r.getUsuario().getIdUsuario(), r.getUsuario().getRol(),
                    r.getUsuario().getNombre());
        }
        return ok ? 0 : 1;
    }
}
