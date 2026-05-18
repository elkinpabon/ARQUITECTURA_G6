package com.example.eurekabank_soap_java.config;

/**
 * Conexión al servidor SOAP — ARCHIVO APARTE.
 * Cambia solo BASE para apuntar a otro servidor (no se toca la lógica).
 *
 * IMPORTANTE (host del servidor según dónde corra la app):
 *  - Emulador Android  -> http://10.0.2.2:8080/...   (10.0.2.2 = localhost del PC)
 *  - Dispositivo físico -> http://IP_DE_TU_PC:8080/... (misma red Wi-Fi)
 *  - El Manifest ya permite tráfico http (usesCleartextTraffic / network_security_config).
 */
public final class ServidorConfig {

    /** Única línea a editar para cambiar de servidor. */
    public static final String BASE = "http://10.0.2.2:8080/eurekabank_soap_java_gr06";

    /** Namespace del servicio (targetNamespace de los @WebService). */
    public static final String NAMESPACE = "http://ws.monster.edu.ec/";

    public static final String EP_LOGIN      = BASE + "/WSLogin";
    public static final String EP_CUENTA     = BASE + "/WSCuenta";
    public static final String EP_MOVIMIENTO = BASE + "/WSMovimiento";

    private ServidorConfig() { }
}
