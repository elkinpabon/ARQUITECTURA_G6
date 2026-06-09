package ec.edu.monster.cliweb.controlador;

import ec.edu.monster.cliweb.servicio.MovimientoClient;
import ec.edu.monster.cliweb.ws.MovimientoModel;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

@WebServlet(name = "MovimientoServlet", urlPatterns = {"/movimientos"})
public class MovimientoServlet extends HttpServlet {

    private final MovimientoClient movimientoClient = new MovimientoClient();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("usuario") == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        String cuenta = req.getParameter("cuenta");
        List<MovimientoModel> movimientos;
        if (!AccesoCuenta.permitida(session, cuenta)) {
            req.setAttribute("cuenta", cuenta);
            req.setAttribute("movimientos", Collections.emptyList());
            req.setAttribute("error", "No tienes acceso a la cuenta " + cuenta + ".");
            req.getRequestDispatcher("/WEB-INF/views/movimientos.jsp").forward(req, resp);
            return;
        }
        try {
            movimientos = movimientoClient.listarMovimientos(cuenta);
        } catch (Exception e) {
            movimientos = Collections.emptyList();
            req.setAttribute("error", "Error al contactar el servidor: " + e.getMessage());
        }
        req.setAttribute("cuenta", cuenta);
        req.setAttribute("movimientos", movimientos);
        req.getRequestDispatcher("/WEB-INF/views/movimientos.jsp").forward(req, resp);
    }
}
