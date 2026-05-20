package ec.edu.monster.persistencia;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * ServletContextListener que Payara invoca al desplegar la aplicacion.
 * Delega la logica a BootstrapEngine.
 *
 * Para desactivarlo (p.ej. en produccion), exportar:
 *   TICKETPREMIUM_BOOTSTRAP_DISABLED=true
 */
@WebListener
public class BootstrapBD implements ServletContextListener {

    private static final Logger LOG = Logger.getLogger(BootstrapBD.class.getName());

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        if ("true".equalsIgnoreCase(System.getenv("TICKETPREMIUM_BOOTSTRAP_DISABLED"))) {
            LOG.info("Bootstrap deshabilitado por env var TICKETPREMIUM_BOOTSTRAP_DISABLED=true");
            return;
        }
        try {
            BootstrapEngine.ejecutar();
        } catch (Exception e) {
            LOG.log(Level.SEVERE,
                    "Bootstrap fallo. El servidor sigue arrancando pero los endpoints "
                  + "que consulten la BD devolveran error hasta que se arregle.", e);
        }
    }
}
