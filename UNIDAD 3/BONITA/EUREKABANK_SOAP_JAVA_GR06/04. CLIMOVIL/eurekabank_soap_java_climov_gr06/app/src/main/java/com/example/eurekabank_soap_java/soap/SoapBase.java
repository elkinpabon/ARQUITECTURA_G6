package com.example.eurekabank_soap_java.soap;

import com.example.eurekabank_soap_java.config.ServidorConfig;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.util.ArrayList;
import java.util.List;

/**
 * Cliente SOAP genérico con ksoap2 contra el servidor JAX-WS
 * (doc/literal wrapped, SOAP 1.1, soapAction vacío).
 */
public final class SoapBase {

    private SoapBase() { }

    /** Construye la petición: op + pares (nombreParam, valor). */
    public static SoapObject req(String op, String... kv) {
        SoapObject so = new SoapObject(ServidorConfig.NAMESPACE, op);
        for (int i = 0; i + 1 < kv.length; i += 2) {
            so.addProperty(kv[i], kv[i + 1] == null ? "" : kv[i + 1]);
        }
        return so;
    }

    /** Ejecuta la operación y devuelve el cuerpo de respuesta (SoapObject wrapper). */
    public static Object llamar(String endpoint, SoapObject request) throws Exception {
        SoapSerializationEnvelope env =
                new SoapSerializationEnvelope(SoapEnvelope.VER11);
        env.dotNet = false;
        env.setOutputSoapObject(request);
        HttpTransportSE t = new HttpTransportSE(endpoint, 20000);
        t.debug = false;
        t.call("", env);                 // JAX-WS RI usa SOAPAction vacío
        return env.bodyIn;
    }

    /** Lista de elementos &lt;return&gt; del wrapper de respuesta. */
    public static List<Object> returns(Object body) {
        List<Object> out = new ArrayList<>();
        if (body instanceof SoapObject) {
            SoapObject resp = (SoapObject) body;
            int n = resp.getPropertyCount();
            for (int i = 0; i < n; i++) {
                out.add(resp.getProperty(i));
            }
        } else if (body != null) {
            out.add(body);
        }
        return out;
    }

    /** Primer &lt;return&gt; como texto (boolean/String), "" si vacío. */
    public static String returnText(Object body) {
        List<Object> r = returns(body);
        if (r.isEmpty()) return "";
        Object o = r.get(0);
        String s = String.valueOf(o);
        return ("anyType{}".equals(s) || "null".equals(s)) ? "" : s;
    }

    public static String prop(SoapObject o, String name) {
        if (o.hasProperty(name)) {
            Object v = o.getProperty(name);
            String s = String.valueOf(v);
            return ("anyType{}".equals(s) || "null".equals(s)) ? null : s;
        }
        return null;
    }

    public static double propD(SoapObject o, String name) {
        String s = prop(o, name);
        try { return s == null ? 0 : Double.parseDouble(s); }
        catch (NumberFormatException e) { return 0; }
    }
}
