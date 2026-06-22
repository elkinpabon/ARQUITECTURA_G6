<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="jakarta.tags.core"%>
<%@taglib prefix="fmt" uri="jakarta.tags.fmt"%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>TicketPremium | Mis compras</title>
    <script src="${pageContext.request.contextPath}/assets/js/theme.js?v=3"></script>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/app.css?v=3">
    <style>
        .compras-grid{ display:grid; grid-template-columns:1fr 1.3fr; gap:16px; align-items:start; }
        @media (max-width:1000px){ .compras-grid{ grid-template-columns:1fr; } }
        tr.sel td{ background:var(--primary-tint) !important; }
        .tot-row{ display:flex; justify-content:space-between; padding:6px 0; font-size:.95rem; }
        .tot-row.big{ font-weight:800; font-size:1.15rem; border-top:2px solid var(--border); padding-top:10px; }
    </style>
</head>
<body>
<div class="shell">
    <header class="card topbar">
        <div class="topbar-left">
            <div class="brand-mark"><span class="brand-dot"></span> TICKETPREMIUM · FIFA 2026</div>
            <nav class="nav-links">
                <a href="${pageContext.request.contextPath}/partidos">Partidos</a>
                <a href="${pageContext.request.contextPath}/home">Carrito y compra</a>
                <a class="active" href="${pageContext.request.contextPath}/compras">Mis compras</a>
                <a href="${pageContext.request.contextPath}/cuenta">Mi cuenta</a>
                <c:if test="${admin}"><a href="${pageContext.request.contextPath}/admin-panel">Administracion</a></c:if>
            </nav>
        </div>
        <div class="topbar-right">
            <button class="theme-toggle" type="button" onclick="toggleTheme()" title="Cambiar tema claro/oscuro">
                <svg class="t-moon" viewBox="0 0 24 24"><path d="M21 12.8A9 9 0 1 1 11.2 3 7 7 0 0 0 21 12.8Z"/></svg>
                <svg class="t-sun" viewBox="0 0 24 24"><circle cx="12" cy="12" r="4"/><path d="M12 2v2M12 20v2M4.9 4.9l1.4 1.4M17.7 17.7l1.4 1.4M2 12h2M20 12h2M4.9 19.1l1.4-1.4M17.7 6.3l1.4-1.4"/></svg>
            </button>
            <span class="app-meta">Hola, ${usuario.nombre}</span>
            <a class="btn-danger" href="${pageContext.request.contextPath}/logout">Cerrar sesion</a>
        </div>
    </header>

    <c:if test="${not empty errorDetalle}">
        <div class="alert alert-error">${errorDetalle}</div>
    </c:if>

    <div class="compras-grid">
        <section class="panel">
            <div class="panel-header">
                <div>
                    <h2 class="panel-title">${admin ? 'Todas las facturas' : 'Mis facturas'}</h2>
                    <p class="panel-subtitle">Haz clic en una factura para ver su detalle.</p>
                </div>
                <span class="chip-soft">${facturasCount}</span>
            </div>
            <div class="table-wrap">
                <table class="table">
                    <thead><tr>
                        <th>#</th><c:if test="${admin}"><th>Cliente</th></c:if>
                        <th>Fecha</th><th>Pago</th><th class="right-align">Total</th><th></th>
                    </tr></thead>
                    <tbody>
                    <c:forEach items="${facturas}" var="f">
                        <tr class="${detalle != null && detalle.idFactura == f.idFactura ? 'sel' : ''}">
                            <td><strong>#${f.idFactura}</strong></td>
                            <c:if test="${admin}"><td>${not empty f.usuarioNombre ? f.usuarioNombre : f.idUsuario}</td></c:if>
                            <td>${f.fecha}</td>
                            <td><span class="chip-soft">${f.tipoPago}</span></td>
                            <td class="right-align"><strong>$<fmt:formatNumber value="${f.total}" pattern="#,##0.00"/></strong></td>
                            <td><a class="btn-secondary" href="${pageContext.request.contextPath}/compras?f=${f.idFactura}">Ver</a></td>
                        </tr>
                    </c:forEach>
                    <c:if test="${empty facturas}"><tr><td colspan="${admin ? 6 : 5}">Aun no tienes compras.</td></tr></c:if>
                    </tbody>
                </table>
            </div>
        </section>

        <section class="panel">
            <c:choose>
                <c:when test="${detalle != null}">
                    <div class="panel-header">
                        <div>
                            <h2 class="panel-title">Factura #${detalle.idFactura}</h2>
                            <p class="panel-subtitle">${detalle.fecha} · ${detalle.tipoPago}
                                <c:if test="${not empty detalle.usuarioNombre}"> · ${detalle.usuarioNombre}</c:if></p>
                        </div>
                        <a class="btn" target="_blank"
                           href="${pageContext.request.contextPath}/comprobante?facturaId=${detalle.idFactura}">Descargar PDF</a>
                    </div>

                    <h3 style="margin:10px 0 8px; font-size:.95rem;">Boletos</h3>
                    <div class="table-wrap">
                        <table class="table">
                            <thead><tr><th>Partido</th><th>Cat.</th><th>Asiento</th><th>Cant.</th>
                                <th class="right-align">P. unit</th><th class="right-align">Total</th></tr></thead>
                            <tbody>
                            <c:forEach items="${detalle.detalles}" var="d">
                                <tr>
                                    <td>${not empty d.descripcionPartido ? d.descripcionPartido : d.codigoPartido}</td>
                                    <td><span class="chip-soft">${d.categoria}</span></td>
                                    <td><c:if test="${not empty d.fila}">${d.fila}-${d.asientos}</c:if></td>
                                    <td>${d.cantidad}</td>
                                    <td class="right-align">$<fmt:formatNumber value="${d.precioUnitario}" pattern="#,##0.00"/></td>
                                    <td class="right-align">$<fmt:formatNumber value="${d.total}" pattern="#,##0.00"/></td>
                                </tr>
                            </c:forEach>
                            </tbody>
                        </table>
                    </div>

                    <div class="section-block">
                        <div class="tot-row"><span>Subtotal</span><span>$<fmt:formatNumber value="${detalle.subtotal}" pattern="#,##0.00"/></span></div>
                        <div class="tot-row"><span>IVA 15%</span><span>$<fmt:formatNumber value="${detalle.iva}" pattern="#,##0.00"/></span></div>
                        <div class="tot-row big"><span>Total ${detalle.moneda}</span><span>$<fmt:formatNumber value="${detalle.total}" pattern="#,##0.00"/></span></div>
                    </div>

                    <c:if test="${detalle.tipoPago eq 'CREDITO'}">
                        <div class="divider"></div>
                        <h3 style="margin:10px 0 8px; font-size:.95rem;">Credito</h3>
                        <div class="tot-row"><span>Entrada</span><span>$<fmt:formatNumber value="${detalle.entrada}" pattern="#,##0.00"/></span></div>
                        <div class="tot-row"><span>Monto financiado</span><span>$<fmt:formatNumber value="${detalle.montoFinanciado}" pattern="#,##0.00"/></span></div>
                        <div class="tot-row"><span>Cuotas</span><span>${detalle.numCuotas} · tasa <fmt:formatNumber value="${detalle.tasaInteres * 100}" pattern="#,##0.##"/>% mensual</span></div>

                        <h3 style="margin:14px 0 8px; font-size:.95rem;">Tabla de amortizacion (sistema frances)</h3>
                        <div class="table-wrap">
                            <table class="table">
                                <thead><tr><th>#</th><th>Vence</th><th class="right-align">Cuota</th>
                                    <th class="right-align">Interes</th><th class="right-align">Capital</th><th class="right-align">Saldo</th></tr></thead>
                                <tbody>
                                <c:forEach items="${detalle.amortizacion}" var="q">
                                    <tr>
                                        <td>${q.numCuota}</td>
                                        <td>${q.fechaVencimiento}</td>
                                        <td class="right-align">$<fmt:formatNumber value="${q.cuota}" pattern="#,##0.00"/></td>
                                        <td class="right-align">$<fmt:formatNumber value="${q.interes}" pattern="#,##0.00"/></td>
                                        <td class="right-align">$<fmt:formatNumber value="${q.abonoCapital}" pattern="#,##0.00"/></td>
                                        <td class="right-align">$<fmt:formatNumber value="${q.saldoFinal}" pattern="#,##0.00"/></td>
                                    </tr>
                                </c:forEach>
                                </tbody>
                            </table>
                        </div>
                    </c:if>
                </c:when>
                <c:otherwise>
                    <h2 class="panel-title">Detalle de factura</h2>
                    <p class="panel-subtitle">Selecciona una factura de la izquierda para ver sus boletos,
                       el pago y (si fue a credito) la tabla de amortizacion.</p>
                </c:otherwise>
            </c:choose>
        </section>
    </div>
</div>
</body>
</html>
