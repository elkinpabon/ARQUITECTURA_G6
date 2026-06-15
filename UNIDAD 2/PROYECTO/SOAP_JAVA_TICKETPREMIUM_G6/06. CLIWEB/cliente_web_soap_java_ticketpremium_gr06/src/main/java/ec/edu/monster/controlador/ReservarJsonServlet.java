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
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/** Reserva un asiento y lo agrega al carrito (AJAX para el mashup). Devuelve JSON. */
@WebServlet("/reservar-json")
public class ReservarJsonServlet extends HttpServlet {

    @Override
    @SuppressWarnings("unchecked")
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json; charset=UTF-8");
        PrintWriter out = resp.getWriter();
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("ctrl") == null) {
            out.print("{\"ok\":false,\"mensaje\":\"Sesion no iniciada\"}");
            return;
        }
        TicketController ctrl = (TicketController) session.getAttribute("ctrl");

        int idSeccion = intParam(req.getParameter("idSeccion"));
        String fila = req.getParameter("fila");
        String asiento = req.getParameter("asiento");

        Resultado r = ctrl.reservarAsiento(idSeccion, fila, asiento);
        if (!r.isExito()) {
            out.print("{\"ok\":false,\"mensaje\":\"" + esc(r.getMensaje()) + "\"}");
            return;
        }

        // agregar linea al carrito
        CartLine linea = new CartLine(
                intParam(req.getParameter("codigoPartido")),
                req.getParameter("partidoDesc"),
                idSeccion,
                req.getParameter("seccionLabel"),
                req.getParameter("categoria"),
                bigParam(req.getParameter("precio")),
                1, fila, asiento);
        List<CartLine> carrito = (List<CartLine>) session.getAttribute("carrito");
        if (carrito == null) carrito = new ArrayList<>();
        carrito.add(linea);
        session.setAttribute("carrito", carrito);
        AsientosHub.empujar(idSeccion);

        out.print("{\"ok\":true,\"mensaje\":\"" + esc(r.getMensaje())
                + "\",\"carritoCount\":" + carrito.size() + "}");
    }

    private String esc(String s) { return s == null ? "" : s.replace("\\", "\\\\").replace("\"", "\\\""); }

    private int intParam(String v) {
        try { return v == null || v.isBlank() ? 0 : Integer.parseInt(v.trim()); }
        catch (NumberFormatException e) { return 0; }
    }

    private BigDecimal bigParam(String v) {
        try { return v == null || v.isBlank() ? BigDecimal.ZERO : new BigDecimal(v.trim()); }
        catch (Exception e) { return BigDecimal.ZERO; }
    }
}
