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

@WebServlet(name = "CuentaServlet", urlPatterns = {"/cuenta"})
public class CuentaServlet extends HttpServlet {

    private final CuentaClient cuentaClient = new CuentaClient();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("usuario") == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        String accion = req.getParameter("accion");
        String cuenta = req.getParameter("cuenta");
        String cuentaDestino = req.getParameter("cuentaDestino");
        String monto = req.getParameter("monto");
        String moneda = req.getParameter("moneda");
        if (moneda == null || moneda.isBlank()) {
            moneda = "02"; // Dólares = moneda preferente
        }
        boolean esAdmin = Boolean.TRUE.equals(session.getAttribute("esAdmin"));

        Resultado r;
        // El depósito a una cuenta propia solo lo realiza el admin.
        if ("depositar".equals(accion) && !esAdmin) {
            r = new Resultado();
            r.setExito(false);
            r.setMensaje("Solo el administrador puede depositar. Usa transferencia.");
            req.setAttribute("accion", accion);
            req.setAttribute("cuenta", cuenta);
            req.setAttribute("resultado", r);
            req.getRequestDispatcher("/WEB-INF/views/resultado.jsp").forward(req, resp);
            return;
        }
        if (!AccesoCuenta.permitida(session, cuenta)) {
            r = new Resultado();
            r.setExito(false);
            r.setMensaje("No tienes acceso a la cuenta " + cuenta + ".");
            req.setAttribute("accion", accion);
            req.setAttribute("cuenta", cuenta);
            req.setAttribute("resultado", r);
            req.getRequestDispatcher("/WEB-INF/views/resultado.jsp").forward(req, resp);
            return;
        }
        try {
            switch (accion == null ? "" : accion) {
                case "depositar"  -> r = cuentaClient.depositar(cuenta, monto, moneda);
                case "retirar"    -> r = cuentaClient.retirar(cuenta, monto, moneda);
                case "saldo"      -> r = cuentaClient.consultarSaldo(cuenta);
                case "transferir" -> r = cuentaClient.transferir(cuenta, cuentaDestino, monto, moneda);
                default -> {
                    r = new Resultado();
                    r.setExito(false);
                    r.setMensaje("Acción no reconocida.");
                }
            }
        } catch (Exception e) {
            r = new Resultado();
            r.setExito(false);
            r.setMensaje("Error al contactar el servidor: " + e.getMessage());
        }

        req.setAttribute("accion", accion);
        req.setAttribute("cuenta", cuenta);
        req.setAttribute("resultado", r);
        req.getRequestDispatcher("/WEB-INF/views/resultado.jsp").forward(req, resp);
    }
}
