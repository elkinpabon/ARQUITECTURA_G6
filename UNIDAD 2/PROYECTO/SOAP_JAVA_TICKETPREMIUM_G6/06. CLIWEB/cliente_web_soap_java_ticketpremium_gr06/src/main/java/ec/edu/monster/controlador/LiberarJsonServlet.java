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
import java.util.List;

/** Libera un asiento RESERVADO del usuario y lo quita del carrito (AJAX, doble clic en el mashup). */
@WebServlet("/liberar-json")
public class LiberarJsonServlet extends HttpServlet {

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

        Resultado r = ctrl.liberarAsiento(idSeccion, fila, asiento);
        if (!r.isExito()) {
            out.print("{\"ok\":false,\"mensaje\":\"" + esc(r.getMensaje()) + "\"}");
            return;
        }

        // quitar la linea correspondiente del carrito
        List<CartLine> carrito = (List<CartLine>) session.getAttribute("carrito");
        int count = 0;
        if (carrito != null) {
            carrito.removeIf(l -> l.getIdSeccion() == idSeccion
                    && fila != null && fila.equals(l.getFila())
                    && asiento != null && asiento.equals(l.getAsientos()));
            session.setAttribute("carrito", carrito);
            count = carrito.size();
        }
        AsientosHub.empujar(idSeccion);
        out.print("{\"ok\":true,\"mensaje\":\"Reserva liberada\",\"carritoCount\":" + count + "}");
    }

    private String esc(String s) { return s == null ? "" : s.replace("\\", "\\\\").replace("\"", "\\\""); }

    private int intParam(String v) {
        try { return v == null || v.isBlank() ? 0 : Integer.parseInt(v.trim()); }
        catch (NumberFormatException e) { return 0; }
    }
}
