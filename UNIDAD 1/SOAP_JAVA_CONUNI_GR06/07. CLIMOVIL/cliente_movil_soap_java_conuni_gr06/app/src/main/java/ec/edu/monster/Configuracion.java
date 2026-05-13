package ec.edu.monster;

/**
 * UN SOLO LUGAR para configurar la conexion al servidor SOAP CONUNI.
 *
 * Si cambias de red (laboratorio -> hotspot del celular -> casa), modifica
 * la IP en URL_SERVIDOR y vuelve a compilar/instalar el APK.
 *
 * Notas utiles:
 *  - Emulador de Android Studio en la MISMA maquina del servidor -> usar 10.0.2.2 en vez de localhost
 *  - Telefono fisico en la misma red Wi-Fi/hotspot que el servidor -> usar la IP LAN del servidor
 *  - HTTPS: cambiar el esquema http:// -> https://
 */
public final class Configuracion {

    private Configuracion() {}

    /** URL completa del endpoint del WebService CONUNI. */
    public static final String URL_SERVIDOR =
            "http://10.25.36.208:8080/servidor_soap_java_conuni_gr06/CONUNI";

    /** Namespace del servicio (targetNamespace generado por JAX-WS a partir del paquete del @WebService). */
    public static final String ESPACIO_NOMBRES =
            "http://controlador.monster.edu.ec/";
}
