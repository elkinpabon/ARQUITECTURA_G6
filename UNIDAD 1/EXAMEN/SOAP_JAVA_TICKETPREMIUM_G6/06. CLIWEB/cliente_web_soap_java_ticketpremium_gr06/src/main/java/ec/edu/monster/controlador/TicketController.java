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
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

/** Controlador MVC del cliente web. */
public class TicketController {

    private final Sesion sesion = new Sesion();
    private WSFederacion ws;

    public Sesion getSesion() { return sesion; }

    private WSFederacion ws() {
        if (ws == null) ws = WsFactory.federacion();
        return ws;
    }

    public boolean login(String usuario, String clave) {
        SesionResultado r = ws().iniciarSesion(usuario, clave);
        if (!r.isExito()) return false;
        sesion.setUsuario(r.getUsuario());
        return true;
    }

    public void logout() {
        sesion.setUsuario(null);
    }

    public List<Partido> partidosDisponibles() {
        return ws().listarPartidosDisponibles();
    }

    public List<Localidad> localidadesDe(int codigoPartido) {
        return ws().listarLocalidadesPorPartido(codigoPartido);
    }

    public List<Localidad> localidadesAdmin(int codigoPartido) {
        if (!sesion.isAdmin()) return Collections.emptyList();
        return ws().listarTodasLocalidadesPorPartido(codigoPartido);
    }

    public Resultado comprar(int codigoPartido, String codigoLocalidad, int cantidad) {
        if (!sesion.activa()) {
            Resultado r = new Resultado();
            r.setExito(false);
            r.setMensaje("Sesion no iniciada.");
            return r;
        }
        return ws().registrarVenta(sesion.getIdUsuario(), codigoPartido, codigoLocalidad, cantidad);
    }

    public List<Factura> misFacturas() {
        if (!sesion.activa()) return Collections.emptyList();
        // Admin ve TODAS las facturas del sistema; cliente solo las suyas
        if (sesion.isAdmin()) {
            return ws().listarTodasFacturas(sesion.getIdUsuario());
        }
        return ws().misFacturas(sesion.getIdUsuario());
    }

    public List<ResumenLocalidad> resumenVentas(int codigoPartido) {
        if (!sesion.isAdmin()) return Collections.emptyList();
        return ws().resumenVentasPartido(codigoPartido);
    }

    public Resultado registrarPartido(String equipoLocal, String equipoVisita, String fecha, String lugar) {
        if (!sesion.isAdmin()) return error("Solo ADMIN puede registrar partidos.");
        return ws().registrarPartido(sesion.getIdUsuario(), equipoLocal, equipoVisita, fecha, lugar);
    }

    public Resultado actualizarPartido(int codigo, String equipoLocal, String equipoVisita, String fecha, String lugar) {
        if (!sesion.isAdmin()) return error("Solo ADMIN puede actualizar partidos.");
        return ws().actualizarPartido(sesion.getIdUsuario(), codigo, equipoLocal, equipoVisita, fecha, lugar);
    }

    public Resultado eliminarPartido(int codigo) {
        if (!sesion.isAdmin()) return error("Solo ADMIN puede eliminar partidos.");
        return ws().eliminarPartido(sesion.getIdUsuario(), codigo);
    }

    public Resultado registrarLocalidad(int codigoPartido, String codigoLocalidad, int disponibilidad, BigDecimal precio) {
        if (!sesion.isAdmin()) return error("Solo ADMIN puede registrar localidades.");
        return ws().registrarLocalidad(sesion.getIdUsuario(), codigoPartido, codigoLocalidad, disponibilidad, precio);
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
