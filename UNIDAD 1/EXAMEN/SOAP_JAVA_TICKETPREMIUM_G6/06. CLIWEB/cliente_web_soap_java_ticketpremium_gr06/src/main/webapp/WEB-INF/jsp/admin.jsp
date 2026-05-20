<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="jakarta.tags.core"%>
<%@taglib prefix="fmt" uri="jakarta.tags.fmt"%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>TicketPremium | Administracion</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/app.css">
</head>
<body>
<div class="shell admin-shell">
    <header class="card topbar">
        <div class="topbar-left">
            <div class="brand-mark"><span class="brand-dot"></span> TICKETPREMIUM</div>
            <h1 class="app-name">Administracion</h1>
            <div class="app-meta">${usuario.nombre} | Rol: ${usuario.rol}</div>
            <div class="chip-row">
                <span class="chip">Panel admin</span>
                <span class="chip-soft">Partidos: ${partidosCount}</span>
                <span class="chip-soft">Localidades: ${localidadesAdminCount}</span>
            </div>
        </div>
        <div class="topbar-right">
            <a class="btn-secondary" href="${pageContext.request.contextPath}/home">Volver al panel</a>
            <a class="btn-danger" href="${pageContext.request.contextPath}/logout">Cerrar sesion</a>
        </div>
    </header>

    <c:if test="${not empty flash}">
        <div class="alert ${flashType eq 'success' ? 'alert-success' : 'alert-error'}">${flash}</div>
    </c:if>

    <div class="admin-grid">
        <!-- ============ Menu lateral ============ -->
        <aside class="admin-sidebar card">
            <div class="admin-side-title">Menu admin</div>
            <p class="admin-side-sub">Selecciona una opcion para gestionar.</p>

            <nav class="admin-nav">
                <a class="admin-nav-item ${tab eq 'partidos' ? 'active' : ''}"
                   href="${pageContext.request.contextPath}/admin-panel?tab=partidos&adminPartido=${adminPartidoSel}">
                    <span class="admin-nav-ico">&#9917;</span>
                    <span>
                        <strong>Partidos</strong>
                        <small>Crear, editar y eliminar partidos</small>
                    </span>
                </a>
                <a class="admin-nav-item ${tab eq 'localidades' ? 'active' : ''}"
                   href="${pageContext.request.contextPath}/admin-panel?tab=localidades&adminPartido=${adminPartidoSel}">
                    <span class="admin-nav-ico">&#9974;</span>
                    <span>
                        <strong>Localidades</strong>
                        <small>Gestionar localidades por partido</small>
                    </span>
                </a>
            </nav>

            <div class="admin-side-footer">
                <a class="admin-side-link" href="${pageContext.request.contextPath}/home">&#8592; Volver al panel</a>
            </div>
        </aside>

        <!-- ============ Contenido principal ============ -->
        <main class="admin-main">

            <%-- ================= PARTIDOS ================= --%>
            <c:if test="${tab eq 'partidos'}">
                <section class="card">
                    <div class="panel-header">
                        <div>
                            <h2 class="panel-title">Gestion de partidos</h2>
                            <p class="panel-subtitle">Selecciona la accion que deseas realizar.</p>
                        </div>
                        <span class="chip-soft">CRUD</span>
                    </div>

                    <div class="subtabs">
                        <input type="radio" name="partidos-action" id="pt-crear" class="subtabs-radio" checked>
                        <input type="radio" name="partidos-action" id="pt-editar" class="subtabs-radio">
                        <input type="radio" name="partidos-action" id="pt-eliminar" class="subtabs-radio">

                        <nav class="subtabs-nav">
                            <label for="pt-crear">Crear partido</label>
                            <label for="pt-editar">Editar partido</label>
                            <label for="pt-eliminar" class="danger">Eliminar partido</label>
                        </nav>

                        <section data-tab="crear" class="subtab-content">
                            <p class="subtab-hint">Completa los datos para registrar un nuevo partido.</p>
                            <form method="post" action="${pageContext.request.contextPath}/admin" class="form-grid cols-2 compact-form">
                                <input type="hidden" name="accion" value="registrarPartido">
                                <input type="hidden" name="volver" value="admin">
                                <input type="hidden" name="adminPartido" value="${adminPartidoSel}">
                                <div class="field"><label>Equipo local</label><input type="text" name="equipoLocal" required></div>
                                <div class="field"><label>Equipo visita</label><input type="text" name="equipoVisita" required></div>
                                <div class="field"><label>Fecha</label><input type="text" name="fecha" placeholder="yyyy-MM-dd HH:mm:ss" required></div>
                                <div class="field"><label>Lugar</label><input type="text" name="lugar" required></div>
                                <div class="form-actions full-row"><button class="btn" type="submit">Registrar partido</button></div>
                            </form>
                        </section>

                        <section data-tab="editar" class="subtab-content">
                            <p class="subtab-hint">Indica el codigo y los nuevos datos del partido.</p>
                            <form method="post" action="${pageContext.request.contextPath}/admin" class="form-grid cols-2 compact-form">
                                <input type="hidden" name="accion" value="actualizarPartido">
                                <input type="hidden" name="volver" value="admin">
                                <input type="hidden" name="adminPartido" value="${adminPartidoSel}">
                                <div class="field full-row"><label>Codigo del partido</label><input type="number" name="codigo" required></div>
                                <div class="field"><label>Equipo local</label><input type="text" name="equipoLocal" required></div>
                                <div class="field"><label>Equipo visita</label><input type="text" name="equipoVisita" required></div>
                                <div class="field"><label>Fecha</label><input type="text" name="fecha" placeholder="yyyy-MM-dd HH:mm:ss" required></div>
                                <div class="field"><label>Lugar</label><input type="text" name="lugar" required></div>
                                <div class="form-actions full-row"><button class="btn-secondary" type="submit">Actualizar partido</button></div>
                            </form>
                        </section>

                        <section data-tab="eliminar" class="subtab-content">
                            <p class="subtab-hint warning">Esta accion es irreversible. Verifica el codigo antes de continuar.</p>
                            <form method="post" action="${pageContext.request.contextPath}/admin" class="form-grid cols-2 compact-form">
                                <input type="hidden" name="accion" value="eliminarPartido">
                                <input type="hidden" name="volver" value="admin">
                                <input type="hidden" name="adminPartido" value="${adminPartidoSel}">
                                <div class="field"><label>Codigo a eliminar</label><input type="number" name="codigo" required></div>
                                <div class="form-actions"><button class="btn-danger" type="submit">Eliminar partido</button></div>
                            </form>
                        </section>
                    </div>
                </section>

                <section class="card section-block">
                    <div class="panel-header">
                        <div>
                            <h2 class="panel-title">Partidos existentes</h2>
                            <p class="panel-subtitle">Consulta de referencia.</p>
                        </div>
                        <span class="chip-soft">${partidosCount} registros</span>
                    </div>
                    <div class="table-wrap">
                        <table class="table">
                            <thead><tr>
                                <th>Codigo</th><th>Local</th><th>Visita</th><th>Fecha</th><th>Lugar</th>
                            </tr></thead>
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
                                <tr><td colspan="5">Sin partidos registrados.</td></tr>
                            </c:if>
                            </tbody>
                        </table>
                    </div>
                </section>
            </c:if>

            <%-- ================= LOCALIDADES ================= --%>
            <c:if test="${tab eq 'localidades'}">
                <section class="card">
                    <div class="panel-header">
                        <div>
                            <h2 class="panel-title">Gestion de localidades</h2>
                            <p class="panel-subtitle">Selecciona el partido y administra sus localidades.</p>
                        </div>
                        <span class="chip-soft">${localidadesAdminCount} registros</span>
                    </div>

                    <form method="get" action="${pageContext.request.contextPath}/admin-panel" class="form-grid cols-2 compact-form section-block">
                        <input type="hidden" name="tab" value="localidades">
                        <div class="field full-row">
                            <label>Partido</label>
                            <select name="adminPartido" onchange="this.form.submit()">
                                <c:forEach items="${partidos}" var="p">
                                    <option value="${p.codigo}" ${p.codigo == adminPartidoSel ? 'selected' : ''}>${p.codigo} - ${p.equipoLocal} vs ${p.equipoVisita}</option>
                                </c:forEach>
                            </select>
                        </div>
                    </form>

                    <div class="table-wrap section-block">
                        <table class="table">
                            <thead><tr>
                                <th>ID</th><th>Localidad</th><th>Disponibilidad</th><th>Precio</th>
                            </tr></thead>
                            <tbody>
                            <c:forEach items="${localidadesAdmin}" var="l">
                                <tr>
                                    <td>${l.id}</td>
                                    <td>${l.codigoLocalidad}</td>
                                    <td>${l.disponibilidad}</td>
                                    <td><fmt:formatNumber value="${l.precio}" pattern="#,##0.00"/></td>
                                </tr>
                            </c:forEach>
                            <c:if test="${empty localidadesAdmin}">
                                <tr><td colspan="4">Sin localidades para administrar.</td></tr>
                            </c:if>
                            </tbody>
                        </table>
                    </div>

                    <div class="subtabs section-block">
                        <input type="radio" name="localidades-action" id="lc-crear" class="subtabs-radio" checked>
                        <input type="radio" name="localidades-action" id="lc-editar" class="subtabs-radio">
                        <input type="radio" name="localidades-action" id="lc-eliminar" class="subtabs-radio">

                        <nav class="subtabs-nav">
                            <label for="lc-crear">Crear localidad</label>
                            <label for="lc-editar">Editar localidad</label>
                            <label for="lc-eliminar" class="danger">Eliminar localidad</label>
                        </nav>

                        <section data-tab="crear" class="subtab-content">
                            <p class="subtab-hint">Crea una localidad para el partido seleccionado.</p>
                            <form method="post" action="${pageContext.request.contextPath}/admin" class="form-grid cols-2 compact-form">
                                <input type="hidden" name="accion" value="registrarLocalidad">
                                <input type="hidden" name="volver" value="admin">
                                <input type="hidden" name="adminPartido" value="${adminPartidoSel}">
                                <div class="field"><label>Codigo partido</label><input type="number" name="codigoPartido" value="${adminPartidoSel}" required></div>
                                <div class="field"><label>Codigo localidad</label><input type="text" name="codigoLocalidad" placeholder="GENERAL, TRIBUNA, ..." required></div>
                                <div class="field"><label>Disponibilidad</label><input type="number" name="disponibilidad" required></div>
                                <div class="field"><label>Precio</label><input type="text" name="precio" placeholder="10.00" required></div>
                                <div class="form-actions full-row"><button class="btn" type="submit">Registrar localidad</button></div>
                            </form>
                        </section>

                        <section data-tab="editar" class="subtab-content">
                            <p class="subtab-hint">Toma el ID de la tabla superior para actualizar disponibilidad y precio.</p>
                            <form method="post" action="${pageContext.request.contextPath}/admin" class="form-grid cols-2 compact-form">
                                <input type="hidden" name="accion" value="actualizarLocalidad">
                                <input type="hidden" name="volver" value="admin">
                                <input type="hidden" name="adminPartido" value="${adminPartidoSel}">
                                <div class="field full-row"><label>ID localidad</label><input type="number" name="idLocalidad" required></div>
                                <div class="field"><label>Disponibilidad</label><input type="number" name="disponibilidad" required></div>
                                <div class="field"><label>Precio</label><input type="text" name="precio" required></div>
                                <div class="form-actions full-row"><button class="btn-secondary" type="submit">Actualizar localidad</button></div>
                            </form>
                        </section>

                        <section data-tab="eliminar" class="subtab-content">
                            <p class="subtab-hint warning">Esta accion es irreversible. Verifica el ID antes de continuar.</p>
                            <form method="post" action="${pageContext.request.contextPath}/admin" class="form-grid cols-2 compact-form">
                                <input type="hidden" name="accion" value="eliminarLocalidad">
                                <input type="hidden" name="volver" value="admin">
                                <input type="hidden" name="adminPartido" value="${adminPartidoSel}">
                                <div class="field"><label>ID localidad a eliminar</label><input type="number" name="idLocalidad" required></div>
                                <div class="form-actions"><button class="btn-danger" type="submit">Eliminar localidad</button></div>
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
