package ec.edu.monster.controlador;

import ec.edu.monster.modelo.Carrito;
import ec.edu.monster.servicio.Sesion;
import ec.edu.monster.servicio.WsFactory;
import ec.edu.monster.ws.Asiento;
import ec.edu.monster.ws.Cuenta;
import ec.edu.monster.ws.Estadio;
import ec.edu.monster.ws.Factura;
import ec.edu.monster.ws.ItemCarrito;
import ec.edu.monster.ws.Localidad;
import ec.edu.monster.ws.Movimiento;
import ec.edu.monster.ws.Partido;
import ec.edu.monster.ws.Resultado;
import ec.edu.monster.ws.ResumenLocalidad;
import ec.edu.monster.ws.Seccion;
import ec.edu.monster.ws.Seleccion;
import ec.edu.monster.ws.SesionResultado;
import ec.edu.monster.ws.WSFederacion;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

/**
 * Controlador MVC del cliente escritorio TicketPremium FIFA 2026.
 * Orquesta TODAS las operaciones del servicio SOAP WSFederacion y guarda el
 * estado de la sesion y del carrito local. La capa Vista llama a este
 * controlador en vez de hablar directo con el WS.
 */
public class TicketController {

    private final Sesion sesion = new Sesion();
    private final Carrito carrito = new Carrito();
    private WSFederacion ws;

    public Sesion getSesion()   { return sesion; }
    public Carrito getCarrito() { return carrito; }

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
        carrito.vaciar();
        return true;
    }

    /** Cierra sesion liberando en el servidor las reservas pendientes. */
    public void logout() {
        liberarMisReservasSilencioso();
        carrito.vaciar();
        sesion.setUsuario(null);
    }

    /** Libera las reservas del usuario sin propagar errores (cierre de app). */
    public void liberarMisReservasSilencioso() {
        try {
            if (sesion.activa()) ws().liberarMisReservas(sesion.getIdUsuario());
        } catch (Exception ignore) { }
    }

    /* ---------- Catalogo ---------- */

    /** Partidos con FECHA >= NOW() (los 72 del Mundial 2026 si aplica). */
    public List<Partido> partidosDisponibles() {
        return ws().listarPartidosDisponibles();
    }

    /** TODOS los partidos (para administracion). */
    public List<Partido> todosPartidos() {
        return ws().listarTodosPartidos();
    }

    /** Localidades (CAT1-4) de un partido con disponibilidad > 0. */
    public List<Localidad> localidadesDe(int codigoPartido) {
        return ws().listarLocalidadesPorPartido(codigoPartido);
    }

    /** TODAS las localidades de un partido (admin, incluye agotadas). */
    public List<Localidad> localidadesAdmin(int codigoPartido) {
        return ws().listarTodasLocalidadesPorPartido(codigoPartido);
    }

    /** Secciones (bloques de asientos) de una localidad. */
    public List<Seccion> seccionesDe(int idLocalidad) {
        return ws().listarSeccionesPorLocalidad(idLocalidad);
    }

    /* ---------- Mapa de asientos (tiempo real por polling) ---------- */

    /** Asientos RESERVADOS u OCUPADOS de una seccion. */
    public List<Asiento> asientosNoLibres(int idSeccion) {
        return ws().asientosNoLibres(idSeccion);
    }

    public Resultado reservarAsiento(int idSeccion, String fila, String asiento) {
        return ws().reservarAsiento(sesion.getIdUsuario(), idSeccion, fila, asiento);
    }

    public Resultado liberarAsiento(int idSeccion, String fila, String asiento) {
        return ws().liberarAsiento(sesion.getIdUsuario(), idSeccion, fila, asiento);
    }

    public Resultado liberarMisReservas() {
        return ws().liberarMisReservas(sesion.getIdUsuario());
    }

    /* ---------- Compra (carrito + contado/credito) ---------- */

    /**
     * Registra la compra del carrito. tasaInteres en DECIMAL mensual
     * (ej. 0.02 para 2%); entrada solo aplica a CREDITO.
     */
    public Resultado comprar(List<ItemCarrito> items, String tipoPago,
                             int numCuotas, BigDecimal tasaInteres, BigDecimal entrada) {
        if (!sesion.activa()) {
            Resultado r = new Resultado();
            r.setExito(false);
            r.setMensaje("Sesion no iniciada.");
            return r;
        }
        return ws().registrarCompra(sesion.getIdUsuario(), items, tipoPago,
                numCuotas, tasaInteres, entrada);
    }

    /* ---------- Facturas / comprobantes ---------- */

    public List<Factura> misFacturas() {
        if (!sesion.activa()) return Collections.emptyList();
        return ws().misFacturas(sesion.getIdUsuario());
    }

    public List<Factura> todasFacturas() {
        if (!sesion.isAdmin()) return Collections.emptyList();
        return ws().listarTodasFacturas(sesion.getIdUsuario());
    }

    /** Factura completa: detalles (boletos) + tabla de amortizacion si CREDITO. */
    public Factura comprobante(int idFactura) {
        return ws().verComprobante(sesion.getIdUsuario(), idFactura);
    }

    /* ---------- Cuenta bancaria simulada ---------- */

    public Cuenta miCuenta() {
        return ws().miCuenta(sesion.getIdUsuario());
    }

    public List<Movimiento> misMovimientos() {
        return ws().misMovimientos(sesion.getIdUsuario());
    }

    /* ---------- Reporte (solo ADMIN) ---------- */

    public List<ResumenLocalidad> resumenVentas(int codigoPartido) {
        if (!sesion.isAdmin()) return Collections.emptyList();
        return ws().resumenVentasPartido(codigoPartido);
    }

    /* ---------- Catalogos para administracion ---------- */

    public List<Estadio> listarEstadios()        { return ws().listarEstadios(); }
    public List<Seleccion> listarSelecciones()   { return ws().listarSelecciones(); }

    /* ---------- Administracion (solo ADMIN) ---------- */

    public Resultado registrarPartido(int idLocal, int idVisita, int idEstadio,
                                      String fecha, String grupo) {
        return ws().registrarPartido(sesion.getIdUsuario(), idLocal, idVisita,
                idEstadio, fecha, grupo);
    }

    public Resultado actualizarPartido(int codigo, int idLocal, int idVisita,
                                       int idEstadio, String fecha, String grupo) {
        return ws().actualizarPartido(sesion.getIdUsuario(), codigo, idLocal,
                idVisita, idEstadio, fecha, grupo);
    }

    public Resultado eliminarPartido(int codigo) {
        return ws().eliminarPartido(sesion.getIdUsuario(), codigo);
    }

    public Resultado registrarLocalidad(int codigoPartido, String categoria,
                                        int disponibilidad, BigDecimal precio) {
        return ws().registrarLocalidad(sesion.getIdUsuario(), codigoPartido,
                categoria, disponibilidad, precio);
    }

    public Resultado actualizarLocalidad(int idLocalidad, int disponibilidad,
                                         BigDecimal precio) {
        return ws().actualizarLocalidad(sesion.getIdUsuario(), idLocalidad,
                disponibilidad, precio);
    }

    public Resultado eliminarLocalidad(int idLocalidad) {
        return ws().eliminarLocalidad(sesion.getIdUsuario(), idLocalidad);
    }
}
