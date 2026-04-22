package ec.edu.monster.util;

/**
 * Constantes para el consumo SOAP del servicio CONUNI.
 * Nota sobre la URL: en el emulador de Android, "10.0.2.2" apunta al localhost
 * de la maquina anfitriona donde corre Payara. Si lo pruebas en un dispositivo
 * fisico conectado a la misma red WiFi, cambia esta IP por la del host (por
 * ejemplo "192.168.1.10").
 */
public class SoapConstants {

    public static final String NAMESPACE = "http://controlador.monster.edu.ec/";
    public static final String URL = "http://10.0.2.2:8080/servidor_soap_java_conuni_gr06/CONUNI";
    public static final String SOAP_ACTION = "";

    // ===== Autenticacion =====
    public static final String METODO_INICIAR_SESION = "iniciarSesion";

    // ===== Longitud =====
    public static final String METODO_METROS_A_PIES          = "metrosAPies";
    public static final String METODO_KILOMETROS_A_MILLAS    = "kilometrosAMillas";
    public static final String METODO_CENTIMETROS_A_PULGADAS = "centimetrosAPulgadas";
    public static final String METODO_YARDAS_A_METROS        = "yardasAMetros";
    public static final String METODO_MILIMETROS_A_PULGADAS  = "milimetrosAPulgadas";

    // ===== Masa =====
    public static final String METODO_KILOGRAMOS_A_LIBRAS    = "kilogramosALibras";
    public static final String METODO_GRAMOS_A_ONZAS         = "gramosAOnzas";
    public static final String METODO_TONELADAS_A_KILOGRAMOS = "toneladasAKilogramos";
    public static final String METODO_LIBRAS_A_ONZAS         = "librasAOnzas";
    public static final String METODO_MILIGRAMOS_A_GRAMOS    = "miligramosAGramos";

    // ===== Temperatura =====
    public static final String METODO_CELSIUS_A_FAHRENHEIT   = "celsiusAFahrenheit";
    public static final String METODO_FAHRENHEIT_A_CELSIUS   = "fahrenheitACelsius";
    public static final String METODO_CELSIUS_A_KELVIN       = "celsiusAKelvin";
    public static final String METODO_KELVIN_A_CELSIUS       = "kelvinACelsius";
    public static final String METODO_FAHRENHEIT_A_KELVIN    = "fahrenheitAKelvin";

    private SoapConstants() { }
}
