package ec.edu.monster.aplicacion;

import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;

/**
 * Configuracion JAX-RS. Declara la ruta base de la API REST.
 * Todas las clases anotadas con @Path quedan registradas automaticamente
 * por la deteccion de Jersey/Payara.
 */
@ApplicationPath("/api")
public class AplicacionRest extends Application {
}
