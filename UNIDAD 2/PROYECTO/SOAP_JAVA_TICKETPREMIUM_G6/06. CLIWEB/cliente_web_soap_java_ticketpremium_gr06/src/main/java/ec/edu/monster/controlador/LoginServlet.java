package ec.edu.monster.controlador;

import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setAttribute("next", nextSeguro(req.getParameter("next")));
        req.getRequestDispatcher("/WEB-INF/jsp/login.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String usuario = req.getParameter("usuario");
        String clave = req.getParameter("contrasena");
        String next = nextSeguro(req.getParameter("next"));

        TicketController ctrl = new TicketController();
        try {
            if (ctrl.login(usuario, clave)) {
                HttpSession session = req.getSession(true);
                session.setAttribute("ctrl", ctrl);
                resp.sendRedirect(req.getContextPath() + (next != null ? next : "/home"));
                return;
            }
            req.setAttribute("error", "Usuario o contrasena invalidos.");
        } catch (Exception e) {
            req.setAttribute("error", "Error contactando el servidor: " + e.getMessage());
        }
        req.setAttribute("next", next);
        req.getRequestDispatcher("/WEB-INF/jsp/login.jsp").forward(req, resp);
    }

    /** Solo acepta rutas internas (evita open redirect). */
    private String nextSeguro(String next) {
        if (next == null || next.isBlank()) return null;
        if (!next.startsWith("/") || next.startsWith("//")) return null;
        return next;
    }
}
