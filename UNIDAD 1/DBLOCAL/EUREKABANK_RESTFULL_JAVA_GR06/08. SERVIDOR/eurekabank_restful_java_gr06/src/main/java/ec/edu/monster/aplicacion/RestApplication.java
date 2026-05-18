package ec.edu.monster.aplicacion;

import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;

/** Configuración JAX-RS. Todos los recursos quedan bajo /api. */
@ApplicationPath("/api")
public class RestApplication extends Application {
}
