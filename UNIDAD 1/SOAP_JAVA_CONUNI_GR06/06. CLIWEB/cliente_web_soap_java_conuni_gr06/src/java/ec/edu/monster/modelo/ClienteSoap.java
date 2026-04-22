package ec.edu.monster.modelo;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * Cliente SOAP generico para consumir el servicio CONUNI.
 * Construye el sobre SOAP, lo envia por HTTP y extrae la respuesta.
 */
public class ClienteSoap {

    private static final String URL_SERVICIO =
            "http://localhost:8080/servidor_soap_java_conuni_gr06/CONUNI";
    private static final String ESPACIO_NOMBRES =
            "http://controlador.monster.edu.ec/";

    public String invocar(String nombreOperacion, Map<String, String> parametros) throws Exception {
        String sobreSoap = construirSobre(nombreOperacion, parametros);
        String respuesta = enviarPeticion(sobreSoap);
        return extraerValorRetorno(respuesta);
    }

    private String construirSobre(String nombreOperacion, Map<String, String> parametros) {
        StringBuilder cuerpo = new StringBuilder();
        for (Map.Entry<String, String> entrada : parametros.entrySet()) {
            cuerpo.append("<").append(entrada.getKey()).append(">")
                  .append(entrada.getValue())
                  .append("</").append(entrada.getKey()).append(">");
        }
        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
             + "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" "
             +   "xmlns:con=\"" + ESPACIO_NOMBRES + "\">"
             +   "<soapenv:Header/>"
             +   "<soapenv:Body>"
             +     "<con:" + nombreOperacion + ">" + cuerpo + "</con:" + nombreOperacion + ">"
             +   "</soapenv:Body>"
             + "</soapenv:Envelope>";
    }

    private String enviarPeticion(String sobreSoap) throws Exception {
        URL url = new URL(URL_SERVICIO);
        HttpURLConnection conexion = (HttpURLConnection) url.openConnection();
        conexion.setRequestMethod("POST");
        conexion.setRequestProperty("Content-Type", "text/xml; charset=utf-8");
        conexion.setRequestProperty("SOAPAction", "");
        conexion.setDoOutput(true);

        byte[] datos = sobreSoap.getBytes(StandardCharsets.UTF_8);
        try (OutputStream salida = conexion.getOutputStream()) {
            salida.write(datos);
        }

        int codigo = conexion.getResponseCode();
        InputStream flujo = (codigo >= 200 && codigo < 300)
                ? conexion.getInputStream()
                : conexion.getErrorStream();

        StringBuilder contenido = new StringBuilder();
        try (BufferedReader lector = new BufferedReader(
                new InputStreamReader(flujo, StandardCharsets.UTF_8))) {
            String linea;
            while ((linea = lector.readLine()) != null) {
                contenido.append(linea);
            }
        }
        return contenido.toString();
    }

    private String extraerValorRetorno(String respuestaXml) {
        int inicio = respuestaXml.indexOf("<return>");
        int fin = respuestaXml.indexOf("</return>");
        if (inicio == -1 || fin == -1) {
            throw new RuntimeException("Respuesta SOAP sin etiqueta <return>: " + respuestaXml);
        }
        return respuestaXml.substring(inicio + "<return>".length(), fin);
    }
}
