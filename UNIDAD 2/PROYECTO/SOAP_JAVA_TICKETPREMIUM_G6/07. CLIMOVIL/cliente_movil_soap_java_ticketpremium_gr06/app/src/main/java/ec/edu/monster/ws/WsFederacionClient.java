package ec.edu.monster.ws;

import android.content.Context;
import ec.edu.monster.config.ServidorConfig;
import ec.edu.monster.modelo.Asiento;
import ec.edu.monster.modelo.Cuenta;
import ec.edu.monster.modelo.Factura;
import ec.edu.monster.modelo.ItemCarrito;
import ec.edu.monster.modelo.Localidad;
import ec.edu.monster.modelo.Movimiento;
import ec.edu.monster.modelo.Partido;
import ec.edu.monster.modelo.Resultado;
import ec.edu.monster.modelo.Seccion;
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
 * Cliente KSOAP2 que consume las operaciones del WSFederacion (contrato
 * FIFA 2026). Las llamadas son SINCRONAS; el caller debe invocar desde un
 * hilo background (lo hace TicketController).
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
        return invocarRequest(request);
    }

    /** Envia un request ya armado (permite propiedades repetidas, ej "items"). */
    private Object invocarRequest(SoapObject request) throws Exception {
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
    // CATALOGO
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

    public List<Seccion> listarSeccionesPorLocalidad(int idLocalidad) throws Exception {
        Map<String, Object> p = new LinkedHashMap<>();
        p.put("idLocalidad", idLocalidad);
        Object body = invocar("listarSeccionesPorLocalidad", p);
        List<Seccion> out = new ArrayList<>();
        for (SoapObject so : rets(body)) out.add(SoapHelper.toSeccion(so));
        return out;
    }

    // ============================================================================
    // ASIENTOS (tiempo real)
    // ============================================================================
    public List<Asiento> asientosNoLibres(int idSeccion) throws Exception {
        Map<String, Object> p = new LinkedHashMap<>();
        p.put("idSeccion", idSeccion);
        Object body = invocar("asientosNoLibres", p);
        List<Asiento> out = new ArrayList<>();
        for (SoapObject so : rets(body)) out.add(SoapHelper.toAsiento(so));
        return out;
    }

    public Resultado reservarAsiento(int idUsuario, int idSeccion, String fila, String asiento) throws Exception {
        Map<String, Object> p = new LinkedHashMap<>();
        p.put("idUsuario", idUsuario);
        p.put("idSeccion", idSeccion);
        p.put("fila", fila);
        p.put("asiento", asiento);
        return SoapHelper.toResultado(ret(invocar("reservarAsiento", p)));
    }

    public Resultado liberarAsiento(int idUsuario, int idSeccion, String fila, String asiento) throws Exception {
        Map<String, Object> p = new LinkedHashMap<>();
        p.put("idUsuario", idUsuario);
        p.put("idSeccion", idSeccion);
        p.put("fila", fila);
        p.put("asiento", asiento);
        return SoapHelper.toResultado(ret(invocar("liberarAsiento", p)));
    }

    public Resultado liberarMisReservas(int idUsuario) throws Exception {
        Map<String, Object> p = new LinkedHashMap<>();
        p.put("idUsuario", idUsuario);
        return SoapHelper.toResultado(ret(invocar("liberarMisReservas", p)));
    }

    // ============================================================================
    // VENTA (carrito: CONTADO / CREDITO)
    // ============================================================================
    public Resultado registrarCompra(int idUsuario, List<ItemCarrito> items, String tipoPago,
                                     int numCuotas, BigDecimal tasaInteres, BigDecimal entrada) throws Exception {
        SoapObject request = new SoapObject(ServidorConfig.namespace(), "registrarCompra");
        request.addProperty("idUsuario", idUsuario);
        if (items != null) {
            for (ItemCarrito it : items) {
                SoapObject item = new SoapObject(ServidorConfig.namespace(), "items");
                item.addProperty("codigoPartido", it.getCodigoPartido());
                item.addProperty("idSeccion", it.getIdSeccion());
                item.addProperty("cantidad", it.getCantidad());
                item.addProperty("fila", it.getFila() == null ? "" : it.getFila());
                item.addProperty("asientos", it.getAsientos() == null ? "" : it.getAsientos());
                // parametro repetido: un elemento <items> por linea del carrito
                request.addProperty("items", item);
            }
        }
        request.addProperty("tipoPago", tipoPago);
        request.addProperty("numCuotas", numCuotas);
        request.addProperty("tasaInteres", tasaInteres == null ? "0" : tasaInteres.toPlainString());
        request.addProperty("entrada", entrada == null ? "0" : entrada.toPlainString());

        return SoapHelper.toResultado(ret(invocarRequest(request)));
    }

    // ============================================================================
    // HISTORIAL / COMPROBANTES
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

    /** Comprobante completo: cabecera + detalles (boletos) + amortizacion. */
    public Factura verComprobante(int idSolicitante, int idFactura) throws Exception {
        Map<String, Object> p = new LinkedHashMap<>();
        p.put("idSolicitante", idSolicitante);
        p.put("idFactura", idFactura);
        return SoapHelper.toFactura(ret(invocar("verComprobante", p)));
    }

    // ============================================================================
    // CUENTA / MOVIMIENTOS
    // ============================================================================
    public Cuenta miCuenta(int idUsuario) throws Exception {
        Map<String, Object> p = new LinkedHashMap<>();
        p.put("idUsuario", idUsuario);
        return SoapHelper.toCuenta(ret(invocar("miCuenta", p)));
    }

    public List<Movimiento> misMovimientos(int idUsuario) throws Exception {
        Map<String, Object> p = new LinkedHashMap<>();
        p.put("idUsuario", idUsuario);
        Object body = invocar("misMovimientos", p);
        List<Movimiento> out = new ArrayList<>();
        for (SoapObject so : rets(body)) out.add(SoapHelper.toMovimiento(so));
        return out;
    }
}
