package com.example.eurekabank_soap_java.config;

/**
 * Configuracion centralizada de endpoints SOAP para Android.
 * Para emulador, 10.0.2.2 representa el PC anfitrion. Para celular fisico,
 * sustituye cada host por la IP LAN del equipo responsable del microservicio.
 */
public final class ServidorConfig {

    // Cambiar cada host para distribuir los servicios entre laptops en la LAN.
    public static String IP_AUTENTICACION = "192.168.1.54";
    public static String IP_CONSULTA = "192.168.1.54";
    public static String IP_DEPOSITO = "192.168.1.54";
    public static String IP_RETIRO = "192.168.1.54";
    public static String IP_TRANSFERENCIA = "192.168.1.54";

    public static int PORT_AUTENTICACION = 8080;
    public static int PORT_CONSULTA      = 8080;
    public static int PORT_DEPOSITO      = 8080;
    public static int PORT_RETIRO        = 8080;
    public static int PORT_TRANSFERENCIA = 8080;

    public static final String NAMESPACE = "http://ws.monster.edu.ec/";

    public static String endpointLogin() {
        return endpoint(IP_AUTENTICACION, PORT_AUTENTICACION, "/ms-autenticacion/WSLogin");
    }

    public static String endpointConsulta() {
        return endpoint(IP_CONSULTA, PORT_CONSULTA, "/ms-consulta/WSConsulta");
    }

    public static String endpointMovimiento() {
        return endpoint(IP_CONSULTA, PORT_CONSULTA, "/ms-consulta/WSMovimiento");
    }

    public static String endpointDeposito() {
        return endpoint(IP_DEPOSITO, PORT_DEPOSITO, "/ms-deposito/WSDeposito");
    }

    public static String endpointRetiro() {
        return endpoint(IP_RETIRO, PORT_RETIRO, "/ms-retiro/WSRetiro");
    }

    public static String endpointTransferencia() {
        return endpoint(IP_TRANSFERENCIA, PORT_TRANSFERENCIA,
                "/ms-transferencia/WSTransferencia");
    }

    private static String endpoint(String host, int puerto, String ruta) {
        return "http://" + host + ":" + puerto + ruta;
    }

    private ServidorConfig() { }
}
