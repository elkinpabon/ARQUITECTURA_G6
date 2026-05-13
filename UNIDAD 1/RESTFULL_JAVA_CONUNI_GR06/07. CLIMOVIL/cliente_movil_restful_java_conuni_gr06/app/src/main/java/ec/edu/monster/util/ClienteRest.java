package ec.edu.monster.util;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * Cliente REST generico para consumir el servicio CONUNI desde Android.
 * Reemplaza al ksoap2 + SoapConstants del cliente movil SOAP. Usa HttpURLConnection
 * (incluido en Android) y org.json.JSONObject (libreria estandar de Android)
 * para parsear las respuestas JSON.
 *
 * Nota sobre la URL: en el emulador de Android, "10.0.2.2" apunta al localhost
 * de la maquina anfitriona donde corre Payara. Si lo pruebas en un dispositivo
 * fisico conectado a la misma red WiFi, cambia esta IP por la del host (por
 * ejemplo "192.168.1.10").
 */
public class ClienteRest {

    public static final String URL_BASE =
            "http://10.25.36.208:8080/servidor_restful_java_conuni_gr06/api";

    /**
     * GET con el parametro {@code valor=...} en query string.
     * Devuelve el JSON parseado.
     */
    public static JSONObject get(String ruta, double valor) throws Exception {
        String urlCompleta = URL_BASE + ruta + "?valor=" +
                URLEncoder.encode(String.valueOf(valor), StandardCharsets.UTF_8.name());
        return new JSONObject(enviar("GET", urlCompleta, null));
    }

    /**
     * POST con cuerpo JSON. Devuelve el JSON parseado.
     */
    public static JSONObject post(String ruta, String cuerpoJson) throws Exception {
        return new JSONObject(enviar("POST", URL_BASE + ruta, cuerpoJson));
    }

    private static String enviar(String metodo, String urlCompleta, String cuerpo) throws Exception {
        URL url = new URL(urlCompleta);
        HttpURLConnection conexion = (HttpURLConnection) url.openConnection();
        conexion.setRequestMethod(metodo);
        conexion.setRequestProperty("Accept", "application/json");
        conexion.setConnectTimeout(10000);
        conexion.setReadTimeout(10000);

        if (cuerpo != null) {
            conexion.setDoOutput(true);
            conexion.setRequestProperty("Content-Type", "application/json; charset=utf-8");
            byte[] datos = cuerpo.getBytes(StandardCharsets.UTF_8);
            try (OutputStream salida = conexion.getOutputStream()) {
                salida.write(datos);
            }
        }

        int codigo = conexion.getResponseCode();
        InputStream flujo = (codigo >= 200 && codigo < 300)
                ? conexion.getInputStream()
                : conexion.getErrorStream();

        StringBuilder contenido = new StringBuilder();
        if (flujo != null) {
            try (BufferedReader lector = new BufferedReader(
                    new InputStreamReader(flujo, StandardCharsets.UTF_8))) {
                String linea;
                while ((linea = lector.readLine()) != null) {
                    contenido.append(linea);
                }
            }
        }
        return contenido.toString();
    }
}
