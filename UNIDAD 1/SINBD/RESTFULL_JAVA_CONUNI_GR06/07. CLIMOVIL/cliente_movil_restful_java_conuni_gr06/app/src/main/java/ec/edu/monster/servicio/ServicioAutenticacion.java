package ec.edu.monster.servicio;

import ec.edu.monster.util.ClienteRest;
import org.json.JSONObject;

public class ServicioAutenticacion {

    public boolean iniciarSesion(String usuario, String contrasena) throws Exception {
        JSONObject cuerpo = new JSONObject();
        cuerpo.put("usuario", usuario == null ? "" : usuario);
        cuerpo.put("contrasena", contrasena == null ? "" : contrasena);
        JSONObject respuesta = ClienteRest.post("/sesion", cuerpo.toString());
        return respuesta.optBoolean("valido", false);
    }
}
