package ec.edu.monster.servicio;

import ec.edu.monster.ws.Cuota;
import ec.edu.monster.ws.DetalleFactura;
import ec.edu.monster.ws.Factura;
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
 * Genera PDFs del cliente escritorio (mismo estilo formal que el cliente web):
 *   - comprobante de compra: detalle del carrito (boletos con fila/asientos)
 *     y tabla de amortizacion si el pago fue a CREDITO (con QR simulado + logo)
 *   - reporte agregado de ventas por partido (para admin)
 */
public final class GeneradorComprobantePDF {

    private static final Color AZUL   = new Color(15, 76, 129);
    private static final Color TEXTO  = new Color(31, 41, 55);
    private static final Color SUAVE  = new Color(95, 104, 122);
    private static final Color ZEBRA  = new Color(247, 249, 252);
    private static final Color BORDE  = new Color(218, 224, 233);

    private GeneradorComprobantePDF() { }

    // ============================================================================
    // COMPROBANTE DE COMPRA (factura completa: detalles + amortizacion)
    // ============================================================================
    public static byte[] comprobante(Factura f) throws IOException {
        try (PDDocument doc = new PDDocument();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            PDImageXObject logo = cargarLogo(doc);
            String codigoR = "TP2026-" + f.getIdFactura();

            PDPage page = new PDPage(PDRectangle.A4);
            doc.addPage(page);
            try (PDPageContentStream cs = new PDPageContentStream(doc, page)) {
                dibujarMarco(cs);
                cabecera(cs, logo, "COMPROBANTE DE COMPRA / ENTRADA DIGITAL",
                        codigoR, "Factura #" + f.getIdFactura());

                float y = 730;

                // ----- Datos generales -----
                escribirTexto(cs, PDType1Font.HELVETICA_BOLD, 12, 52, y,
                        "DATOS DE LA COMPRA", AZUL);
                y -= 18;
                y = parDato(cs, "Factura", "#" + f.getIdFactura(),
                        "Fecha", nn(f.getFecha()), y);
                y = parDato(cs, "Cliente", nn(f.getUsuarioNombre()),
                        "Tipo de pago", nn(f.getTipoPago()), y);
                y -= 8;

                // ----- Tabla de boletos (detalle del carrito) -----
                escribirTexto(cs, PDType1Font.HELVETICA_BOLD, 12, 52, y,
                        "BOLETOS", AZUL);
                y -= 16;
                float[] xs = { 52, 250, 300, 345, 410, 455, 505 };
                y = filaCabecera(cs, y, xs,
                        "PARTIDO", "CAT.", "FILA", "ASIENTOS", "CANT.", "P.UNIT", "TOTAL");

                boolean zebra = false;
                List<DetalleFactura> dets = f.getDetalles();
                if (dets != null) {
                    for (DetalleFactura d : dets) {
                        if (zebra) franja(cs, y);
                        zebra = !zebra;
                        escribirTexto(cs, PDType1Font.HELVETICA, 9, xs[0], y,
                                limitar(nn(d.getDescripcionPartido()), 42), TEXTO);
                        escribirTexto(cs, PDType1Font.HELVETICA, 9, xs[1], y,
                                nn(d.getCategoria()), TEXTO);
                        escribirTexto(cs, PDType1Font.HELVETICA, 9, xs[2], y,
                                nn(d.getFila()), TEXTO);
                        escribirTexto(cs, PDType1Font.HELVETICA, 9, xs[3], y,
                                limitar(nn(d.getAsientos()), 12), TEXTO);
                        escribirTexto(cs, PDType1Font.HELVETICA, 9, xs[4], y,
                                String.valueOf(d.getCantidad()), TEXTO);
                        escribirTexto(cs, PDType1Font.HELVETICA, 9, xs[5], y,
                                monto(d.getPrecioUnitario()), TEXTO);
                        escribirTexto(cs, PDType1Font.HELVETICA, 9, xs[6], y,
                                monto(d.getTotal()), TEXTO);
                        y -= 18;
                        if (y < 230) break;     // espacio reservado para totales/QR
                    }
                }
                y -= 6;

                // ----- Totales -----
                float xLbl = 360, xVal = 455;
                y = lineaTotal(cs, xLbl, xVal, y, "Subtotal", monto(f.getSubtotal()), false);
                y = lineaTotal(cs, xLbl, xVal, y, "IVA", monto(f.getIva()), false);
                y = lineaTotal(cs, xLbl, xVal, y, "TOTAL", monto(f.getTotal()), true);

                boolean credito = "CREDITO".equalsIgnoreCase(nn(f.getTipoPago()));
                if (credito) {
                    y = lineaTotal(cs, xLbl, xVal, y, "Entrada", monto(f.getEntrada()), false);
                    y = lineaTotal(cs, xLbl, xVal, y, "Financiado", monto(f.getMontoFinanciado()), false);
                    y = lineaTotal(cs, xLbl, xVal, y, "Cuotas", String.valueOf(f.getNumCuotas()), false);
                    y = lineaTotal(cs, xLbl, xVal, y, "Tasa mensual", tasaPct(f.getTasaInteres()), false);
                }

                // ----- Panel de validacion (QR simulado) -----
                dibujarQrFalso(cs, codigoR, 60, 95, 110);
                escribirTexto(cs, PDType1Font.HELVETICA_BOLD, 10, 185, 175,
                        "PASE DIGITAL", AZUL);
                escribirTexto(cs, PDType1Font.HELVETICA, 9, 185, 160,
                        "Codigo interno: " + codigoR, TEXTO);
                escribirTexto(cs, PDType1Font.HELVETICA_OBLIQUE, 8, 185, 146,
                        "QR simulado de acceso. Entrada valida solo para", SUAVE);
                escribirTexto(cs, PDType1Font.HELVETICA_OBLIQUE, 8, 185, 135,
                        "fines demostrativos. Presentalo al ingreso.", SUAVE);

                pie(cs, "TICKETPREMIUM FIFA 2026 | " + codigoR
                        + " | FACTURA #" + f.getIdFactura());
            }

            // ----- Pagina 2: tabla de amortizacion (solo CREDITO) -----
            List<Cuota> amort = f.getAmortizacion();
            if ("CREDITO".equalsIgnoreCase(nn(f.getTipoPago()))
                    && amort != null && !amort.isEmpty()) {
                agregarPaginaAmortizacion(doc, logo, f, amort, codigoR);
            }

            doc.save(out);
            return out.toByteArray();
        }
    }

    private static void agregarPaginaAmortizacion(PDDocument doc, PDImageXObject logo,
                                                  Factura f, List<Cuota> amort,
                                                  String codigoR) throws IOException {
        int i = 0;
        while (i < amort.size()) {
            PDPage page = new PDPage(PDRectangle.A4);
            doc.addPage(page);
            try (PDPageContentStream cs = new PDPageContentStream(doc, page)) {
                dibujarMarco(cs);
                cabecera(cs, logo, "TABLA DE AMORTIZACION (PAGO A CREDITO)",
                        codigoR, "Factura #" + f.getIdFactura());

                float y = 730;
                escribirTexto(cs, PDType1Font.HELVETICA_BOLD, 11, 52, y,
                        "Financiado: " + monto(f.getMontoFinanciado())
                        + "   Cuotas: " + f.getNumCuotas()
                        + "   Tasa mensual: " + tasaPct(f.getTasaInteres()), TEXTO);
                y -= 24;

                float[] xs = { 52, 110, 230, 310, 390, 470 };
                y = filaCabecera(cs, y, xs,
                        "CUOTA", "VENCIMIENTO", "VALOR", "INTERES", "CAPITAL", "SALDO");

                boolean zebra = false;
                while (i < amort.size() && y > 80) {
                    Cuota c = amort.get(i++);
                    if (zebra) franja(cs, y);
                    zebra = !zebra;
                    escribirTexto(cs, PDType1Font.HELVETICA, 9, xs[0], y,
                            String.valueOf(c.getNumCuota()), TEXTO);
                    escribirTexto(cs, PDType1Font.HELVETICA, 9, xs[1], y,
                            nn(c.getFechaVencimiento()), TEXTO);
                    escribirTexto(cs, PDType1Font.HELVETICA, 9, xs[2], y,
                            monto(c.getCuota()), TEXTO);
                    escribirTexto(cs, PDType1Font.HELVETICA, 9, xs[3], y,
                            monto(c.getInteres()), TEXTO);
                    escribirTexto(cs, PDType1Font.HELVETICA, 9, xs[4], y,
                            monto(c.getAbonoCapital()), TEXTO);
                    escribirTexto(cs, PDType1Font.HELVETICA, 9, xs[5], y,
                            monto(c.getSaldoFinal()), TEXTO);
                    y -= 18;
                }
                pie(cs, "TICKETPREMIUM FIFA 2026 | Amortizacion factura #"
                        + f.getIdFactura());
            }
        }
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
                cabecera(cs, logo, "REPORTE DE VENTAS POR PARTIDO", "", "");

                escribirTexto(cs, PDType1Font.HELVETICA_BOLD, 11, 52, 730,
                        "Partido: ", AZUL);
                escribirTexto(cs, PDType1Font.HELVETICA, 11, 100, 730,
                        partido == null ? "(sin partido)" : partido, TEXTO);

                String fechaGen = LocalDateTime.now().format(
                        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
                escribirTexto(cs, PDType1Font.HELVETICA_BOLD, 11, 52, 712,
                        "Generado: ", AZUL);
                escribirTexto(cs, PDType1Font.HELVETICA, 11, 110, 712,
                        fechaGen, TEXTO);
                escribirTexto(cs, PDType1Font.HELVETICA_BOLD, 11, 280, 712,
                        "Operador: ", AZUL);
                escribirTexto(cs, PDType1Font.HELVETICA, 11, 338, 712,
                        operador == null ? "-" : operador, TEXTO);

                float[] xs = { 52, 220, 340 };
                float y = filaCabecera(cs, 680, xs,
                        "LOCALIDAD", "VENDIDOS", "TOTAL RECAUDADO");

                int totalVendidos = 0;
                BigDecimal totalRec = BigDecimal.ZERO;
                boolean zebra = false;
                if (filas == null || filas.isEmpty()) {
                    escribirTexto(cs, PDType1Font.HELVETICA_OBLIQUE, 10, xs[0], y,
                            "(sin ventas registradas)", SUAVE);
                    y -= 22;
                } else {
                    for (ResumenLocalidad r : filas) {
                        if (zebra) franja(cs, y);
                        zebra = !zebra;
                        escribirTexto(cs, PDType1Font.HELVETICA, 10, xs[0], y,
                                limitar(r.getLocalidad(), 30), TEXTO);
                        escribirTexto(cs, PDType1Font.HELVETICA, 10, xs[1], y,
                                String.valueOf(r.getVendidos()), TEXTO);
                        escribirTexto(cs, PDType1Font.HELVETICA, 10, xs[2], y,
                                monto(r.getTotalRecaudado()), TEXTO);
                        totalVendidos += r.getVendidos();
                        if (r.getTotalRecaudado() != null) {
                            totalRec = totalRec.add(r.getTotalRecaudado());
                        }
                        y -= 22;
                        if (y < 120) break;   // hoja unica
                    }
                }

                y -= 8;
                cs.setNonStrokingColor(AZUL);
                cs.addRect(48, y - 10, 511, 28);
                cs.fill();
                escribirTexto(cs, PDType1Font.HELVETICA_BOLD, 11, xs[0], y + 2,
                        "TOTAL", Color.WHITE);
                escribirTexto(cs, PDType1Font.HELVETICA_BOLD, 11, xs[1], y + 2,
                        String.valueOf(totalVendidos), Color.WHITE);
                escribirTexto(cs, PDType1Font.HELVETICA_BOLD, 11, xs[2], y + 2,
                        monto(totalRec), Color.WHITE);

                pie(cs, "TICKETPREMIUM FIFA 2026 GR06 | Reporte de ventas");
            }
            doc.save(out);
            return out.toByteArray();
        }
    }

    // ============================================================================
    // Bloques compartidos
    // ============================================================================
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

    private static void cabecera(PDPageContentStream cs, PDImageXObject logo,
                                 String subtitulo, String ref1, String ref2) throws IOException {
        cs.setNonStrokingColor(AZUL);
        cs.addRect(36, 760, 523, 46);
        cs.fill();
        if (logo != null) cs.drawImage(logo, 44, 762, 42, 42);

        escribirTexto(cs, PDType1Font.HELVETICA_BOLD, 20, 96, 788,
                "TICKETPREMIUM", Color.WHITE);
        escribirTexto(cs, PDType1Font.HELVETICA_BOLD, 11, 96, 770,
                subtitulo, Color.WHITE);

        float xRight = 549;
        if (ref1 != null && !ref1.isBlank()) {
            textoDerecha(cs, PDType1Font.HELVETICA_OBLIQUE, 9, xRight, 790, ref1);
        }
        if (ref2 != null && !ref2.isBlank()) {
            textoDerecha(cs, PDType1Font.HELVETICA_OBLIQUE, 9, xRight, 776, ref2);
        }
    }

    /** Texto alineado a la derecha (blanco, para la cabecera). */
    private static void textoDerecha(PDPageContentStream cs, PDType1Font fuente, int tam,
                                     float xDerecha, int y, String texto) throws IOException {
        float ancho = fuente.getStringWidth(texto) / 1000f * tam;
        escribirTexto(cs, fuente, tam, xDerecha - ancho, y, texto, Color.WHITE);
    }

    private static void pie(PDPageContentStream cs, String texto) throws IOException {
        cs.setNonStrokingColor(AZUL);
        cs.addRect(36, 36, 523, 38);
        cs.fill();
        escribirTexto(cs, PDType1Font.HELVETICA_BOLD, 9, 52, 52, texto, Color.WHITE);
        escribirTexto(cs, PDType1Font.HELVETICA_OBLIQUE, 8, 52, 41,
                "Documento generado automaticamente por el cliente escritorio.", Color.WHITE);
    }

    /** Cabecera azul de tabla; devuelve el Y de la primera fila de datos. */
    private static float filaCabecera(PDPageContentStream cs, float y, float[] xs,
                                      String... titulos) throws IOException {
        cs.setNonStrokingColor(AZUL);
        cs.addRect(48, y - 6, 511, 22);
        cs.fill();
        for (int i = 0; i < titulos.length && i < xs.length; i++) {
            escribirTexto(cs, PDType1Font.HELVETICA_BOLD, 9, xs[i], y, titulos[i], Color.WHITE);
        }
        return y - 22;
    }

    private static void franja(PDPageContentStream cs, float y) throws IOException {
        cs.setNonStrokingColor(ZEBRA);
        cs.addRect(48, y - 6, 511, 18);
        cs.fill();
    }

    /** Dos pares etiqueta/valor en una misma linea. */
    private static float parDato(PDPageContentStream cs, String l1, String v1,
                                 String l2, String v2, float y) throws IOException {
        escribirTexto(cs, PDType1Font.HELVETICA_BOLD, 10, 52, y, l1 + ":", TEXTO);
        escribirTexto(cs, PDType1Font.HELVETICA, 10, 130, y, limitar(v1, 30), TEXTO);
        escribirTexto(cs, PDType1Font.HELVETICA_BOLD, 10, 300, y, l2 + ":", TEXTO);
        escribirTexto(cs, PDType1Font.HELVETICA, 10, 390, y, limitar(v2, 26), TEXTO);
        return y - 18;
    }

    private static float lineaTotal(PDPageContentStream cs, float xLbl, float xVal,
                                    float y, String etiqueta, String valor,
                                    boolean destacado) throws IOException {
        if (destacado) {
            cs.setNonStrokingColor(AZUL);
            cs.addRect(xLbl - 8, y - 6, 215, 20);
            cs.fill();
            escribirTexto(cs, PDType1Font.HELVETICA_BOLD, 11, xLbl, y, etiqueta, Color.WHITE);
            escribirTexto(cs, PDType1Font.HELVETICA_BOLD, 11, xVal, y, valor, Color.WHITE);
        } else {
            escribirTexto(cs, PDType1Font.HELVETICA_BOLD, 10, xLbl, y, etiqueta, TEXTO);
            escribirTexto(cs, PDType1Font.HELVETICA, 10, xVal, y, valor, TEXTO);
        }
        return y - 20;
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

    private static void dibujarMarcador(PDPageContentStream cs, float x, float y,
                                        float module) throws IOException {
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
        cs.showText(text == null ? "" : text);
        cs.endText();
    }

    private static String monto(BigDecimal v) {
        return v == null ? "0.00" : String.format("%.2f", v);
    }

    /** Tasa decimal (0.02) -> "2.00 %". */
    private static String tasaPct(BigDecimal tasa) {
        if (tasa == null) return "0.00 %";
        return String.format("%.2f %%", tasa.multiply(BigDecimal.valueOf(100)));
    }

    private static String nn(String s) { return s == null ? "" : s; }

    private static String limitar(String value, int max) {
        if (value == null) return "";
        String limpio = value.replace("\n", " ").replace("\r", " ");
        if (limpio.length() <= max) return limpio;
        return limpio.substring(0, Math.max(0, max - 3)) + "...";
    }
}
