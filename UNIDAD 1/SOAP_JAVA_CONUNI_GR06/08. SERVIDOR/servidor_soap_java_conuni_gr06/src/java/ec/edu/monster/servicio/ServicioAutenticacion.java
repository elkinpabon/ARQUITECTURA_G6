package ec.edu.monster.servicio;

import ec.edu.monster.modelo.Credencial;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class ServicioAutenticacion {

    private static final String RECURSO_CREDENCIALES = "credenciales.txt";

    public boolean autenticar(String usuario, String contrasena) {
        if (usuario == null || contrasena == null) {
            return false;
        }
        Credencial ingresada = new Credencial(usuario, contrasena);
        Credencial almacenada = leerCredencialArchivo();
        return almacenada != null && almacenada.coincide(ingresada);
    }

    private Credencial leerCredencialArchivo() {
        try (InputStream entrada = getClass().getClassLoader().getResourceAsStream(RECURSO_CREDENCIALES)) {
            if (entrada == null) {
                return null;
            }
            try (BufferedReader lector = new BufferedReader(new InputStreamReader(entrada, StandardCharsets.UTF_8))) {
                String linea;
                while ((linea = lector.readLine()) != null) {
                    linea = linea.trim();
                    if (linea.isEmpty() || linea.startsWith("#")) {
                        continue;
                    }
                    String[] partes = linea.split(":", 2);
                    if (partes.length == 2) {
                        return new Credencial(partes[0], partes[1]);
                    }
                }
                return null;
            }
        } catch (Exception excepcion) {
            return null;
        }
    }
}
