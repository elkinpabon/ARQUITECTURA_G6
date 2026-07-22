package ec.edu.monster.cliweb.servicio;

import ec.edu.monster.cliweb.ws.MovimientoModel;
import java.util.List;

/** Wrapper del WSMovimiento con endpoint configurable. */
public class MovimientoClient {

    public List<MovimientoModel> listarMovimientos(String cuenta) {
        return WsFactory.movimiento().listarMovimientos(cuenta);
    }
}
