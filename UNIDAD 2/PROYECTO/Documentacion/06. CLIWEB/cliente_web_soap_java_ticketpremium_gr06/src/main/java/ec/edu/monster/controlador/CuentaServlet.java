package ec.edu.monster.controlador;

import ec.edu.monster.ws.Cuenta;
import ec.edu.monster.ws.Movimiento;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

/** Mi cuenta: saldo y movimientos bancarios (core) del usuario. */
@WebServlet("/cuenta")
public class CuentaServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("ctrl") == null) {
            resp.sendRedirect(req.getContextPath() + "/login?next=%2Fcuenta");
            return;
        }
        TicketController ctrl = (TicketController) session.getAttribute("ctrl");
        if (!ctrl.getSesion().activa()) {
            resp.sendRedirect(req.getContextPath() + "/login?next=%2Fcuenta");
            return;
        }

        Cuenta cuenta = ctrl.miCuenta();
        List<Movimiento> movimientos = ctrl.misMovimientos();

        req.setAttribute("cuenta", cuenta);
        req.setAttribute("movimientos", movimientos);
        req.setAttribute("movimientosCount", movimientos.size());
        req.setAttribute("usuario", ctrl.getSesion().getUsuario());
        req.setAttribute("admin", ctrl.getSesion().isAdmin());
        req.getRequestDispatcher("/WEB-INF/jsp/cuenta.jsp").forward(req, resp);
    }
}
