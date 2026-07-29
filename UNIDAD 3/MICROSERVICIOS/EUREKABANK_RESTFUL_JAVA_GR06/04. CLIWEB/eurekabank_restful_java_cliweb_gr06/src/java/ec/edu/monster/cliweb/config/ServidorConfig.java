package ec.edu.monster.cliweb.config;

import java.io.InputStream;
import java.util.Properties;

/**
 * Bases de los microservicios REST. La prioridad por clave es propiedad de
 * sistema, variable de entorno en mayúsculas y /microservices.properties.
 */
public final class ServidorConfig {

    private static final Properties PROPIEDADES = cargar();

    private ServidorConfig() { }

    public static String base(String clave) {
        String valor = System.getProperty(clave);
        if (vacio(valor)) valor = System.getenv(clave.toUpperCase().replace('.', '_'));
        if (vacio(valor)) valor = PROPIEDADES.getProperty(clave);
        if (vacio(valor)) throw new IllegalArgumentException("Base no configurada: " + clave);
        return valor.trim();
    }

    private static boolean vacio(String s) {
        return s == null || s.isBlank();
    }

    private static Properties cargar() {
        Properties p = new Properties();
        try (InputStream in = ServidorConfig.class
                .getResourceAsStream("/microservices.properties")) {
            if (in != null) {
                p.load(in);
            }
        } catch (Exception ignore) { }
        return p;
    }
}
