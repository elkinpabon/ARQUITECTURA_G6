package ec.edu.monster.controlador;

import ec.edu.monster.servicio.WsFactory;
import ec.edu.monster.ws.Partido;
import ec.edu.monster.ws.WSFederacion;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

/**
 * Landing PUBLICA: cartelera de los partidos del Mundial con banderas.
 * No requiere sesion; para comprar se exige iniciar sesion (login?next=...).
 */
@WebServlet("/partidos")
public class PartidosServlet extends HttpServlet {

    /** Catalogo compartido (sin sesion) con cache de 60s para no golpear el SOAP en cada visita. */
    private static volatile WSFederacion port;
    private static volatile List<Partido> cache = Collections.emptyList();
    private static volatile long cacheTs = 0;

    static List<Partido> catalogo() {
        long ahora = System.currentTimeMillis();
        if (ahora - cacheTs > 60_000 || cache.isEmpty()) {
            synchronized (PartidosServlet.class) {
                if (ahora - cacheTs > 60_000 || cache.isEmpty()) {
                    if (port == null) port = WsFactory.federacion();
                    cache = port.listarPartidosDisponibles();
                    cacheTs = ahora;
                }
            }
        }
        return cache;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        List<Partido> partidos;
        String errorCarga = null;
        try {
            partidos = catalogo();
        } catch (Exception e) {
            partidos = Collections.emptyList();
            errorCarga = "No se pudo cargar la cartelera: " + e.getMessage();
        }

        // si hay sesion activa, mostrar el nombre y los enlaces del usuario
        HttpSession session = req.getSession(false);
        TicketController ctrl = session == null ? null : (TicketController) session.getAttribute("ctrl");
        boolean logueado = ctrl != null && ctrl.getSesion().activa();

        req.setAttribute("partidos", partidos);
        req.setAttribute("partidosCount", partidos.size());
        req.setAttribute("logueado", logueado);
        req.setAttribute("usuario", logueado ? ctrl.getSesion().getUsuario() : null);
        req.setAttribute("admin", logueado && ctrl.getSesion().isAdmin());
        req.setAttribute("errorCarga", errorCarga);
        req.getRequestDispatcher("/WEB-INF/jsp/partidos.jsp").forward(req, resp);
    }
}
