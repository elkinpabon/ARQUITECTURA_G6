package ec.edu.monster.controlador;

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

/** Controlador MVC del cliente consola (TicketPremium FIFA 2026). */
public class TicketController {

    private final Sesion sesion = new Sesion();
    private WSFederacion ws;

    public Sesion getSesion() { return sesion; }

    private WSFederacion ws() {
        if (ws == null) ws = WsFactory.federacion();
        return ws;
    }

    // -------------------------------------------------------------- sesion
    public boolean login(String usuario, String clave) {
        SesionResultado r = ws().iniciarSesion(usuario, clave);
        if (!r.isExito()) return false;
        sesion.setUsuario(r.getUsuario());
        return true;
    }

    public void logout() { sesion.setUsuario(null); }

    // -------------------------------------------------------------- catalogo
    public List<Partido> partidosDisponibles() { return ws().listarPartidosDisponibles(); }

    public List<Partido> todosPartidos() {
        if (!sesion.isAdmin()) return Collections.emptyList();
        return ws().listarTodosPartidos();
    }

    public List<Localidad> categoriasDe(int codigoPartido) {
        return ws().listarLocalidadesPorPartido(codigoPartido);
    }

    public List<Seccion> seccionesDe(int idLocalidad) {
        return ws().listarSeccionesPorLocalidad(idLocalidad);
    }

    public List<Estadio> estadios()       { return ws().listarEstadios(); }
    public List<Seleccion> selecciones()  { return ws().listarSelecciones(); }

    public List<Localidad> localidadesAdmin(int codigoPartido) {
        if (!sesion.isAdmin()) return Collections.emptyList();
        return ws().listarTodasLocalidadesPorPartido(codigoPartido);
    }

    // -------------------------------------------------------------- asientos
    public List<Asiento> asientosNoLibres(int idSeccion) {
        return ws().asientosNoLibres(idSeccion);
    }

    public Resultado reservarAsiento(int idSeccion, String fila, String asiento) {
        if (!sesion.activa()) return error("Sesion no iniciada.");
        return ws().reservarAsiento(sesion.getIdUsuario(), idSeccion, fila, asiento);
    }

    public Resultado liberarAsiento(int idSeccion, String fila, String asiento) {
        if (!sesion.activa()) return error("Sesion no iniciada.");
        return ws().liberarAsiento(sesion.getIdUsuario(), idSeccion, fila, asiento);
    }

    public Resultado liberarMisReservas() {
        if (!sesion.activa()) return error("Sesion no iniciada.");
        return ws().liberarMisReservas(sesion.getIdUsuario());
    }

    // -------------------------------------------------------------- compra (carrito)
    public Resultado comprar(List<ItemCarrito> items, String tipoPago,
                             int numCuotas, BigDecimal tasaInteres, BigDecimal entrada) {
        if (!sesion.activa()) return error("Sesion no iniciada.");
        return ws().registrarCompra(sesion.getIdUsuario(), items, tipoPago, numCuotas, tasaInteres, entrada);
    }

    public Factura comprobante(int idFactura) {
        if (!sesion.activa()) return null;
        return ws().verComprobante(sesion.getIdUsuario(), idFactura);
    }

    // -------------------------------------------------------------- cuenta bancaria
    public Cuenta miCuenta() {
        if (!sesion.activa()) return null;
        return ws().miCuenta(sesion.getIdUsuario());
    }

    public List<Movimiento> misMovimientos() {
        if (!sesion.activa()) return Collections.emptyList();
        return ws().misMovimientos(sesion.getIdUsuario());
    }

    // -------------------------------------------------------------- historial / reporte
    public List<Factura> misFacturas() {
        if (!sesion.activa()) return Collections.emptyList();
        return ws().misFacturas(sesion.getIdUsuario());
    }

    public List<Factura> todasFacturas() {
        if (!sesion.isAdmin()) return Collections.emptyList();
        return ws().listarTodasFacturas(sesion.getIdUsuario());
    }

    public List<ResumenLocalidad> resumenVentas(int codigoPartido) {
        if (!sesion.isAdmin()) return Collections.emptyList();
        return ws().resumenVentasPartido(codigoPartido);
    }

    // -------------------------------------------------------------- admin: partidos
    public Resultado registrarPartido(int idLocal, int idVisita, int idEstadio, String fecha, String grupo) {
        if (!sesion.isAdmin()) return error("Solo ADMIN puede registrar partidos.");
        return ws().registrarPartido(sesion.getIdUsuario(), idLocal, idVisita, idEstadio, fecha, grupo);
    }

    public Resultado actualizarPartido(int codigo, int idLocal, int idVisita, int idEstadio, String fecha, String grupo) {
        if (!sesion.isAdmin()) return error("Solo ADMIN puede actualizar partidos.");
        return ws().actualizarPartido(sesion.getIdUsuario(), codigo, idLocal, idVisita, idEstadio, fecha, grupo);
    }

    public Resultado eliminarPartido(int codigo) {
        if (!sesion.isAdmin()) return error("Solo ADMIN puede eliminar partidos.");
        return ws().eliminarPartido(sesion.getIdUsuario(), codigo);
    }

    // -------------------------------------------------------------- admin: localidades
    public Resultado registrarLocalidad(int codigoPartido, String categoria, int disponibilidad, BigDecimal precio) {
        if (!sesion.isAdmin()) return error("Solo ADMIN puede registrar localidades.");
        return ws().registrarLocalidad(sesion.getIdUsuario(), codigoPartido, categoria, disponibilidad, precio);
    }

    public Resultado actualizarLocalidad(int idLocalidad, int disponibilidad, BigDecimal precio) {
        if (!sesion.isAdmin()) return error("Solo ADMIN puede actualizar localidades.");
        return ws().actualizarLocalidad(sesion.getIdUsuario(), idLocalidad, disponibilidad, precio);
    }

    public Resultado eliminarLocalidad(int idLocalidad) {
        if (!sesion.isAdmin()) return error("Solo ADMIN puede eliminar localidades.");
        return ws().eliminarLocalidad(sesion.getIdUsuario(), idLocalidad);
    }

    private Resultado error(String mensaje) {
        Resultado r = new Resultado();
        r.setExito(false);
        r.setMensaje(mensaje);
        return r;
    }
}
