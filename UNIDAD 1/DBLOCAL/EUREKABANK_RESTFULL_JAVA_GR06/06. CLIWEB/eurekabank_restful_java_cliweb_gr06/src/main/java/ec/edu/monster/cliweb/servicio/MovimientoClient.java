package ec.edu.monster.cliweb.servicio;

import ec.edu.monster.cliweb.rest.Rest;
import ec.edu.monster.cliweb.ws.MovimientoModel;
import jakarta.json.JsonObject;
import jakarta.json.JsonValue;
import java.util.ArrayList;
import java.util.List;

/** Cliente REST de movimientos. El servidor los devuelve ordenados desc. */
public class MovimientoClient {

    public List<MovimientoModel> listarMovimientos(String cuenta) {
        List<MovimientoModel> l = new ArrayList<>();
        for (JsonValue v : Rest.getArray("/movimientos?cuenta=" + Rest.enc(cuenta))) {
            JsonObject o = v.asJsonObject();
            MovimientoModel m = new MovimientoModel();
            m.setNumeroMovimiento(Rest.integer(o, "numeroMovimiento"));
            m.setFechaMovimiento(Rest.str(o, "fechaMovimiento"));
            m.setCodigoEmpleado(Rest.str(o, "codigoEmpleado"));
            m.setCodigoTipoMovimiento(Rest.str(o, "codigoTipoMovimiento"));
            m.setTipoDescripcion(Rest.str(o, "tipoDescripcion"));
            m.setImporteMovimiento(Rest.dbl(o, "importeMovimiento"));
            m.setCuentaReferencia(Rest.str(o, "cuentaReferencia"));
            m.setMonedaOrigen(Rest.str(o, "monedaOrigen"));
            m.setImporteOrigen(Rest.dblN(o, "importeOrigen"));
            m.setTasaAplicada(Rest.dblN(o, "tasaAplicada"));
            l.add(m);
        }
        return l;
    }
}
