package ec.edu.monster.controlador;

import ec.edu.monster.modelo.CartLine;
import ec.edu.monster.ws.Cuota;
import ec.edu.monster.ws.Factura;
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
import java.math.RoundingMode;
import java.util.List;

/**
 * Simula la amortizacion del credito para el carrito actual (AJAX, JSON), SIN comprar.
 * El calculo lo hace el servidor SOAP (misma matematica que la compra real).
 */
@WebServlet("/simular-credito")
public class SimularCreditoServlet extends HttpServlet {

    @Override
    @SuppressWarnings("unchecked")
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json; charset=UTF-8");
        PrintWriter out = resp.getWriter();

        HttpSession session = req.getSession(false);
        TicketController ctrl = session == null ? null : (TicketController) session.getAttribute("ctrl");
        if (ctrl == null) { out.print("{\"ok\":false,\"mensaje\":\"Sesion no iniciada.\"}"); return; }

        List<CartLine> carrito = (List<CartLine>) session.getAttribute("carrito");
        if (carrito == null || carrito.isEmpty()) {
            out.print("{\"ok\":false,\"mensaje\":\"El carrito esta vacio.\"}"); return;
        }
        BigDecimal subtotal = BigDecimal.ZERO;
        for (CartLine l : carrito) subtotal = subtotal.add(l.getTotal());

        int numCuotas = intParam(req.getParameter("numCuotas"), 0);
        BigDecimal entrada = bigParam(req.getParameter("entrada"));
        // la tasa se ingresa como % mensual (ej. 2) -> decimal (0.02)
        BigDecimal tasa = bigParam(req.getParameter("tasaInteres"))
                .divide(BigDecimal.valueOf(100), 6, RoundingMode.HALF_UP);

        Resultado r;
        try {
            r = ctrl.simularCredito(subtotal, entrada, numCuotas, tasa);
        } catch (Exception e) {
            out.print("{\"ok\":false,\"mensaje\":\"Error al simular: " + esc(e.getMessage()) + "\"}");
            return;
        }
        if (r == null || !r.isExito() || r.getFactura() == null) {
            out.print("{\"ok\":false,\"mensaje\":\"" + esc(r == null ? "Sin respuesta" : r.getMensaje()) + "\"}");
            return;
        }

        Factura f = r.getFactura();
        StringBuilder sb = new StringBuilder();
        sb.append("{\"ok\":true")
          .append(",\"subtotal\":").append(f.getSubtotal())
          .append(",\"iva\":").append(f.getIva())
          .append(",\"total\":").append(f.getTotal())
          .append(",\"entrada\":").append(f.getEntrada())
          .append(",\"financiado\":").append(f.getMontoFinanciado())
          .append(",\"numCuotas\":").append(f.getNumCuotas())
          .append(",\"cuotas\":[");
        List<Cuota> cuotas = f.getAmortizacion();
        for (int i = 0; i < cuotas.size(); i++) {
            Cuota c = cuotas.get(i);
            if (i > 0) sb.append(',');
            sb.append("{\"n\":").append(c.getNumCuota())
              .append(",\"venc\":\"").append(esc(c.getFechaVencimiento())).append("\"")
              .append(",\"saldoIni\":").append(c.getSaldoInicial())
              .append(",\"cuota\":").append(c.getCuota())
              .append(",\"interes\":").append(c.getInteres())
              .append(",\"abono\":").append(c.getAbonoCapital())
              .append(",\"saldoFin\":").append(c.getSaldoFinal())
              .append("}");
        }
        sb.append("]}");
        out.print(sb);
    }

    private String esc(String s) { return s == null ? "" : s.replace("\\", "\\\\").replace("\"", "\\\""); }

    private int intParam(String v, int def) {
        try { return v == null || v.isBlank() ? def : Integer.parseInt(v.trim()); }
        catch (NumberFormatException e) { return def; }
    }

    private BigDecimal bigParam(String v) {
        try { return v == null || v.isBlank() ? BigDecimal.ZERO : new BigDecimal(v.trim()); }
        catch (Exception e) { return BigDecimal.ZERO; }
    }
}
