package ec.edu.monster.controlador;

import ec.edu.monster.modelo.Resultado;
import ec.edu.monster.servicio.TransferenciaService;
import jakarta.jws.WebMethod;
import jakarta.jws.WebParam;
import jakarta.jws.WebService;

/** Fachada SOAP del microservicio ms-transferencia. */
@WebService(serviceName = "WSTransferencia", targetNamespace = "http://ws.monster.edu.ec/")
public class WSTransferencia {

    private final TransferenciaService transferenciaService = new TransferenciaService();

    @WebMethod(operationName = "transferir")
    public Resultado transferir(@WebParam(name = "origen") String origen,
                                @WebParam(name = "destino") String destino,
                                @WebParam(name = "monto") String monto,
                                @WebParam(name = "moneda") String moneda) {
        return transferenciaService.transferir(origen, destino, monto, moneda);
    }
}
