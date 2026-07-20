package ec.edu.monster.rest;

import ec.edu.monster.config.ServidorConfig;
import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;
import jakarta.json.JsonStructure;

import java.io.StringReader;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

/** Cliente HTTP/JSON hacia el servidor REST (java.net.http + JSON-P). */
public final class Rest {

    private static final HttpClient HTTP = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10)).build();

    private Rest() { }

    public static String enc(String s) {
        return URLEncoder.encode(s == null ? "" : s, StandardCharsets.UTF_8);
    }

    private static String url(String path) { return ServidorConfig.base() + path; }

    private static String enviar(HttpRequest req) {
        try {
            HttpResponse<String> r = HTTP.send(req,
                    HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
            if (r.statusCode() >= 400) {
                throw new RuntimeException("HTTP " + r.statusCode() + " en " + req.uri());
            }
            return r.body();
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("No se pudo contactar el servidor REST: "
                    + e.getMessage(), e);
        }
    }

    public static String getText(String path) {
        return enviar(HttpRequest.newBuilder(URI.create(url(path))).GET().build());
    }

    private static JsonStructure parse(String body) {
        if (body == null || body.isBlank()) return null;
        try (JsonReader rd = Json.createReader(new StringReader(body))) {
            return rd.read();
        }
    }

    public static JsonObject getObject(String path) {
        return (JsonObject) parse(getText(path));
    }

    public static JsonArray getArray(String path) {
        JsonStructure s = parse(getText(path));
        return s == null ? Json.createArrayBuilder().build() : (JsonArray) s;
    }

    public static JsonObject post(String path, String body) {
        HttpRequest req = HttpRequest.newBuilder(URI.create(url(path)))
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(
                        body == null ? "{}" : body, StandardCharsets.UTF_8))
                .build();
        return (JsonObject) parse(enviar(req));
    }

    public static JsonObject delete(String path) {
        HttpRequest req = HttpRequest.newBuilder(URI.create(url(path)))
                .header("Accept", "application/json").DELETE().build();
        return (JsonObject) parse(enviar(req));
    }

    public static String str(JsonObject o, String k) {
        return o != null && o.containsKey(k) && !o.isNull(k) ? o.getString(k) : null;
    }
    public static double dbl(JsonObject o, String k) {
        return o != null && o.containsKey(k) && !o.isNull(k)
                ? o.getJsonNumber(k).doubleValue() : 0d;
    }
    public static Double dblN(JsonObject o, String k) {
        return o != null && o.containsKey(k) && !o.isNull(k)
                ? o.getJsonNumber(k).doubleValue() : null;
    }
    public static int integer(JsonObject o, String k) {
        return o != null && o.containsKey(k) && !o.isNull(k)
                ? o.getJsonNumber(k).intValue() : 0;
    }
    public static boolean bool(JsonObject o, String k) {
        return o != null && o.containsKey(k) && o.getBoolean(k, false);
    }
}
