package ec.edu.monster.servicio;

import ec.edu.monster.modelo.ComprobanteCompra;
import ec.edu.monster.ws.ResumenLocalidad;
import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

/**
 * Genera PDFs con el mismo estilo que el cliente web:
 *   - comprobante de compra individual (con QR falso + logo)
 *   - reporte agregado de ventas por partido (para admin)
 */
public final class GeneradorComprobantePDF {

    private GeneradorComprobantePDF() { }

    // ============================================================================
    // COMPROBANTE INDIVIDUAL
    // ============================================================================
    public static byte[] comprobante(ComprobanteCompra c) throws IOException {
        try (PDDocument doc = new PDDocument();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            PDPage page = new PDPage(PDRectangle.A4);
            doc.addPage(page);
            PDImageXObject logo = cargarLogo(doc);

            try (PDPageContentStream cs = new PDPageContentStream(doc, page)) {
                dibujarMarco(cs);
                escribirCabecera(cs, c, logo);
                escribirDetalles(cs, c);
                dibujarPanelValidacion(cs, c);
                escribirPie(cs, c);
            }
            doc.save(out);
            return out.toByteArray();
        }
    }

    private static PDImageXObject cargarLogo(PDDocument doc) {
        try (InputStream in = GeneradorComprobantePDF.class
                .getResourceAsStream("/images/moster.png")) {
            if (in == null) return null;
            return PDImageXObject.createFromByteArray(doc, in.readAllBytes(), "moster");
        } catch (Exception ignore) {
            return null;
        }
    }

    private static void dibujarMarco(PDPageContentStream cs) throws IOException {
        cs.setStrokingColor(Color.DARK_GRAY);
        cs.setLineWidth(1.0f);
        cs.addRect(36, 36, 523, 770);
        cs.stroke();
    }

    private static void escribirCabecera(PDPageContentStream cs, ComprobanteCompra c,
                                         PDImageXObject logo) throws IOException {
        cs.setNonStrokingColor(new Color(19, 32, 64));
        cs.addRect(36, 760, 523, 46);
        cs.fill();

        if (logo != null) {
            cs.drawImage(logo, 44, 762, 42, 42);
        }

        cs.setNonStrokingColor(Color.WHITE);
        escribirTexto(cs, PDType1Font.HELVETICA_BOLD, 20, 96, 788, "TICKETPREMIUM", Color.WHITE);
        escribirTexto(cs, PDType1Font.HELVETICA_BOLD, 12, 96, 770,
                "ENTRADA DIGITAL / COMPROBANTE DE COMPRA", Color.WHITE);

        // Sub-info alineado a la derecha (dos lineas) para no salir del banner
        float xRight = 549;     // 10pt de padding desde x=559 (borde del banner)
        textoDerecha(cs, PDType1Font.HELVETICA_OBLIQUE, 9, xRight, 790, c.getCodigoR());
        textoDerecha(cs, PDType1Font.HELVETICA_OBLIQUE, 9, xRight, 776, "Factura #" + c.getIdFactura());

        cs.setNonStrokingColor(new Color(230, 236, 248));
        cs.addRect(36, 752, 523, 1.2f);
        cs.fill();
    }

    /** Escribe un texto alineado a la derecha (su borde derecho queda en xRight). */
    private static void textoDerecha(PDPageContentStream cs, PDType1Font font, float size,
                                     float xRight, float y, String texto) throws IOException {
        float w = font.getStringWidth(texto) / 1000f * size;
        escribirTexto(cs, font, size, xRight - w, y, texto, Color.WHITE);
    }

    private static void escribirDetalles(PDPageContentStream cs, ComprobanteCompra c) throws IOException {
        cs.setNonStrokingColor(new Color(247, 249, 252));
        cs.addRect(48, 128, 286, 592);
        cs.fill();

        cs.setStrokingColor(new Color(218, 224, 233));
        cs.addRect(48, 128, 286, 592);
        cs.stroke();

        escribirTexto(cs, PDType1Font.HELVETICA_BOLD, 13, 62, 695, "DATOS DE LA COMPRA",
                new Color(19, 32, 64));
        escribirTexto(cs, PDType1Font.HELVETICA_OBLIQUE, 8, 62, 680,
                "Verifica que los datos coincidan con tu boleto.", new Color(95, 104, 122));

        float y = 650;
        filaDato(cs, "Codigo R", c.getCodigoR(), y); y -= 42;
        filaDato(cs, "Factura", "#" + c.getIdFactura(), y); y -= 42;
        filaDato(cs, "Fecha", c.getFecha(), y); y -= 42;
        filaDato(cs, "Cliente", c.getUsuario(), y); y -= 42;
        filaDato(cs, "Partido", c.getPartido(), y); y -= 42;
        filaDato(cs, "Localidad", c.getLocalidad(), y); y -= 42;
        filaDato(cs, "Cantidad", String.valueOf(c.getCantidad()), y); y -= 42;
        filaDato(cs, "Subtotal", monto(c.getSubtotal()), y); y -= 42;
        filaDato(cs, "IVA", monto(c.getIva()), y); y -= 42;

        cs.setNonStrokingColor(new Color(19, 32, 64));
        cs.addRect(60, y - 6, 260, 32);
        cs.fill();
        escribirTexto(cs, PDType1Font.HELVETICA_BOLD, 13, 72, y + 6,
                "TOTAL  " + monto(c.getTotal()), Color.WHITE);

        escribirTexto(cs, PDType1Font.HELVETICA_OBLIQUE, 10, 62, 142,
                "Entrada simulada. Presenta este comprobante al ingreso.",
                new Color(95, 104, 122));
    }

    private static void dibujarPanelValidacion(PDPageContentStream cs, ComprobanteCompra c) throws IOException {
        cs.setNonStrokingColor(new Color(242, 245, 250));
        cs.addRect(350, 170, 160, 550);
        cs.fill();

        cs.setStrokingColor(new Color(218, 224, 233));
        cs.addRect(350, 170, 160, 550);
        cs.stroke();

        escribirTexto(cs, PDType1Font.HELVETICA_BOLD, 12, 370, 682, "VALIDACION",
                new Color(19, 32, 64));
        escribirTexto(cs, PDType1Font.HELVETICA_OBLIQUE, 8, 370, 668,
                "QR simulado de acceso", new Color(95, 104, 122));

        dibujarQrFalso(cs, c.getCodigoR() + "-" + c.getIdFactura(), 375, 520, 110);

        escribirTexto(cs, PDType1Font.HELVETICA_BOLD, 10, 372, 498, "PASE DIGITAL",
                new Color(19, 32, 64));
        escribirTexto(cs, PDType1Font.HELVETICA, 8, 372, 485,
                "Codigo interno: " + c.getCodigoR(), new Color(70, 78, 92));

        cs.setNonStrokingColor(new Color(19, 32, 64));
        cs.addRect(366, 452, 128, 2.8f);
        cs.fill();

        escribirTexto(cs, PDType1Font.HELVETICA_OBLIQUE, 8, 372, 430,
                "Entrada valida solo para fines demostrativos.",
                new Color(95, 104, 122));
    }

    private static void escribirPie(PDPageContentStream cs, ComprobanteCompra c) throws IOException {
        cs.setNonStrokingColor(new Color(19, 32, 64));
        cs.addRect(36, 36, 523, 38);
        cs.fill();

        escribirTexto(cs, PDType1Font.HELVETICA_BOLD, 9, 52, 52,
                "TICKETPREMIUM | " + c.getCodigoR() + " | FACTURA #" + c.getIdFactura(),
                Color.WHITE);
        escribirTexto(cs, PDType1Font.HELVETICA_OBLIQUE, 8, 52, 41,
                "Comprueba el codigo en la plataforma antes del ingreso.", Color.WHITE);
    }

    private static void filaDato(PDPageContentStream cs, String etiqueta, String valor, float y) throws IOException {
        cs.setNonStrokingColor(new Color(232, 237, 245));
        cs.addRect(60, y - 2, 86, 26);
        cs.fill();

        cs.setNonStrokingColor(Color.WHITE);
        cs.addRect(146, y - 2, 188, 26);
        cs.fill();

        cs.setStrokingColor(new Color(218, 224, 233));
        cs.addRect(60, y - 2, 274, 26);
        cs.stroke();

        escribirTexto(cs, PDType1Font.HELVETICA_BOLD, 9, 66, y + 7,
                etiqueta.toUpperCase(), new Color(19, 32, 64));
        escribirTexto(cs, PDType1Font.HELVETICA, 9, 152, y + 7,
                limitar(valor, 26), new Color(52, 61, 75));
    }

    // ============================================================================
    // REPORTE DE VENTAS POR PARTIDO (admin)
    // ============================================================================
    public static byte[] reporteVentas(String partido, List<ResumenLocalidad> filas,
                                       String operador) throws IOException {
        try (PDDocument doc = new PDDocument();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            PDPage page = new PDPage(PDRectangle.A4);
            doc.addPage(page);
            PDImageXObject logo = cargarLogo(doc);

            try (PDPageContentStream cs = new PDPageContentStream(doc, page)) {
                dibujarMarco(cs);

                // Cabecera azul + logo
                cs.setNonStrokingColor(new Color(19, 32, 64));
                cs.addRect(36, 760, 523, 46);
                cs.fill();
                if (logo != null) cs.drawImage(logo, 44, 762, 42, 42);
                escribirTexto(cs, PDType1Font.HELVETICA_BOLD, 20, 96, 788,
                        "TICKETPREMIUM", Color.WHITE);
                escribirTexto(cs, PDType1Font.HELVETICA_BOLD, 12, 96, 770,
                        "REPORTE DE VENTAS POR PARTIDO", Color.WHITE);

                // Encabezado meta
                escribirTexto(cs, PDType1Font.HELVETICA_BOLD, 11, 52, 730,
                        "Partido: ", new Color(19, 32, 64));
                escribirTexto(cs, PDType1Font.HELVETICA, 11, 100, 730,
                        partido == null ? "(sin partido)" : partido,
                        new Color(52, 61, 75));

                String fechaGen = LocalDateTime.now().format(
                        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
                escribirTexto(cs, PDType1Font.HELVETICA_BOLD, 11, 52, 712,
                        "Generado: ", new Color(19, 32, 64));
                escribirTexto(cs, PDType1Font.HELVETICA, 11, 110, 712,
                        fechaGen, new Color(52, 61, 75));

                escribirTexto(cs, PDType1Font.HELVETICA_BOLD, 11, 280, 712,
                        "Operador: ", new Color(19, 32, 64));
                escribirTexto(cs, PDType1Font.HELVETICA, 11, 338, 712,
                        operador == null ? "-" : operador, new Color(52, 61, 75));

                // Cabecera tabla
                float[] xs = { 52, 220, 340, 460 };
                float yh = 680;
                cs.setNonStrokingColor(new Color(19, 32, 64));
                cs.addRect(48, yh - 6, 511, 24);
                cs.fill();
                escribirTexto(cs, PDType1Font.HELVETICA_BOLD, 10, xs[0], yh + 2,
                        "LOCALIDAD", Color.WHITE);
                escribirTexto(cs, PDType1Font.HELVETICA_BOLD, 10, xs[1], yh + 2,
                        "VENDIDOS", Color.WHITE);
                escribirTexto(cs, PDType1Font.HELVETICA_BOLD, 10, xs[2], yh + 2,
                        "TOTAL RECAUDADO", Color.WHITE);

                // Filas
                float y = yh - 24;
                int totalVendidos = 0;
                BigDecimal totalRec = BigDecimal.ZERO;
                boolean zebra = false;
                if (filas == null || filas.isEmpty()) {
                    escribirTexto(cs, PDType1Font.HELVETICA_OBLIQUE, 10, xs[0], y - 4,
                            "(sin ventas registradas)", new Color(95, 104, 122));
                    y -= 24;
                } else {
                    for (ResumenLocalidad r : filas) {
                        if (zebra) {
                            cs.setNonStrokingColor(new Color(247, 249, 252));
                            cs.addRect(48, y - 8, 511, 22);
                            cs.fill();
                        }
                        zebra = !zebra;
                        escribirTexto(cs, PDType1Font.HELVETICA, 10, xs[0], y - 2,
                                limitar(r.getLocalidad(), 30), new Color(52, 61, 75));
                        escribirTexto(cs, PDType1Font.HELVETICA, 10, xs[1], y - 2,
                                String.valueOf(r.getVendidos()), new Color(52, 61, 75));
                        escribirTexto(cs, PDType1Font.HELVETICA, 10, xs[2], y - 2,
                                monto(r.getTotalRecaudado()), new Color(52, 61, 75));
                        totalVendidos += r.getVendidos();
                        totalRec = totalRec.add(r.getTotalRecaudado());
                        y -= 22;
                        if (y < 120) break;   // hoja unica
                    }
                }

                // Totales
                y -= 8;
                cs.setNonStrokingColor(new Color(19, 32, 64));
                cs.addRect(48, y - 10, 511, 28);
                cs.fill();
                escribirTexto(cs, PDType1Font.HELVETICA_BOLD, 11, xs[0], y + 2,
                        "TOTAL", Color.WHITE);
                escribirTexto(cs, PDType1Font.HELVETICA_BOLD, 11, xs[1], y + 2,
                        String.valueOf(totalVendidos), Color.WHITE);
                escribirTexto(cs, PDType1Font.HELVETICA_BOLD, 11, xs[2], y + 2,
                        monto(totalRec), Color.WHITE);

                // Pie
                cs.setNonStrokingColor(new Color(19, 32, 64));
                cs.addRect(36, 36, 523, 38);
                cs.fill();
                escribirTexto(cs, PDType1Font.HELVETICA_BOLD, 9, 52, 52,
                        "TICKETPREMIUM GR06 | Reporte de ventas", Color.WHITE);
                escribirTexto(cs, PDType1Font.HELVETICA_OBLIQUE, 8, 52, 41,
                        "Documento generado automaticamente.", Color.WHITE);
            }
            doc.save(out);
            return out.toByteArray();
        }
    }

    // ============================================================================
    // QR falso + utilidades
    // ============================================================================
    private static void dibujarQrFalso(PDPageContentStream cs, String seed,
                                       float x, float y, float size) throws IOException {
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
                if (esZonaMarcador(fila, columna, modules)) continue;
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

    private static void dibujarMarcador(PDPageContentStream cs, float x, float y, float module) throws IOException {
        cs.addRect(x, y, module * 7, module * 7);
        cs.fill();
        cs.setNonStrokingColor(Color.WHITE);
        cs.addRect(x + module, y + module, module * 5, module * 5);
        cs.fill();
        cs.setNonStrokingColor(Color.BLACK);
        cs.addRect(x + module * 2, y + module * 2, module * 3, module * 3);
        cs.fill();
    }

    private static boolean esZonaMarcador(int fila, int columna, int modules) {
        return dentroMarcador(fila, columna, 0, 0)
                || dentroMarcador(fila, columna, 0, modules - 7)
                || dentroMarcador(fila, columna, modules - 7, 0)
                || dentroMarcador(fila, columna, modules - 7, modules - 7);
    }

    private static boolean dentroMarcador(int fila, int columna, int inicioFila, int inicioColumna) {
        return fila >= inicioFila && fila < inicioFila + 7
                && columna >= inicioColumna && columna < inicioColumna + 7;
    }

    private static void escribirTexto(PDPageContentStream cs, PDType1Font font, float size,
                                      float x, float y, String text, Color color) throws IOException {
        cs.setNonStrokingColor(color);
        cs.beginText();
        cs.setFont(font, size);
        cs.newLineAtOffset(x, y);
        cs.showText(text);
        cs.endText();
    }

    private static String monto(BigDecimal v) {
        return v == null ? "0.00" : String.format("%.2f", v);
    }

    private static String limitar(String value, int max) {
        if (value == null) return "";
        String limpio = value.replace("\n", " ").replace("\r", " ");
        if (limpio.length() <= max) return limpio;
        return limpio.substring(0, Math.max(0, max - 3)) + "...";
    }
}
