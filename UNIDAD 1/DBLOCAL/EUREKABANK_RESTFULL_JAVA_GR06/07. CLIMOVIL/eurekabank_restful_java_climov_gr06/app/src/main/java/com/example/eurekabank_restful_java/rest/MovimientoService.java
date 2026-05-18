package com.example.eurekabank_restful_java.rest;

import com.example.eurekabank_restful_java.modelo.Movimiento;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/** Movimientos vía REST/JSON (el servidor los devuelve ordenados desc). */
public class MovimientoService {

    public List<Movimiento> listarMovimientos(String cuenta) throws Exception {
        JSONArray a = new JSONArray(
                Http.get("/movimientos?cuenta=" + Http.enc(cuenta)));
        List<Movimiento> l = new ArrayList<>();
        for (int i = 0; i < a.length(); i++) {
            JSONObject o = a.getJSONObject(i);
            Movimiento m = new Movimiento();
            m.setNumero(o.optInt("numeroMovimiento", 0));
            m.setFecha(o.optString("fechaMovimiento", null));
            m.setTipoCodigo(o.optString("codigoTipoMovimiento", null));
            m.setTipoDescripcion(o.optString("tipoDescripcion", null));
            m.setImporte(o.optDouble("importeMovimiento", 0));
            m.setCuentaReferencia(o.has("cuentaReferencia")
                    && !o.isNull("cuentaReferencia")
                    ? o.optString("cuentaReferencia") : null);
            m.setMonedaOrigen(o.has("monedaOrigen") && !o.isNull("monedaOrigen")
                    ? o.optString("monedaOrigen") : null);
            m.setImporteOrigen(o.has("importeOrigen") && !o.isNull("importeOrigen")
                    ? o.optDouble("importeOrigen") : null);
            m.setTasaAplicada(o.has("tasaAplicada") && !o.isNull("tasaAplicada")
                    ? o.optDouble("tasaAplicada") : null);
            l.add(m);
        }
        return l;
    }
}
