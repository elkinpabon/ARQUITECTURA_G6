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

/**
 * Busca las cuentas de un cliente (por codigo o DNI) y las deja en sesion
 * para que menu.jsp muestre el selector. Patron PRG: redirige a /menu.
 */
@WebServlet(name = "CuentasServlet", urlPatterns = {"/cuentas"})
public class CuentasServlet extends HttpServlet {

    private final CuentaClient cuentaClient = new CuentaClient();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("usuario") == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        // Solo 'monster' (admin) puede buscar cualquier cliente.
        // Un usuario restringido queda forzado a SU cliente asignado.
        Boolean esAdmin = (Boolean) session.getAttribute("esAdmin");
        String cliente;
        if (Boolean.TRUE.equals(esAdmin)) {
            cliente = req.getParameter("cliente");
            cliente = cliente == null ? "" : cliente.trim();
        } else {
            String asignado = (String) session.getAttribute("clienteAsignado");
            cliente = asignado == null ? "" : asignado.trim();
        }

        List<CuentaResumen> cuentas;
        String mensaje;
        try {
            cuentas = cuentaClient.listarCuentasPorCliente(cliente);
            mensaje = cuentas.isEmpty()
                    ? "No se encontraron cuentas para el cliente \"" + cliente + "\"."
                    : null;
        } catch (Exception e) {
            cuentas = Collections.emptyList();
            mensaje = "Error al contactar el servidor: " + e.getMessage();
        }

        session.setAttribute("clienteBuscado", cliente);
        session.setAttribute("cuentasCliente", cuentas);
        session.setAttribute("cuentasMsg", mensaje);
        resp.sendRedirect(req.getContextPath() + "/menu");
    }
}
