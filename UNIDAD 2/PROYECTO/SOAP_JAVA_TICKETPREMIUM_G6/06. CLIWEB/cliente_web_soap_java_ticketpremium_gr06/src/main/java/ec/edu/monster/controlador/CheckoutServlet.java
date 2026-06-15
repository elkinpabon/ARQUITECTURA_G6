package ec.edu.monster.controlador;

import ec.edu.monster.modelo.CartLine;
import ec.edu.monster.servicio.AsientosHub;
import ec.edu.monster.ws.Factura;
import ec.edu.monster.ws.ItemCarrito;
import ec.edu.monster.ws.Resultado;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

/** Finaliza la compra del carrito: pago a CONTADO o a CREDITO (con amortizacion). */
@WebServlet("/checkout")
public class CheckoutServlet extends HttpServlet {

    @Override
    @SuppressWarnings("unchecked")
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("ctrl") == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }
        TicketController ctrl = (TicketController) session.getAttribute("ctrl");
        List<CartLine> carrito = (List<CartLine>) session.getAttribute("carrito");

        if (carrito == null || carrito.isEmpty()) {
            session.setAttribute("flash", "El carrito esta vacio.");
            session.setAttribute("flashType", "error");
            resp.sendRedirect(req.getContextPath() + "/home");
            return;
        }

        String tipoPago = req.getParameter("tipoPago");
        if (tipoPago == null || tipoPago.isBlank()) tipoPago = "CONTADO";
        int numCuotas = intParam(req.getParameter("numCuotas"), 0);
        BigDecimal entrada = bigParam(req.getParameter("entrada"));
        // La tasa se ingresa como % mensual (ej 2) -> se convierte a decimal (0.02)
        BigDecimal tasaPct = bigParam(req.getParameter("tasaInteres"));
        BigDecimal tasa = tasaPct.divide(BigDecimal.valueOf(100), 6, RoundingMode.HALF_UP);

        List<ItemCarrito> items = new ArrayList<>();
        for (CartLine l : carrito) {
            ItemCarrito it = new ItemCarrito();
            it.setCodigoPartido(l.getCodigoPartido());
            it.setIdSeccion(l.getIdSeccion());
            it.setCantidad(l.getCantidad());
            it.setFila(l.getFila() == null ? "" : l.getFila());
            it.setAsientos(l.getAsientos() == null ? "" : l.getAsientos());
            items.add(it);
        }

        try {
            Resultado r = ctrl.comprar(items, tipoPago, numCuotas, tasa, entrada);
            if (r.isExito()) {
                session.removeAttribute("carrito");
                // tiempo real: los asientos pagados pasan a OCUPADO en todos los navegadores
                carrito.stream().map(CartLine::getIdSeccion).distinct().forEach(AsientosHub::empujar);
                Factura f = r.getFactura();
                if (f != null) {
                    // recargar comprobante completo (con detalles + amortizacion)
                    Factura completa = ctrl.comprobante(f.getIdFactura());
                    session.setAttribute("ultimaFactura", completa != null ? completa : f);
                }
                session.setAttribute("flash", r.getMensaje());
                session.setAttribute("flashType", "success");
            } else {
                session.setAttribute("flash", r.getMensaje());
                session.setAttribute("flashType", "error");
            }
        } catch (Exception e) {
            session.setAttribute("flash", "Error al finalizar la compra: " + e.getMessage());
            session.setAttribute("flashType", "error");
        }
        resp.sendRedirect(req.getContextPath() + "/home");
    }

    private int intParam(String v, int def) {
        try { return v == null || v.isBlank() ? def : Integer.parseInt(v.trim()); }
        catch (NumberFormatException e) { return def; }
    }

    private BigDecimal bigParam(String v) {
        try { return v == null || v.isBlank() ? BigDecimal.ZERO : new BigDecimal(v.trim()); }
        catch (Exception e) { return BigDecimal.ZERO; }
    }
}
