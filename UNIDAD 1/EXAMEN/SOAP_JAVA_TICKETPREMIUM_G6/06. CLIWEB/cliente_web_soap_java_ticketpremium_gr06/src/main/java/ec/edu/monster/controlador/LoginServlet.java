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
        req.getRequestDispatcher("/WEB-INF/jsp/login.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String usuario = req.getParameter("usuario");
        String clave = req.getParameter("contrasena");

        TicketController ctrl = new TicketController();
        try {
            if (ctrl.login(usuario, clave)) {
                HttpSession session = req.getSession(true);
                session.setAttribute("ctrl", ctrl);
                resp.sendRedirect(req.getContextPath() + "/home");
                return;
            }
            req.setAttribute("error", "Usuario o contrasena invalidos.");
        } catch (Exception e) {
            req.setAttribute("error", "Error contactando el servidor: " + e.getMessage());
        }
        req.getRequestDispatcher("/WEB-INF/jsp/login.jsp").forward(req, resp);
    }
}
