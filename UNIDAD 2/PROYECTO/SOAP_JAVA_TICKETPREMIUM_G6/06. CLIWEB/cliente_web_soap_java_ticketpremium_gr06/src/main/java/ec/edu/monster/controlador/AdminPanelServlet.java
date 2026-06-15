package ec.edu.monster.controlador;

import ec.edu.monster.ws.Estadio;
import ec.edu.monster.ws.Localidad;
import ec.edu.monster.ws.Partido;
import ec.edu.monster.ws.Seleccion;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

/** Pantalla dedicada de administracion (solo ADMIN). */
@WebServlet("/admin-panel")
public class AdminPanelServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null) { resp.sendRedirect(req.getContextPath() + "/login"); return; }
        TicketController ctrl = (TicketController) session.getAttribute("ctrl");
        if (ctrl == null || !ctrl.getSesion().activa()) {
            session.invalidate();
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }
        if (!ctrl.getSesion().isAdmin()) {
            session.setAttribute("flash", "Acceso restringido al administrador.");
            session.setAttribute("flashType", "error");
            resp.sendRedirect(req.getContextPath() + "/home");
            return;
        }

        String tab = req.getParameter("tab");
        if (tab == null || tab.isBlank()) tab = "partidos";

        List<Partido> partidos = ctrl.partidosDisponibles();
        List<Seleccion> selecciones = ctrl.selecciones();
        List<Estadio> estadios = ctrl.estadios();
        int adminPartidoSel = intParam(req.getParameter("adminPartido"),
                partidos.isEmpty() ? 0 : partidos.get(0).getCodigo());
        List<Localidad> localidadesAdmin = adminPartidoSel > 0
                ? ctrl.localidadesAdmin(adminPartidoSel) : Collections.emptyList();

        req.setAttribute("flash", session.getAttribute("flash"));
        req.setAttribute("flashType", session.getAttribute("flashType"));
        session.removeAttribute("flash");
        session.removeAttribute("flashType");

        req.setAttribute("tab", tab);
        req.setAttribute("partidos", partidos);
        req.setAttribute("partidosCount", partidos.size());
        req.setAttribute("selecciones", selecciones);
        req.setAttribute("estadios", estadios);
        req.setAttribute("adminPartidoSel", adminPartidoSel);
        req.setAttribute("localidadesAdmin", localidadesAdmin);
        req.setAttribute("localidadesAdminCount", localidadesAdmin.size());
        req.setAttribute("usuario", ctrl.getSesion().getUsuario());

        req.getRequestDispatcher("/WEB-INF/jsp/admin.jsp").forward(req, resp);
    }

    private int intParam(String value, int def) {
        try { return value == null || value.isBlank() ? def : Integer.parseInt(value); }
        catch (NumberFormatException e) { return def; }
    }
}
