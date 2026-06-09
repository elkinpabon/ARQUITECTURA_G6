package ec.edu.monster.servicio;

/** Wrapper de WSLogin. La clave viaja en texto plano; el servidor aplica SHA1. */
public class LoginClient {

    public boolean iniciarSesion(String usuario, String clave) {
        return WsFactory.login().iniciarSesion(usuario, clave);
    }

    /** Código de cliente del usuario, o "" si es administrativo (monster). */
    public String clienteDeUsuario(String usuario) {
        return WsFactory.login().clienteDeUsuario(usuario);
    }
}
