package ec.edu.monster.controlador;

import ec.edu.monster.modelo.CartLine;
import ec.edu.monster.servicio.AsientosHub;
import ec.edu.monster.ws.Resultado;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/** Carrito: agregar (rapido), reservarAgregar (con asiento), quitar, vaciar. */
@WebServlet("/carrito")
public class CarritoServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("ctrl") == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }
        TicketController ctrl = (TicketController) session.getAttribute("ctrl");
        String accion = req.getParameter("accion");
        int partidoSel = intParam(req.getParameter("partido"), 0);
        String redirect = req.getContextPath() + "/home" + (partidoSel > 0 ? "?partido=" + partidoSel : "");

        try {
            switch (accion == null ? "" : accion) {
                case "agregar"        -> agregar(req, session, null, null);
                case "reservarAgregar"-> redirect = reservarAgregar(req, session, ctrl);
                case "quitar"         -> quitar(req, session, ctrl);
                case "vaciar"         -> vaciar(session, ctrl);
                default -> { /* nada */ }
            }
        } catch (Exception e) {
            session.setAttribute("flash", "Error en el carrito: " + e.getMessage());
            session.setAttribute("flashType", "error");
        }
        resp.sendRedirect(redirect);
    }

    /** Reserva el asiento elegido y, si se logra, lo agrega al carrito. Vuelve al mapa. */
    private String reservarAgregar(HttpServletRequest req, HttpSession session, TicketController ctrl) {
        int idSeccion = intParam(req.getParameter("idSeccion"), 0);
        String seat = req.getParameter("seat");        // formato "F3|7"
        String fila = "", asiento = "";
        if (seat != null && seat.contains("|")) {
            String[] p = seat.split("\\|", 2);
            fila = p[0]; asiento = p[1];
        }
        Resultado r = ctrl.reservarAsiento(idSeccion, fila, asiento);
        AsientosHub.empujar(idSeccion);
        if (r.isExito()) {
            agregar(req, session, fila, asiento);
            session.setAttribute("flash", "Asiento " + fila + "-" + asiento + " reservado y agregado al carrito.");
            session.setAttribute("flashType", "success");
        } else {
            session.setAttribute("flash", r.getMensaje());
            session.setAttribute("flashType", "error");
        }
        // volver al mapa de asientos con los mismos parametros
        return req.getContextPath() + "/asientos?" + queryAsientos(req);
    }

    @SuppressWarnings("unchecked")
    private void agregar(HttpServletRequest req, HttpSession session, String filaForzada, String asientoForzado) {
        int cantidad = intParam(req.getParameter("cantidad"), 1);
        if (cantidad <= 0) cantidad = 1;
        String fila = filaForzada != null ? filaForzada : req.getParameter("fila");
        String asiento = asientoForzado != null ? asientoForzado : req.getParameter("asientos");
        CartLine linea = new CartLine(
                intParam(req.getParameter("codigoPartido"), 0),
                req.getParameter("partidoDesc"),
                intParam(req.getParameter("idSeccion"), 0),
                req.getParameter("seccionLabel"),
                req.getParameter("categoria"),
                bigParam(req.getParameter("precio")),
                asientoForzado != null ? 1 : cantidad,   // por-asiento => 1
                fila, asiento);
        if (linea.getIdSeccion() <= 0) {
            session.setAttribute("flash", "Selecciona una seccion valida.");
            session.setAttribute("flashType", "error");
            return;
        }
        List<CartLine> carrito = (List<CartLine>) session.getAttribute("carrito");
        if (carrito == null) carrito = new ArrayList<>();
        carrito.add(linea);
        session.setAttribute("carrito", carrito);
        if (asientoForzado == null) {
            session.setAttribute("flash", "Agregado al carrito: " + linea.getCategoria() + " x" + linea.getCantidad());
            session.setAttribute("flashType", "success");
        }
    }

    @SuppressWarnings("unchecked")
    private void quitar(HttpServletRequest req, HttpSession session, TicketController ctrl) {
        int idx = intParam(req.getParameter("indice"), -1);
        List<CartLine> carrito = (List<CartLine>) session.getAttribute("carrito");
        if (carrito != null && idx >= 0 && idx < carrito.size()) {
            CartLine l = carrito.get(idx);
            // si la linea tiene un asiento concreto, liberar la reserva
            if (l.getIdSeccion() > 0 && l.getFila() != null && !l.getFila().isBlank()
                    && l.getAsientos() != null && !l.getAsientos().isBlank()) {
                ctrl.liberarAsiento(l.getIdSeccion(), l.getFila(), l.getAsientos());
                AsientosHub.empujar(l.getIdSeccion());
            }
            carrito.remove(idx);
            session.setAttribute("carrito", carrito);
        }
    }

    /** Libera todas las reservas del usuario y notifica a las secciones afectadas. */
    @SuppressWarnings("unchecked")
    private void vaciar(HttpSession session, TicketController ctrl) {
        ctrl.liberarMisReservas();
        List<CartLine> carrito = (List<CartLine>) session.getAttribute("carrito");
        if (carrito != null) {
            carrito.stream().map(CartLine::getIdSeccion).distinct().forEach(AsientosHub::empujar);
        }
        session.removeAttribute("carrito");
    }

    private String queryAsientos(HttpServletRequest req) {
        StringBuilder sb = new StringBuilder();
        String[] keys = {"idSeccion", "numFilas", "asientosPorFila", "categoria", "precio",
                         "partido", "codigoPartido", "partidoDesc"};
        for (String k : keys) {
            String v = req.getParameter(k);
            if (v != null) {
                if (sb.length() > 0) sb.append('&');
                sb.append(k).append('=').append(URLEncoder.encode(v, StandardCharsets.UTF_8));
            }
        }
        return sb.toString();
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
