package ec.edu.monster.config;

import ec.edu.monster.servicio.AsientosHub;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

/** Arranca y detiene el hub de tiempo real junto con la webapp. */
@WebListener
public class AppLifecycle implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        AsientosHub.iniciar();
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        AsientosHub.detener();
    }
}
