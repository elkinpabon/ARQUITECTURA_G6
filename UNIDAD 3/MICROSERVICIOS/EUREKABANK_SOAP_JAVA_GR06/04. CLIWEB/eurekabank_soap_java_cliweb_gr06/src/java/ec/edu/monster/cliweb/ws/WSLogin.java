package ec.edu.monster.cliweb.ws;

import jakarta.jws.WebMethod;
import jakarta.jws.WebParam;
import jakarta.jws.WebService;

@WebService(name = "WSLogin", targetNamespace = "http://ws.monster.edu.ec/")
public interface WSLogin {

    @WebMethod(operationName = "iniciarSesion")
    boolean iniciarSesion(
            @WebParam(name = "usuario") String usuario,
            @WebParam(name = "clave") String clave);

    @WebMethod(operationName = "clienteDeUsuario")
    String clienteDeUsuario(@WebParam(name = "usuario") String usuario);
}
