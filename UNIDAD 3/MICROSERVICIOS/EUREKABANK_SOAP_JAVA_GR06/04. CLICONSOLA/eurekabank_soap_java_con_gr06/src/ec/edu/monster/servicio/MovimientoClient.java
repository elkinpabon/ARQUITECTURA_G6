package ec.edu.monster.servicio;

import ec.edu.monster.ws.MovimientoModel;
import java.util.List;

/** Wrapper de WSMovimiento. */
public class MovimientoClient {

    public List<MovimientoModel> listarMovimientos(String cuenta) {
        return WsFactory.movimiento().listarMovimientos(cuenta);
    }
}
