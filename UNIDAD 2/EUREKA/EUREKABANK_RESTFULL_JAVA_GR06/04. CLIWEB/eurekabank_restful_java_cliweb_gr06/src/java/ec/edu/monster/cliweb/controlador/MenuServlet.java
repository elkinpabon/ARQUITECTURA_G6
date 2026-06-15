package ec.edu.monster.cliweb.controlador;

import ec.edu.monster.cliweb.servicio.CuentaClient;
import ec.edu.monster.cliweb.ws.CuentaResumen;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

@WebServlet(name = "MenuServlet", urlPatterns = {"/menu"})
public class MenuServlet extends HttpServlet {

    private final CuentaClient cuentaClient = new CuentaClient();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("usuario") == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        Boolean esAdmin = (Boolean) session.getAttribute("esAdmin");
        String clienteAsignado = (String) session.getAttribute("clienteAsignado");

        // Admin: combo con TODOS los clientes registrados.
        if (Boolean.TRUE.equals(esAdmin)) {
            try {
                req.setAttribute("todosClientes", cuentaClient.listarClientes());
            } catch (Exception e) {
                req.setAttribute("todosClientes", Collections.emptyList());
            }
        }

        boolean refrescar = req.getParameter("refrescar") != null;

        // Usuario NO admin: carga automática (1ª vez) o al pulsar "Actualizar".
        boolean noAdminSinCuentas = Boolean.FALSE.equals(esAdmin)
                && session.getAttribute("cuentasCliente") == null;
        if (noAdminSinCuentas || (refrescar && Boolean.FALSE.equals(esAdmin))) {
            recargar(session, clienteAsignado);
        }

        // Admin: al pulsar "Actualizar", recarga las cuentas del cliente ya buscado.
        if (refrescar && Boolean.TRUE.equals(esAdmin)) {
            String yaBuscado = (String) session.getAttribute("clienteBuscado");
            if (yaBuscado != null && !yaBuscado.isBlank()) {
                recargar(session, yaBuscado);
            }
        }

        req.getRequestDispatcher("/WEB-INF/views/menu.jsp").forward(req, resp);
    }

    /** Re-consulta las cuentas del cliente y refresca los saldos en sesión. */
    private void recargar(HttpSession session, String criterio) {
        List<CuentaResumen> cuentas;
        String msg;
        if (criterio == null || criterio.isBlank()) {
            cuentas = Collections.emptyList();
            msg = "Tu usuario no tiene un cliente asociado. Contacta al administrador.";
        } else {
            try {
                cuentas = cuentaClient.listarCuentasPorCliente(criterio);
                msg = cuentas.isEmpty() ? "No hay cuentas registradas." : null;
            } catch (Exception e) {
                cuentas = Collections.emptyList();
                msg = "Error al contactar el servidor: " + e.getMessage();
            }
        }
        session.setAttribute("clienteBuscado", criterio);
        session.setAttribute("cuentasCliente", cuentas);
        session.setAttribute("cuentasMsg", msg);
    }
}
