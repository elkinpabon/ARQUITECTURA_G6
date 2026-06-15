package ec.edu.monster.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

/** Resuelve la URL base del servidor SOAP TicketPremium. */
public final class ServidorConfig {

    private static final String CLAVE = "servidor.base";
    private static final String DEFAULT = "http://localhost:8080/servidor_soap_java_federacion_gr06";
    private static final String base;

    static {
        String v = System.getProperty("ticketpremium.servidor");
        if (vacio(v)) v = System.getenv("TICKETPREMIUM_SERVIDOR_BASE");
        if (vacio(v)) v = desdeArchivoExterno();
        if (vacio(v)) v = desdeClasspath();
        if (vacio(v)) v = DEFAULT;
        base = v.trim().replaceAll("/+$", "");
    }

    private ServidorConfig() { }

    public static String base() { return base; }
    public static String wsdlFederacion() { return base + "/WSFederacion?wsdl"; }
    public static String endpointFederacion() { return base + "/WSFederacion"; }

    private static boolean vacio(String s) { return s == null || s.isBlank(); }

    private static String rutaExterna() {
        String env = System.getenv("TICKETPREMIUM_CONF");
        if (!vacio(env)) return env;
        return System.getProperty("user.home", ".") + File.separator + "ticketpremium-servidor.properties";
    }

    private static String desdeArchivoExterno() {
        File f = new File(rutaExterna());
        if (!f.isFile()) return null;
        try (InputStream in = new FileInputStream(f)) {
            Properties p = new Properties();
            p.load(in);
            return p.getProperty(CLAVE);
        } catch (Exception ignore) {
            return null;
        }
    }

    private static String desdeClasspath() {
        try (InputStream in = ServidorConfig.class.getResourceAsStream("/servidor.properties")) {
            if (in == null) return null;
            Properties p = new Properties();
            p.load(in);
            return p.getProperty(CLAVE);
        } catch (Exception ignore) {
            return null;
        }
    }
}
