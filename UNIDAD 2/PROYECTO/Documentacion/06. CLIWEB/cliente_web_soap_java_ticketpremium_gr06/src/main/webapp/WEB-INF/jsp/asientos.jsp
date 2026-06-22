<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="jakarta.tags.core"%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>TicketPremium | Asientos</title>
    <script src="${pageContext.request.contextPath}/assets/js/theme.js?v=3"></script>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/app.css?v=3">
    <style>
        .seatmap { display:flex; flex-direction:column; gap:4px; overflow:auto; max-height:60vh; padding:8px; }
        .seatrow { display:flex; gap:4px; align-items:center; }
        .seatrow .rowlabel { width:34px; font-size:11px; color:#5f687a; }
        .seat { width:30px; height:28px; border:none; border-radius:5px; font-size:10px; cursor:pointer; color:#10241f; }
        .seat.free { background:#2ecc71; }       /* verde: libre */
        .seat.res  { background:#f1c40f; cursor:not-allowed; }   /* amarillo: reservado */
        .seat.occ  { background:#e74c3c; color:#fff; cursor:not-allowed; }  /* rojo: ocupado */
        .legend { display:flex; gap:16px; margin:10px 0; font-size:13px; }
        .legend span { display:inline-flex; align-items:center; gap:6px; }
        .dot { width:14px; height:14px; border-radius:4px; display:inline-block; }
    </style>
</head>
<body>
<div class="shell">
    <header class="card topbar">
        <div class="topbar-left">
            <div class="brand-mark"><span class="brand-dot"></span> TICKETPREMIUM · FIFA 2026</div>
            <h1 class="app-name">Mapa de asientos</h1>
            <div class="app-meta">${partidoDesc} · ${categoria} · $${precio}</div>
        </div>
        <div class="topbar-right">
            <a class="btn-secondary" href="${pageContext.request.contextPath}/home?partido=${partido}">Volver / ver carrito</a>
        </div>
    </header>

    <c:if test="${not empty flash}">
        <div class="alert ${flashType eq 'success' ? 'alert-success' : 'alert-error'}">${flash}</div>
    </c:if>

    <section class="card">
        <p class="panel-subtitle">Haz clic en un asiento <strong>libre</strong> para reservarlo y agregarlo al carrito. Se actualiza en tiempo real (cada 5s). Las reservas expiran a los 10 minutos.</p>
        <div class="legend">
            <span><i class="dot" style="background:#2ecc71"></i> Libre</span>
            <span><i class="dot" style="background:#f1c40f"></i> Reservado</span>
            <span><i class="dot" style="background:#e74c3c"></i> Ocupado</span>
        </div>

        <c:choose>
          <c:when test="${numFilas gt 0 and asientosPorFila gt 0}">
            <form method="post" action="${pageContext.request.contextPath}/carrito" class="seatmap">
                <input type="hidden" name="accion" value="reservarAgregar">
                <input type="hidden" name="idSeccion" value="${idSeccion}">
                <input type="hidden" name="numFilas" value="${numFilas}">
                <input type="hidden" name="asientosPorFila" value="${asientosPorFila}">
                <input type="hidden" name="categoria" value="${categoria}">
                <input type="hidden" name="precio" value="${precio}">
                <input type="hidden" name="partido" value="${partido}">
                <input type="hidden" name="codigoPartido" value="${codigoPartido}">
                <input type="hidden" name="partidoDesc" value="${partidoDesc}">
                <input type="hidden" name="seccionLabel" value="${categoria}">
                <c:forEach var="r" begin="1" end="${numFilas}">
                    <div class="seatrow">
                        <span class="rowlabel">F${r}</span>
                        <c:forEach var="a" begin="1" end="${asientosPorFila}">
                            <c:set var="k" value="F${r}|${a}"/>
                            <c:set var="est" value="${estados[k]}"/>
                            <button type="submit" name="seat" value="F${r}|${a}"
                                    id="s-F${r}-${a}"
                                    class="seat ${est eq 'OCUPADO' ? 'occ' : (est eq 'RESERVADO' ? 'res' : 'free')}"
                                    ${not empty est ? 'disabled' : ''}>${a}</button>
                        </c:forEach>
                    </div>
                </c:forEach>
            </form>
          </c:when>
          <c:otherwise>
            <div class="alert alert-error">No se recibieron las dimensiones de la seccion.</div>
          </c:otherwise>
        </c:choose>
    </section>
</div>

<script>
    const ctx = '${pageContext.request.contextPath}';
    const idSeccion = '${idSeccion}';
    function refrescarAsientos() {
        fetch(ctx + '/asientos-data?idSeccion=' + idSeccion)
            .then(r => r.json())
            .then(data => {
                document.querySelectorAll('.seat').forEach(b => { b.className = 'seat free'; b.disabled = false; });
                data.forEach(s => {
                    const el = document.getElementById('s-' + s.fila + '-' + s.asiento);
                    if (el) {
                        el.className = 'seat ' + (s.estado === 'OCUPADO' ? 'occ' : 'res');
                        el.disabled = true;
                    }
                });
            })
            .catch(() => {});
    }
    setInterval(refrescarAsientos, 5000);
</script>
</body>
</html>
