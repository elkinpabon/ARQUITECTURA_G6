package ec.edu.monster.servicio;

import ec.edu.monster.rest.Rest;
import jakarta.json.Json;
import jakarta.json.JsonObject;

/** Cliente REST de login (JSON sobre HTTP). */
public class LoginClient {

    public boolean iniciarSesion(String usuario, String clave) {
        JsonObject body = Json.createObjectBuilder()
                .add("usuario", usuario == null ? "" : usuario)
                .add("clave", clave == null ? "" : clave)
                .build();
        JsonObject r = Rest.post("/login", body.toString());
        return Rest.bool(r, "exito");
    }

    /** Código de cliente del usuario, o "" si es administrativo (monster). */
    public String clienteDeUsuario(String usuario) {
        String s = Rest.getText("/login/cliente/" + Rest.enc(usuario));
        return s == null ? "" : s.trim();
    }
}
