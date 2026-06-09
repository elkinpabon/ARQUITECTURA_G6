package ec.edu.monster.cliweb.servicio;

import ec.edu.monster.cliweb.ws.WSLogin;

/** Wrapper del WSLogin. Endpoint configurable vía WsFactory/ServidorConfig. */
public class LoginClient {

    private WSLogin port() {
        return WsFactory.login();
    }

    public boolean iniciarSesion(String usuario, String clave) {
        // La clave viaja en texto plano; el SERVIDOR aplica SHA1.
        return port().iniciarSesion(usuario, clave);
    }

    /** Código de cliente del usuario, o "" si es administrativo (monster). */
    public String clienteDeUsuario(String usuario) {
        return port().clienteDeUsuario(usuario);
    }
}
