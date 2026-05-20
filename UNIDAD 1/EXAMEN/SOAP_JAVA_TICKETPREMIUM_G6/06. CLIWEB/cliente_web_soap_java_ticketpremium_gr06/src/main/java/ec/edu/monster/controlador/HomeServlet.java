package ec.edu.monster.controlador;

import ec.edu.monster.modelo.ComprobanteCompra;
import ec.edu.monster.ws.Factura;
import ec.edu.monster.ws.Localidad;
import ec.edu.monster.ws.Partido;
import ec.edu.monster.ws.ResumenLocalidad;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/home")
public class HomeServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        TicketController ctrl = ctrl(req, resp);
        if (ctrl == null) return;

        List<Partido> partidos = ctrl.partidosDisponibles();
        int partidoSel = intParam(req.getParameter("partido"), partidos.isEmpty() ? 0 : partidos.get(0).getCodigo());
        if (partidoSel == 0 && !partidos.isEmpty()) partidoSel = partidos.get(0).getCodigo();
        List<Localidad> localidades = partidoSel > 0 ? ctrl.localidadesDe(partidoSel) : Collections.emptyList();

        int reporteSel = intParam(req.getParameter("reportePartido"), partidoSel);
        List<ResumenLocalidad> reporte = ctrl.getSesion().isAdmin() && reporteSel > 0
                ? ctrl.resumenVentas(reporteSel)
                : Collections.emptyList();

        int adminPartidoSel = intParam(req.getParameter("adminPartido"), partidoSel);
        List<Localidad> localidadesAdmin = ctrl.getSesion().isAdmin() && adminPartidoSel > 0
                ? ctrl.localidadesAdmin(adminPartidoSel)
                : Collections.emptyList();

        HttpSession session = req.getSession(false);
        List<ComprobanteCompra> comprobantes = comprobantes(session);
        ComprobanteCompra ultimo = comprobantes.isEmpty() ? null : comprobantes.get(comprobantes.size() - 1);
        List<Factura> facturas = ctrl.misFacturas();
        req.setAttribute("ultimoComprobante", ultimo);
        req.setAttribute("comprobantes", comprobantes);
        int partidosCount = partidos.size();
        int localidadesCount = localidades.size();
        int facturasCount = facturas.size();
        int reporteCount = reporte.size();
        int localidadesAdminCount = localidadesAdmin.size();
        int comprobantesCount = comprobantes.size();

        if (session != null) {
            req.setAttribute("flash", session.getAttribute("flash"));
            req.setAttribute("flashType", session.getAttribute("flashType"));
            session.removeAttribute("flash");
            session.removeAttribute("flashType");
        }

        req.setAttribute("partidos", partidos);
        req.setAttribute("partidoSel", partidoSel);
        req.setAttribute("localidades", localidades);
        req.setAttribute("reporteSel", reporteSel);
        req.setAttribute("reporte", reporte);
        req.setAttribute("adminPartidoSel", adminPartidoSel);
        req.setAttribute("localidadesAdmin", localidadesAdmin);
        req.setAttribute("partidosCount", partidosCount);
        req.setAttribute("localidadesCount", localidadesCount);
        req.setAttribute("facturasCount", facturasCount);
        req.setAttribute("comprobantesCount", comprobantesCount);
        req.setAttribute("reporteCount", reporteCount);
        req.setAttribute("localidadesAdminCount", localidadesAdminCount);
        req.setAttribute("facturas", facturas);
        req.setAttribute("usuario", ctrl.getSesion().getUsuario());
        req.setAttribute("admin", ctrl.getSesion().isAdmin());
        req.getRequestDispatcher("/WEB-INF/jsp/home.jsp").forward(req, resp);
    }

    @SuppressWarnings("unchecked")
    private List<ComprobanteCompra> comprobantes(HttpSession session) {
        if (session == null) {
            return Collections.emptyList();
        }
        List<ComprobanteCompra> comprobantes = (List<ComprobanteCompra>) session.getAttribute("historialComprobantes");
        if (comprobantes == null) {
            return Collections.emptyList();
        }
        return new ArrayList<>(comprobantes);
    }

    private TicketController ctrl(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession(false);
        if (session == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return null;
        }
        TicketController ctrl = (TicketController) session.getAttribute("ctrl");
        if (ctrl == null || !ctrl.getSesion().activa()) {
            session.invalidate();
            resp.sendRedirect(req.getContextPath() + "/login");
            return null;
        }
        return ctrl;
    }

    private int intParam(String value, int def) {
        try {
            return value == null || value.isBlank() ? def : Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return def;
        }
    }
}
