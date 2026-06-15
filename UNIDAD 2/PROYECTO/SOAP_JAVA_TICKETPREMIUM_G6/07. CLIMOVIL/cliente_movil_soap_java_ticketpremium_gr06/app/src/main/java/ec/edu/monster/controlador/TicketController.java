package ec.edu.monster.controlador;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import ec.edu.monster.modelo.Asiento;
import ec.edu.monster.modelo.Carrito;
import ec.edu.monster.modelo.Cuenta;
import ec.edu.monster.modelo.Factura;
import ec.edu.monster.modelo.ItemCarrito;
import ec.edu.monster.modelo.Localidad;
import ec.edu.monster.modelo.Movimiento;
import ec.edu.monster.modelo.Partido;
import ec.edu.monster.modelo.Resultado;
import ec.edu.monster.modelo.Seccion;
import ec.edu.monster.modelo.Sesion;
import ec.edu.monster.modelo.SesionResultado;
import ec.edu.monster.ws.WsFederacionClient;
import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Fachada usada por las Activities/Fragments. Ejecuta cada llamada SOAP en un
 * Executor de I/O y publica el resultado en el main thread via Handler.
 *
 * Patron de uso:
 *   ctrl.partidos(callback);
 *   callback recibe (datos, errorMsg) — uno de los dos sera null.
 */
public class TicketController {

    public interface Callback<T> {
        void done(T datos, String error);
    }

    private static final ExecutorService IO = Executors.newCachedThreadPool();
    private static final Handler UI = new Handler(Looper.getMainLooper());

    private final WsFederacionClient ws;

    public TicketController(Context ctx) {
        this.ws = new WsFederacionClient(ctx);
    }

    private <T> void async(java.util.concurrent.Callable<T> tarea, Callback<T> cb) {
        IO.execute(() -> {
            try {
                T r = tarea.call();
                UI.post(() -> cb.done(r, null));
            } catch (Exception e) {
                String msg = e.getMessage() == null ? e.getClass().getSimpleName() : e.getMessage();
                UI.post(() -> cb.done(null, msg));
            }
        });
    }

    // ============================================================================
    // Autenticacion
    // ============================================================================
    public void login(String usuario, String clave, Callback<SesionResultado> cb) {
        async(() -> {
            SesionResultado r = ws.iniciarSesion(usuario, clave);
            if (r.isExito() && r.getUsuario() != null) Sesion.abrir(r.getUsuario());
            return r;
        }, cb);
    }

    /**
     * Cierra sesion: libera en el servidor todas las reservas no pagadas
     * del usuario y vacia el carrito local. Best-effort (no bloquea la UI).
     */
    public void logout() {
        final int id = Sesion.idUsuario();
        Sesion.cerrar();
        Carrito.limpiar();
        if (id > 0) {
            IO.execute(() -> {
                try { ws.liberarMisReservas(id); } catch (Exception ignored) { }
            });
        }
    }

    // ============================================================================
    // Catalogo
    // ============================================================================
    public void partidos(Callback<List<Partido>> cb) {
        async(ws::listarPartidosDisponibles, cb);
    }

    public void localidades(int codigoPartido, Callback<List<Localidad>> cb) {
        async(() -> ws.listarLocalidadesPorPartido(codigoPartido), cb);
    }

    public void secciones(int idLocalidad, Callback<List<Seccion>> cb) {
        async(() -> ws.listarSeccionesPorLocalidad(idLocalidad), cb);
    }

    // ============================================================================
    // Asientos (tiempo real)
    // ============================================================================
    public void asientosNoLibres(int idSeccion, Callback<List<Asiento>> cb) {
        async(() -> ws.asientosNoLibres(idSeccion), cb);
    }

    public void reservarAsiento(int idSeccion, String fila, String asiento, Callback<Resultado> cb) {
        async(() -> ws.reservarAsiento(Sesion.idUsuario(), idSeccion, fila, asiento), cb);
    }

    public void liberarAsiento(int idSeccion, String fila, String asiento, Callback<Resultado> cb) {
        async(() -> ws.liberarAsiento(Sesion.idUsuario(), idSeccion, fila, asiento), cb);
    }

    public void liberarMisReservas(Callback<Resultado> cb) {
        async(() -> ws.liberarMisReservas(Sesion.idUsuario()), cb);
    }

    // ============================================================================
    // Compra (carrito)
    // ============================================================================
    public void registrarCompra(List<ItemCarrito> items, String tipoPago, int numCuotas,
                                BigDecimal tasaInteres, BigDecimal entrada, Callback<Resultado> cb) {
        async(() -> ws.registrarCompra(Sesion.idUsuario(), items, tipoPago, numCuotas, tasaInteres, entrada), cb);
    }

    // ============================================================================
    // Historial / comprobantes
    // ============================================================================
    public void misFacturas(Callback<List<Factura>> cb) {
        // Admin ve TODAS las facturas; cliente solo las suyas
        if (Sesion.isAdmin()) {
            async(() -> ws.listarTodasFacturas(Sesion.idUsuario()), cb);
        } else {
            async(() -> ws.misFacturas(Sesion.idUsuario()), cb);
        }
    }

    public void verComprobante(int idFactura, Callback<Factura> cb) {
        async(() -> ws.verComprobante(Sesion.idUsuario(), idFactura), cb);
    }

    // ============================================================================
    // Cuenta bancaria
    // ============================================================================
    public void miCuenta(Callback<Cuenta> cb) {
        async(() -> ws.miCuenta(Sesion.idUsuario()), cb);
    }

    public void misMovimientos(Callback<List<Movimiento>> cb) {
        async(() -> ws.misMovimientos(Sesion.idUsuario()), cb);
    }
}
