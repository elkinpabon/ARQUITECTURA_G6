package ec.edu.monster.controlador;

import ec.edu.monster.modelo.Factura;
import ec.edu.monster.modelo.Localidad;
import ec.edu.monster.modelo.Partido;
import ec.edu.monster.modelo.Resultado;
import ec.edu.monster.modelo.ResumenLocalidad;
import ec.edu.monster.modelo.SesionResultado;
import ec.edu.monster.servicio.LocalidadService;
import ec.edu.monster.servicio.PartidoService;
import ec.edu.monster.servicio.ReporteService;
import ec.edu.monster.servicio.SesionService;
import ec.edu.monster.servicio.VentaService;
import jakarta.jws.WebMethod;
import jakarta.jws.WebParam;
import jakarta.jws.WebService;
import java.math.BigDecimal;
import java.util.List;

/**
 * Fachada SOAP del servidor "Federacion de Futbol" - TicketPremium GR06.
 *
 * Operaciones expuestas:
 *   == Rubrica (lado servidor) ==
 *   1) listarPartidosDisponibles    [1.0 pt]  partidos con FECHA >= NOW()
 *   2) listarLocalidadesPorPartido  [1.0 pt]  localidades de un partido con DISP>0
 *   3) registrarVenta               [0.5 pt]  inserta factura+detalle, descuenta stock
 *   4) resumenVentasPartido         [1.0 pt]  reporte agrupado por localidad
 *
 *   == Mejoras (autenticacion + historial) ==
 *   5) iniciarSesion                          login con usuario/contrasena
 *   6) misFacturas                            historial de un usuario
 *
 * WSDL:  http://localhost:8080/servidor_soap_java_federacion_gr06/WSFederacion?wsdl
 */
@WebService(serviceName = "WSFederacion", targetNamespace = "http://ws.monster.edu.ec/")
public class WSFederacion {

    private final PartidoService   partidoService   = new PartidoService();
    private final LocalidadService localidadService = new LocalidadService();
    private final VentaService     ventaService     = new VentaService();
    private final ReporteService   reporteService   = new ReporteService();
    private final SesionService    sesionService    = new SesionService();

    // ---------- Autenticacion ----------

    /** Login del cliente. Devuelve usuario+rol si las credenciales son validas. */
    @WebMethod(operationName = "iniciarSesion")
    public SesionResultado iniciarSesion(
            @WebParam(name = "usuario")    String usuario,
            @WebParam(name = "contrasena") String contrasena) {
        return sesionService.iniciarSesion(usuario, contrasena);
    }

    // ---------- Rubrica oficial ----------

    /** Rubrica 1.0 pt - Web Service que retorna los partidos de futbol disponibles. */
    @WebMethod(operationName = "listarPartidosDisponibles")
    public List<Partido> listarPartidosDisponibles() {
        return partidoService.listarDisponibles();
    }

    /** Rubrica 1.0 pt - Web Service que retorna las localidades disponibles de un partido. */
    @WebMethod(operationName = "listarLocalidadesPorPartido")
    public List<Localidad> listarLocalidadesPorPartido(
            @WebParam(name = "codigoPartido") int codigoPartido) {
        return localidadService.listarPorPartido(codigoPartido);
    }

    /** Rubrica 0.5 pt - Decrementa disponibilidad y registra factura+detalle (IVA 15%). */
    @WebMethod(operationName = "registrarVenta")
    public Resultado registrarVenta(
            @WebParam(name = "idUsuario")       int idUsuario,
            @WebParam(name = "codigoPartido")   int codigoPartido,
            @WebParam(name = "codigoLocalidad") String codigoLocalidad,
            @WebParam(name = "cantidad")        int cantidad) {
        return ventaService.registrarVenta(idUsuario, codigoPartido, codigoLocalidad, cantidad);
    }

    /** Rubrica 1.0 pt - Reporte "Resumen de Ventas de un Partido". */
    @WebMethod(operationName = "resumenVentasPartido")
    public List<ResumenLocalidad> resumenVentasPartido(
            @WebParam(name = "codigoPartido") int codigoPartido) {
        return reporteService.resumenVentasPorPartido(codigoPartido);
    }

    // ---------- Historial ----------

    /** Devuelve las facturas del usuario autenticado (historial de compras). */
    @WebMethod(operationName = "misFacturas")
    public List<Factura> misFacturas(@WebParam(name = "idUsuario") int idUsuario) {
        return ventaService.listarFacturasPorUsuario(idUsuario);
    }

    // =========================================================================
    //  Administracion (solo rol ADMIN — el servicio valida)
    // =========================================================================

    /** Lista TODAS las localidades de un partido (incluye DISPONIBILIDAD=0). Util para el panel admin. */
    @WebMethod(operationName = "listarTodasLocalidadesPorPartido")
    public List<Localidad> listarTodasLocalidadesPorPartido(
            @WebParam(name = "codigoPartido") int codigoPartido) {
        return localidadService.listarTodasPorPartido(codigoPartido);
    }

    // ---------- CRUD PARTIDOS ----------

    @WebMethod(operationName = "registrarPartido")
    public Resultado registrarPartido(
            @WebParam(name = "idAdmin")       int idAdmin,
            @WebParam(name = "equipoLocal")   String equipoLocal,
            @WebParam(name = "equipoVisita")  String equipoVisita,
            @WebParam(name = "fecha")         String fecha,
            @WebParam(name = "lugar")         String lugar) {
        return partidoService.registrar(idAdmin, equipoLocal, equipoVisita, fecha, lugar);
    }

    @WebMethod(operationName = "actualizarPartido")
    public Resultado actualizarPartido(
            @WebParam(name = "idAdmin")       int idAdmin,
            @WebParam(name = "codigo")        int codigo,
            @WebParam(name = "equipoLocal")   String equipoLocal,
            @WebParam(name = "equipoVisita")  String equipoVisita,
            @WebParam(name = "fecha")         String fecha,
            @WebParam(name = "lugar")         String lugar) {
        return partidoService.actualizar(idAdmin, codigo, equipoLocal, equipoVisita, fecha, lugar);
    }

    @WebMethod(operationName = "eliminarPartido")
    public Resultado eliminarPartido(
            @WebParam(name = "idAdmin") int idAdmin,
            @WebParam(name = "codigo")  int codigo) {
        return partidoService.eliminar(idAdmin, codigo);
    }

    // ---------- CRUD LOCALIDADES ----------

    @WebMethod(operationName = "registrarLocalidad")
    public Resultado registrarLocalidad(
            @WebParam(name = "idAdmin")          int idAdmin,
            @WebParam(name = "codigoPartido")    int codigoPartido,
            @WebParam(name = "codigoLocalidad")  String codigoLocalidad,
            @WebParam(name = "disponibilidad")   int disponibilidad,
            @WebParam(name = "precio")           BigDecimal precio) {
        return localidadService.registrar(idAdmin, codigoPartido, codigoLocalidad, disponibilidad, precio);
    }

    @WebMethod(operationName = "actualizarLocalidad")
    public Resultado actualizarLocalidad(
            @WebParam(name = "idAdmin")         int idAdmin,
            @WebParam(name = "idLocalidad")     int idLocalidad,
            @WebParam(name = "disponibilidad")  int disponibilidad,
            @WebParam(name = "precio")          BigDecimal precio) {
        return localidadService.actualizar(idAdmin, idLocalidad, disponibilidad, precio);
    }

    @WebMethod(operationName = "eliminarLocalidad")
    public Resultado eliminarLocalidad(
            @WebParam(name = "idAdmin")     int idAdmin,
            @WebParam(name = "idLocalidad") int idLocalidad) {
        return localidadService.eliminar(idAdmin, idLocalidad);
    }
}
