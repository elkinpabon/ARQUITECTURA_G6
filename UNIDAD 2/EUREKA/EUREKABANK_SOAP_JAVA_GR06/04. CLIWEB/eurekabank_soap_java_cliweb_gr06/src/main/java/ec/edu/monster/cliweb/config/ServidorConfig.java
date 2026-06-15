package ec.edu.monster.cliweb.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

/**
 * Conexión al servidor SOAP — UN SOLO ARCHIVO para cambiar el host/IP.
 *
 * Precedencia (gana el primero que exista):
 *  1. -Deurekabank.servidor=http://IP:8080/eurekabank_soap_java_gr06
 *  2. Archivo EXTERNO editable (cambiar IP sin recompilar/redeployar):
 *       &lt;HOME&gt;/eurekabank-servidor.properties   (clave servidor.base)
 *       (o la ruta de la variable de entorno EUREKABANK_CONF)
 *  3. Classpath /servidor.properties  (por defecto del WAR)
 *  4. Default localhost
 */
public final class ServidorConfig {

    private static final String CLAVE = "servidor.base";
    private static final String DEFAULT =
            "http://localhost:8080/eurekabank_soap_java_gr06";
    private static final String base;

    static {
        String v = System.getProperty("eurekabank.servidor");
        if (vacio(v)) v = desdeArchivoExterno();
        if (vacio(v)) v = desdeClasspath();
        if (vacio(v)) v = DEFAULT;
        base = v.trim().replaceAll("/+$", "");
    }

    private ServidorConfig() { }

    public static String base() { return base; }
    public static String wsdlLogin()      { return base + "/WSLogin?wsdl"; }
    public static String wsdlCuenta()     { return base + "/WSCuenta?wsdl"; }
    public static String wsdlMovimiento() { return base + "/WSMovimiento?wsdl"; }
    public static String epLogin()        { return base + "/WSLogin"; }
    public static String epCuenta()       { return base + "/WSCuenta"; }
    public static String epMovimiento()   { return base + "/WSMovimiento"; }

    private static boolean vacio(String s) { return s == null || s.isBlank(); }

    private static String rutaExterna() {
        String env = System.getenv("EUREKABANK_CONF");
        if (!vacio(env)) return env;
        return System.getProperty("user.home", ".")
                + File.separator + "eurekabank-servidor.properties";
    }

    private static String desdeArchivoExterno() {
        File f = new File(rutaExterna());
        if (f.isFile()) {
            try (InputStream in = new FileInputStream(f)) {
                Properties p = new Properties();
                p.load(in);
                return p.getProperty(CLAVE);
            } catch (Exception ignore) { }
        }
        return null;
    }

    private static String desdeClasspath() {
        try (InputStream in = ServidorConfig.class
                .getResourceAsStream("/servidor.properties")) {
            if (in != null) {
                Properties p = new Properties();
                p.load(in);
                return p.getProperty(CLAVE);
            }
        } catch (Exception ignore) { }
        return null;
    }
}
