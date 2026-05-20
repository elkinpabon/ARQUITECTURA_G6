package ec.edu.monster.controlador;

import ec.edu.monster.servicio.Sesion;
import ec.edu.monster.servicio.WsFactory;
import ec.edu.monster.ws.Factura;
import ec.edu.monster.ws.Localidad;
import ec.edu.monster.ws.Partido;
import ec.edu.monster.ws.Resultado;
import ec.edu.monster.ws.ResumenLocalidad;
import ec.edu.monster.ws.SesionResultado;
import ec.edu.monster.ws.WSFederacion;
import java.util.Collections;
import java.util.List;

/**
 * Controlador MVC del cliente escritorio. Orquesta las 6 operaciones del
 * servicio SOAP y guarda el estado de la sesion. La capa Vista llama a este
 * controlador en vez de hablar directo con el WS.
 */
public class TicketController {

    private final Sesion sesion = new Sesion();
    private WSFederacion ws;

    public Sesion getSesion() { return sesion; }

    /** Obtiene el port SOAP (lazy + cacheado). */
    private WSFederacion ws() {
        if (ws == null) ws = WsFactory.federacion();
        return ws;
    }

    /* ---------- Autenticacion ---------- */

    public boolean login(String usuario, String clave) {
        SesionResultado r = ws().iniciarSesion(usuario, clave);
        if (!r.isExito()) return false;
        sesion.setUsuario(r.getUsuario());
        return true;
    }

    public void logout() {
        sesion.setUsuario(null);
    }

    /* ---------- Catalogo ---------- */

    /** Lista partidos cuya FECHA >= NOW(). */
    public List<Partido> partidosDisponibles() {
        return ws().listarPartidosDisponibles();
    }

    /** Lista localidades de un partido con DISPONIBILIDAD > 0. */
    public List<Localidad> localidadesDe(int codigoPartido) {
        return ws().listarLocalidadesPorPartido(codigoPartido);
    }

    /* ---------- Compra ---------- */

    /** Compra cantidad boletos. Devuelve el Resultado con la Factura si exito. */
    public Resultado comprar(int codigoPartido, String codigoLocalidad, int cantidad) {
        if (!sesion.activa()) {
            Resultado r = new Resultado();
            r.setExito(false);
            r.setMensaje("Sesion no iniciada.");
            return r;
        }
        return ws().registrarVenta(sesion.getIdUsuario(),
                codigoPartido, codigoLocalidad, cantidad);
    }

    /** Historial de facturas del usuario logueado. */
    public List<Factura> misFacturas() {
        if (!sesion.activa()) return Collections.emptyList();
        return ws().misFacturas(sesion.getIdUsuario());
    }

    /* ---------- Reporte (solo ADMIN) ---------- */

    /** Reporte "Resumen de Ventas de un Partido". */
    public List<ResumenLocalidad> resumenVentas(int codigoPartido) {
        if (!sesion.isAdmin()) return Collections.emptyList();
        return ws().resumenVentasPartido(codigoPartido);
    }

    /* ---------- Administracion (solo ADMIN) ---------- */

    /** Lista TODAS las localidades de un partido (incluye con DISPONIBILIDAD=0). */
    public List<Localidad> localidadesAdmin(int codigoPartido) {
        return ws().listarTodasLocalidadesPorPartido(codigoPartido);
    }

    // ---- Partidos ----
    public Resultado registrarPartido(String local, String visita, String fecha, String lugar) {
        return ws().registrarPartido(sesion.getIdUsuario(), local, visita, fecha, lugar);
    }

    public Resultado actualizarPartido(int codigo, String local, String visita,
                                       String fecha, String lugar) {
        return ws().actualizarPartido(sesion.getIdUsuario(), codigo, local, visita, fecha, lugar);
    }

    public Resultado eliminarPartido(int codigo) {
        return ws().eliminarPartido(sesion.getIdUsuario(), codigo);
    }

    // ---- Localidades ----
    public Resultado registrarLocalidad(int codigoPartido, String codigoLocalidad,
                                        int disponibilidad, java.math.BigDecimal precio) {
        return ws().registrarLocalidad(sesion.getIdUsuario(), codigoPartido,
                codigoLocalidad, disponibilidad, precio);
    }

    public Resultado actualizarLocalidad(int idLocalidad, int disponibilidad,
                                         java.math.BigDecimal precio) {
        return ws().actualizarLocalidad(sesion.getIdUsuario(), idLocalidad, disponibilidad, precio);
    }

    public Resultado eliminarLocalidad(int idLocalidad) {
        return ws().eliminarLocalidad(sesion.getIdUsuario(), idLocalidad);
    }
}
