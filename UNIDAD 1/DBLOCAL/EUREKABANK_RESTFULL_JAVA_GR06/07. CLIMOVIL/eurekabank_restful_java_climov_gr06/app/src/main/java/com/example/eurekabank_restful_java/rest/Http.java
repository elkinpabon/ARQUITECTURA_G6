package com.example.eurekabank_restful_java.rest;

import com.example.eurekabank_restful_java.config.ServidorConfig;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/** Cliente HTTP/JSON hacia el servidor REST (HttpURLConnection + org.json). */
public final class Http {

    private Http() { }

    public static String enc(String s) {
        try {
            return URLEncoder.encode(s == null ? "" : s, "UTF-8");
        } catch (Exception e) { return ""; }
    }

    private static String leer(HttpURLConnection c) throws Exception {
        int code = c.getResponseCode();
        InputStream is = (code >= 400) ? c.getErrorStream() : c.getInputStream();
        StringBuilder sb = new StringBuilder();
        if (is != null) {
            try (BufferedReader br = new BufferedReader(
                    new InputStreamReader(is, StandardCharsets.UTF_8))) {
                String l;
                while ((l = br.readLine()) != null) sb.append(l);
            }
        }
        if (code >= 400) {
            throw new RuntimeException("HTTP " + code + ": " + sb);
        }
        return sb.toString();
    }

    private static HttpURLConnection abrir(String path, String metodo)
            throws Exception {
        URL u = new URL(ServidorConfig.BASE + path);
        HttpURLConnection c = (HttpURLConnection) u.openConnection();
        c.setRequestMethod(metodo);
        c.setConnectTimeout(15000);
        c.setReadTimeout(20000);
        c.setRequestProperty("Accept", "application/json");
        return c;
    }

    public static String get(String path) throws Exception {
        HttpURLConnection c = abrir(path, "GET");
        return leer(c);
    }

    public static String post(String path, String json) throws Exception {
        HttpURLConnection c = abrir(path, "POST");
        c.setDoOutput(true);
        c.setRequestProperty("Content-Type", "application/json");
        try (OutputStream os = c.getOutputStream()) {
            os.write((json == null ? "{}" : json)
                    .getBytes(StandardCharsets.UTF_8));
        }
        return leer(c);
    }

    public static String delete(String path) throws Exception {
        HttpURLConnection c = abrir(path, "DELETE");
        return leer(c);
    }
}
