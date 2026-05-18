package com.example.eurekabank_soap_java.soap;

import com.example.eurekabank_soap_java.config.ServidorConfig;
import com.example.eurekabank_soap_java.modelo.Movimiento;

import org.ksoap2.serialization.SoapObject;

import java.util.ArrayList;
import java.util.List;

/** WSMovimiento vía ksoap2. */
public class MovimientoService {

    public List<Movimiento> listarMovimientos(String cuenta) throws Exception {
        Object body = SoapBase.llamar(ServidorConfig.EP_MOVIMIENTO,
                SoapBase.req("listarMovimientos", "cuenta", cuenta));
        List<Movimiento> out = new ArrayList<>();
        for (Object o : SoapBase.returns(body)) {
            if (!(o instanceof SoapObject)) continue;
            SoapObject s = (SoapObject) o;
            Movimiento m = new Movimiento();
            try { m.setNumero(Integer.parseInt(
                    String.valueOf(SoapBase.prop(s, "numeroMovimiento")))); }
            catch (Exception ignore) { }
            m.setFecha(SoapBase.prop(s, "fechaMovimiento"));
            m.setTipoCodigo(SoapBase.prop(s, "codigoTipoMovimiento"));
            m.setTipoDescripcion(SoapBase.prop(s, "tipoDescripcion"));
            m.setImporte(SoapBase.propD(s, "importeMovimiento"));
            m.setCuentaReferencia(SoapBase.prop(s, "cuentaReferencia"));
            m.setMonedaOrigen(SoapBase.prop(s, "monedaOrigen"));
            String io = SoapBase.prop(s, "importeOrigen");
            String tx = SoapBase.prop(s, "tasaAplicada");
            m.setImporteOrigen(io == null ? null : Double.valueOf(io));
            m.setTasaAplicada(tx == null ? null : Double.valueOf(tx));
            out.add(m);
        }
        return out;
    }
}
