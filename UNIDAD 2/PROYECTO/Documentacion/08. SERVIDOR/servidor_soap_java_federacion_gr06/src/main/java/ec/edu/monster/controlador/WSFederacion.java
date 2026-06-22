package ec.edu.monster.controlador;

import ec.edu.monster.modelo.Asiento;
import ec.edu.monster.modelo.Cuenta;
import ec.edu.monster.modelo.Cuota;
import ec.edu.monster.modelo.DetalleFactura;
import ec.edu.monster.modelo.Estadio;
import ec.edu.monster.modelo.Movimiento;
import ec.edu.monster.modelo.Factura;
import ec.edu.monster.modelo.ItemCarrito;
import ec.edu.monster.modelo.Localidad;
import ec.edu.monster.modelo.Partido;
import ec.edu.monster.modelo.Resultado;
import ec.edu.monster.modelo.Seccion;
import ec.edu.monster.modelo.Seleccion;
import ec.edu.monster.modelo.ResumenLocalidad;
import ec.edu.monster.modelo.SesionResultado;
import ec.edu.monster.servicio.BancoService;
import ec.edu.monster.servicio.CatalogoService;
import ec.edu.monster.servicio.LocalidadService;
import ec.edu.monster.servicio.PartidoService;
import ec.edu.monster.servicio.ReporteService;
import ec.edu.monster.servicio.ReservaService;
import ec.edu.monster.servicio.SeccionService;
import ec.edu.monster.servicio.SesionService;
import ec.edu.monster.servicio.VentaService;
import jakarta.jws.WebMethod;
import jakarta.jws.WebParam;
import jakarta.jws.WebService;
import java.math.BigDecimal;
import java.util.List;

/**
 * Fachada SOAP "Federacion de Futbol" - TicketPremium FIFA 2026 (GR06).
 *
 *  == Catalogo / consulta ==
 *   iniciarSesion, listarPartidosDisponibles, listarLocalidadesPorPartido,
 *   listarSeccionesPorPartido/PorLocalidad, listarEstadios, listarSelecciones
 *  == Venta ==
 *   registrarVenta (simple, contado), registrarCompra (CARRITO, contado/credito + amortizacion)
 *  == Historial / reporte ==
 *   misFacturas, listarTodasFacturas (admin), verComprobante, amortizacionDe, resumenVentasPartido
 *  == Administracion (rol ADMIN: monster) ==
 *   CRUD de partidos y de localidades (categorias Cat 1-4)
 *
 * WSDL: http://localhost:8080/servidor_soap_java_federacion_gr06/WSFederacion?wsdl
 */
@WebService(serviceName = "WSFederacion", targetNamespace = "http://ws.monster.edu.ec/")
public class WSFederacion {

    private final PartidoService   partidoService   = new PartidoService();
    private final LocalidadService localidadService = new LocalidadService();
    private final SeccionService   seccionService   = new SeccionService();
    private final CatalogoService  catalogoService  = new CatalogoService();
    private final VentaService     ventaService     = new VentaService();
    private final ReporteService   reporteService   = new ReporteService();
    private final ReservaService   reservaService   = new ReservaService();
    private final BancoService     bancoService     = new BancoService();
    private final SesionService    sesionService    = new SesionService();

    // ---------------------------------------------------------------- Autenticacion

    @WebMethod(operationName = "iniciarSesion")
    public SesionResultado iniciarSesion(
            @WebParam(name = "usuario")    String usuario,
            @WebParam(name = "contrasena") String contrasena) {
        return sesionService.iniciarSesion(usuario, contrasena);
    }

    // ---------------------------------------------------------------- Catalogo

    @WebMethod(operationName = "listarPartidosDisponibles")
    public List<Partido> listarPartidosDisponibles() {
        return partidoService.listarDisponibles();
    }

    @WebMethod(operationName = "listarTodosPartidos")
    public List<Partido> listarTodosPartidos() {
        return partidoService.listarTodos();
    }

    @WebMethod(operationName = "listarLocalidadesPorPartido")
    public List<Localidad> listarLocalidadesPorPartido(
            @WebParam(name = "codigoPartido") int codigoPartido) {
        return localidadService.listarPorPartido(codigoPartido);
    }

    @WebMethod(operationName = "listarTodasLocalidadesPorPartido")
    public List<Localidad> listarTodasLocalidadesPorPartido(
            @WebParam(name = "codigoPartido") int codigoPartido) {
        return localidadService.listarTodasPorPartido(codigoPartido);
    }

    @WebMethod(operationName = "listarSeccionesPorPartido")
    public List<Seccion> listarSeccionesPorPartido(
            @WebParam(name = "codigoPartido") int codigoPartido) {
        return seccionService.listarPorPartido(codigoPartido);
    }

    @WebMethod(operationName = "listarSeccionesPorLocalidad")
    public List<Seccion> listarSeccionesPorLocalidad(
            @WebParam(name = "idLocalidad") int idLocalidad) {
        return seccionService.listarPorLocalidad(idLocalidad);
    }

    @WebMethod(operationName = "listarEstadios")
    public List<Estadio> listarEstadios() {
        return catalogoService.listarEstadios();
    }

    @WebMethod(operationName = "listarSelecciones")
    public List<Seleccion> listarSelecciones() {
        return catalogoService.listarSelecciones();
    }

    // ---------------------------------------------------------------- Asientos (tiempo real)

    /** Asientos NO libres (RESERVADO/OCUPADO) de una seccion. El resto se asume LIBRE. */
    @WebMethod(operationName = "asientosNoLibres")
    public List<Asiento> asientosNoLibres(@WebParam(name = "idSeccion") int idSeccion) {
        return reservaService.asientosNoLibres(idSeccion);
    }

    /** Reserva un asiento (RESERVADO) para el usuario al agregarlo al carrito. */
    @WebMethod(operationName = "reservarAsiento")
    public Resultado reservarAsiento(
            @WebParam(name = "idUsuario") int idUsuario,
            @WebParam(name = "idSeccion") int idSeccion,
            @WebParam(name = "fila")      String fila,
            @WebParam(name = "asiento")   String asiento) {
        return reservaService.reservarAsiento(idUsuario, idSeccion, fila, asiento);
    }

    /** Libera un asiento RESERVADO del propio usuario (quitar del carrito). */
    @WebMethod(operationName = "liberarAsiento")
    public Resultado liberarAsiento(
            @WebParam(name = "idUsuario") int idUsuario,
            @WebParam(name = "idSeccion") int idSeccion,
            @WebParam(name = "fila")      String fila,
            @WebParam(name = "asiento")   String asiento) {
        return reservaService.liberarAsiento(idUsuario, idSeccion, fila, asiento);
    }

    /** Libera TODAS las reservas (no pagadas) del usuario (vaciar carrito). */
    @WebMethod(operationName = "liberarMisReservas")
    public Resultado liberarMisReservas(@WebParam(name = "idUsuario") int idUsuario) {
        return reservaService.liberarTodas(idUsuario);
    }

    // ---------------------------------------------------------------- Venta

    /** Venta simple (rubrica): un item, a CONTADO, por categoria (Cat 1-4). */
    @WebMethod(operationName = "registrarVenta")
    public Resultado registrarVenta(
            @WebParam(name = "idUsuario")     int idUsuario,
            @WebParam(name = "codigoPartido") int codigoPartido,
            @WebParam(name = "categoria")     String categoria,
            @WebParam(name = "cantidad")      int cantidad) {
        return ventaService.registrarVenta(idUsuario, codigoPartido, categoria, cantidad);
    }

    /**
     * Compra por CARRITO: una sola factura con N items. Pago CONTADO o CREDITO;
     * en credito devuelve la tabla de amortizacion dentro de la factura.
     */
    @WebMethod(operationName = "registrarCompra")
    public Resultado registrarCompra(
            @WebParam(name = "idUsuario")   int idUsuario,
            @WebParam(name = "items")       List<ItemCarrito> items,
            @WebParam(name = "tipoPago")    String tipoPago,
            @WebParam(name = "numCuotas")   int numCuotas,
            @WebParam(name = "tasaInteres") BigDecimal tasaInteres,
            @WebParam(name = "entrada")     BigDecimal entrada) {
        return ventaService.registrarCompra(idUsuario, items, tipoPago, numCuotas, tasaInteres, entrada);
    }

    /** Simula la amortizacion de un credito (sin comprar) para el subtotal del carrito. */
    @WebMethod(operationName = "simularCredito")
    public Resultado simularCredito(
            @WebParam(name = "subtotal")    BigDecimal subtotal,
            @WebParam(name = "entrada")     BigDecimal entrada,
            @WebParam(name = "numCuotas")   int numCuotas,
            @WebParam(name = "tasaMensual") BigDecimal tasaMensual) {
        return ventaService.simularCredito(subtotal, entrada, numCuotas, tasaMensual);
    }

    // ---------------------------------------------------------------- Cuenta / movimientos

    /** Cuenta bancaria del usuario (incluye SALDO = deuda por creditos). */
    @WebMethod(operationName = "miCuenta")
    public Cuenta miCuenta(@WebParam(name = "idUsuario") int idUsuario) {
        return bancoService.cuentaDe(idUsuario);
    }

    /** Movimientos de la cuenta del usuario (compras contado y creditos). */
    @WebMethod(operationName = "misMovimientos")
    public List<Movimiento> misMovimientos(@WebParam(name = "idUsuario") int idUsuario) {
        return bancoService.movimientosDe(idUsuario);
    }

    // ---------------------------------------------------------------- Historial / reporte

    @WebMethod(operationName = "misFacturas")
    public List<Factura> misFacturas(@WebParam(name = "idUsuario") int idUsuario) {
        return ventaService.listarFacturasPorUsuario(idUsuario);
    }

    @WebMethod(operationName = "listarTodasFacturas")
    public List<Factura> listarTodasFacturas(@WebParam(name = "idAdmin") int idAdmin) {
        return ventaService.listarTodasFacturas(idAdmin);
    }

    /** Comprobante completo (cabecera + detalles + amortizacion). Valida propietario/ADMIN. */
    @WebMethod(operationName = "verComprobante")
    public Factura verComprobante(
            @WebParam(name = "idSolicitante") int idSolicitante,
            @WebParam(name = "idFactura")     int idFactura) {
        return ventaService.verComprobante(idSolicitante, idFactura);
    }

    @WebMethod(operationName = "listarDetallesFactura")
    public List<DetalleFactura> listarDetallesFactura(@WebParam(name = "idFactura") int idFactura) {
        return ventaService.detalleFactura(idFactura);
    }

    @WebMethod(operationName = "amortizacionDe")
    public List<Cuota> amortizacionDe(@WebParam(name = "idFactura") int idFactura) {
        return ventaService.amortizacionDe(idFactura);
    }

    @WebMethod(operationName = "resumenVentasPartido")
    public List<ResumenLocalidad> resumenVentasPartido(
            @WebParam(name = "codigoPartido") int codigoPartido) {
        return reporteService.resumenVentasPorPartido(codigoPartido);
    }

    // ---------------------------------------------------------------- CRUD partidos (ADMIN)

    @WebMethod(operationName = "registrarPartido")
    public Resultado registrarPartido(
            @WebParam(name = "idAdmin")    int idAdmin,
            @WebParam(name = "idLocal")    int idLocal,
            @WebParam(name = "idVisita")   int idVisita,
            @WebParam(name = "idEstadio")  int idEstadio,
            @WebParam(name = "fecha")      String fecha,
            @WebParam(name = "grupo")      String grupo) {
        return partidoService.registrar(idAdmin, idLocal, idVisita, idEstadio, fecha, grupo);
    }

    @WebMethod(operationName = "actualizarPartido")
    public Resultado actualizarPartido(
            @WebParam(name = "idAdmin")    int idAdmin,
            @WebParam(name = "codigo")     int codigo,
            @WebParam(name = "idLocal")    int idLocal,
            @WebParam(name = "idVisita")   int idVisita,
            @WebParam(name = "idEstadio")  int idEstadio,
            @WebParam(name = "fecha")      String fecha,
            @WebParam(name = "grupo")      String grupo) {
        return partidoService.actualizar(idAdmin, codigo, idLocal, idVisita, idEstadio, fecha, grupo);
    }

    @WebMethod(operationName = "eliminarPartido")
    public Resultado eliminarPartido(
            @WebParam(name = "idAdmin") int idAdmin,
            @WebParam(name = "codigo")  int codigo) {
        return partidoService.eliminar(idAdmin, codigo);
    }

    // ---------------------------------------------------------------- CRUD localidades (ADMIN)

    @WebMethod(operationName = "registrarLocalidad")
    public Resultado registrarLocalidad(
            @WebParam(name = "idAdmin")        int idAdmin,
            @WebParam(name = "codigoPartido")  int codigoPartido,
            @WebParam(name = "categoria")      String categoria,
            @WebParam(name = "disponibilidad") int disponibilidad,
            @WebParam(name = "precio")         BigDecimal precio) {
        return localidadService.registrar(idAdmin, codigoPartido, categoria, disponibilidad, precio);
    }

    @WebMethod(operationName = "actualizarLocalidad")
    public Resultado actualizarLocalidad(
            @WebParam(name = "idAdmin")        int idAdmin,
            @WebParam(name = "idLocalidad")    int idLocalidad,
            @WebParam(name = "disponibilidad") int disponibilidad,
            @WebParam(name = "precio")         BigDecimal precio) {
        return localidadService.actualizar(idAdmin, idLocalidad, disponibilidad, precio);
    }

    @WebMethod(operationName = "eliminarLocalidad")
    public Resultado eliminarLocalidad(
            @WebParam(name = "idAdmin")     int idAdmin,
            @WebParam(name = "idLocalidad") int idLocalidad) {
        return localidadService.eliminar(idAdmin, idLocalidad);
    }
}
