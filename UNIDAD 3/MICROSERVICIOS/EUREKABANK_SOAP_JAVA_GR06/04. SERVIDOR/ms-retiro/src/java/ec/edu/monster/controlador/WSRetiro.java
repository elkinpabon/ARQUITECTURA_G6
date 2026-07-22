package ec.edu.monster.controlador;

import ec.edu.monster.modelo.Resultado;
import ec.edu.monster.servicio.RetiroService;
import jakarta.jws.WebMethod;
import jakarta.jws.WebParam;
import jakarta.jws.WebService;

/** Fachada SOAP del microservicio ms-retiro. */
@WebService(serviceName = "WSRetiro", targetNamespace = "http://ws.monster.edu.ec/")
public class WSRetiro {

    private final RetiroService retiroService = new RetiroService();

    @WebMethod(operationName = "retirar")
    public Resultado retirar(@WebParam(name = "cuenta") String cuenta,
                             @WebParam(name = "monto") String monto,
                             @WebParam(name = "moneda") String moneda) {
        return retiroService.retirar(cuenta, monto, moneda);
    }
}
