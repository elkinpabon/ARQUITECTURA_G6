package ec.edu.monster.util;

import ec.edu.monster.ws.MovimientoModel;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Set;

/** Genera el estado de cuenta como archivo HTML (se abre e imprime a PDF). */
public final class ExportHtml {

    private static final Set<String> INGRESOS =
            Set.of("001", "003", "005", "008");

    private ExportHtml() { }

    /** Escribe el HTML y devuelve la ruta absoluta del archivo creado. */
    public static String estadoCuenta(String cuenta, String titular,
            String monedaCuenta, List<MovimientoModel> ms) throws IOException {

        StringBuilder h = new StringBuilder();
        h.append("<!DOCTYPE html><html lang='es'><head><meta charset='UTF-8'>")
         .append("<title>Estado de cuenta ").append(cuenta).append("</title><style>")
         .append("body{font-family:Segoe UI,Arial,sans-serif;margin:32px;color:#0f172a}")
         .append("h1{color:#0ea5e9;margin:0} .sub{color:#555;margin:4px 0 18px}")
         .append("table{width:100%;border-collapse:collapse;font-size:13px}")
         .append("th,td{border-bottom:1px solid #999;padding:8px;text-align:left}")
         .append("th{color:#0ea5e9} .in{color:#166534;font-weight:bold}")
         .append(".out{color:#991b1b;font-weight:bold} .tot{font-weight:bold}")
         .append(".conv{color:#555;font-size:11px}")
         .append("@media print{@page{margin:1.4cm}}</style></head><body>");
        h.append("<h1>EUREKABANK GR06</h1>")
         .append("<div class='sub'>Estado de cuenta &middot; Cliente Consola (Java)</div>");
        h.append("<p><b>Titular:</b> ").append(safe(titular))
         .append(" &nbsp; <b>Cuenta:</b> ").append(safe(cuenta))
         .append(" &nbsp; <b>Moneda:</b> ").append(Moneda.nombre(monedaCuenta))
         .append(" &nbsp; <b>Emitido:</b> ")
         .append(new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm")
                 .format(new java.util.Date())).append("</p>");
        h.append("<p class='sub'>Movimientos ordenados por fecha (descendente)</p>");
        h.append("<table><tr><th>#</th><th>Fecha</th><th>Tipo</th><th>Mov.</th>")
         .append("<th>Crédito</th><th>Débito</th><th>Cta.Ref.</th></tr>");

        double tin = 0, tout = 0;
        for (MovimientoModel m : ms) {
            boolean in = INGRESOS.contains(m.getCodigoTipoMovimiento());
            double imp = m.getImporteMovimiento();
            if (in) tin += imp; else tout += imp;
            h.append("<tr><td>").append(m.getNumeroMovimiento()).append("</td><td>")
             .append(safe(m.getFechaMovimiento())).append("</td><td>")
             .append(safe(m.getTipoDescripcion())).append("</td>")
             .append("<td class='").append(in ? "in" : "out").append("'>")
             .append(in ? "CRÉDITO" : "DÉBITO").append("</td>")
             .append("<td class='in'>").append(in ? f(imp) : "").append("</td>")
             .append("<td class='out'>").append(in ? "" : f(imp)).append("</td>")
             .append("<td>").append(m.getCuentaReferencia() == null
                     ? "-" : m.getCuentaReferencia()).append("</td></tr>");
            if (m.getMonedaOrigen() != null && !m.getMonedaOrigen().isBlank()) {
                h.append("<tr><td colspan='7' class='conv'>Conversión: ")
                 .append(f(m.getImporteOrigen() == null ? 0 : m.getImporteOrigen()))
                 .append(" ").append(Moneda.nombre(m.getMonedaOrigen()))
                 .append(" &times; tasa ").append(m.getTasaAplicada())
                 .append(" = ").append(f(imp)).append(" ")
                 .append(Moneda.nombre(monedaCuenta)).append("</td></tr>");
            }
        }
        h.append("<tr class='tot'><td colspan='4'>TOTALES</td>")
         .append("<td class='in'>").append(f(tin)).append("</td>")
         .append("<td class='out'>").append(f(tout)).append("</td><td></td></tr>");
        h.append("</table><p style='color:#888;font-size:11px;margin-top:24px'>")
         .append("Documento generado por EUREKABANK GR06 — Banca RESTFULL. Sin valor legal.")
         .append("</p></body></html>");

        String ruta = System.getProperty("user.home") + java.io.File.separator
                + "EstadoCuenta_" + cuenta + "_"
                + System.currentTimeMillis() + ".html";
        try (FileWriter w = new FileWriter(ruta)) {
            w.write(h.toString());
        }
        return ruta;
    }

    private static String f(double v) { return String.format("%,.2f", v); }

    private static String safe(String s) {
        return s == null ? "" : s.replace("&", "&amp;")
                .replace("<", "&lt;").replace(">", "&gt;");
    }
}
