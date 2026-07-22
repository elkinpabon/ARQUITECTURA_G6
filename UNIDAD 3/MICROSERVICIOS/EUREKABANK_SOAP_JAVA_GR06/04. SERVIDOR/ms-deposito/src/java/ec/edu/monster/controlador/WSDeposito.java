package ec.edu.monster.controlador;

import ec.edu.monster.modelo.Resultado;
import ec.edu.monster.servicio.DepositoService;
import jakarta.jws.WebMethod;
import jakarta.jws.WebParam;
import jakarta.jws.WebService;

/** Fachada SOAP del microservicio ms-deposito. */
@WebService(serviceName = "WSDeposito", targetNamespace = "http://ws.monster.edu.ec/")
public class WSDeposito {

    private final DepositoService depositoService = new DepositoService();

    @WebMethod(operationName = "depositar")
    public Resultado depositar(@WebParam(name = "cuenta") String cuenta,
                               @WebParam(name = "monto") String monto,
                               @WebParam(name = "moneda") String moneda) {
        return depositoService.depositar(cuenta, monto, moneda);
    }
}
