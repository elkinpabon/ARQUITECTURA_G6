package ec.edu.monster.ws;

import android.content.Context;
import ec.edu.monster.config.ServidorConfig;
import ec.edu.monster.modelo.Factura;
import ec.edu.monster.modelo.Localidad;
import ec.edu.monster.modelo.Partido;
import ec.edu.monster.modelo.Resultado;
import ec.edu.monster.modelo.ResumenLocalidad;
import ec.edu.monster.modelo.SesionResultado;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

/**
 * Cliente KSOAP2 que consume todas las operaciones del WSFederacion.
 * Las llamadas son SINCRONAS; el caller debe invocar desde un hilo background.
 */
public class WsFederacionClient {

    private static final int TIMEOUT_MS = 15000;

    private final Context ctx;

    public WsFederacionClient(Context ctx) {
        this.ctx = ctx.getApplicationContext();
    }

    // ============================================================================
    // INVOCACION GENERICA
    // ============================================================================
    private Object invocar(String operacion, Map<String, Object> parametros) throws Exception {
        SoapObject request = new SoapObject(ServidorConfig.namespace(), operacion);
        if (parametros != null) {
            for (Map.Entry<String, Object> e : parametros.entrySet()) {
                request.addProperty(e.getKey(), e.getValue() == null ? "" : e.getValue());
            }
        }
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.setOutputSoapObject(request);
        envelope.dotNet = false;
        envelope.implicitTypes = true;

        HttpTransportSE transport = new HttpTransportSE(ServidorConfig.endpoint(ctx), TIMEOUT_MS);
        transport.debug = false;
        transport.call("", envelope);
        return envelope.bodyIn;
    }

    /** Devuelve el primer hijo "return" del Body (objeto unico). */
    private SoapObject ret(Object body) {
        if (!(body instanceof SoapObject)) return null;
        SoapObject resp = (SoapObject) body;
        try {
            Object r = resp.getProperty("return");
            return r instanceof SoapObject ? (SoapObject) r : null;
        } catch (Exception e) {
            return null;
        }
    }

    /** Devuelve TODOS los hijos "return" del Body (caso List<X>). */
    private List<SoapObject> rets(Object body) {
        List<SoapObject> out = new ArrayList<>();
        if (!(body instanceof SoapObject)) return out;
        SoapObject resp = (SoapObject) body;
        for (int i = 0; i < resp.getPropertyCount(); i++) {
            Object o = resp.getProperty(i);
            if (o instanceof SoapObject) out.add((SoapObject) o);
        }
        return out;
    }

    // ============================================================================
    // AUTENTICACION
    // ============================================================================
    public SesionResultado iniciarSesion(String usuario, String contrasena) throws Exception {
        Map<String, Object> p = new LinkedHashMap<>();
        p.put("usuario", usuario);
        p.put("contrasena", contrasena);
        Object body = invocar("iniciarSesion", p);
        SoapObject r = ret(body);
        SesionResultado res = SoapHelper.toSesionResultado(r);
        if (res.getMensaje() == null || res.getMensaje().isEmpty()) {
            res.setMensaje(res.isExito() ? "OK" : "Credenciales invalidas");
        }
        return res;
    }

    // ============================================================================
    // RUBRICA OFICIAL
    // ============================================================================
    public List<Partido> listarPartidosDisponibles() throws Exception {
        Object body = invocar("listarPartidosDisponibles", null);
        List<Partido> out = new ArrayList<>();
        for (SoapObject so : rets(body)) out.add(SoapHelper.toPartido(so));
        return out;
    }

    public List<Localidad> listarLocalidadesPorPartido(int codigoPartido) throws Exception {
        Map<String, Object> p = new LinkedHashMap<>();
        p.put("codigoPartido", codigoPartido);
        Object body = invocar("listarLocalidadesPorPartido", p);
        List<Localidad> out = new ArrayList<>();
        for (SoapObject so : rets(body)) out.add(SoapHelper.toLocalidad(so));
        return out;
    }

    public Resultado registrarVenta(int idUsuario, int codigoPartido, String codigoLocalidad, int cantidad) throws Exception {
        Map<String, Object> p = new LinkedHashMap<>();
        p.put("idUsuario", idUsuario);
        p.put("codigoPartido", codigoPartido);
        p.put("codigoLocalidad", codigoLocalidad);
        p.put("cantidad", cantidad);
        Object body = invocar("registrarVenta", p);
        return SoapHelper.toResultado(ret(body));
    }

    public List<ResumenLocalidad> resumenVentasPartido(int codigoPartido) throws Exception {
        Map<String, Object> p = new LinkedHashMap<>();
        p.put("codigoPartido", codigoPartido);
        Object body = invocar("resumenVentasPartido", p);
        List<ResumenLocalidad> out = new ArrayList<>();
        for (SoapObject so : rets(body)) out.add(SoapHelper.toResumen(so));
        return out;
    }

    // ============================================================================
    // HISTORIAL
    // ============================================================================
    public List<Factura> misFacturas(int idUsuario) throws Exception {
        Map<String, Object> p = new LinkedHashMap<>();
        p.put("idUsuario", idUsuario);
        Object body = invocar("misFacturas", p);
        List<Factura> out = new ArrayList<>();
        for (SoapObject so : rets(body)) out.add(SoapHelper.toFactura(so));
        return out;
    }

    /** TODAS las facturas del sistema (solo si idAdmin es ADMIN en el servidor). */
    public List<Factura> listarTodasFacturas(int idAdmin) throws Exception {
        Map<String, Object> p = new LinkedHashMap<>();
        p.put("idAdmin", idAdmin);
        Object body = invocar("listarTodasFacturas", p);
        List<Factura> out = new ArrayList<>();
        for (SoapObject so : rets(body)) out.add(SoapHelper.toFactura(so));
        return out;
    }

    // ============================================================================
    // ADMIN
    // ============================================================================
    public List<Localidad> listarTodasLocalidadesPorPartido(int codigoPartido) throws Exception {
        Map<String, Object> p = new LinkedHashMap<>();
        p.put("codigoPartido", codigoPartido);
        Object body = invocar("listarTodasLocalidadesPorPartido", p);
        List<Localidad> out = new ArrayList<>();
        for (SoapObject so : rets(body)) out.add(SoapHelper.toLocalidad(so));
        return out;
    }

    public Resultado registrarPartido(int idAdmin, String equipoLocal, String equipoVisita, String fecha, String lugar) throws Exception {
        Map<String, Object> p = new LinkedHashMap<>();
        p.put("idAdmin", idAdmin);
        p.put("equipoLocal", equipoLocal);
        p.put("equipoVisita", equipoVisita);
        p.put("fecha", fecha);
        p.put("lugar", lugar);
        return SoapHelper.toResultado(ret(invocar("registrarPartido", p)));
    }

    public Resultado actualizarPartido(int idAdmin, int codigo, String equipoLocal, String equipoVisita, String fecha, String lugar) throws Exception {
        Map<String, Object> p = new LinkedHashMap<>();
        p.put("idAdmin", idAdmin);
        p.put("codigo", codigo);
        p.put("equipoLocal", equipoLocal);
        p.put("equipoVisita", equipoVisita);
        p.put("fecha", fecha);
        p.put("lugar", lugar);
        return SoapHelper.toResultado(ret(invocar("actualizarPartido", p)));
    }

    public Resultado eliminarPartido(int idAdmin, int codigo) throws Exception {
        Map<String, Object> p = new LinkedHashMap<>();
        p.put("idAdmin", idAdmin);
        p.put("codigo", codigo);
        return SoapHelper.toResultado(ret(invocar("eliminarPartido", p)));
    }

    public Resultado registrarLocalidad(int idAdmin, int codigoPartido, String codigoLocalidad, int disponibilidad, BigDecimal precio) throws Exception {
        Map<String, Object> p = new LinkedHashMap<>();
        p.put("idAdmin", idAdmin);
        p.put("codigoPartido", codigoPartido);
        p.put("codigoLocalidad", codigoLocalidad);
        p.put("disponibilidad", disponibilidad);
        p.put("precio", precio.toPlainString());
        return SoapHelper.toResultado(ret(invocar("registrarLocalidad", p)));
    }

    public Resultado actualizarLocalidad(int idAdmin, int idLocalidad, int disponibilidad, BigDecimal precio) throws Exception {
        Map<String, Object> p = new LinkedHashMap<>();
        p.put("idAdmin", idAdmin);
        p.put("idLocalidad", idLocalidad);
        p.put("disponibilidad", disponibilidad);
        p.put("precio", precio.toPlainString());
        return SoapHelper.toResultado(ret(invocar("actualizarLocalidad", p)));
    }

    public Resultado eliminarLocalidad(int idAdmin, int idLocalidad) throws Exception {
        Map<String, Object> p = new LinkedHashMap<>();
        p.put("idAdmin", idAdmin);
        p.put("idLocalidad", idLocalidad);
        return SoapHelper.toResultado(ret(invocar("eliminarLocalidad", p)));
    }
}
