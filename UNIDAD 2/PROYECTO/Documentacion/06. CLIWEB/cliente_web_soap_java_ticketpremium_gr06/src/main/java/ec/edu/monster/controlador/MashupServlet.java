package ec.edu.monster.controlador;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

/** Mashup estilo StubHub: diagrama de estadio + asientos + mapa. Shell HTML; los datos los carga via JSON. */
@WebServlet("/mashup")
public class MashupServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // el mashup es PUBLICO (se puede ver el estadio y los asientos);
        // reservar/comprar exige sesion (el front redirige a login al primer clic)
        HttpSession session = req.getSession(false);
        TicketController ctrl = session == null ? null : (TicketController) session.getAttribute("ctrl");
        boolean logueado = ctrl != null && ctrl.getSesion().activa();

        int codigoPartido = intParam(req.getParameter("codigoPartido"), 0);
        req.setAttribute("codigoPartido", codigoPartido);
        req.setAttribute("logueado", logueado);
        req.setAttribute("usuario", logueado ? ctrl.getSesion().getUsuario() : null);
        req.getRequestDispatcher("/WEB-INF/jsp/mashup.jsp").forward(req, resp);
    }

    private int intParam(String v, int def) {
        try { return v == null || v.isBlank() ? def : Integer.parseInt(v.trim()); }
        catch (NumberFormatException e) { return def; }
    }
}
