package ec.edu.monster.controlador;

import ec.edu.monster.ws.Asiento;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

/** Endpoint JSON para el polling del mapa de asientos (solo no-libres). */
@WebServlet("/asientos-data")
public class AsientosDataServlet extends HttpServlet {

    /** Controlador anonimo compartido: ver el estado de los asientos es publico. */
    private static final TicketController ANONIMO = new TicketController();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json; charset=UTF-8");
        HttpSession session = req.getSession(false);
        PrintWriter out = resp.getWriter();
        TicketController ctrl = session == null ? null : (TicketController) session.getAttribute("ctrl");
        if (ctrl == null) ctrl = ANONIMO;
        int idSeccion = intParam(req.getParameter("idSeccion"));
        List<Asiento> noLibres = idSeccion > 0 ? ctrl.asientosNoLibres(idSeccion) : List.of();

        StringBuilder sb = new StringBuilder("[");
        boolean first = true;
        for (Asiento a : noLibres) {
            if (!first) sb.append(',');
            first = false;
            sb.append("{\"fila\":\"").append(esc(a.getFila()))
              .append("\",\"asiento\":\"").append(esc(a.getAsiento()))
              .append("\",\"estado\":\"").append(esc(a.getEstado())).append("\"}");
        }
        sb.append(']');
        out.print(sb);
    }

    private String esc(String s) {
        return s == null ? "" : s.replace("\\", "\\\\").replace("\"", "\\\"");
    }

    private int intParam(String v) {
        try { return v == null || v.isBlank() ? 0 : Integer.parseInt(v.trim()); }
        catch (NumberFormatException e) { return 0; }
    }
}
