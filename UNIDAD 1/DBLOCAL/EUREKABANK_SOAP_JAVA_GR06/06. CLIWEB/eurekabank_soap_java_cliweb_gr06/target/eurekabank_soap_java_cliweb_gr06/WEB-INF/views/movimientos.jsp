<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="ec.edu.monster.cliweb.ws.MovimientoModel" %>
<%@ page import="ec.edu.monster.cliweb.ws.CuentaResumen" %>
<%@ page import="ec.edu.monster.cliweb.util.Moneda" %>
<%
    List<MovimientoModel> movs = (List<MovimientoModel>) request.getAttribute("movimientos");
    String cuentaSel = String.valueOf(request.getAttribute("cuenta"));
    java.util.Set<String> ingresos =
            new java.util.HashSet<>(java.util.Arrays.asList("001","003","005","008"));

    // Datos del titular: se buscan en la lista que dejó la búsqueda de cliente
    List<CuentaResumen> cacheCtas =
            (List<CuentaResumen>) session.getAttribute("cuentasCliente");
    String titular = "-", moneda = "-", saldoCta = null;
    if (cacheCtas != null) {
        for (CuentaResumen c : cacheCtas) {
            if (c.getCodigoCuenta().equals(cuentaSel)) {
                titular = c.getNombreCliente();
                moneda = c.getMoneda();
                saldoCta = String.format("%,.2f", c.getSaldo());
                break;
            }
        }
    }

    double totIn = 0, totOut = 0;
    if (movs != null) {
        for (MovimientoModel m : movs) {
            if (ingresos.contains(m.getCodigoTipoMovimiento())) totIn += m.getImporteMovimiento();
            else totOut += m.getImporteMovimiento();
        }
    }
    String hoy = new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm")
            .format(new java.util.Date());
%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>EUREKABANK - Estado de cuenta</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/styles.css?v=4">
</head>
<body>
<div class="wrap">
    <div class="card printable">
        <div class="topbar">
            <div class="brand">
                <img src="${pageContext.request.contextPath}/img/moster.webp" alt="Monster">
                <h1>EUREKABANK GR06</h1>
            </div>
            <span class="user no-print">Usuario: <strong>${sessionScope.usuario}</strong></span>
            <span class="print-only">Estado de cuenta</span>
        </div>

        <div class="statement-head">
            <div class="col">
                <span class="k">Titular</span><span class="v"><%= titular %></span>
            </div>
            <div class="col">
                <span class="k">Cuenta</span><span class="v"><%= cuentaSel %></span>
            </div>
            <div class="col">
                <span class="k">Moneda</span><span class="v"><%= Moneda.nombre(moneda) %></span>
            </div>
            <div class="col">
                <span class="k">Saldo actual</span>
                <span class="v"><%= saldoCta == null ? "-" : saldoCta %></span>
            </div>
            <div class="col">
                <span class="k">Emitido</span><span class="v"><%= hoy %></span>
            </div>
        </div>

        <% if (request.getAttribute("error") != null) { %>
            <div class="alert err"><%= request.getAttribute("error") %></div>
        <% } %>

        <% if (movs == null || movs.isEmpty()) { %>
            <div class="alert err">No se encontraron movimientos.</div>
        <% } else { %>
            <div class="summary">
                <div class="pill"><div class="k">Movimientos</div>
                    <div class="v"><%= movs.size() %></div></div>
                <div class="pill"><div class="k">Total créditos</div>
                    <div class="v in">+ <%= String.format("%,.2f", totIn) %></div></div>
                <div class="pill"><div class="k">Total débitos</div>
                    <div class="v out">- <%= String.format("%,.2f", totOut) %></div></div>
                <div class="pill"><div class="k">Neto</div>
                    <div class="v"><%= String.format("%,.2f", totIn - totOut) %></div></div>
            </div>

            <div class="table-scroll">
            <table>
                <tr>
                    <th>#</th><th>Fecha</th><th>Cuenta</th><th>Tipo</th>
                    <th>Mov.</th>
                    <th style="text-align:right;">Crédito</th>
                    <th style="text-align:right;">Débito</th>
                    <th>Cta. Ref.</th>
                    <th class="no-print">Detalle</th>
                </tr>
                <% int idx = 0;
                   for (MovimientoModel m : movs) {
                       idx++;
                       boolean in = ingresos.contains(m.getCodigoTipoMovimiento());
                       String imp = String.format("%,.2f", m.getImporteMovimiento());
                       boolean conv = m.getMonedaOrigen() != null
                               && !m.getMonedaOrigen().isBlank(); %>
                    <tr>
                        <td><%= m.getNumeroMovimiento() %></td>
                        <td><%= m.getFechaMovimiento() %></td>
                        <td><%= cuentaSel %></td>
                        <td><span class="tag <%= in ? "in" : "out" %>"><%= m.getTipoDescripcion() %></span></td>
                        <td><strong style="color:<%= in ? "#16a34a" : "#dc2626" %>;"><%= in ? "CRÉDITO" : "DÉBITO" %></strong></td>
                        <td style="text-align:right;color:#4ade80;font-weight:700;">
                            <%= in ? "+ " + imp : "" %></td>
                        <td style="text-align:right;color:#f87171;font-weight:700;">
                            <%= in ? "" : "- " + imp %></td>
                        <td><%= m.getCuentaReferencia() == null ? "-" : m.getCuentaReferencia() %></td>
                        <td class="no-print" style="text-align:center;">
                            <% if (conv) { %>
                                <button type="button" title="Ver conversión de moneda"
                                        onclick="document.getElementById('cv<%= idx %>').classList.toggle('hide')"
                                        style="width:auto;margin:0;padding:4px 10px;">&#128065;</button>
                            <% } else { %>&mdash;<% } %>
                        </td>
                    </tr>
                    <% if (conv) {
                           double io = m.getImporteOrigen() == null ? 0 : m.getImporteOrigen();
                           double tx = m.getTasaAplicada() == null ? 0 : m.getTasaAplicada(); %>
                        <tr id="cv<%= idx %>" class="conv-detail hide">
                            <td colspan="9">
                                <strong>Conversión de moneda</strong> &nbsp;·&nbsp;
                                Monto original: <strong><%= String.format("%,.2f", io) %>
                                <%= Moneda.nombre(m.getMonedaOrigen()) %></strong>
                                &nbsp;×&nbsp; tasa <strong><%= tx %></strong>
                                &nbsp;=&nbsp; <strong><%= imp %>
                                <%= Moneda.nombre(moneda) %></strong>
                                (moneda de la cuenta <%= cuentaSel %>)
                            </td>
                        </tr>
                    <% } %>
                <% } %>
                <tr style="border-top:2px solid #334155;font-weight:800;">
                    <td colspan="5">TOTALES</td>
                    <td style="text-align:right;color:#4ade80;">+ <%= String.format("%,.2f", totIn) %></td>
                    <td style="text-align:right;color:#f87171;">- <%= String.format("%,.2f", totOut) %></td>
                    <td></td><td class="no-print"></td>
                </tr>
            </table>
            </div>

            <p class="print-only" style="margin-top:24px;font-size:11px;color:#000;">
                Documento generado por EUREKABANK GR06 — Banca SOAP. Sin valor legal.
            </p>
        <% } %>

        <div class="actions no-print">
            <a class="link" href="${pageContext.request.contextPath}/menu"
               style="margin-top:0;">&larr; Volver al menú</a>
            <% if (movs != null && !movs.isEmpty()) { %>
                <button type="button" onclick="window.print()">🖨 Imprimir estado de cuenta</button>
            <% } %>
        </div>
    </div>
</div>
</body>
</html>
