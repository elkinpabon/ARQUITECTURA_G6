package ec.edu.monster.cliweb.servicio;

import ec.edu.monster.cliweb.ws.MovimientoModel;
import ec.edu.monster.cliweb.ws.WSMovimiento;
import ec.edu.monster.cliweb.ws.WSMovimiento_Service;
import java.util.List;

/** Wrapper del WSMovimiento. */
public class MovimientoClient {

    public List<MovimientoModel> listarMovimientos(String cuenta) {
        WSMovimiento_Service service = new WSMovimiento_Service();
        WSMovimiento port = service.getWSMovimientoPort();
        return port.listarMovimientos(cuenta);
    }
}
