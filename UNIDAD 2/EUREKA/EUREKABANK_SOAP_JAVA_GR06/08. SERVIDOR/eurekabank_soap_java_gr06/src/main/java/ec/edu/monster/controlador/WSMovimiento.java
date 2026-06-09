package ec.edu.monster.controlador;

import ec.edu.monster.modelo.MovimientoModel;
import ec.edu.monster.servicio.MovimientoService;
import jakarta.jws.WebMethod;
import jakarta.jws.WebParam;
import jakarta.jws.WebService;
import java.util.List;

/** Fachada SOAP de consulta de movimientos. */
@WebService(serviceName = "WSMovimiento", targetNamespace = "http://ws.monster.edu.ec/")
public class WSMovimiento {

    private final MovimientoService movimientoService = new MovimientoService();

    @WebMethod(operationName = "listarMovimientos")
    public List<MovimientoModel> listarMovimientos(@WebParam(name = "cuenta") String cuenta) {
        return movimientoService.listarMovimientos(cuenta);
    }
}
