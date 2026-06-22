<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="jakarta.tags.core"%>
<%@taglib prefix="fmt" uri="jakarta.tags.fmt"%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>TicketPremium | Administracion</title>
    <script src="${pageContext.request.contextPath}/assets/js/theme.js?v=3"></script>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/app.css?v=3">
</head>
<body>
<div class="shell admin-shell">
    <header class="card topbar">
        <div class="topbar-left">
            <div class="brand-mark"><span class="brand-dot"></span> TICKETPREMIUM · FIFA 2026</div>
            <h1 class="app-name">Administracion</h1>
            <div class="app-meta">${usuario.nombre} | Rol: ${usuario.rol}</div>
            <div class="chip-row">
                <span class="chip">Panel admin</span>
                <span class="chip-soft">Partidos: ${partidosCount}</span>
                <span class="chip-soft">Localidades: ${localidadesAdminCount}</span>
            </div>
        </div>
        <div class="topbar-right">
            <button class="theme-toggle" type="button" onclick="toggleTheme()" title="Cambiar tema claro/oscuro">
                <svg class="t-moon" viewBox="0 0 24 24"><path d="M21 12.8A9 9 0 1 1 11.2 3 7 7 0 0 0 21 12.8Z"/></svg>
                <svg class="t-sun" viewBox="0 0 24 24"><circle cx="12" cy="12" r="4"/><path d="M12 2v2M12 20v2M4.9 4.9l1.4 1.4M17.7 17.7l1.4 1.4M2 12h2M20 12h2M4.9 19.1l1.4-1.4M17.7 6.3l1.4-1.4"/></svg>
            </button>
            <a class="btn-secondary" href="${pageContext.request.contextPath}/partidos">Partidos</a>
            <a class="btn-secondary" href="${pageContext.request.contextPath}/home">Volver al panel</a>
            <a class="btn-secondary" href="${pageContext.request.contextPath}/compras">Mis compras</a>
            <a class="btn-secondary" href="${pageContext.request.contextPath}/cuenta">Mi cuenta</a>
            <a class="btn-danger" href="${pageContext.request.contextPath}/logout">Cerrar sesion</a>
        </div>
    </header>

    <c:if test="${not empty flash}">
        <div class="alert ${flashType eq 'success' ? 'alert-success' : 'alert-error'}">${flash}</div>
    </c:if>

    <div class="admin-grid">
        <aside class="admin-sidebar card">
            <div class="admin-side-title">Menu admin</div>
            <nav class="admin-nav">
                <a class="admin-nav-item ${tab eq 'partidos' ? 'active' : ''}"
                   href="${pageContext.request.contextPath}/admin-panel?tab=partidos&adminPartido=${adminPartidoSel}">
                    <span class="admin-nav-ico">&#9917;</span>
                    <span><strong>Partidos</strong><small>Crear, editar y eliminar</small></span>
                </a>
                <a class="admin-nav-item ${tab eq 'localidades' ? 'active' : ''}"
                   href="${pageContext.request.contextPath}/admin-panel?tab=localidades&adminPartido=${adminPartidoSel}">
                    <span class="admin-nav-ico">&#9974;</span>
                    <span><strong>Localidades</strong><small>Categorias por partido</small></span>
                </a>
            </nav>
            <div class="admin-side-footer">
                <a class="admin-side-link" href="${pageContext.request.contextPath}/home">&#8592; Volver al panel</a>
            </div>
        </aside>

        <main class="admin-main">
            <%-- ============ PARTIDOS ============ --%>
            <c:if test="${tab eq 'partidos'}">
                <section class="card">
                    <div class="panel-header"><div><h2 class="panel-title">Gestion de partidos</h2>
                        <p class="panel-subtitle">Selecciones y estadio por dropdown.</p></div><span class="chip-soft">CRUD</span></div>

                    <div class="subtabs">
                        <input type="radio" name="partidos-action" id="pt-crear" class="subtabs-radio" checked>
                        <input type="radio" name="partidos-action" id="pt-editar" class="subtabs-radio">
                        <input type="radio" name="partidos-action" id="pt-eliminar" class="subtabs-radio">
                        <nav class="subtabs-nav">
                            <label for="pt-crear">Crear</label>
                            <label for="pt-editar">Editar</label>
                            <label for="pt-eliminar" class="danger">Eliminar</label>
                        </nav>

                        <section data-tab="crear" class="subtab-content">
                            <form method="post" action="${pageContext.request.contextPath}/admin" class="form-grid cols-2 compact-form">
                                <input type="hidden" name="accion" value="registrarPartido">
                                <input type="hidden" name="adminPartido" value="${adminPartidoSel}">
                                <div class="field"><label>Seleccion local</label>
                                    <select name="idLocal" required>
                                        <c:forEach items="${selecciones}" var="s"><option value="${s.idSeleccion}">${s.nombre} (${s.grupo})</option></c:forEach>
                                    </select></div>
                                <div class="field"><label>Seleccion visitante</label>
                                    <select name="idVisita" required>
                                        <c:forEach items="${selecciones}" var="s"><option value="${s.idSeleccion}">${s.nombre} (${s.grupo})</option></c:forEach>
                                    </select></div>
                                <div class="field"><label>Estadio</label>
                                    <select name="idEstadio" required>
                                        <c:forEach items="${estadios}" var="e"><option value="${e.idEstadio}">${e.nombreOficial} - ${e.ciudad}</option></c:forEach>
                                    </select></div>
                                <div class="field"><label>Grupo</label><input type="text" name="grupo" maxlength="1" placeholder="A-L" required></div>
                                <div class="field full-row"><label>Fecha</label><input type="text" name="fecha" placeholder="yyyy-MM-dd HH:mm:ss" required></div>
                                <div class="form-actions full-row"><button class="btn" type="submit">Registrar partido</button></div>
                            </form>
                        </section>

                        <section data-tab="editar" class="subtab-content">
                            <form method="post" action="${pageContext.request.contextPath}/admin" class="form-grid cols-2 compact-form">
                                <input type="hidden" name="accion" value="actualizarPartido">
                                <input type="hidden" name="adminPartido" value="${adminPartidoSel}">
                                <div class="field full-row"><label>Codigo del partido</label><input type="number" name="codigo" required></div>
                                <div class="field"><label>Seleccion local</label>
                                    <select name="idLocal" required>
                                        <c:forEach items="${selecciones}" var="s"><option value="${s.idSeleccion}">${s.nombre} (${s.grupo})</option></c:forEach>
                                    </select></div>
                                <div class="field"><label>Seleccion visitante</label>
                                    <select name="idVisita" required>
                                        <c:forEach items="${selecciones}" var="s"><option value="${s.idSeleccion}">${s.nombre} (${s.grupo})</option></c:forEach>
                                    </select></div>
                                <div class="field"><label>Estadio</label>
                                    <select name="idEstadio" required>
                                        <c:forEach items="${estadios}" var="e"><option value="${e.idEstadio}">${e.nombreOficial} - ${e.ciudad}</option></c:forEach>
                                    </select></div>
                                <div class="field"><label>Grupo</label><input type="text" name="grupo" maxlength="1" placeholder="A-L" required></div>
                                <div class="field full-row"><label>Fecha</label><input type="text" name="fecha" placeholder="yyyy-MM-dd HH:mm:ss" required></div>
                                <div class="form-actions full-row"><button class="btn-secondary" type="submit">Actualizar partido</button></div>
                            </form>
                        </section>

                        <section data-tab="eliminar" class="subtab-content">
                            <p class="subtab-hint warning">Accion irreversible. No se puede si el partido tiene ventas.</p>
                            <form method="post" action="${pageContext.request.contextPath}/admin" class="form-grid cols-2 compact-form">
                                <input type="hidden" name="accion" value="eliminarPartido">
                                <input type="hidden" name="adminPartido" value="${adminPartidoSel}">
                                <div class="field"><label>Codigo a eliminar</label><input type="number" name="codigo" required></div>
                                <div class="form-actions"><button class="btn-danger" type="submit">Eliminar partido</button></div>
                            </form>
                        </section>
                    </div>
                </section>

                <section class="card section-block">
                    <div class="panel-header"><div><h2 class="panel-title">Partidos existentes</h2></div><span class="chip-soft">${partidosCount}</span></div>
                    <div class="table-wrap">
                        <table class="table">
                            <thead><tr><th>Cod</th><th>Grupo</th><th>Local</th><th>Visita</th><th>Fecha</th><th>Estadio</th></tr></thead>
                            <tbody>
                            <c:forEach items="${partidos}" var="p">
                                <tr><td>${p.codigo}</td><td>${p.grupo}</td><td>${p.equipoLocal}</td><td>${p.equipoVisita}</td><td>${p.fecha}</td><td>${p.lugar}</td></tr>
                            </c:forEach>
                            <c:if test="${empty partidos}"><tr><td colspan="6">Sin partidos.</td></tr></c:if>
                            </tbody>
                        </table>
                    </div>
                </section>
            </c:if>

            <%-- ============ LOCALIDADES ============ --%>
            <c:if test="${tab eq 'localidades'}">
                <section class="card">
                    <div class="panel-header"><div><h2 class="panel-title">Gestion de categorias</h2>
                        <p class="panel-subtitle">Cat 1-4 por partido.</p></div><span class="chip-soft">${localidadesAdminCount}</span></div>

                    <form method="get" action="${pageContext.request.contextPath}/admin-panel" class="form-grid cols-2 compact-form section-block">
                        <input type="hidden" name="tab" value="localidades">
                        <div class="field full-row"><label>Partido</label>
                            <select name="adminPartido" onchange="this.form.submit()">
                                <c:forEach items="${partidos}" var="p">
                                    <option value="${p.codigo}" ${p.codigo == adminPartidoSel ? 'selected' : ''}>${p.codigo} - ${p.equipoLocal} vs ${p.equipoVisita}</option>
                                </c:forEach>
                            </select>
                        </div>
                    </form>

                    <div class="table-wrap section-block">
                        <table class="table">
                            <thead><tr><th>ID</th><th>Categoria</th><th>Disponibilidad</th><th>Precio</th></tr></thead>
                            <tbody>
                            <c:forEach items="${localidadesAdmin}" var="l">
                                <tr><td>${l.id}</td><td>${l.categoria}</td><td>${l.disponibilidad}</td><td><fmt:formatNumber value="${l.precio}" pattern="#,##0.00"/></td></tr>
                            </c:forEach>
                            <c:if test="${empty localidadesAdmin}"><tr><td colspan="4">Sin categorias.</td></tr></c:if>
                            </tbody>
                        </table>
                    </div>

                    <div class="subtabs section-block">
                        <input type="radio" name="loc-action" id="lc-crear" class="subtabs-radio" checked>
                        <input type="radio" name="loc-action" id="lc-editar" class="subtabs-radio">
                        <input type="radio" name="loc-action" id="lc-eliminar" class="subtabs-radio">
                        <nav class="subtabs-nav">
                            <label for="lc-crear">Crear</label>
                            <label for="lc-editar">Editar</label>
                            <label for="lc-eliminar" class="danger">Eliminar</label>
                        </nav>

                        <section data-tab="crear" class="subtab-content">
                            <form method="post" action="${pageContext.request.contextPath}/admin" class="form-grid cols-2 compact-form">
                                <input type="hidden" name="accion" value="registrarLocalidad">
                                <input type="hidden" name="adminPartido" value="${adminPartidoSel}">
                                <div class="field"><label>Codigo partido</label><input type="number" name="codigoPartido" value="${adminPartidoSel}" required></div>
                                <div class="field"><label>Categoria</label>
                                    <select name="categoria" required>
                                        <option value="CAT1">CAT1</option><option value="CAT2">CAT2</option>
                                        <option value="CAT3">CAT3</option><option value="CAT4">CAT4</option>
                                    </select></div>
                                <div class="field"><label>Disponibilidad</label><input type="number" name="disponibilidad" required></div>
                                <div class="field"><label>Precio (USD)</label><input type="text" name="precio" placeholder="100.00" required></div>
                                <div class="form-actions full-row"><button class="btn" type="submit">Registrar categoria</button></div>
                            </form>
                        </section>

                        <section data-tab="editar" class="subtab-content">
                            <form method="post" action="${pageContext.request.contextPath}/admin" class="form-grid cols-2 compact-form">
                                <input type="hidden" name="accion" value="actualizarLocalidad">
                                <input type="hidden" name="adminPartido" value="${adminPartidoSel}">
                                <div class="field full-row"><label>ID localidad</label><input type="number" name="idLocalidad" required></div>
                                <div class="field"><label>Disponibilidad</label><input type="number" name="disponibilidad" required></div>
                                <div class="field"><label>Precio (USD)</label><input type="text" name="precio" required></div>
                                <div class="form-actions full-row"><button class="btn-secondary" type="submit">Actualizar categoria</button></div>
                            </form>
                        </section>

                        <section data-tab="eliminar" class="subtab-content">
                            <p class="subtab-hint warning">Accion irreversible. No se puede si la categoria tiene ventas.</p>
                            <form method="post" action="${pageContext.request.contextPath}/admin" class="form-grid cols-2 compact-form">
                                <input type="hidden" name="accion" value="eliminarLocalidad">
                                <input type="hidden" name="adminPartido" value="${adminPartidoSel}">
                                <div class="field"><label>ID a eliminar</label><input type="number" name="idLocalidad" required></div>
                                <div class="form-actions"><button class="btn-danger" type="submit">Eliminar categoria</button></div>
                            </form>
                        </section>
                    </div>
                </section>
            </c:if>
        </main>
    </div>
</div>
</body>
</html>
