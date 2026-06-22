package ec.edu.monster.controlador;

import ec.edu.monster.ws.Factura;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

/** Mis compras: historial de facturas + detalle (items, pago, amortizacion) + PDF. */
@WebServlet("/compras")
public class ComprasServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("ctrl") == null) {
            resp.sendRedirect(req.getContextPath() + "/login?next=%2Fcompras");
            return;
        }
        TicketController ctrl = (TicketController) session.getAttribute("ctrl");
        if (!ctrl.getSesion().activa()) {
            resp.sendRedirect(req.getContextPath() + "/login?next=%2Fcompras");
            return;
        }

        List<Factura> facturas = ctrl.misFacturas();

        // detalle de una factura concreta (?f=ID) via verComprobante (valida propietario/ADMIN)
        int idDetalle = intParam(req.getParameter("f"));
        Factura detalle = null;
        if (idDetalle > 0) {
            try { detalle = ctrl.comprobante(idDetalle); }
            catch (Exception e) { req.setAttribute("errorDetalle", e.getMessage()); }
        }

        req.setAttribute("facturas", facturas);
        req.setAttribute("facturasCount", facturas.size());
        req.setAttribute("detalle", detalle);
        req.setAttribute("usuario", ctrl.getSesion().getUsuario());
        req.setAttribute("admin", ctrl.getSesion().isAdmin());
        req.getRequestDispatcher("/WEB-INF/jsp/compras.jsp").forward(req, resp);
    }

    private int intParam(String v) {
        try { return v == null || v.isBlank() ? 0 : Integer.parseInt(v.trim()); }
        catch (NumberFormatException e) { return 0; }
    }
}
