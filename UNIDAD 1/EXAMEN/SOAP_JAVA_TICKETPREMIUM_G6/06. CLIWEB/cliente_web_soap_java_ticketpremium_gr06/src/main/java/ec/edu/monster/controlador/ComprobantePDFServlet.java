package ec.edu.monster.controlador;

import ec.edu.monster.modelo.ComprobanteCompra;
import java.io.IOException;
import java.awt.Color;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

@WebServlet("/comprobante")
public class ComprobantePDFServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        int facturaId = intParam(req.getParameter("facturaId"));
        ComprobanteCompra comprobante = buscarComprobante(session, facturaId);
        if (comprobante == null) {
            session.setAttribute("flash", "No hay compra disponible para imprimir.");
            session.setAttribute("flashType", "error");
            resp.sendRedirect(req.getContextPath() + "/home");
            return;
        }

        String nombreArchivo = (comprobante.getCodigoR() + "-" + comprobante.getIdFactura()).replaceAll("[^A-Za-z0-9_-]", "_") + ".pdf";
        resp.setContentType("application/pdf");
        resp.setHeader("Content-Disposition", "attachment; filename=\"" + nombreArchivo + "\"");

        try (PDDocument doc = new PDDocument()) {
            PDPage page = new PDPage(PDRectangle.A4);
            doc.addPage(page);

            try (PDPageContentStream cs = new PDPageContentStream(doc, page)) {
                dibujarMarco(cs);
                escribirCabecera(cs, comprobante);
                escribirDetalles(cs, comprobante);
                dibujarPanelValidacion(cs, comprobante);
                escribirPie(cs, comprobante);
            }

            doc.save(resp.getOutputStream());
        }
    }

    @SuppressWarnings("unchecked")
    private ComprobanteCompra buscarComprobante(HttpSession session, int facturaId) {
        List<ComprobanteCompra> historial = (List<ComprobanteCompra>) session.getAttribute("historialComprobantes");
        if (historial == null || historial.isEmpty()) {
            ComprobanteCompra ultimo = (ComprobanteCompra) session.getAttribute("ultimoComprobante");
            if (ultimo != null && (facturaId <= 0 || ultimo.getIdFactura() == facturaId)) {
                return ultimo;
            }
            return null;
        }

        if (facturaId > 0) {
            for (ComprobanteCompra comprobante : historial) {
                if (comprobante.getIdFactura() == facturaId) {
                    return comprobante;
                }
            }
            return null;
        }

        return historial.get(historial.size() - 1);
    }

    private void dibujarMarco(PDPageContentStream cs) throws IOException {
        cs.setStrokingColor(Color.DARK_GRAY);
        cs.setLineWidth(1.0f);
        cs.addRect(36, 36, 523, 770);
        cs.stroke();
    }

    private void escribirCabecera(PDPageContentStream cs, ComprobanteCompra c) throws IOException {
        cs.setNonStrokingColor(new Color(19, 32, 64));
        cs.addRect(36, 760, 523, 46);
        cs.fill();

        cs.setNonStrokingColor(Color.WHITE);
        escribirTexto(cs, PDType1Font.HELVETICA_BOLD, 20, 52, 788, "TICKETPREMIUM", Color.WHITE);
        escribirTexto(cs, PDType1Font.HELVETICA_BOLD, 12, 52, 770, "ENTRADA DIGITAL / COMPROBANTE DE COMPRA", Color.WHITE);
        escribirTexto(cs, PDType1Font.HELVETICA_OBLIQUE, 8, 390, 771, c.getCodigoR() + "  |  Factura #" + c.getIdFactura(), Color.WHITE);

        cs.setNonStrokingColor(new Color(230, 236, 248));
        cs.addRect(36, 752, 523, 1.2f);
        cs.fill();
    }

    private void escribirDetalles(PDPageContentStream cs, ComprobanteCompra c) throws IOException {
        cs.setNonStrokingColor(new Color(247, 249, 252));
        cs.addRect(48, 128, 286, 592);
        cs.fill();

        cs.setStrokingColor(new Color(218, 224, 233));
        cs.addRect(48, 128, 286, 592);
        cs.stroke();

        escribirTexto(cs, PDType1Font.HELVETICA_BOLD, 13, 62, 695, "DATOS DE LA COMPRA", new Color(19, 32, 64));
        escribirTexto(cs, PDType1Font.HELVETICA_OBLIQUE, 8, 62, 680, "Verifica que los datos coincidan con tu boleto.", new Color(95, 104, 122));

        float y = 650;
        filaDato(cs, "Codigo R", c.getCodigoR(), y);
        y -= 42;
        filaDato(cs, "Factura", "#" + c.getIdFactura(), y);
        y -= 42;
        filaDato(cs, "Fecha", c.getFecha(), y);
        y -= 42;
        filaDato(cs, "Cliente", c.getUsuario(), y);
        y -= 42;
        filaDato(cs, "Partido", c.getPartido(), y);
        y -= 42;
        filaDato(cs, "Localidad", c.getLocalidad(), y);
        y -= 42;
        filaDato(cs, "Cantidad", String.valueOf(c.getCantidad()), y);
        y -= 42;
        filaDato(cs, "Subtotal", monto(c.getSubtotal()), y);
        y -= 42;
        filaDato(cs, "IVA", monto(c.getIva()), y);
        y -= 42;

        cs.setNonStrokingColor(new Color(19, 32, 64));
        cs.addRect(60, y - 6, 260, 32);
        cs.fill();
        escribirTexto(cs, PDType1Font.HELVETICA_BOLD, 13, 72, y + 6, "TOTAL  " + monto(c.getTotal()), Color.WHITE);

        escribirTexto(cs, PDType1Font.HELVETICA_OBLIQUE, 10, 62, 142, "Entrada simulada. Presenta este comprobante al ingreso.", new Color(95, 104, 122));
    }

    private void dibujarPanelValidacion(PDPageContentStream cs, ComprobanteCompra c) throws IOException {
        cs.setNonStrokingColor(new Color(242, 245, 250));
        cs.addRect(350, 170, 160, 550);
        cs.fill();

        cs.setStrokingColor(new Color(218, 224, 233));
        cs.addRect(350, 170, 160, 550);
        cs.stroke();

        escribirTexto(cs, PDType1Font.HELVETICA_BOLD, 12, 370, 682, "VALIDACION", new Color(19, 32, 64));
        escribirTexto(cs, PDType1Font.HELVETICA_OBLIQUE, 8, 370, 668, "QR simulado de acceso", new Color(95, 104, 122));

        dibujarQrFalso(cs, c.getCodigoR() + "-" + c.getIdFactura(), 375, 520, 110);

        escribirTexto(cs, PDType1Font.HELVETICA_BOLD, 10, 372, 498, "PASE DIGITAL", new Color(19, 32, 64));
        escribirTexto(cs, PDType1Font.HELVETICA, 8, 372, 485, "Codigo interno: " + c.getCodigoR(), new Color(70, 78, 92));

        cs.setNonStrokingColor(new Color(19, 32, 64));
        cs.addRect(366, 452, 128, 2.8f);
        cs.fill();

        escribirTexto(cs, PDType1Font.HELVETICA_OBLIQUE, 8, 372, 430, "Entrada valida solo para fines demostrativos.", new Color(95, 104, 122));
    }

    private void escribirPie(PDPageContentStream cs, ComprobanteCompra c) throws IOException {
        cs.setNonStrokingColor(new Color(19, 32, 64));
        cs.addRect(36, 36, 523, 38);
        cs.fill();

        escribirTexto(cs, PDType1Font.HELVETICA_BOLD, 9, 52, 52, "TICKETPREMIUM | " + c.getCodigoR() + " | FACTURA #" + c.getIdFactura(), Color.WHITE);
        escribirTexto(cs, PDType1Font.HELVETICA_OBLIQUE, 8, 52, 41, "Comprueba el codigo en la plataforma antes del ingreso.", Color.WHITE);
    }

    private void filaDato(PDPageContentStream cs, String etiqueta, String valor, float y) throws IOException {
        cs.setNonStrokingColor(new Color(232, 237, 245));
        cs.addRect(60, y - 2, 86, 26);
        cs.fill();

        cs.setNonStrokingColor(Color.WHITE);
        cs.addRect(146, y - 2, 188, 26);
        cs.fill();

        cs.setStrokingColor(new Color(218, 224, 233));
        cs.addRect(60, y - 2, 274, 26);
        cs.stroke();

        escribirTexto(cs, PDType1Font.HELVETICA_BOLD, 9, 66, y + 7, etiqueta.toUpperCase(), new Color(19, 32, 64));
        escribirTexto(cs, PDType1Font.HELVETICA, 9, 152, y + 7, limitar(valor, 26), new Color(52, 61, 75));
    }

    private void escribirTexto(PDPageContentStream cs, PDType1Font font, float size, float x, float y, String text) throws IOException {
        escribirTexto(cs, font, size, x, y, text, Color.BLACK);
    }

    private void escribirTexto(PDPageContentStream cs, PDType1Font font, float size, float x, float y, String text, Color color) throws IOException {
        cs.setNonStrokingColor(color);
        cs.beginText();
        cs.setFont(font, size);
        cs.newLineAtOffset(x, y);
        cs.showText(text);
        cs.endText();
    }

    private void dibujarQrFalso(PDPageContentStream cs, String seed, float x, float y, float size) throws IOException {
        int modules = 21;
        float module = size / modules;
        int hash = seed.hashCode();

        cs.setStrokingColor(Color.BLACK);
        cs.addRect(x, y, size, size);
        cs.stroke();

        dibujarMarcador(cs, x, y, module);
        dibujarMarcador(cs, x + size - module * 7, y, module);
        dibujarMarcador(cs, x, y + size - module * 7, module);
        dibujarMarcador(cs, x + size - module * 7, y + size - module * 7, module);

        cs.setNonStrokingColor(Color.BLACK);
        for (int fila = 0; fila < modules; fila++) {
            for (int columna = 0; columna < modules; columna++) {
                if (esZonaMarcador(fila, columna, modules)) {
                    continue;
                }
                int bit = Math.abs(hash + fila * 31 + columna * 17 + (fila * columna * 7));
                if ((bit % 3) != 0) {
                    float px = x + columna * module;
                    float py = y + (modules - 1 - fila) * module;
                    cs.addRect(px, py, module, module);
                    cs.fill();
                }
            }
        }

        cs.setNonStrokingColor(Color.WHITE);
        cs.addRect(x + module * 8, y + module * 8, module * 5, module * 5);
        cs.fill();
    }

    private void dibujarMarcador(PDPageContentStream cs, float x, float y, float module) throws IOException {
        cs.addRect(x, y, module * 7, module * 7);
        cs.fill();
        cs.setNonStrokingColor(Color.WHITE);
        cs.addRect(x + module, y + module, module * 5, module * 5);
        cs.fill();
        cs.setNonStrokingColor(Color.BLACK);
        cs.addRect(x + module * 2, y + module * 2, module * 3, module * 3);
        cs.fill();
    }

    private boolean esZonaMarcador(int fila, int columna, int modules) {
        return dentroMarcador(fila, columna, 0, 0)
                || dentroMarcador(fila, columna, 0, modules - 7)
                || dentroMarcador(fila, columna, modules - 7, 0)
                || dentroMarcador(fila, columna, modules - 7, modules - 7);
    }

    private boolean dentroMarcador(int fila, int columna, int inicioFila, int inicioColumna) {
        return fila >= inicioFila && fila < inicioFila + 7 && columna >= inicioColumna && columna < inicioColumna + 7;
    }

    private String monto(java.math.BigDecimal v) {
        return v == null ? "0.00" : String.format("%.2f", v);
    }

    private String limitar(String value, int max) {
        if (value == null) {
            return "";
        }
        String limpio = value.replace("\n", " ").replace("\r", " ");
        if (limpio.length() <= max) {
            return limpio;
        }
        return limpio.substring(0, Math.max(0, max - 3)) + "...";
    }

    private int intParam(String value) {
        try {
            return value == null || value.isBlank() ? 0 : Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}
