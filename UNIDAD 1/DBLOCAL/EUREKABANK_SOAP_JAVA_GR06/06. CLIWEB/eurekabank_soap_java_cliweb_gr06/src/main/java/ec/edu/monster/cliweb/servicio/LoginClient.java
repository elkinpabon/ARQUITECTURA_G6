package ec.edu.monster.cliweb.servicio;

import ec.edu.monster.cliweb.ws.WSLogin;
import ec.edu.monster.cliweb.ws.WSLogin_Service;

/** Wrapper del WSLogin (stubs generados por wsimport). */
public class LoginClient {

    private WSLogin port() {
        return new WSLogin_Service().getWSLoginPort();
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
