package ec.edu.monster.cliweb.controlador;

import ec.edu.monster.cliweb.servicio.LoginClient;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet(name = "LoginServlet", urlPatterns = {"/login"})
public class LoginServlet extends HttpServlet {

    private final LoginClient loginClient = new LoginClient();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String usuario = req.getParameter("usuario");
        String clave = req.getParameter("clave");
        // Quita espacios accidentales (autocompletado del navegador, copiar/pegar).
        if (usuario != null) usuario = usuario.trim();
        if (clave != null) clave = clave.trim();
        boolean ok = false;
        try {
            ok = loginClient.iniciarSesion(usuario, clave);
        } catch (Exception e) {
            req.setAttribute("error", "No se pudo contactar el servidor: " + e.getMessage());
            req.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(req, resp);
            return;
        }
        if (ok) {
            HttpSession session = req.getSession(true);
            session.setAttribute("usuario", usuario);

            // Rol: solo 'monster' es ADMIN (puede buscar cualquier cliente).
            boolean esAdmin = "monster".equalsIgnoreCase(usuario);
            String clienteAsignado = "";
            if (!esAdmin) {
                try {
                    clienteAsignado = loginClient.clienteDeUsuario(usuario);
                } catch (Exception e) {
                    clienteAsignado = "";
                }
            }
            session.setAttribute("esAdmin", esAdmin);
            session.setAttribute("clienteAsignado", clienteAsignado);
            // Limpia cualquier búsqueda previa.
            session.removeAttribute("cuentasCliente");
            session.removeAttribute("clienteBuscado");
            session.removeAttribute("cuentasMsg");

            resp.sendRedirect(req.getContextPath() + "/menu");
        } else {
            req.setAttribute("error", "Usuario o clave inválidos.");
            req.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(req, resp);
        }
    }
}
