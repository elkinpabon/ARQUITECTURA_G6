package ec.edu.monster.controlador;

import ec.edu.monster.ws.Cuota;
import ec.edu.monster.ws.DetalleFactura;
import ec.edu.monster.ws.Factura;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.awt.Color;
import java.io.IOException;
import java.math.BigDecimal;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

/** Genera el PDF del comprobante (carrito + pago + amortizacion si es a credito). */
@WebServlet("/comprobante")
public class ComprobantePDFServlet extends HttpServlet {

    private static final Color AZUL = new Color(19, 32, 64);

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("ctrl") == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }
        TicketController ctrl = (TicketController) session.getAttribute("ctrl");
        int facturaId = intParam(req.getParameter("facturaId"));
        Factura f = facturaId > 0 ? ctrl.comprobante(facturaId) : null;
        if (f == null) {
            session.setAttribute("flash", "No se encontro la factura #" + facturaId + " o no es tuya.");
            session.setAttribute("flashType", "error");
            resp.sendRedirect(req.getContextPath() + "/home");
            return;
        }

        resp.setContentType("application/pdf");
        resp.setHeader("Content-Disposition", "attachment; filename=\"comprobante-" + f.getIdFactura() + ".pdf\"");

        try (PDDocument doc = new PDDocument()) {
            PDPage page = new PDPage(PDRectangle.A4);
            doc.addPage(page);
            try (PDPageContentStream cs = new PDPageContentStream(doc, page)) {
                float y = 800;

                // Banner
                cs.setNonStrokingColor(AZUL);
                cs.addRect(36, 792, 523, 38); cs.fill();
                texto(cs, PDType1Font.HELVETICA_BOLD, 18, 48, 805, "TICKETPREMIUM  -  FIFA 2026", Color.WHITE);
                texto(cs, PDType1Font.HELVETICA, 9, 48, 795, "Comprobante de compra", Color.WHITE);
                y = 770;

                // Cabecera factura
                texto(cs, PDType1Font.HELVETICA_BOLD, 12, 40, y, "Factura #" + f.getIdFactura(), AZUL); y -= 16;
                texto(cs, PDType1Font.HELVETICA, 10, 40, y, "Fecha: " + str(f.getFecha()), Color.BLACK); y -= 14;
                texto(cs, PDType1Font.HELVETICA, 10, 40, y, "Cliente: " + str(f.getUsuarioNombre()), Color.BLACK); y -= 14;
                texto(cs, PDType1Font.HELVETICA, 10, 40, y, "Tipo de pago: " + str(f.getTipoPago())
                        + " (" + str(f.getMoneda()) + ")", Color.BLACK); y -= 22;

                // Tabla de detalles (carrito)
                texto(cs, PDType1Font.HELVETICA_BOLD, 11, 40, y, "DETALLE DE ENTRADAS", AZUL); y -= 4;
                y -= 12;
                cabeceraDetalle(cs, y); y -= 16;
                if (f.getDetalles() != null) {
                    for (DetalleFactura d : f.getDetalles()) {
                        texto(cs, PDType1Font.HELVETICA, 8, 40, y, corta(str(d.getDescripcionPartido()), 34), Color.BLACK);
                        texto(cs, PDType1Font.HELVETICA, 8, 250, y, str(d.getCategoria()), Color.BLACK);
                        texto(cs, PDType1Font.HELVETICA, 8, 300, y, str(d.getFila()) + " " + str(d.getAsientos()), Color.BLACK);
                        texto(cs, PDType1Font.HELVETICA, 8, 400, y, String.valueOf(d.getCantidad()), Color.BLACK);
                        texto(cs, PDType1Font.HELVETICA, 8, 440, y, money(d.getPrecioUnitario()), Color.BLACK);
                        texto(cs, PDType1Font.HELVETICA, 8, 500, y, money(d.getTotal()), Color.BLACK);
                        y -= 14;
                    }
                }
                y -= 8;
                texto(cs, PDType1Font.HELVETICA, 10, 400, y, "Subtotal:", Color.BLACK);
                texto(cs, PDType1Font.HELVETICA, 10, 500, y, money(f.getSubtotal()), Color.BLACK); y -= 14;
                texto(cs, PDType1Font.HELVETICA, 10, 400, y, "IVA (15%):", Color.BLACK);
                texto(cs, PDType1Font.HELVETICA, 10, 500, y, money(f.getIva()), Color.BLACK); y -= 14;
                texto(cs, PDType1Font.HELVETICA_BOLD, 11, 400, y, "TOTAL:", AZUL);
                texto(cs, PDType1Font.HELVETICA_BOLD, 11, 500, y, money(f.getTotal()), AZUL); y -= 24;

                // Amortizacion (si credito)
                if ("CREDITO".equalsIgnoreCase(str(f.getTipoPago())) && f.getAmortizacion() != null
                        && !f.getAmortizacion().isEmpty()) {
                    texto(cs, PDType1Font.HELVETICA_BOLD, 11, 40, y, "TABLA DE AMORTIZACION (sistema frances)", AZUL); y -= 14;
                    texto(cs, PDType1Font.HELVETICA, 9, 40, y, "Entrada: " + money(f.getEntrada())
                            + "   Financiado: " + money(f.getMontoFinanciado())
                            + "   Cuotas: " + f.getNumCuotas()
                            + "   Tasa mensual: " + pct(f.getTasaInteres()), Color.BLACK); y -= 16;
                    cabeceraAmort(cs, y); y -= 14;
                    for (Cuota c : f.getAmortizacion()) {
                        texto(cs, PDType1Font.HELVETICA, 8, 44, y, String.valueOf(c.getNumCuota()), Color.BLACK);
                        texto(cs, PDType1Font.HELVETICA, 8, 80, y, str(c.getFechaVencimiento()), Color.BLACK);
                        texto(cs, PDType1Font.HELVETICA, 8, 170, y, money(c.getSaldoInicial()), Color.BLACK);
                        texto(cs, PDType1Font.HELVETICA, 8, 250, y, money(c.getCuota()), Color.BLACK);
                        texto(cs, PDType1Font.HELVETICA, 8, 330, y, money(c.getInteres()), Color.BLACK);
                        texto(cs, PDType1Font.HELVETICA, 8, 410, y, money(c.getAbonoCapital()), Color.BLACK);
                        texto(cs, PDType1Font.HELVETICA, 8, 490, y, money(c.getSaldoFinal()), Color.BLACK);
                        y -= 13;
                    }
                }

                texto(cs, PDType1Font.HELVETICA_OBLIQUE, 8, 40, 50,
                        "Comprobante demostrativo - TicketPremium GR06 - Mundial FIFA 2026.", Color.GRAY);
            }
            doc.save(resp.getOutputStream());
        }
    }

    private void cabeceraDetalle(PDPageContentStream cs, float y) throws IOException {
        texto(cs, PDType1Font.HELVETICA_BOLD, 8, 40, y, "PARTIDO", AZUL);
        texto(cs, PDType1Font.HELVETICA_BOLD, 8, 250, y, "CAT", AZUL);
        texto(cs, PDType1Font.HELVETICA_BOLD, 8, 300, y, "FILA/ASIENTOS", AZUL);
        texto(cs, PDType1Font.HELVETICA_BOLD, 8, 400, y, "CANT", AZUL);
        texto(cs, PDType1Font.HELVETICA_BOLD, 8, 440, y, "P.UNIT", AZUL);
        texto(cs, PDType1Font.HELVETICA_BOLD, 8, 500, y, "TOTAL", AZUL);
    }

    private void cabeceraAmort(PDPageContentStream cs, float y) throws IOException {
        texto(cs, PDType1Font.HELVETICA_BOLD, 8, 44, y, "#", AZUL);
        texto(cs, PDType1Font.HELVETICA_BOLD, 8, 80, y, "VENCE", AZUL);
        texto(cs, PDType1Font.HELVETICA_BOLD, 8, 170, y, "SALDO INI", AZUL);
        texto(cs, PDType1Font.HELVETICA_BOLD, 8, 250, y, "CUOTA", AZUL);
        texto(cs, PDType1Font.HELVETICA_BOLD, 8, 330, y, "INTERES", AZUL);
        texto(cs, PDType1Font.HELVETICA_BOLD, 8, 410, y, "ABONO", AZUL);
        texto(cs, PDType1Font.HELVETICA_BOLD, 8, 490, y, "SALDO FIN", AZUL);
    }

    private void texto(PDPageContentStream cs, PDType1Font font, float size, float x, float y,
                       String text, Color color) throws IOException {
        cs.setNonStrokingColor(color);
        cs.beginText();
        cs.setFont(font, size);
        cs.newLineAtOffset(x, y);
        cs.showText(text == null ? "" : text);
        cs.endText();
    }

    private String money(BigDecimal v) { return v == null ? "0.00" : "$" + String.format("%.2f", v); }
    private String pct(BigDecimal v)   { return v == null ? "0%" : String.format("%.2f%%", v.multiply(BigDecimal.valueOf(100))); }
    private String str(String s)       { return s == null ? "" : s; }
    private String corta(String s, int max) { return s.length() <= max ? s : s.substring(0, max - 1) + "."; }

    private int intParam(String value) {
        try { return value == null || value.isBlank() ? 0 : Integer.parseInt(value); }
        catch (NumberFormatException e) { return 0; }
    }
}
