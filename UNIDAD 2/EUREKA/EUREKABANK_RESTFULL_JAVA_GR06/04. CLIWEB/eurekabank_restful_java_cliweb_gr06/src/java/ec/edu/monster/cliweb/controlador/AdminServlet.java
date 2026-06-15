package ec.edu.monster.cliweb.controlador;

import ec.edu.monster.cliweb.servicio.CuentaClient;
import ec.edu.monster.cliweb.ws.Resultado;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

/** Alta de clientes y cuentas — SOLO administrador ('monster'). */
@WebServlet(name = "AdminServlet", urlPatterns = {"/admin"})
public class AdminServlet extends HttpServlet {

    private final CuentaClient cuentaClient = new CuentaClient();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("usuario") == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }
        boolean esAdmin = Boolean.TRUE.equals(session.getAttribute("esAdmin"));
        String accion = req.getParameter("accion");

        Resultado r = new Resultado();
        if (!esAdmin) {
            r.setExito(false);
            r.setMensaje("Solo el administrador puede registrar clientes o cuentas.");
        } else {
            try {
                switch (accion == null ? "" : accion) {
                    case "nuevoCliente" -> r = cuentaClient.registrarCliente(
                            req.getParameter("paterno"), req.getParameter("materno"),
                            req.getParameter("nombre"), req.getParameter("dni"),
                            req.getParameter("ciudad"), req.getParameter("direccion"),
                            req.getParameter("telefono"), req.getParameter("email"));
                    case "nuevaCuenta" -> r = cuentaClient.registrarCuenta(
                            req.getParameter("cliente"), req.getParameter("moneda"));
                    case "eliminarCuenta" -> r = cuentaClient.eliminarCuenta(
                            req.getParameter("cuenta"));
                    default -> {
                        r.setExito(false);
                        r.setMensaje("Acción no reconocida.");
                    }
                }
            } catch (Exception e) {
                r.setExito(false);
                r.setMensaje("Error al contactar el servidor: " + e.getMessage());
            }
        }

        req.setAttribute("accion", "Registro (" + accion + ")");
        req.setAttribute("cuenta", "-");
        req.setAttribute("resultado", r);
        req.getRequestDispatcher("/WEB-INF/views/resultado.jsp").forward(req, resp);
    }
}
