package com.example.eurekabank_restful_java.config;

/**
 * Conexión a los microservicios REST.
 * Cada host se puede cambiar de forma independiente para una red LAN.
 *
 * IMPORTANTE (host del servidor según dónde corra la app):
 *  - Emulador Android   -> http://10.0.2.2:8080/...   (10.0.2.2 = localhost del PC)
 *  - Dispositivo físico  -> http://IP_DE_TU_PC:8080/... (misma red Wi-Fi)
 *  - El Manifest ya permite tráfico http (usesCleartextTraffic / network_security_config).
 */
public final class ServidorConfig {

    private static final String HOST_EMULADOR = "http://10.239.122.135:8080";

    // Sustituye solo el host que corresponda por http://IP_DE_TU_PC:8080 para LAN.
    public static final String HOST_AUTENTICACION = HOST_EMULADOR;
    public static final String HOST_CONSULTA = HOST_EMULADOR;
    public static final String HOST_TRANSACCIONES = HOST_EMULADOR;

    public static final String BASE_AUTENTICACION =
            HOST_AUTENTICACION + "/ms-rest-autenticacion/api";
    public static final String BASE_CONSULTA =
            HOST_CONSULTA + "/ms-rest-consulta/api";
    public static final String BASE_TRANSACCIONES =
            HOST_TRANSACCIONES + "/ms-rest-transacciones/api";

    private ServidorConfig() { }
}
