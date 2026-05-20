<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="jakarta.tags.core"%>
<%@taglib prefix="fmt" uri="jakarta.tags.fmt"%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>TicketPremium | Panel</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/app.css">
</head>
<body>
<div class="shell">
    <header class="card topbar">
        <div class="topbar-left">
            <div class="brand-mark"><span class="brand-dot"></span> TICKETPREMIUM</div>
            <h1 class="app-name">Panel principal</h1>
            <div class="app-meta">Bienvenido, ${usuario.nombre} | Rol: ${usuario.rol}</div>
            <div class="chip-row">
                <span class="chip">TicketPremium</span>
                <span class="chip-soft">Partidos: ${partidosCount}</span>
                <span class="chip-soft">Facturas: ${facturasCount}</span>
                <c:if test="${admin}"><span class="chip-soft">Admin</span></c:if>
            </div>
        </div>
        <div class="topbar-right">
            <c:if test="${admin}">
                <a class="btn btn-admin" href="${pageContext.request.contextPath}/admin-panel">
                    &#9881; Administracion
                </a>
            </c:if>
            <a class="btn-secondary" href="${pageContext.request.contextPath}/home">Actualizar</a>
            <a class="btn-danger" href="${pageContext.request.contextPath}/logout">Cerrar sesion</a>
        </div>
    </header>

    <c:if test="${not empty flash}">
        <div class="alert ${flashType eq 'success' ? 'alert-success' : 'alert-error'}">${flash}</div>
    </c:if>

    <section class="stats-grid">
        <article class="stat-card">
            <div class="stat-label">Partidos visibles</div>
            <div class="stat-value">${partidosCount}</div>
            <div class="stat-sub">Disponibles para compra</div>
        </article>
        <article class="stat-card">
            <div class="stat-label">Localidades</div>
            <div class="stat-value">${localidadesCount}</div>
            <div class="stat-sub">Del partido seleccionado</div>
        </article>
        <article class="stat-card">
            <div class="stat-label">Boletos imprimibles</div>
            <div class="stat-value">${comprobantesCount}</div>
            <div class="stat-sub">Compras con PDF</div>
        </article>
        <article class="stat-card">
            <div class="stat-label">Reporte</div>
            <div class="stat-value">${reporteCount}</div>
            <div class="stat-sub">Filas del resumen</div>
        </article>
    </section>

    <main class="dashboard-grid">
        <div class="stack">
            <details class="panel accordion" open>
                <summary>
                    <div>
                        <h2 class="panel-title">Partidos</h2>
                        <p class="panel-subtitle">Ver partidos disponibles.</p>
                    </div>
                    <span class="chip-soft">${partidosCount} partidos</span>
                    <span class="accordion-caret"></span>
                </summary>
                <div class="accordion-body">
                    <div class="table-wrap">
                        <table class="table">
                            <thead>
                            <tr>
                                <th>Codigo</th>
                                <th>Local</th>
                                <th>Visita</th>
                                <th>Fecha</th>
                                <th>Lugar</th>
                            </tr>
                            </thead>
                            <tbody>
                            <c:forEach items="${partidos}" var="p">
                                <tr>
                                    <td>${p.codigo}</td>
                                    <td>${p.equipoLocal}</td>
                                    <td>${p.equipoVisita}</td>
                                    <td>${p.fecha}</td>
                                    <td>${p.lugar}</td>
                                </tr>
                            </c:forEach>
                            <c:if test="${empty partidos}">
                                <tr><td colspan="5">Sin partidos disponibles.</td></tr>
                            </c:if>
                            </tbody>
                        </table>
                    </div>
                </div>
            </details>

            <details class="panel accordion" open>
                <summary>
                    <div>
                        <h2 class="panel-title">Resumen de ventas</h2>
                        <p class="panel-subtitle">Solo para administrador.</p>
                    </div>
                    <span class="chip-soft">${reporteCount} filas</span>
                    <span class="accordion-caret"></span>
                </summary>
                <div class="accordion-body">
                    <form method="get" action="${pageContext.request.contextPath}/home" class="form-grid cols-2 compact-form section-block">
                        <div class="field">
                            <label>Partido</label>
                            <select name="reportePartido">
                                <c:forEach items="${partidos}" var="p">
                                    <option value="${p.codigo}" ${p.codigo == reporteSel ? 'selected' : ''}>${p.codigo} - ${p.equipoLocal} vs ${p.equipoVisita}</option>
                                </c:forEach>
                            </select>
                        </div>
                        <div class="form-actions">
                            <input type="hidden" name="partido" value="${partidoSel}">
                            <input type="hidden" name="adminPartido" value="${adminPartidoSel}">
                            <button class="btn" type="submit">Ver reporte</button>
                        </div>
                    </form>

                    <div class="table-wrap section-block">
                        <table class="table">
                            <thead>
                            <tr>
                                <th>Localidad</th>
                                <th>Vendidos</th>
                                <th>Total recaudado</th>
                            </tr>
                            </thead>
                            <tbody>
                            <c:forEach items="${reporte}" var="r">
                                <tr>
                                    <td>${r.localidad}</td>
                                    <td>${r.vendidos}</td>
                                    <td><fmt:formatNumber value="${r.totalRecaudado}" pattern="#,##0.00"/></td>
                                </tr>
                            </c:forEach>
                            <c:if test="${empty reporte}">
                                <tr><td colspan="3">Sin ventas para el partido seleccionado.</td></tr>
                            </c:if>
                            </tbody>
                        </table>
                    </div>
                </div>
            </details>

            <c:if test="${admin}">
                <div class="card admin-cta">
                    <div>
                        <h2 class="panel-title">Funciones de administracion</h2>
                        <p class="panel-subtitle">Gestiona partidos y localidades en un panel dedicado.</p>
                    </div>
                    <a class="btn btn-admin" href="${pageContext.request.contextPath}/admin-panel">
                        &#9881; Ir al panel admin
                    </a>
                </div>
            </c:if>
        </div>

        <aside class="stack">
            <c:if test="${not empty ultimoComprobante}">
                <details class="panel accordion" open>
                    <summary>
                        <div>
                            <h2 class="panel-title">Comprobante reciente</h2>
                            <p class="panel-subtitle">Detalle de tu ultima compra.</p>
                        </div>
                        <span class="chip-soft">${ultimoComprobante.codigoR}</span>
                        <span class="accordion-caret"></span>
                    </summary>
                    <div class="accordion-body">
                        <div class="mini-list">
                            <div class="mini-row"><div><strong>Factura</strong><span>#${ultimoComprobante.idFactura}</span></div></div>
                            <div class="mini-row"><div><strong>Fecha</strong><span>${ultimoComprobante.fecha}</span></div></div>
                            <div class="mini-row"><div><strong>Partido</strong><span>${ultimoComprobante.partido}</span></div></div>
                            <div class="mini-row"><div><strong>Localidad</strong><span>${ultimoComprobante.localidad}</span></div></div>
                            <div class="mini-row"><div><strong>Cantidad</strong><span>${ultimoComprobante.cantidad}</span></div></div>
                            <div class="mini-row"><div><strong>Total</strong><span><fmt:formatNumber value="${ultimoComprobante.total}" pattern="#,##0.00"/></span></div></div>
                        </div>
                        <div class="form-actions section-block">
                            <a class="btn-secondary" href="${pageContext.request.contextPath}/comprobante?facturaId=${ultimoComprobante.idFactura}">Descargar PDF</a>
                        </div>
                    </div>
                </details>
            </c:if>

            <c:if test="${not empty comprobantes}">
                <details class="panel accordion" open>
                    <summary>
                        <div>
                            <h2 class="panel-title">Mis boletos</h2>
                            <p class="panel-subtitle">Descarga el PDF de cada compra.</p>
                        </div>
                        <span class="chip-soft">${comprobantesCount} compras</span>
                        <span class="accordion-caret"></span>
                    </summary>
                    <div class="accordion-body">
                        <div class="table-wrap">
                            <table class="table">
                                <thead>
                                <tr>
                                    <th>Factura</th>
                                    <th>Fecha</th>
                                    <th>Partido</th>
                                    <th>Localidad</th>
                                    <th>Total</th>
                                    <th>PDF</th>
                                </tr>
                                </thead>
                                <tbody>
                                <c:forEach items="${comprobantes}" var="c">
                                    <tr>
                                        <td>#${c.idFactura}</td>
                                        <td>${c.fecha}</td>
                                        <td>${c.partido}</td>
                                        <td>${c.localidad}</td>
                                        <td><fmt:formatNumber value="${c.total}" pattern="#,##0.00"/></td>
                                        <td><a class="btn-secondary btn-small" href="${pageContext.request.contextPath}/comprobante?facturaId=${c.idFactura}">Generar</a></td>
                                    </tr>
                                </c:forEach>
                                </tbody>
                            </table>
                        </div>
                        <div class="small-note">Solo se muestran compras ya registradas en tu sesion.</div>
                    </div>
                </details>
            </c:if>

            <details class="panel accordion" open>
                <summary>
                    <div>
                        <h2 class="panel-title">Comprar</h2>
                        <p class="panel-subtitle">Elegir partido, localidad y cantidad.</p>
                    </div>
                    <span class="chip-soft">${localidadesCount} localidades</span>
                    <span class="accordion-caret"></span>
                </summary>
                <div class="accordion-body">
                    <form method="get" action="${pageContext.request.contextPath}/home" class="form-grid compact-form section-block">
                        <div class="field">
                            <label>Partido</label>
                            <select name="partido" onchange="this.form.submit()">
                                <c:forEach items="${partidos}" var="p">
                                    <option value="${p.codigo}" ${p.codigo == partidoSel ? 'selected' : ''}>${p.codigo} - ${p.equipoLocal} vs ${p.equipoVisita}</option>
                                </c:forEach>
                            </select>
                        </div>
                        <input type="hidden" name="reportePartido" value="${reporteSel}">
                        <input type="hidden" name="adminPartido" value="${adminPartidoSel}">
                    </form>

                    <form method="post" action="${pageContext.request.contextPath}/comprar" class="form-grid cols-1 compact-form section-block">
                        <input type="hidden" name="partido" value="${partidoSel}">
                        <input type="hidden" name="reportePartido" value="${reporteSel}">
                        <input type="hidden" name="adminPartido" value="${adminPartidoSel}">
                        <div class="field">
                            <label>Localidad</label>
                            <select name="localidad">
                                <c:forEach items="${localidades}" var="l">
                                    <option value="${l.codigoLocalidad}">${l.codigoLocalidad} (${l.disponibilidad})</option>
                                </c:forEach>
                            </select>
                        </div>
                        <div class="field">
                            <label>Cantidad</label>
                            <input type="number" min="1" value="1" name="cantidad">
                        </div>
                        <div class="form-actions">
                            <button class="btn" type="submit">Registrar compra</button>
                        </div>
                    </form>
                    <div class="small-note">El total se calcula al registrar.</div>
                </div>
            </details>

        </aside>
    </main>
</div>
</body>
</html>
