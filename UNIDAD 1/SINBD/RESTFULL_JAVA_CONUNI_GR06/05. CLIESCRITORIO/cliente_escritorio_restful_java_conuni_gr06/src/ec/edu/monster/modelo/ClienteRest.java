package ec.edu.monster.modelo;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * Cliente REST generico para consumir el servicio CONUNI.
 * Reemplaza al ClienteSoap del cliente escritorio SOAP. Usa HttpURLConnection
 * y construye/parsea JSON manualmente con utilidades simples.
 */
public class ClienteRest {

    private static final String URL_BASE =
            "http://10.25.36.208:8080/servidor_restful_java_conuni_gr06/api";

    public String get(String ruta, double valor) throws Exception {
        String urlCompleta = URL_BASE + ruta + "?valor=" + URLEncoder.encode(String.valueOf(valor), StandardCharsets.UTF_8);
        return enviar("GET", urlCompleta, null);
    }

    public String post(String ruta, String cuerpoJson) throws Exception {
        return enviar("POST", URL_BASE + ruta, cuerpoJson);
    }

    private String enviar(String metodo, String urlCompleta, String cuerpo) throws Exception {
        URL url = new URL(urlCompleta);
        HttpURLConnection conexion = (HttpURLConnection) url.openConnection();
        conexion.setRequestMethod(metodo);
        conexion.setRequestProperty("Accept", "application/json");

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

    public static double extraerNumero(String json, String campo) {
        String patron = "\"" + campo + "\"";
        int inicio = json.indexOf(patron);
        if (inicio == -1) {
            throw new RuntimeException("Respuesta sin campo " + campo + ": " + json);
        }
        int dosPuntos = json.indexOf(':', inicio + patron.length());
        int i = dosPuntos + 1;
        while (i < json.length() && Character.isWhitespace(json.charAt(i))) i++;
        int fin = i;
        while (fin < json.length() && "-+0123456789.eE".indexOf(json.charAt(fin)) >= 0) fin++;
        return Double.parseDouble(json.substring(i, fin));
    }

    public static boolean extraerBooleano(String json, String campo) {
        String patron = "\"" + campo + "\"";
        int inicio = json.indexOf(patron);
        if (inicio == -1) {
            throw new RuntimeException("Respuesta sin campo " + campo + ": " + json);
        }
        int dosPuntos = json.indexOf(':', inicio + patron.length());
        int i = dosPuntos + 1;
        while (i < json.length() && Character.isWhitespace(json.charAt(i))) i++;
        return json.startsWith("true", i);
    }
}
