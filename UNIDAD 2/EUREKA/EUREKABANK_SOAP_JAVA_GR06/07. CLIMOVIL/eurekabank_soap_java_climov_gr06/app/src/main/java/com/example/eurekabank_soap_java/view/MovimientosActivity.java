package com.example.eurekabank_soap_java.view;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.graphics.pdf.PdfDocument;
import android.os.Bundle;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.eurekabank_soap_java.controlador.BancoController;
import com.example.eurekabank_soap_java.modelo.Movimiento;
import com.example.eurekabank_soap_java.soap.Async;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/** Estado de cuenta — UI mejorada, ingresos/egresos y ojito de conversión. */
public class MovimientosActivity extends AppCompatActivity {

    private static final int BG     = Color.parseColor("#0F172A");
    private static final int CARD   = Color.parseColor("#1E293B");
    private static final int ACCENT = Color.parseColor("#38BDF8");
    private static final int TEXT   = Color.parseColor("#E2E8F0");
    private static final int SUB    = Color.parseColor("#94A3B8");
    private static final int GREEN  = Color.parseColor("#4ADE80");
    private static final int RED    = Color.parseColor("#F87171");
    private static final Set<String> INGRESOS =
            new HashSet<>(Arrays.asList("001", "003", "005", "008"));

    private final BancoController ctrl = new BancoController();
    private LinearLayout cont;
    private String cuenta;
    private List<Movimiento> datos;

    @Override protected void onCreate(Bundle b) {
        super.onCreate(b);
        cuenta = getIntent().getStringExtra("cuenta");

        ScrollView sv = new ScrollView(this);
        sv.setBackgroundColor(BG);
        cont = new LinearLayout(this);
        cont.setOrientation(LinearLayout.VERTICAL);
        cont.setPadding(36, 48, 36, 48);
        sv.addView(cont);
        setContentView(sv);

        TextView t = new TextView(this);
        t.setText("Estado de cuenta");
        t.setTextColor(ACCENT);
        t.setTextSize(22);
        t.setTypeface(null, Typeface.BOLD);
        cont.addView(t);

        TextView ct = new TextView(this);
        ct.setText("Cuenta " + cuenta);
        ct.setTextColor(SUB);
        ct.setPadding(0, 2, 0, 16);
        cont.addView(ct);

        Async.run(() -> ctrl.movimientos(cuenta),
            this::pintar,
            e -> Toast.makeText(this, "Error: " + e.getMessage(),
                    Toast.LENGTH_LONG).show());
    }

    private LinearLayout card() {
        LinearLayout c = new LinearLayout(this);
        c.setOrientation(LinearLayout.VERTICAL);
        GradientDrawable g = new GradientDrawable();
        g.setColor(CARD);
        g.setCornerRadius(24f);
        c.setBackground(g);
        c.setPadding(26, 20, 26, 20);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.setMargins(0, 8, 0, 8);
        c.setLayoutParams(lp);
        return c;
    }

    private void pintar(List<Movimiento> ms) {
        datos = ms;
        if (ms == null || ms.isEmpty()) {
            TextView v = new TextView(this);
            v.setText("Sin movimientos o sin acceso.");
            v.setTextColor(TEXT);
            cont.addView(v);
            agregarVolver();
            return;
        }
        double tin = 0, tout = 0;
        for (final Movimiento m : ms) {
            boolean in = INGRESOS.contains(m.getTipoCodigo());
            if (in) tin += m.getImporte(); else tout += m.getImporte();

            LinearLayout c = card();
            TextView l1 = new TextView(this);
            l1.setText("#" + m.getNumero() + "   " + m.getFecha());
            l1.setTextColor(SUB);
            c.addView(l1);

            TextView l2 = new TextView(this);
            l2.setText(m.getTipoDescripcion());
            l2.setTextColor(TEXT);
            l2.setTypeface(null, Typeface.BOLD);
            c.addView(l2);

            TextView l3 = new TextView(this);
            l3.setText((in ? "CRÉDITO  + " : "DÉBITO  - ")
                    + String.format("%,.2f", m.getImporte())
                    + (m.getCuentaReferencia() == null ? ""
                       : "    Ref: " + m.getCuentaReferencia()));
            l3.setTextColor(in ? GREEN : RED);
            l3.setTextSize(17);
            l3.setTypeface(null, Typeface.BOLD);
            c.addView(l3);

            if (m.tieneConversion()) {
                Button ojo = new Button(this);
                ojo.setText("👁 Ver conversión");
                ojo.setAllCaps(false);
                ojo.setBackgroundColor(Color.parseColor("#0B1220"));
                ojo.setTextColor(ACCENT);
                final TextView det = new TextView(this);
                double io = m.getImporteOrigen() == null ? 0 : m.getImporteOrigen();
                String mo = "02".equals(m.getMonedaOrigen()) ? "Dólares" : "Soles";
                det.setText(String.format(
                        "Monto original: %,.2f %s  ×  tasa %s  =  %,.2f (moneda de la cuenta)",
                        io, mo, String.valueOf(m.getTasaAplicada()), m.getImporte()));
                det.setTextColor(SUB);
                det.setVisibility(android.view.View.GONE);
                det.setPadding(0, 8, 0, 0);
                ojo.setOnClickListener(x -> det.setVisibility(
                        det.getVisibility() == android.view.View.GONE
                        ? android.view.View.VISIBLE : android.view.View.GONE));
                c.addView(ojo);
                c.addView(det);
            }
            cont.addView(c);
        }

        LinearLayout tot = card();
        TextView tt = new TextView(this);
        tt.setText("TOTALES");
        tt.setTextColor(ACCENT);
        tt.setTypeface(null, Typeface.BOLD);
        tot.addView(tt);
        TextView tv = new TextView(this);
        tv.setText("Créditos  + " + String.format("%,.2f", tin)
                + "\nDébitos   - " + String.format("%,.2f", tout)
                + "\nNeto        " + String.format("%,.2f", tin - tout));
        tv.setTextColor(TEXT);
        tot.addView(tv);
        cont.addView(tot);

        Button pdf = new Button(this);
        pdf.setText("Exportar PDF");
        pdf.setAllCaps(false);
        GradientDrawable g = new GradientDrawable();
        g.setColor(ACCENT); g.setCornerRadius(20f);
        pdf.setBackground(g);
        pdf.setTextColor(Color.parseColor("#04263A"));
        LinearLayout.LayoutParams lpp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        lpp.setMargins(0, 18, 0, 0);
        pdf.setLayoutParams(lpp);
        pdf.setOnClickListener(x -> exportarPdf());
        cont.addView(pdf);

        agregarVolver();
    }

    /** Genera el reporte (orden descendente) como PDF en almacenamiento de la app. */
    private void exportarPdf() {
        if (datos == null || datos.isEmpty()) {
            Toast.makeText(this, "No hay movimientos para exportar.",
                    Toast.LENGTH_LONG).show();
            return;
        }
        try {
            PdfDocument doc = new PdfDocument();
            Paint p = new Paint();
            Paint bold = new Paint();
            bold.setFakeBoldText(true);
            int W = 595, H = 842, margin = 40;
            int y = margin;
            PdfDocument.Page page = doc.startPage(
                    new PdfDocument.PageInfo.Builder(W, H, 1).create());
            Canvas cv = page.getCanvas();

            bold.setTextSize(18);
            cv.drawText("EUREKABANK GR06 - Estado de cuenta", margin, y, bold);
            y += 22;
            p.setTextSize(11);
            cv.drawText("Cuenta " + cuenta
                    + "   ·   Ordenado por fecha (descendente)", margin, y, p);
            y += 26;
            bold.setTextSize(11);
            cv.drawText("#   Fecha        Tipo                 Mov.      "
                    + "Crédito       Débito", margin, y, bold);
            y += 8;
            cv.drawLine(margin, y, W - margin, y, p);
            y += 16;

            double tin = 0, tout = 0;
            int pageNo = 1;
            for (Movimiento m : datos) {
                boolean in = INGRESOS.contains(m.getTipoCodigo());
                if (in) tin += m.getImporte(); else tout += m.getImporte();
                if (y > H - margin - 40) {
                    doc.finishPage(page);
                    pageNo++;
                    page = doc.startPage(new PdfDocument.PageInfo.Builder(
                            W, H, pageNo).create());
                    cv = page.getCanvas();
                    y = margin;
                }
                String linea = String.format("%-3d %-12s %-20s %-8s %11s %11s",
                        m.getNumero(),
                        m.getFecha() == null ? "" : m.getFecha(),
                        trunc(m.getTipoDescripcion(), 20),
                        in ? "CRÉDITO" : "DÉBITO",
                        in ? String.format("%,.2f", m.getImporte()) : "",
                        in ? "" : String.format("%,.2f", m.getImporte()));
                cv.drawText(linea, margin, y, p);
                y += 18;
                if (m.tieneConversion()) {
                    cv.drawText("    conv: "
                            + String.format("%,.2f", m.getImporteOrigen() == null
                                ? 0 : m.getImporteOrigen())
                            + " " + ("02".equals(m.getMonedaOrigen())
                                ? "USD" : "PEN")
                            + " x " + m.getTasaAplicada()
                            + " = " + String.format("%,.2f", m.getImporte()),
                            margin, y, p);
                    y += 16;
                }
            }
            y += 10;
            cv.drawText("TOTALES   Créditos + " + String.format("%,.2f", tin)
                    + "   Débitos - " + String.format("%,.2f", tout)
                    + "   Neto " + String.format("%,.2f", tin - tout),
                    margin, y, bold);
            doc.finishPage(page);

            File dir = getExternalFilesDir(null);
            File out = new File(dir, "EstadoCuenta_" + cuenta + "_"
                    + System.currentTimeMillis() + ".pdf");
            FileOutputStream fos = new FileOutputStream(out);
            doc.writeTo(fos);
            fos.close();
            doc.close();
            Toast.makeText(this, "PDF guardado en:\n" + out.getAbsolutePath(),
                    Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Toast.makeText(this, "Error exportando PDF: " + e.getMessage(),
                    Toast.LENGTH_LONG).show();
        }
    }

    private String trunc(String s, int n) {
        if (s == null) return "";
        return s.length() > n ? s.substring(0, n) : s;
    }

    private void agregarVolver() {
        Button volver = new Button(this);
        volver.setText("Volver");
        volver.setAllCaps(false);
        volver.setBackgroundColor(ACCENT);
        volver.setTextColor(Color.parseColor("#04263A"));
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.setMargins(0, 16, 0, 0);
        volver.setLayoutParams(lp);
        volver.setOnClickListener(x -> finish());
        cont.addView(volver);
    }
}
