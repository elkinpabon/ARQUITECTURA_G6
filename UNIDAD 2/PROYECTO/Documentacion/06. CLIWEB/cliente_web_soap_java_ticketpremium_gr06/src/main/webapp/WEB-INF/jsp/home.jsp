<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="jakarta.tags.core"%>
<%@taglib prefix="fmt" uri="jakarta.tags.fmt"%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>TicketPremium FIFA 2026 | Carrito y compra</title>
    <script src="${pageContext.request.contextPath}/assets/js/theme.js?v=4"></script>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/app.css?v=4">
    <style>
        .checkout-wrap{ max-width:780px; margin:0 auto; width:100%; }
        .panel-head{ display:flex; align-items:flex-start; justify-content:space-between;
                     gap:12px; margin-bottom:12px; }
        .inline-form{ display:inline-block; margin:0; }
        .cart-line-seat{ color:var(--muted); font-size:.85rem; }
        .checkout-foot{ display:flex; flex-wrap:wrap; gap:10px; align-items:center;
                        justify-content:space-between; margin-top:14px; }
        .total-box{ text-align:right; }
        .total-box .big{ font-size:1.45rem; font-weight:800; color:var(--text); }
        .empty-cart{ text-align:center; padding:38px 16px; }
        .empty-cart svg{ width:54px; height:54px; stroke:var(--muted); fill:none; stroke-width:1.6; }
        .empty-cart p{ color:var(--muted); margin:12px 0 18px; }
        .pay-fields{ margin-top:6px; }
        /* Se ocultan SOLO mientras tengan la clase .oculto; togglePago() la quita/pone.
           (No usar .credito-only{display:none} a secas: al mostrar con style.display=''
            el elemento volveria a la regla CSS y seguiria oculto.) */
        .credito-only.oculto{ display:none; }
    </style>
</head>
<body>
<div class="shell">
    <header class="card topbar">
        <div class="topbar-left">
            <div class="brand-mark"><span class="brand-dot"></span> TICKETPREMIUM · FIFA 2026</div>
            <nav class="nav-links">
                <a href="${pageContext.request.contextPath}/partidos">Partidos</a>
                <a class="active" href="${pageContext.request.contextPath}/home">Carrito y compra</a>
                <a href="${pageContext.request.contextPath}/compras">Mis compras</a>
                <a href="${pageContext.request.contextPath}/cuenta">Mi cuenta</a>
            </nav>
            <div class="app-meta">Bienvenido, ${usuario.nombre} | Rol: ${usuario.rol}</div>
        </div>
        <div class="topbar-right">
            <button class="theme-toggle" type="button" onclick="toggleTheme()" title="Cambiar tema claro/oscuro">
                <svg class="t-moon" viewBox="0 0 24 24"><path d="M21 12.8A9 9 0 1 1 11.2 3 7 7 0 0 0 21 12.8Z"/></svg>
                <svg class="t-sun" viewBox="0 0 24 24"><circle cx="12" cy="12" r="4"/><path d="M12 2v2M12 20v2M4.9 4.9l1.4 1.4M17.7 17.7l1.4 1.4M2 12h2M20 12h2M4.9 19.1l1.4-1.4M17.7 6.3l1.4-1.4"/></svg>
            </button>
            <c:if test="${admin}">
                <a class="btn btn-admin" href="${pageContext.request.contextPath}/admin-panel">Administracion</a>
            </c:if>
            <a class="btn-danger" href="${pageContext.request.contextPath}/logout">Cerrar sesion</a>
        </div>
    </header>

    <c:if test="${not empty flash}">
        <div class="alert ${flashType eq 'success' ? 'alert-success' : 'alert-error'}">${flash}</div>
    </c:if>

    <main class="checkout-wrap">

        <%-- ===== Comprobante de la ultima compra ===== --%>
        <c:if test="${not empty ultimaFactura}">
        <div class="panel section-block">
            <div class="panel-head">
                <div><h2 class="panel-title">Compra realizada</h2>
                    <p class="panel-subtitle">Factura #${ultimaFactura.idFactura} · ${ultimaFactura.tipoPago}</p></div>
                <span class="chip-soft"><fmt:formatNumber value="${ultimaFactura.total}" pattern="#,##0.00"/> ${ultimaFactura.moneda}</span>
            </div>
            <div class="mini-list">
                <div class="mini-row"><div><strong>Total</strong><span><fmt:formatNumber value="${ultimaFactura.total}" pattern="#,##0.00"/> ${ultimaFactura.moneda}</span></div></div>
                <c:if test="${ultimaFactura.tipoPago eq 'CREDITO'}">
                    <div class="mini-row"><div><strong>Entrada</strong><span><fmt:formatNumber value="${ultimaFactura.entrada}" pattern="#,##0.00"/></span></div></div>
                    <div class="mini-row"><div><strong>Financiado</strong><span><fmt:formatNumber value="${ultimaFactura.montoFinanciado}" pattern="#,##0.00"/></span></div></div>
                    <div class="mini-row"><div><strong>Cuotas</strong><span>${ultimaFactura.numCuotas} @ <fmt:formatNumber value="${ultimaFactura.tasaInteres * 100}" pattern="#0.##"/>% mensual</span></div></div>
                </c:if>
            </div>
            <c:if test="${ultimaFactura.tipoPago eq 'CREDITO' and not empty ultimaFactura.amortizacion}">
                <div class="table-wrap section-block">
                    <table class="table">
                        <thead><tr><th>#</th><th>Vence</th><th>Saldo ini</th><th>Cuota</th><th>Interes</th><th>Abono</th><th>Saldo fin</th></tr></thead>
                        <tbody>
                        <c:forEach items="${ultimaFactura.amortizacion}" var="q">
                            <tr>
                                <td>${q.numCuota}</td><td>${q.fechaVencimiento}</td>
                                <td><fmt:formatNumber value="${q.saldoInicial}" pattern="#,##0.00"/></td>
                                <td><fmt:formatNumber value="${q.cuota}" pattern="#,##0.00"/></td>
                                <td><fmt:formatNumber value="${q.interes}" pattern="#,##0.00"/></td>
                                <td><fmt:formatNumber value="${q.abonoCapital}" pattern="#,##0.00"/></td>
                                <td><fmt:formatNumber value="${q.saldoFinal}" pattern="#,##0.00"/></td>
                            </tr>
                        </c:forEach>
                        </tbody>
                    </table>
                </div>
            </c:if>
            <div class="form-actions section-block">
                <a class="btn-secondary" href="${pageContext.request.contextPath}/comprobante?facturaId=${ultimaFactura.idFactura}">Descargar PDF</a>
                <a class="btn-secondary" href="${pageContext.request.contextPath}/compras">Ver mis compras</a>
            </div>
        </div>
        </c:if>

        <%-- ===== El carrito ===== --%>
        <div class="panel">
            <div class="panel-head">
                <div><h2 class="panel-title">Tu carrito</h2>
                    <p class="panel-subtitle">Revisa tus entradas y finaliza la compra.</p></div>
                <span class="chip-soft">${carritoCount} ${carritoCount == 1 ? 'entrada' : 'entradas'}</span>
            </div>

            <c:choose>
                <%-- --- Carrito vacio --- --%>
                <c:when test="${empty carrito}">
                    <div class="empty-cart">
                        <svg viewBox="0 0 24 24"><circle cx="9" cy="21" r="1"/><circle cx="20" cy="21" r="1"/><path d="M1 1h4l2.7 13.4a2 2 0 0 0 2 1.6h9.7a2 2 0 0 0 2-1.6L23 6H6"/></svg>
                        <p>Tu carrito esta vacio. Elige un partido y reserva tus asientos.</p>
                        <a class="btn btn-admin" href="${pageContext.request.contextPath}/partidos">Explorar partidos</a>
                    </div>
                </c:when>

                <%-- --- Carrito con items --- --%>
                <c:otherwise>
                    <div class="table-wrap">
                        <table class="table">
                            <thead><tr><th>Partido</th><th>Cat / Seccion</th><th>Asiento</th><th>Cant</th><th>Precio</th><th>Subtotal</th><th></th></tr></thead>
                            <tbody>
                            <c:forEach items="${carrito}" var="l" varStatus="st">
                                <tr>
                                    <td>${l.partidoDesc}</td>
                                    <td>${l.seccionLabel}</td>
                                    <td><span class="cart-line-seat">${empty l.fila ? 'General' : l.fila} ${l.asientos}</span></td>
                                    <td>${l.cantidad}</td>
                                    <td><fmt:formatNumber value="${l.precio}" pattern="#,##0.00"/></td>
                                    <td><strong><fmt:formatNumber value="${l.total}" pattern="#,##0.00"/></strong></td>
                                    <td>
                                        <form method="post" action="${pageContext.request.contextPath}/carrito" class="inline-form">
                                            <input type="hidden" name="accion" value="quitar">
                                            <input type="hidden" name="indice" value="${st.index}">
                                            <button class="btn-danger btn-small" type="submit" title="Quitar">&times;</button>
                                        </form>
                                    </td>
                                </tr>
                            </c:forEach>
                            </tbody>
                        </table>
                    </div>

                    <div class="checkout-foot">
                        <form method="post" action="${pageContext.request.contextPath}/carrito" class="inline-form">
                            <input type="hidden" name="accion" value="vaciar">
                            <button class="btn-secondary btn-small" type="submit">Vaciar carrito</button>
                        </form>
                        <div class="total-box">
                            <div class="cart-line-seat">Subtotal (sin IVA)</div>
                            <div class="big"><fmt:formatNumber value="${carritoTotal}" pattern="#,##0.00"/></div>
                            <div class="cart-line-seat">El IVA 15% se suma al finalizar.</div>
                        </div>
                    </div>
                </c:otherwise>
            </c:choose>
        </div>

        <%-- ===== Pago (solo si hay algo en el carrito) ===== --%>
        <c:if test="${not empty carrito}">
        <div class="panel section-block">
            <div class="panel-head">
                <div><h2 class="panel-title">Forma de pago</h2>
                    <p class="panel-subtitle">Elige como deseas pagar tus entradas.</p></div>
            </div>
            <form method="post" action="${pageContext.request.contextPath}/checkout" id="formCheckout" onsubmit="return validarPago()" class="form-grid cols-2 compact-form pay-fields">
                <div class="field full-row"><label>Tipo de pago</label>
                    <select name="tipoPago" id="tipoPago" onchange="togglePago()">
                        <option value="CONTADO">Contado</option>
                        <option value="CREDITO">Credito (con amortizacion)</option>
                    </select>
                </div>
                <div class="field credito-only oculto"><label>Entrada</label><input type="text" id="fEntrada" name="entrada" placeholder="0.00" oninput="simInvalida()"></div>
                <div class="field credito-only oculto"><label>N&deg; de cuotas</label><input type="number" id="fNumCuotas" name="numCuotas" min="1" step="1" placeholder="6" oninput="simInvalida()"></div>
                <div class="field credito-only oculto"><label>Tasa mensual %</label><input type="text" id="fTasa" name="tasaInteres" placeholder="2" oninput="simInvalida()"></div>
                <div class="small-note full-row credito-only oculto" id="notaCredito">Para CREDITO: total = subtotal + IVA; se financia (total - entrada) en N cuotas a la tasa indicada. El n&uacute;mero de cuotas debe ser mayor a cero.</div>
                <div class="form-actions full-row">
                    <button class="btn-secondary credito-only oculto" type="button" onclick="simular()">Simular cr&eacute;dito</button>
                    <button class="btn btn-admin" type="submit" id="btnFinalizar">Finalizar compra</button>
                </div>
            </form>
            <div id="simResult" class="section-block"></div>
        </div>
        </c:if>

        <%-- ===== Volver a cambiar / seguir comprando ===== --%>
        <div class="form-actions section-block">
            <a class="btn-secondary" href="${pageContext.request.contextPath}/partidos">&#8592; Seguir comprando</a>
        </div>
    </main>
</div>

<script>
    var ctx = '${pageContext.request.contextPath}';
    var simOk = false;   // hay una simulacion valida para los datos actuales

    function esCredito(){ return document.getElementById('tipoPago').value === 'CREDITO'; }

    function togglePago(){
        var credito = esCredito();
        document.querySelectorAll('.credito-only').forEach(function(el){ el.classList.toggle('oculto', !credito); });
        if (!credito){ document.getElementById('simResult').innerHTML = ''; }
        simInvalida();
    }

    // cualquier cambio en los datos del credito invalida la simulacion previa
    function simInvalida(){ simOk = false; }

    function cuotasValidas(){
        var n = parseInt(document.getElementById('fNumCuotas').value, 10);
        return !isNaN(n) && n >= 1;
    }

    function money(x){ return Number(x).toFixed(2); }

    function simular(){
        var box = document.getElementById('simResult');
        if (!cuotasValidas()){
            box.innerHTML = '<div class="alert alert-error">El cr&eacute;dito requiere un n&uacute;mero de cuotas mayor a cero.</div>';
            document.getElementById('fNumCuotas').focus();
            return;
        }
        box.innerHTML = '<div class="small-note">Calculando simulaci&oacute;n...</div>';
        var body = 'entrada=' + encodeURIComponent(document.getElementById('fEntrada').value || '0')
                 + '&numCuotas=' + encodeURIComponent(document.getElementById('fNumCuotas').value || '0')
                 + '&tasaInteres=' + encodeURIComponent(document.getElementById('fTasa').value || '0');
        fetch(ctx + '/simular-credito', {
            method: 'POST',
            headers: {'Content-Type': 'application/x-www-form-urlencoded'},
            body: body
        })
        .then(function(r){ return r.json(); })
        .then(function(d){
            if (!d.ok){ box.innerHTML = '<div class="alert alert-error">' + (d.mensaje || 'No se pudo simular.') + '</div>'; return; }
            simOk = true;
            var h = '<div class="panel">'
                  + '<div class="panel-head"><div><h2 class="panel-title">Simulaci&oacute;n del cr&eacute;dito</h2>'
                  + '<p class="panel-subtitle">Asi quedaria tu plan de pagos. Si est&aacute; bien, finaliza la compra.</p></div></div>'
                  + '<div class="mini-list">'
                  + '<div class="mini-row"><div><strong>Total (con IVA)</strong><span>$' + money(d.total) + '</span></div></div>'
                  + '<div class="mini-row"><div><strong>Entrada</strong><span>$' + money(d.entrada) + '</span></div></div>'
                  + '<div class="mini-row"><div><strong>Monto financiado</strong><span>$' + money(d.financiado) + '</span></div></div>'
                  + '<div class="mini-row"><div><strong>Cuotas</strong><span>' + d.numCuotas + '</span></div></div>'
                  + '</div>'
                  + '<div class="table-wrap section-block"><table class="table">'
                  + '<thead><tr><th>#</th><th>Vence</th><th>Saldo ini</th><th>Cuota</th><th>Inter&eacute;s</th><th>Abono</th><th>Saldo fin</th></tr></thead><tbody>';
            d.cuotas.forEach(function(c){
                h += '<tr><td>' + c.n + '</td><td>' + c.venc + '</td>'
                   + '<td>' + money(c.saldoIni) + '</td><td><strong>' + money(c.cuota) + '</strong></td>'
                   + '<td>' + money(c.interes) + '</td><td>' + money(c.abono) + '</td><td>' + money(c.saldoFin) + '</td></tr>';
            });
            h += '</tbody></table></div></div>';
            box.innerHTML = h;
        })
        .catch(function(){ box.innerHTML = '<div class="alert alert-error">Error de red al simular.</div>'; });
    }

    function validarPago(){
        if (!esCredito()) return true;            // CONTADO: sin validacion extra
        if (!cuotasValidas()){
            alert('El credito requiere un numero de cuotas mayor a cero.');
            document.getElementById('fNumCuotas').focus();
            return false;
        }
        if (!simOk){
            alert('Primero simula el credito para revisar las cuotas; luego finaliza la compra.');
            simular();
            return false;
        }
        return true;
    }

    togglePago();
</script>
</body>
</html>
