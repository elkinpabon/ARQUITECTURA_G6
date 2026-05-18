package ec.edu.monster.config;

import java.io.InputStream;
import java.util.Properties;

/**
 * Configuración de conexión al servidor SOAP — archivo aparte.
 * Lee {@code servidor.properties} del classpath (clave {@code servidor.base}).
 * Cambiar el servidor = editar solo ese archivo .properties.
 */
public final class ServidorConfig {

    private static final String DEFAULT =
            "http://localhost:8080/eurekabank_soap_java_gr06";
    private static final String base;

    static {
        String v = DEFAULT;
        try (InputStream in = ServidorConfig.class
                .getResourceAsStream("/servidor.properties")) {
            if (in != null) {
                Properties p = new Properties();
                p.load(in);
                v = p.getProperty("servidor.base", DEFAULT).trim();
            }
        } catch (Exception e) {
            // se queda con el valor por defecto
        }
        base = v;
    }

    private ServidorConfig() { }

    public static String base()        { return base; }
    public static String wsdlLogin()       { return base + "/WSLogin?wsdl"; }
    public static String endpointLogin()      { return base + "/WSLogin"; }
    public static String endpointCuenta()     { return base + "/WSCuenta"; }
    public static String endpointMovimiento() { return base + "/WSMovimiento"; }
}
