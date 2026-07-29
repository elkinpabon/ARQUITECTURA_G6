package ec.edu.monster.cliweb.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/** Endpoints SOAP configurables sin consultar los WSDL en tiempo de ejecucion. */
public final class MicroserviciosConfig {

    private static final Properties PROPERTIES = load();

    private MicroserviciosConfig() { }

    private static Properties load() {
        Properties properties = new Properties();
        try (InputStream input = MicroserviciosConfig.class.getClassLoader()
                .getResourceAsStream("microservices.properties")) {
            if (input != null) properties.load(input);
        } catch (IOException e) {
            throw new IllegalStateException("No se pudo leer microservices.properties", e);
        }
        return properties;
    }

    private static String endpoint(String key, String defaultValue) {
        String value = System.getProperty(key);
        if (value == null || value.isBlank()) value = System.getenv(key.toUpperCase().replace('.', '_'));
        if (value == null || value.isBlank()) value = PROPERTIES.getProperty(key, defaultValue);
        return value.trim();
    }

    public static String epLogin() { return endpoint("ms.autenticacion", "http://localhost:8080/ms-autenticacion/WSLogin"); }
    public static String epConsulta() { return endpoint("ms.consulta", "http://localhost:8080/ms-consulta/WSConsulta"); }
    public static String epMovimiento() { return endpoint("ms.movimiento", "http://localhost:8080/ms-consulta/WSMovimiento"); }
    public static String epDeposito() { return endpoint("ms.deposito", "http://localhost:8080/ms-deposito/WSDeposito"); }
    public static String epRetiro() { return endpoint("ms.retiro", "http://localhost:8080/ms-retiro/WSRetiro"); }
    public static String epTransferencia() { return endpoint("ms.transferencia", "http://localhost:8080/ms-transferencia/WSTransferencia"); }
}
