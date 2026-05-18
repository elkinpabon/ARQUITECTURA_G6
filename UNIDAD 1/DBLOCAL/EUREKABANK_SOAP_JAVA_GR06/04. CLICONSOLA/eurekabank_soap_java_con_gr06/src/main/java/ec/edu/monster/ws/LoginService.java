package ec.edu.monster.ws;

import ec.edu.monster.ws.WSLogin;
import ec.edu.monster.ws.WSLogin_Service;

public class LoginService {
    public boolean auth(String usuario, String clave) {
        WSLogin_Service service = new WSLogin_Service();
        WSLogin port = service.getWSLoginPort();
        // El servidor aplica SHA1 internamente: se envia la clave en texto plano.
        return port.iniciarSesion(usuario, clave);
    }
}
