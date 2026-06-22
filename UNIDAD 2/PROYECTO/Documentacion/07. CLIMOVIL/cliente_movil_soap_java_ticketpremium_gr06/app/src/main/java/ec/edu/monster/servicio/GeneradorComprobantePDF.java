package ec.edu.monster.servicio;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.pdf.PdfDocument;
import androidx.core.content.FileProvider;
import ec.edu.monster.modelo.ComprobanteCompra;
import ec.edu.monster.modelo.ResumenLocalidad;
import ec.edu.monster.R;
import java.io.File;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Genera los mismos PDFs que el web/escritorio pero con la API nativa de
 * Android (PdfDocument). A4 = 595x842 pt.
 */
public final class GeneradorComprobantePDF {

    private static final int A4_W = 595;
    private static final int A4_H = 842;

    private GeneradorComprobantePDF() { }

    // ============================================================================
    // API PUBLICA
    // ============================================================================
    public static File comprobante(Context ctx, ComprobanteCompra c) throws Exception {
        PdfDocument doc = new PdfDocument();
        PdfDocument.PageInfo info = new PdfDocument.PageInfo.Builder(A4_W, A4_H, 1).create();
        PdfDocument.Page page = doc.startPage(info);
        Canvas g = page.getCanvas();

        Bitmap logo = cargarLogo(ctx);
        marco(g);
        cabeceraComprobante(g, c, logo);
        detallesCompra(g, c);
        panelValidacion(g, c);
        pie(g, "TICKETPREMIUM | " + c.getCodigoR() + " | FACTURA #" + c.getIdFactura());

        doc.finishPage(page);

        String nombre = (c.getCodigoR() + "-" + c.getIdFactura())
                .replaceAll("[^A-Za-z0-9_-]", "_") + ".pdf";
        File f = nuevoArchivo(ctx, nombre);
        try (FileOutputStream fos = new FileOutputStream(f)) { doc.writeTo(fos); }
        doc.close();
        if (logo != null && !logo.isRecycled()) logo.recycle();
        return f;
    }

    public static File reporteVentas(Context ctx, String partido,
                                     List<ResumenLocalidad> filas, String operador) throws Exception {
        PdfDocument doc = new PdfDocument();
        PdfDocument.PageInfo info = new PdfDocument.PageInfo.Builder(A4_W, A4_H, 1).create();
        PdfDocument.Page page = doc.startPage(info);
        Canvas g = page.getCanvas();

        Bitmap logo = cargarLogo(ctx);
        marco(g);
        cabeceraReporte(g, logo);

        Paint pBold = paint(11, true, 0xFF132040);
        Paint pTxt  = paint(11, false, 0xFF343D4B);

        g.drawText("Partido: ", 52, 90, pBold);
        g.drawText(safe(partido, "(sin partido)"), 100, 90, pTxt);

        String gen = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(new Date());
        g.drawText("Generado: ", 52, 108, pBold);
        g.drawText(gen, 116, 108, pTxt);

        g.drawText("Operador: ", 280, 108, pBold);
        g.drawText(safe(operador, "-"), 344, 108, pTxt);

        // Tabla
        float[] xs = { 52, 220, 340, 460 };
        float yh = 138;
        rect(g, 48, yh - 16, 511, 24, 0xFF132040);
        g.drawText("LOCALIDAD",        xs[0], yh, paint(10, true, Color.WHITE));
        g.drawText("VENDIDOS",         xs[1], yh, paint(10, true, Color.WHITE));
        g.drawText("TOTAL RECAUDADO",  xs[2], yh, paint(10, true, Color.WHITE));

        float y = yh + 24;
        int totalVend = 0;
        BigDecimal totalRec = BigDecimal.ZERO;
        boolean zebra = false;

        if (filas == null || filas.isEmpty()) {
            g.drawText("(sin ventas registradas)", xs[0], y, paint(10, false, 0xFF5F687A));
            y += 22;
        } else {
            for (ResumenLocalidad r : filas) {
                if (zebra) rect(g, 48, y - 16, 511, 22, 0xFFF7F9FC);
                zebra = !zebra;
                g.drawText(limitar(r.getLocalidad(), 30), xs[0], y, paint(10, false, 0xFF343D4B));
                g.drawText(String.valueOf(r.getVendidos()), xs[1], y, paint(10, false, 0xFF343D4B));
                g.drawText(monto(r.getTotalRecaudado()),    xs[2], y, paint(10, false, 0xFF343D4B));
                totalVend += r.getVendidos();
                totalRec = totalRec.add(r.getTotalRecaudado());
                y += 22;
                if (y > A4_H - 100) break;
            }
        }

        // Totales
        y += 8;
        rect(g, 48, y - 16, 511, 26, 0xFF132040);
        g.drawText("TOTAL",                   xs[0], y, paint(11, true, Color.WHITE));
        g.drawText(String.valueOf(totalVend), xs[1], y, paint(11, true, Color.WHITE));
        g.drawText(monto(totalRec),           xs[2], y, paint(11, true, Color.WHITE));

        pie(g, "TICKETPREMIUM GR06 | Reporte de ventas");
        doc.finishPage(page);

        String nombre = ("reporte-" + safe(partido, "partido"))
                .replaceAll("[^A-Za-z0-9_-]", "_") + ".pdf";
        File f = nuevoArchivo(ctx, nombre);
        try (FileOutputStream fos = new FileOutputStream(f)) { doc.writeTo(fos); }
        doc.close();
        if (logo != null && !logo.isRecycled()) logo.recycle();
        return f;
    }

    /** Uri compartible por FileProvider para abrir / enviar el PDF. */
    public static android.net.Uri uriCompartible(Context ctx, File pdf) {
        return FileProvider.getUriForFile(ctx, ctx.getPackageName() + ".fileprovider", pdf);
    }

    // ============================================================================
    // CABECERAS / SECCIONES
    // ============================================================================
    private static void marco(Canvas g) {
        Paint p = new Paint();
        p.setStyle(Paint.Style.STROKE);
        p.setColor(0xFF555555);
        p.setStrokeWidth(1);
        g.drawRect(20, 20, A4_W - 20, A4_H - 20, p);
    }

    private static void cabeceraComprobante(Canvas g, ComprobanteCompra c, Bitmap logo) {
        rect(g, 20, 20, A4_W - 40, 56, 0xFF132040);
        if (logo != null) g.drawBitmap(logo, null, new Rect(28, 26, 70, 68), null);
        g.drawText("TICKETPREMIUM",                           80, 50, paint(22, true, Color.WHITE));
        g.drawText("ENTRADA DIGITAL / COMPROBANTE DE COMPRA", 80, 68, paint(12, true, Color.WHITE));

        // Sub-info en dos lineas alineadas a la derecha (no se sale del banner)
        float xRight = A4_W - 30;        // 10pt de padding desde x=A4_W-20
        textoDerecha(g, c.getCodigoR(),                         xRight, 38, paint(9, false, Color.WHITE));
        textoDerecha(g, "Factura #" + c.getIdFactura(),         xRight, 52, paint(9, false, Color.WHITE));

        rect(g, 20, 76, A4_W - 40, 1, 0xFFE6ECF8);
    }

    /** Dibuja texto alineado a la derecha (su borde derecho queda en xRight). */
    private static void textoDerecha(Canvas g, String texto, float xRight, float y, Paint p) {
        float w = p.measureText(texto);
        g.drawText(texto, xRight - w, y, p);
    }

    private static void cabeceraReporte(Canvas g, Bitmap logo) {
        rect(g, 20, 20, A4_W - 40, 56, 0xFF132040);
        if (logo != null) g.drawBitmap(logo, null, new Rect(28, 26, 70, 68), null);
        g.drawText("TICKETPREMIUM",                  80, 50, paint(22, true, Color.WHITE));
        g.drawText("REPORTE DE VENTAS POR PARTIDO",  80, 70, paint(12, true, Color.WHITE));
    }

    private static void detallesCompra(Canvas g, ComprobanteCompra c) {
        rect(g, 32, 122, 286, 580, 0xFFF7F9FC);
        rectStroke(g, 32, 122, 286, 580, 0xFFDAE0E9);
        g.drawText("DATOS DE LA COMPRA",                       46, 144, paint(13, true, 0xFF132040));
        g.drawText("Verifica que los datos coincidan con tu boleto.",
                                                              46, 158, paint(8, false, 0xFF5F687A));
        float y = 180;
        y = filaDato(g, "Codigo R",  c.getCodigoR(),                  y);
        y = filaDato(g, "Factura",   "#" + c.getIdFactura(),          y);
        y = filaDato(g, "Fecha",     c.getFecha(),                    y);
        y = filaDato(g, "Cliente",   c.getUsuario(),                  y);
        y = filaDato(g, "Partido",   c.getPartido(),                  y);
        y = filaDato(g, "Localidad", c.getLocalidad(),                y);
        y = filaDato(g, "Cantidad",  String.valueOf(c.getCantidad()), y);
        y = filaDato(g, "Subtotal",  monto(c.getSubtotal()),          y);
        y = filaDato(g, "IVA",       monto(c.getIva()),               y);

        rect(g, 46, y - 8, 260, 32, 0xFF132040);
        g.drawText("TOTAL  " + monto(c.getTotal()), 56, y + 12, paint(13, true, Color.WHITE));

        g.drawText("Entrada simulada. Presenta este comprobante al ingreso.",
                   46, 692, paint(10, false, 0xFF5F687A));
    }

    private static float filaDato(Canvas g, String etiqueta, String valor, float y) {
        rect(g, 46, y - 14, 86, 26, 0xFFE8EDF5);
        rect(g, 132, y - 14, 174, 26, Color.WHITE);
        rectStroke(g, 46, y - 14, 260, 26, 0xFFDAE0E9);
        g.drawText(etiqueta.toUpperCase(), 52, y + 4, paint(9, true, 0xFF132040));
        g.drawText(limitar(valor, 26),     138, y + 4, paint(9, false, 0xFF343D4B));
        return y + 42;
    }

    private static void panelValidacion(Canvas g, ComprobanteCompra c) {
        rect(g, 332, 130, 160, 560, 0xFFF2F5FA);
        rectStroke(g, 332, 130, 160, 560, 0xFFDAE0E9);
        g.drawText("VALIDACION",         350, 150, paint(12, true, 0xFF132040));
        g.drawText("QR simulado de acceso", 350, 164, paint(8, false, 0xFF5F687A));
        dibujarQrFalso(g, c.getCodigoR() + "-" + c.getIdFactura(), 354, 178, 116);

        g.drawText("PASE DIGITAL",                       352, 320, paint(10, true, 0xFF132040));
        g.drawText("Codigo interno: " + c.getCodigoR(),  352, 334, paint(8, false, 0xFF464E5C));
        rect(g, 346, 342, 128, 2, 0xFF132040);
        g.drawText("Entrada valida solo para fines",     352, 360, paint(8, false, 0xFF5F687A));
        g.drawText("demostrativos.",                     352, 372, paint(8, false, 0xFF5F687A));
    }

    private static void pie(Canvas g, String linea) {
        rect(g, 20, A4_H - 60, A4_W - 40, 40, 0xFF132040);
        g.drawText(linea, 36, A4_H - 38, paint(9, true, Color.WHITE));
        g.drawText("Documento generado automaticamente.", 36, A4_H - 26, paint(8, false, Color.WHITE));
    }

    // ============================================================================
    // QR falso (mismo algoritmo que web/escritorio)
    // ============================================================================
    private static void dibujarQrFalso(Canvas g, String seed, float x, float y, float size) {
        int modules = 21;
        float module = size / modules;
        int hash = seed.hashCode();

        Paint negro = new Paint();
        negro.setColor(Color.BLACK);
        Paint blanco = new Paint();
        blanco.setColor(Color.WHITE);
        Paint borde = new Paint();
        borde.setStyle(Paint.Style.STROKE);
        borde.setColor(Color.BLACK);
        g.drawRect(x, y, x + size, y + size, borde);

        dibujarMarcador(g, x, y, module);
        dibujarMarcador(g, x + size - module * 7, y, module);
        dibujarMarcador(g, x, y + size - module * 7, module);
        dibujarMarcador(g, x + size - module * 7, y + size - module * 7, module);

        for (int fila = 0; fila < modules; fila++) {
            for (int columna = 0; columna < modules; columna++) {
                if (esZonaMarcador(fila, columna, modules)) continue;
                int bit = Math.abs(hash + fila * 31 + columna * 17 + (fila * columna * 7));
                if ((bit % 3) != 0) {
                    float px = x + columna * module;
                    float py = y + fila * module;
                    g.drawRect(px, py, px + module, py + module, negro);
                }
            }
        }
        g.drawRect(x + module * 8, y + module * 8,
                   x + module * 13, y + module * 13, blanco);
    }

    private static void dibujarMarcador(Canvas g, float x, float y, float module) {
        Paint negro = new Paint(); negro.setColor(Color.BLACK);
        Paint blanco = new Paint(); blanco.setColor(Color.WHITE);
        g.drawRect(x, y, x + module * 7, y + module * 7, negro);
        g.drawRect(x + module, y + module, x + module * 6, y + module * 6, blanco);
        g.drawRect(x + module * 2, y + module * 2, x + module * 5, y + module * 5, negro);
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

    // ============================================================================
    // Util
    // ============================================================================
    private static Paint paint(int sizeSp, boolean bold, int color) {
        Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);
        p.setColor(color);
        p.setTextSize(sizeSp);
        p.setTypeface(Typeface.create(Typeface.SANS_SERIF, bold ? Typeface.BOLD : Typeface.NORMAL));
        return p;
    }

    private static void rect(Canvas g, float x, float y, float w, float h, int color) {
        Paint p = new Paint();
        p.setColor(color);
        g.drawRect(new RectF(x, y, x + w, y + h), p);
    }

    private static void rectStroke(Canvas g, float x, float y, float w, float h, int color) {
        Paint p = new Paint();
        p.setStyle(Paint.Style.STROKE);
        p.setColor(color);
        g.drawRect(new RectF(x, y, x + w, y + h), p);
    }

    private static Bitmap cargarLogo(Context ctx) {
        try {
            return BitmapFactory.decodeResource(ctx.getResources(), R.drawable.moster);
        } catch (Exception e) {
            return null;
        }
    }

    private static File nuevoArchivo(Context ctx, String nombre) {
        File dir = new File(ctx.getFilesDir(), "comprobantes");
        if (!dir.exists()) dir.mkdirs();
        return new File(dir, nombre);
    }

    private static String monto(BigDecimal v) {
        return v == null ? "0.00" : String.format(Locale.US, "%.2f", v);
    }

    private static String limitar(String s, int max) {
        if (s == null) return "";
        String x = s.replace("\n", " ").replace("\r", " ");
        if (x.length() <= max) return x;
        return x.substring(0, Math.max(0, max - 3)) + "...";
    }

    private static String safe(String s, String def) {
        return s == null || s.trim().isEmpty() ? def : s;
    }
}
