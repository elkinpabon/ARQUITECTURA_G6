package ec.edu.monster.controlador;

import ec.edu.monster.servicio.LoginService;
import jakarta.jws.WebMethod;
import jakarta.jws.WebParam;
import jakarta.jws.WebService;

/** Fachada SOAP de autenticacion del microservicio ms-autenticacion. */
@WebService(serviceName = "WSLogin", targetNamespace = "http://ws.monster.edu.ec/")
public class WSLogin {

    private final LoginService loginService = new LoginService();

    @WebMethod(operationName = "iniciarSesion")
    public boolean iniciarSesion(@WebParam(name = "usuario") String usuario,
                                 @WebParam(name = "clave") String clave) {
        return loginService.login(usuario, clave);
    }

    @WebMethod(operationName = "clienteDeUsuario")
    public String clienteDeUsuario(@WebParam(name = "usuario") String usuario) {
        return loginService.clienteDe(usuario);
    }
}
