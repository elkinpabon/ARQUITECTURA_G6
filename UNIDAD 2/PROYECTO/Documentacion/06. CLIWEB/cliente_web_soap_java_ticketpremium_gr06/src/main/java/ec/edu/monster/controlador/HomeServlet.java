package ec.edu.monster.controlador;

import ec.edu.monster.modelo.CartLine;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

/**
 * Pagina "Carrito y compra": muestra SOLO el carrito + el checkout (tipo de pago)
 * + el comprobante de la ultima compra. El catalogo de partidos vive en /partidos,
 * el historial en /compras y la cuenta en /cuenta.
 */
@WebServlet("/home")
public class HomeServlet extends HttpServlet {

    @Override
    @SuppressWarnings("unchecked")
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        TicketController ctrl = ctrl(req, resp);
        if (ctrl == null) return;
        HttpSession session = req.getSession(false);

        // Carrito en sesion + subtotal
        List<CartLine> carrito = (List<CartLine>) session.getAttribute("carrito");
        if (carrito == null) carrito = Collections.emptyList();
        BigDecimal carritoTotal = BigDecimal.ZERO;
        for (CartLine l : carrito) carritoTotal = carritoTotal.add(l.getTotal());

        // flash + ultima factura (resultado de la compra)
        req.setAttribute("flash", session.getAttribute("flash"));
        req.setAttribute("flashType", session.getAttribute("flashType"));
        session.removeAttribute("flash");
        session.removeAttribute("flashType");
        req.setAttribute("ultimaFactura", session.getAttribute("ultimaFactura"));

        req.setAttribute("carrito", carrito);
        req.setAttribute("carritoTotal", carritoTotal);
        req.setAttribute("carritoCount", carrito.size());
        req.setAttribute("usuario", ctrl.getSesion().getUsuario());
        req.setAttribute("admin", ctrl.getSesion().isAdmin());
        req.getRequestDispatcher("/WEB-INF/jsp/home.jsp").forward(req, resp);
    }

    private TicketController ctrl(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession(false);
        if (session == null) { resp.sendRedirect(req.getContextPath() + "/login"); return null; }
        TicketController ctrl = (TicketController) session.getAttribute("ctrl");
        if (ctrl == null || !ctrl.getSesion().activa()) {
            session.invalidate();
            resp.sendRedirect(req.getContextPath() + "/login");
            return null;
        }
        return ctrl;
    }
}
