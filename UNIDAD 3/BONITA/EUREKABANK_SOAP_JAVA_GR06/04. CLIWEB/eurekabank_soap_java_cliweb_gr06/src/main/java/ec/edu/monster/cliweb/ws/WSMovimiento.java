package ec.edu.monster.cliweb.ws;

import jakarta.jws.WebMethod;
import jakarta.jws.WebParam;
import jakarta.jws.WebService;
import java.util.List;

@WebService(name = "WSMovimiento", targetNamespace = "http://ws.monster.edu.ec/")
public interface WSMovimiento {

    @WebMethod(operationName = "listarMovimientos")
    List<MovimientoModel> listarMovimientos(@WebParam(name = "cuenta") String cuenta);
}
