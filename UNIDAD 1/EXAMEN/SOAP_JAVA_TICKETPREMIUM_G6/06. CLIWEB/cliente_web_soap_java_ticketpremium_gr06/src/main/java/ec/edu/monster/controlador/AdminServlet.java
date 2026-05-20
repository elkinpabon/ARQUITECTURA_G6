package ec.edu.monster.controlador;

import ec.edu.monster.ws.Resultado;
import java.io.IOException;
import java.math.BigDecimal;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/admin")
public class AdminServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("ctrl") == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        TicketController ctrl = (TicketController) session.getAttribute("ctrl");
        if (!ctrl.getSesion().isAdmin()) {
            session.setAttribute("flash", "Operacion no permitida.");
            session.setAttribute("flashType", "error");
            resp.sendRedirect(req.getContextPath() + "/home");
            return;
        }

        String accion = req.getParameter("accion");
        Resultado resultado;

        try {
            resultado = switch (accion == null ? "" : accion) {
                case "registrarPartido" -> ctrl.registrarPartido(
                        req.getParameter("equipoLocal"),
                        req.getParameter("equipoVisita"),
                        req.getParameter("fecha"),
                        req.getParameter("lugar"));
                case "actualizarPartido" -> ctrl.actualizarPartido(
                        intParam(req.getParameter("codigo")),
                        req.getParameter("equipoLocal"),
                        req.getParameter("equipoVisita"),
                        req.getParameter("fecha"),
                        req.getParameter("lugar"));
                case "eliminarPartido" -> ctrl.eliminarPartido(intParam(req.getParameter("codigo")));
                case "registrarLocalidad" -> ctrl.registrarLocalidad(
                        intParam(req.getParameter("codigoPartido")),
                        req.getParameter("codigoLocalidad"),
                        intParam(req.getParameter("disponibilidad")),
                        bigDecimalParam(req.getParameter("precio")));
                case "actualizarLocalidad" -> ctrl.actualizarLocalidad(
                        intParam(req.getParameter("idLocalidad")),
                        intParam(req.getParameter("disponibilidad")),
                        bigDecimalParam(req.getParameter("precio")));
                case "eliminarLocalidad" -> ctrl.eliminarLocalidad(intParam(req.getParameter("idLocalidad")));
                default -> error("Accion invalida.");
            };
        } catch (Exception e) {
            resultado = error("Error ejecutando operacion: " + e.getMessage());
        }

        session.setAttribute("flash", resultado.getMensaje());
        session.setAttribute("flashType", resultado.isExito() ? "success" : "error");

        String partido = req.getParameter("partido");
        String reportePartido = req.getParameter("reportePartido");
        String adminPartido = req.getParameter("adminPartido");
        StringBuilder redirect = new StringBuilder(req.getContextPath()).append("/home");
        boolean first = true;
        if (partido != null && !partido.isBlank()) {
            redirect.append(first ? "?" : "&").append("partido=").append(partido);
            first = false;
        }
        if (reportePartido != null && !reportePartido.isBlank()) {
            redirect.append(first ? "?" : "&").append("reportePartido=").append(reportePartido);
            first = false;
        }
        if (adminPartido != null && !adminPartido.isBlank()) {
            redirect.append(first ? "?" : "&").append("adminPartido=").append(adminPartido);
        }
        resp.sendRedirect(redirect.toString());
    }

    private int intParam(String value) {
        try {
            return value == null || value.isBlank() ? 0 : Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private BigDecimal bigDecimalParam(String value) {
        try {
            return value == null || value.isBlank() ? BigDecimal.ZERO : new BigDecimal(value);
        } catch (Exception e) {
            return BigDecimal.ZERO;
        }
    }

    private Resultado error(String mensaje) {
        Resultado r = new Resultado();
        r.setExito(false);
        r.setMensaje(mensaje);
        return r;
    }
}
