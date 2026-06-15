package ec.edu.monster.controlador;

import ec.edu.monster.ws.Localidad;
import ec.edu.monster.ws.Partido;
import ec.edu.monster.ws.Seccion;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

/** JSON con el partido + sus categorias y secciones, para el mashup. */
@WebServlet("/mashup-data")
public class MashupDataServlet extends HttpServlet {

    /** Controlador anonimo compartido: el catalogo es publico (solo lectura). */
    private static final TicketController ANONIMO = new TicketController();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json; charset=UTF-8");
        PrintWriter out = resp.getWriter();
        HttpSession session = req.getSession(false);
        TicketController ctrl = session == null ? null : (TicketController) session.getAttribute("ctrl");
        if (ctrl == null) ctrl = ANONIMO;
        int codigo = intParam(req.getParameter("codigoPartido"));

        Partido partido = null;
        for (Partido p : ctrl.partidosDisponibles()) {
            if (p.getCodigo() == codigo) { partido = p; break; }
        }
        if (partido == null) { out.print("{\"error\":\"Partido no encontrado\"}"); return; }

        StringBuilder sb = new StringBuilder();
        sb.append("{\"codigoPartido\":").append(codigo)
          .append(",\"partido\":\"").append(esc(partido.getEquipoLocal() + " vs " + partido.getEquipoVisita())).append("\"")
          .append(",\"estadio\":\"").append(esc(partido.getEstadio())).append("\"")
          .append(",\"ciudad\":\"").append(esc(partido.getCiudad())).append("\"")
          .append(",\"pais\":\"").append(esc(partido.getPais())).append("\"")
          .append(",\"fecha\":\"").append(esc(partido.getFecha())).append("\"")
          .append(",\"categorias\":[");

        List<Localidad> cats = ctrl.categoriasDe(codigo);
        for (int i = 0; i < cats.size(); i++) {
            Localidad l = cats.get(i);
            if (i > 0) sb.append(',');
            sb.append("{\"idLocalidad\":").append(l.getId())
              .append(",\"categoria\":\"").append(esc(l.getCategoria())).append("\"")
              .append(",\"precio\":").append(l.getPrecio())
              .append(",\"disponibilidad\":").append(l.getDisponibilidad())
              .append(",\"secciones\":[");
            List<Seccion> secs = ctrl.seccionesDe(l.getId());
            for (int j = 0; j < secs.size(); j++) {
                Seccion s = secs.get(j);
                if (j > 0) sb.append(',');
                sb.append("{\"idSeccion\":").append(s.getIdSeccion())
                  .append(",\"codigo\":\"").append(esc(s.getCodigoSeccion())).append("\"")
                  .append(",\"numFilas\":").append(s.getNumFilas())
                  .append(",\"asientosPorFila\":").append(s.getAsientosPorFila()).append("}");
            }
            sb.append("]}");
        }
        sb.append("]}");
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
