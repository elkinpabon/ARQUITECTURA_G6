<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="jakarta.tags.core"%>
<%@taglib prefix="fmt" uri="jakarta.tags.fmt"%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>TicketPremium | Mi cuenta</title>
    <script src="${pageContext.request.contextPath}/assets/js/theme.js?v=3"></script>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/app.css?v=3">
    <style>
        .saldo-card{ background:var(--primary); color:#fff; border-radius:var(--radius); border:1px solid var(--primary-strong);
            padding:26px; display:flex; justify-content:space-between; align-items:center; flex-wrap:wrap; gap:14px; margin-bottom:18px; }
        html[data-theme="dark"] .saldo-card{ background:#1c2c40; border-color:#2b3548; }
        .saldo-card .num{ font-size:.9rem; opacity:.8; }
        .saldo-card .saldo{ font-size:2.3rem; font-weight:800; letter-spacing:-.04em; }
        .saldo-card .hint{ font-size:.82rem; opacity:.78; max-width:46ch; }
        .mov-pos{ color:var(--success); font-weight:700; }
        .mov-neg{ color:var(--danger); font-weight:700; }
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
                <a href="${pageContext.request.contextPath}/compras">Mis compras</a>
                <a class="active" href="${pageContext.request.contextPath}/cuenta">Mi cuenta</a>
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

    <section class="saldo-card">
        <div>
            <div class="num">Cuenta ${cuenta != null ? cuenta.numero : 'N/D'} · ${usuario.nombre}</div>
            <div class="saldo">$<fmt:formatNumber value="${cuenta != null ? cuenta.saldo : 0}" pattern="#,##0.00"/></div>
            <div class="num">Saldo (deuda por compras a credito)</div>
        </div>
        <div class="hint">
            Las compras a CONTADO se registran como movimiento informativo.
            Las compras a CREDITO suman el monto financiado al saldo de tu cuenta
            y generan la tabla de amortizacion (ver en <a href="${pageContext.request.contextPath}/compras" style="color:#ffd34d">Mis compras</a>).
        </div>
    </section>

    <section class="panel">
        <div class="panel-header">
            <div>
                <h2 class="panel-title">Movimientos</h2>
                <p class="panel-subtitle">Historial bancario de tu cuenta.</p>
            </div>
            <span class="chip-soft">${movimientosCount} movimientos</span>
        </div>
        <div class="table-wrap">
            <table class="table">
                <thead><tr><th>#</th><th>Fecha</th><th>Tipo</th><th>Descripcion</th><th>Factura</th><th class="right-align">Monto</th></tr></thead>
                <tbody>
                <c:forEach items="${movimientos}" var="m">
                    <tr>
                        <td>${m.idMovimiento}</td>
                        <td>${m.fecha}</td>
                        <td><span class="chip-soft">${m.tipo}</span></td>
                        <td>${m.descripcion}</td>
                        <td><c:if test="${m.idFactura > 0}">
                            <a href="${pageContext.request.contextPath}/compras?f=${m.idFactura}">#${m.idFactura}</a>
                        </c:if></td>
                        <td class="right-align ${m.tipo eq 'CREDITO' ? 'mov-neg' : 'mov-pos'}">
                            $<fmt:formatNumber value="${m.monto}" pattern="#,##0.00"/>
                        </td>
                    </tr>
                </c:forEach>
                <c:if test="${empty movimientos}"><tr><td colspan="6">Aun no tienes movimientos.</td></tr></c:if>
                </tbody>
            </table>
        </div>
    </section>
</div>
</body>
</html>
