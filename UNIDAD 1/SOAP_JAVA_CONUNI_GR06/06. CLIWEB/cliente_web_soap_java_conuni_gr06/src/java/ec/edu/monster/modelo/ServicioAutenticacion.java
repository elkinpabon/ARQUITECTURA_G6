package ec.edu.monster.modelo;

import java.util.LinkedHashMap;
import java.util.Map;

public class ServicioAutenticacion {

    private final ClienteSoap clienteSoap = new ClienteSoap();

    public boolean iniciarSesion(String usuario, String contrasena) throws Exception {
        Map<String, String> parametros = new LinkedHashMap<>();
        parametros.put("usuario", usuario);
        parametros.put("contrasena", contrasena);
        String respuesta = clienteSoap.invocar("iniciarSesion", parametros);
        return Boolean.parseBoolean(respuesta);
    }
}
