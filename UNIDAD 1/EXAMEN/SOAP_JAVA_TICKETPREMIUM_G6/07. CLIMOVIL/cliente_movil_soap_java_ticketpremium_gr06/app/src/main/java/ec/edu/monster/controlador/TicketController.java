package ec.edu.monster.controlador;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import ec.edu.monster.modelo.Factura;
import ec.edu.monster.modelo.Localidad;
import ec.edu.monster.modelo.Partido;
import ec.edu.monster.modelo.Resultado;
import ec.edu.monster.modelo.ResumenLocalidad;
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

    public void logout() { Sesion.cerrar(); }

    // ============================================================================
    // Compra
    // ============================================================================
    public void partidos(Callback<List<Partido>> cb)                                { async(ws::listarPartidosDisponibles, cb); }
    public void localidades(int codigoPartido, Callback<List<Localidad>> cb)        { async(() -> ws.listarLocalidadesPorPartido(codigoPartido), cb); }
    public void localidadesAdmin(int codigoPartido, Callback<List<Localidad>> cb)   { async(() -> ws.listarTodasLocalidadesPorPartido(codigoPartido), cb); }

    public void comprar(int codigoPartido, String localidad, int cantidad, Callback<Resultado> cb) {
        async(() -> ws.registrarVenta(Sesion.idUsuario(), codigoPartido, localidad, cantidad), cb);
    }

    public void misFacturas(Callback<List<Factura>> cb) {
        // Admin ve TODAS las facturas; cliente solo las suyas
        if (Sesion.isAdmin()) {
            async(() -> ws.listarTodasFacturas(Sesion.idUsuario()), cb);
        } else {
            async(() -> ws.misFacturas(Sesion.idUsuario()), cb);
        }
    }

    public void resumenVentas(int codigoPartido, Callback<List<ResumenLocalidad>> cb) {
        async(() -> ws.resumenVentasPartido(codigoPartido), cb);
    }

    // ============================================================================
    // Admin
    // ============================================================================
    public void registrarPartido(String local, String visita, String fecha, String lugar, Callback<Resultado> cb) {
        async(() -> ws.registrarPartido(Sesion.idUsuario(), local, visita, fecha, lugar), cb);
    }

    public void actualizarPartido(int codigo, String local, String visita, String fecha, String lugar, Callback<Resultado> cb) {
        async(() -> ws.actualizarPartido(Sesion.idUsuario(), codigo, local, visita, fecha, lugar), cb);
    }

    public void eliminarPartido(int codigo, Callback<Resultado> cb) {
        async(() -> ws.eliminarPartido(Sesion.idUsuario(), codigo), cb);
    }

    public void registrarLocalidad(int codigoPartido, String codigoLoc, int disp, BigDecimal precio, Callback<Resultado> cb) {
        async(() -> ws.registrarLocalidad(Sesion.idUsuario(), codigoPartido, codigoLoc, disp, precio), cb);
    }

    public void actualizarLocalidad(int idLocalidad, int disp, BigDecimal precio, Callback<Resultado> cb) {
        async(() -> ws.actualizarLocalidad(Sesion.idUsuario(), idLocalidad, disp, precio), cb);
    }

    public void eliminarLocalidad(int idLocalidad, Callback<Resultado> cb) {
        async(() -> ws.eliminarLocalidad(Sesion.idUsuario(), idLocalidad), cb);
    }
}
