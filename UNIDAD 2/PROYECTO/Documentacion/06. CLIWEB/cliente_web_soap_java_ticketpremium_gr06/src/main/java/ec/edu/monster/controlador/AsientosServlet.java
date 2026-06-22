package ec.edu.monster.controlador;

import ec.edu.monster.ws.Asiento;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/** Mapa de asientos de una seccion (estado por asiento en tiempo real). */
@WebServlet("/asientos")
public class AsientosServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("ctrl") == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }
        TicketController ctrl = (TicketController) session.getAttribute("ctrl");
        if (!ctrl.getSesion().activa()) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        int idSeccion = intParam(req.getParameter("idSeccion"), 0);
        int numFilas = intParam(req.getParameter("numFilas"), 0);
        int asientosPorFila = intParam(req.getParameter("asientosPorFila"), 0);

        // Estados actuales (solo no-libres): clave "F{r}|{a}" -> estado
        Map<String, String> estados = new HashMap<>();
        if (idSeccion > 0) {
            for (Asiento a : ctrl.asientosNoLibres(idSeccion)) {
                estados.put(a.getFila() + "|" + a.getAsiento(), a.getEstado());
            }
        }

        if (session.getAttribute("flash") != null) {
            req.setAttribute("flash", session.getAttribute("flash"));
            req.setAttribute("flashType", session.getAttribute("flashType"));
            session.removeAttribute("flash");
            session.removeAttribute("flashType");
        }

        req.setAttribute("idSeccion", idSeccion);
        req.setAttribute("numFilas", numFilas);
        req.setAttribute("asientosPorFila", asientosPorFila);
        req.setAttribute("categoria", req.getParameter("categoria"));
        req.setAttribute("precio", req.getParameter("precio"));
        req.setAttribute("partido", req.getParameter("partido"));
        req.setAttribute("codigoPartido", req.getParameter("codigoPartido"));
        req.setAttribute("partidoDesc", req.getParameter("partidoDesc"));
        req.setAttribute("estados", estados);
        req.getRequestDispatcher("/WEB-INF/jsp/asientos.jsp").forward(req, resp);
    }

    private int intParam(String v, int def) {
        try { return v == null || v.isBlank() ? def : Integer.parseInt(v.trim()); }
        catch (NumberFormatException e) { return def; }
    }
}
