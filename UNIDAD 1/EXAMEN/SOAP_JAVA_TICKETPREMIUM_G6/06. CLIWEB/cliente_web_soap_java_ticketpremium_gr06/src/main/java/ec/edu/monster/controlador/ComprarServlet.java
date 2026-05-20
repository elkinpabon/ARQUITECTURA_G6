package ec.edu.monster.controlador;

import ec.edu.monster.modelo.ComprobanteCompra;
import ec.edu.monster.ws.Factura;
import ec.edu.monster.ws.Partido;
import ec.edu.monster.ws.Resultado;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/comprar")
    public class ComprarServlet extends HttpServlet {

    private static final DateTimeFormatter FECHA_FORMATO = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("ctrl") == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        TicketController ctrl = (TicketController) session.getAttribute("ctrl");
        int partido = intParam(req.getParameter("partido"));
        String localidad = req.getParameter("localidad");
        int cantidad = intParam(req.getParameter("cantidad"));
        int reportePartido = intParam(req.getParameter("reportePartido"));
        int adminPartido = intParam(req.getParameter("adminPartido"));

        try {
            Resultado r = ctrl.comprar(partido, localidad, cantidad);
            if (r.isExito() && r.getFactura() != null) {
                ComprobanteCompra comprobante = crearComprobante(ctrl, partido, localidad, cantidad, r.getFactura());
                agregarComprobante(session, comprobante);
                session.setAttribute("ultimoComprobante", comprobante);
                session.setAttribute("flash", "Compra registrada. Comprobante " + comprobante.getCodigoR());
                session.setAttribute("flashType", "success");
            } else {
                session.setAttribute("flash", r.getMensaje());
                session.setAttribute("flashType", "error");
            }
        } catch (Exception e) {
            session.setAttribute("flash", "Error registrando la compra: " + e.getMessage());
            session.setAttribute("flashType", "error");
        }

        StringBuilder redirect = new StringBuilder(req.getContextPath()).append("/home?partido=").append(partido);
        if (reportePartido > 0) {
            redirect.append("&reportePartido=").append(reportePartido);
        }
        if (adminPartido > 0) {
            redirect.append("&adminPartido=").append(adminPartido);
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

    private ComprobanteCompra crearComprobante(TicketController ctrl, int codigoPartido,
                                               String codigoLocalidad, int cantidad,
                                               Factura factura) {
        ComprobanteCompra c = new ComprobanteCompra();
        c.setCodigoR(codigoRecibo());
        c.setFecha(LocalDateTime.now().format(FECHA_FORMATO));
        c.setUsuario(ctrl.getSesion().getNombre());
        c.setIdFactura(factura.getIdFactura());
        c.setPartido(descripcionPartido(ctrl.partidosDisponibles(), codigoPartido));
        c.setLocalidad(codigoLocalidad);
        c.setCantidad(cantidad);
        c.setSubtotal(factura.getSubtotal());
        c.setIva(factura.getIva());
        c.setTotal(factura.getTotal());
        return c;
    }

    private String codigoRecibo() {
        String fecha = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss"));
        int random = ThreadLocalRandom.current().nextInt(1000, 10000);
        return "R-" + fecha + "-" + random;
    }

    private String descripcionPartido(List<Partido> partidos, int codigoPartido) {
        for (Partido p : partidos) {
            if (p.getCodigo() == codigoPartido) {
                return p.getEquipoLocal() + " vs " + p.getEquipoVisita();
            }
        }
        return "Partido #" + codigoPartido;
    }

    @SuppressWarnings("unchecked")
    private void agregarComprobante(HttpSession session, ComprobanteCompra comprobante) {
        List<ComprobanteCompra> historial = (List<ComprobanteCompra>) session.getAttribute("historialComprobantes");
        if (historial == null) {
            historial = new ArrayList<>();
        }
        historial.add(comprobante);
        session.setAttribute("historialComprobantes", historial);
    }
}
