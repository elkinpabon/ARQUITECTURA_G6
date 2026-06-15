<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="jakarta.tags.core"%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>TicketPremium | Mundial FIFA 2026 - Boletos</title>
    <script src="${pageContext.request.contextPath}/assets/js/theme.js?v=3"></script>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/app.css?v=3">
    <style>
        .ico{ width:17px; height:17px; stroke:currentColor; stroke-width:2; fill:none;
              stroke-linecap:round; stroke-linejoin:round; vertical-align:-3px; flex:none; }

        /* ---------- hero ---------- */
        .hero{ display:grid; grid-template-columns:1.15fr .85fr; gap:30px; align-items:center;
               padding:48px 0 40px; perspective:900px; }
        .eyebrow{ display:inline-flex; align-items:center; gap:8px; font-size:.74rem; font-weight:700;
            letter-spacing:1.4px; text-transform:uppercase; color:var(--primary);
            background:var(--primary-tint); border:1px solid var(--border);
            padding:8px 14px; border-radius:999px; }
        .eyebrow .dotp{ width:7px; height:7px; border-radius:50%; background:var(--success); animation:pul 1.8s infinite; }
        @keyframes pul{ 0%,100%{opacity:1} 50%{opacity:.4} }
        h1.big{ margin:20px 0 14px; font-size:clamp(2.1rem,4.8vw,3.5rem); line-height:1.06;
                letter-spacing:-.04em; font-weight:800; }
        h1.big em{ font-style:normal; color:var(--primary); }
        .lead{ margin:0 0 24px; color:var(--muted); font-size:1.02rem; line-height:1.7; max-width:54ch; }
        .cta-row{ display:flex; gap:12px; flex-wrap:wrap; }
        .stats{ display:flex; gap:34px; margin-top:36px; flex-wrap:wrap; }
        .stat b{ display:block; font-size:1.8rem; font-weight:800; letter-spacing:-.03em; color:var(--primary); }
        .stat span{ color:var(--muted); font-size:.78rem; font-weight:600; letter-spacing:.6px; text-transform:uppercase; }

        /* balon flotante con chips orbitales (colores planos) */
        .ballwrap{ display:grid; place-items:center; position:relative; min-height:360px; transform-style:preserve-3d; }
        .ring{ position:absolute; width:320px; height:320px; border-radius:50%;
               border:1px dashed var(--border); animation:spin 28s linear infinite; }
        .ring.r2{ width:410px; height:410px; opacity:.6; animation-duration:42s; animation-direction:reverse; }
        @keyframes spin{ to{ transform:rotate(360deg); } }
        .orb{ position:absolute; padding:9px 14px; border-radius:9px; font-size:.78rem; font-weight:700;
            background:var(--surface); border:1px solid var(--border); box-shadow:var(--shadow);
            display:flex; gap:7px; align-items:center; color:var(--text); }
        .orb svg{ width:15px; height:15px; color:var(--primary); }
        .o1{ top:8%; left:6%;   animation:flt 5.2s ease-in-out infinite; }
        .o2{ bottom:14%; left:0; animation:flt 6.1s .6s ease-in-out infinite; }
        .o3{ top:18%; right:2%;  animation:flt 5.7s .3s ease-in-out infinite; }
        .o4{ bottom:6%; right:8%; animation:flt 6.6s .9s ease-in-out infinite; }
        @keyframes flt{ 0%,100%{transform:translateY(0)} 50%{transform:translateY(-12px)} }
        .ball{ width:215px; animation:flt 6s ease-in-out infinite; filter:drop-shadow(0 14px 22px rgba(15,76,129,.25)); }
        .ball .rot{ transform-origin:115px 115px; animation:spin 36s linear infinite; }
        .ball circle.base{ fill:#f4f6f9; stroke:#c8d2e0; stroke-width:2; }
        html[data-theme="dark"] .ball circle.base{ fill:#dfe5ee; }

        /* ---------- filtros ---------- */
        .filters{ display:grid; grid-template-columns:2fr 1fr 1fr; gap:12px; padding:14px;
            background:var(--surface); border:1px solid var(--border); border-radius:var(--radius);
            box-shadow:var(--shadow); margin-bottom:24px; }
        .f-field{ position:relative; }
        .f-field > svg{ position:absolute; left:13px; top:50%; transform:translateY(-50%);
                        width:16px; height:16px; color:var(--muted); }
        .filters input,.filters select{ width:100%; padding:12px 13px 12px 40px; border-radius:var(--radius-sm);
            font:inherit; font-size:.9rem; border:1px solid var(--border); background:var(--surface);
            color:var(--text); outline:none; }
        .filters input::placeholder{ color:var(--muted); }
        .filters input:focus,.filters select:focus{ border-color:var(--primary); box-shadow:0 0 0 3px var(--primary-tint); }

        .sec-head{ display:flex; align-items:baseline; gap:14px; margin:34px 0 16px; }
        .sec-head h2{ margin:0; font-size:1.55rem; font-weight:800; letter-spacing:-.03em; }
        .sec-head span{ color:var(--muted); font-size:.88rem; }

        /* ---------- tarjetas de partido ---------- */
        .matches{ display:grid; grid-template-columns:repeat(auto-fill,minmax(330px,1fr)); gap:14px; padding-bottom:26px; }
        .match-card{ background:var(--surface); border:1px solid var(--border); border-radius:var(--radius);
            padding:18px; display:flex; flex-direction:column; gap:14px; box-shadow:var(--shadow);
            transition:transform .2s, border-color .2s, box-shadow .2s;
            opacity:0; transform:translateY(22px); }
        .match-card.in{ opacity:1; transform:translateY(0);
            transition:opacity .5s ease, transform .5s ease, border-color .2s, box-shadow .2s; }
        .match-card:hover{ transform:translateY(-4px); border-color:var(--primary);
            box-shadow:0 10px 26px rgba(15,40,90,.14); }
        .match-head{ display:flex; justify-content:space-between; align-items:center; font-size:.8rem; color:var(--muted); }
        .grupo-chip{ background:var(--primary-tint); border:1px solid var(--border); color:var(--primary);
            font-weight:700; border-radius:6px; padding:4px 11px; font-size:.74rem; letter-spacing:.5px; }
        .fechachip{ display:inline-flex; align-items:center; gap:6px; }
        .fechachip svg{ width:14px; height:14px; }
        .teams{ display:grid; grid-template-columns:1fr auto 1fr; align-items:center; gap:8px; }
        .team{ display:flex; flex-direction:column; align-items:center; gap:9px; text-align:center; }
        .team .fwrap{ width:56px; height:40px; border-radius:6px; overflow:hidden;
            border:1px solid var(--border); box-shadow:var(--shadow); background:var(--surface-2); }
        .team img{ width:100%; height:100%; object-fit:cover; display:block; }
        .team strong{ font-size:.9rem; line-height:1.25; font-weight:700; }
        .vs{ font-weight:800; font-size:.76rem; color:var(--muted); border:1px solid var(--border);
            border-radius:50%; width:34px; height:34px; display:grid; place-items:center; background:var(--surface-2); }
        .venue{ display:flex; flex-direction:column; gap:7px; font-size:.84rem; color:var(--muted);
            border-top:1px solid var(--border); padding-top:12px; }
        .venue span{ display:inline-flex; align-items:center; gap:8px; }
        .venue svg{ width:15px; height:15px; color:var(--primary); }
        .btn-card{ display:flex; align-items:center; justify-content:center; gap:9px; text-decoration:none;
            background:var(--primary); color:var(--on-primary); font-weight:700; font-size:.9rem;
            padding:12px; border-radius:var(--radius-sm); transition:background .18s; }
        .btn-card svg{ width:16px; height:16px; transition:transform .2s; }
        .btn-card:hover{ background:var(--primary-strong); }
        .btn-card:hover svg{ transform:translateX(4px); }
        .nores{ display:none; text-align:center; color:var(--muted); padding:40px 0; }

        .foot{ border-top:1px solid var(--border); margin-top:26px; padding:24px 0 32px;
            display:flex; justify-content:space-between; gap:14px; flex-wrap:wrap;
            color:var(--muted); font-size:.82rem; }

        @media (max-width:1020px){
            .hero{ grid-template-columns:1fr; padding-top:30px; }
            .ballwrap{ min-height:320px; order:-1; }
        }
        @media (max-width:720px){ .filters{ grid-template-columns:1fr; } .stats{ gap:22px; } }
        @media (prefers-reduced-motion:reduce){
            *{ animation:none !important; transition:none !important; }
            .match-card{ opacity:1; transform:none; }
        }
    </style>
</head>
<body>

<%-- sprite de iconos SVG (trazo profesional, sin emojis) --%>
<svg width="0" height="0" style="position:absolute">
    <symbol id="i-search" viewBox="0 0 24 24"><circle cx="11" cy="11" r="7"/><path d="M21 21l-4.3-4.3"/></symbol>
    <symbol id="i-cal" viewBox="0 0 24 24"><rect x="3" y="5" width="18" height="16" rx="2"/><path d="M16 3v4M8 3v4M3 11h18"/></symbol>
    <symbol id="i-pin" viewBox="0 0 24 24"><path d="M20 10c0 6-8 12-8 12S4 16 4 10a8 8 0 1 1 16 0Z"/><circle cx="12" cy="10" r="3"/></symbol>
    <symbol id="i-stadium" viewBox="0 0 24 24"><ellipse cx="12" cy="7" rx="9" ry="3.5"/><path d="M3 7v8c0 1.9 4 3.5 9 3.5s9-1.6 9-3.5V7"/><path d="M8 10.4V18M16 10.4V18"/></symbol>
    <symbol id="i-ticket" viewBox="0 0 24 24"><path d="M3 9V7a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2v2a3 3 0 0 0 0 6v2a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-2a3 3 0 0 0 0-6Z"/><path d="M13 5v2M13 11v2M13 17v2"/></symbol>
    <symbol id="i-arrow" viewBox="0 0 24 24"><path d="M5 12h14M13 6l6 6-6 6"/></symbol>
    <symbol id="i-globe" viewBox="0 0 24 24"><circle cx="12" cy="12" r="9"/><path d="M3 12h18M12 3c2.7 2.6 4 5.9 4 9s-1.3 6.4-4 9c-2.7-2.6-4-5.9-4-9s1.3-6.4 4-9Z"/></symbol>
    <symbol id="i-card" viewBox="0 0 24 24"><rect x="2" y="5" width="20" height="14" rx="2"/><path d="M2 10h20M6 15h4"/></symbol>
    <symbol id="i-trophy" viewBox="0 0 24 24"><path d="M8 21h8M12 17v4M7 4h10v6a5 5 0 0 1-10 0V4Z"/><path d="M7 6H4a2 2 0 0 0 2 4h1M17 6h3a2 2 0 0 1-2 4h-1"/></symbol>
    <symbol id="i-user" viewBox="0 0 24 24"><circle cx="12" cy="8" r="4"/><path d="M4 21c0-4 3.6-6 8-6s8 2 8 6"/></symbol>
    <symbol id="i-out" viewBox="0 0 24 24"><path d="M9 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h4M16 17l5-5-5-5M21 12H9"/></symbol>
    <symbol id="i-shield" viewBox="0 0 24 24"><path d="M12 3l8 3v6c0 5-3.5 8-8 9-4.5-1-8-4-8-9V6l8-3Z"/><path d="M9 12l2 2 4-4"/></symbol>
    <symbol id="i-live" viewBox="0 0 24 24"><circle cx="12" cy="12" r="2.5"/><path d="M7.5 7.5a6.4 6.4 0 0 0 0 9M16.5 7.5a6.4 6.4 0 0 1 0 9M4.7 4.7a10.4 10.4 0 0 0 0 14.6M19.3 4.7a10.4 10.4 0 0 1 0 14.6"/></symbol>
</svg>

<div class="shell">
    <%-- ============ TOPBAR (mismo estilo en todo el sitio) ============ --%>
    <header class="card topbar">
        <div class="topbar-left">
            <div class="brand-mark"><span class="brand-dot"></span> TICKETPREMIUM · FIFA 2026</div>
            <nav class="nav-links">
                <a class="active" href="${pageContext.request.contextPath}/partidos">Partidos</a>
                <c:if test="${logueado}">
                    <a href="${pageContext.request.contextPath}/home">Carrito y compra</a>
                    <a href="${pageContext.request.contextPath}/compras">Mis compras</a>
                    <a href="${pageContext.request.contextPath}/cuenta">Mi cuenta</a>
                    <c:if test="${admin}"><a href="${pageContext.request.contextPath}/admin-panel">Administracion</a></c:if>
                </c:if>
            </nav>
        </div>
        <div class="topbar-right">
            <button class="theme-toggle" type="button" onclick="toggleTheme()" title="Cambiar tema claro/oscuro">
                <svg class="t-moon" viewBox="0 0 24 24"><path d="M21 12.8A9 9 0 1 1 11.2 3 7 7 0 0 0 21 12.8Z"/></svg>
                <svg class="t-sun" viewBox="0 0 24 24"><circle cx="12" cy="12" r="4"/><path d="M12 2v2M12 20v2M4.9 4.9l1.4 1.4M17.7 17.7l1.4 1.4M2 12h2M20 12h2M4.9 19.1l1.4-1.4M17.7 6.3l1.4-1.4"/></svg>
            </button>
            <c:choose>
                <c:when test="${logueado}">
                    <span class="app-meta">Hola, ${usuario.nombre}</span>
                    <a class="btn-danger" href="${pageContext.request.contextPath}/logout">Cerrar sesion</a>
                </c:when>
                <c:otherwise>
                    <a class="btn btn-admin" href="${pageContext.request.contextPath}/login">Iniciar sesion</a>
                </c:otherwise>
            </c:choose>
        </div>
    </header>

    <%-- ============ HERO ============ --%>
    <section class="hero" id="heroTilt">
        <div>
            <span class="eyebrow"><span class="dotp"></span> 11 jun — 19 jul 2026 · USA · Canada · Mexico</span>
            <h1 class="big">Vive el <em>Mundial 2026</em><br>desde el mejor asiento.</h1>
            <p class="lead">Boletos oficiales de la fase de grupos con mapa interactivo del estadio,
               seleccion de asientos <strong>en tiempo real</strong> y pago de contado o a credito con
               tabla de amortizacion.</p>
            <div class="cta-row">
                <a class="btn" href="#cartelera">Explorar partidos
                    <svg class="ico"><use href="#i-arrow"/></svg></a>
                <c:if test="${not logueado}">
                    <a class="btn-secondary" href="${pageContext.request.contextPath}/login">
                        <svg class="ico"><use href="#i-shield"/></svg> Ya tengo cuenta</a>
                </c:if>
            </div>
            <div class="stats">
                <div class="stat"><b data-n="${partidosCount}">0</b><span>Partidos</span></div>
                <div class="stat"><b data-n="48">0</b><span>Selecciones</span></div>
                <div class="stat"><b data-n="16">0</b><span>Estadios</span></div>
                <div class="stat"><b data-n="3">0</b><span>Paises sede</span></div>
            </div>
        </div>

        <div class="ballwrap" id="ballwrap">
            <span class="ring"></span><span class="ring r2"></span>
            <svg class="ball" viewBox="0 0 230 230">
                <circle class="base" cx="115" cy="115" r="106"/>
                <g class="rot" fill="#16213a">
                    <path d="M115 78 L150 103 L137 144 L93 144 L80 103 Z"/>
                    <path d="M115 10 L138 28 L130 54 L100 54 L92 28 Z"/>
                    <path d="M208 92 L219 122 L202 147 L178 138 L180 108 Z"/>
                    <path d="M22 92 L50 108 L52 138 L28 147 L11 122 Z"/>
                    <path d="M74 204 L66 176 L88 158 L112 172 L106 202 Z"/>
                    <path d="M156 204 L124 202 L118 172 L142 158 L164 176 Z"/>
                </g>
                <g class="rot" stroke="#16213a" stroke-width="3" fill="none" opacity=".45">
                    <path d="M115 78 L115 54M150 103 L180 108M137 144 L142 158M93 144 L88 158M80 103 L52 108"/>
                </g>
            </svg>
            <span class="orb o1"><svg class="ico"><use href="#i-live"/></svg> Asientos en vivo</span>
            <span class="orb o2"><svg class="ico"><use href="#i-ticket"/></svg> Cat 1 — 4 · desde $60</span>
            <span class="orb o3"><svg class="ico"><use href="#i-card"/></svg> Contado o credito</span>
            <span class="orb o4"><svg class="ico"><use href="#i-stadium"/></svg> 16 sedes oficiales</span>
        </div>
    </section>

    <c:if test="${not empty errorCarga}">
        <div class="alert alert-error">${errorCarga}</div>
    </c:if>

    <%-- ============ CARTELERA ============ --%>
    <div class="sec-head" id="cartelera">
        <h2>Fase de grupos</h2>
        <span>${partidosCount} partidos · calendario oficial</span>
    </div>

    <div class="filters">
        <div class="f-field">
            <svg><use href="#i-search"/></svg>
            <input type="text" id="fBuscar" placeholder="Buscar seleccion, estadio o ciudad..." oninput="filtrar()">
        </div>
        <div class="f-field">
            <svg><use href="#i-trophy"/></svg>
            <select id="fGrupo" onchange="filtrar()">
                <option value="">Todos los grupos</option>
                <c:forEach var="g" items="${['A','B','C','D','E','F','G','H','I','J','K','L']}">
                    <option value="${g}">Grupo ${g}</option>
                </c:forEach>
            </select>
        </div>
        <div class="f-field">
            <svg><use href="#i-globe"/></svg>
            <select id="fPais" onchange="filtrar()">
                <option value="">Todas las sedes</option>
                <option>Estados Unidos</option><option>Mexico</option><option>Canada</option>
            </select>
        </div>
    </div>

    <div class="matches" id="matches">
        <c:forEach items="${partidos}" var="p">
            <article class="match-card"
                     data-texto="${p.equipoLocal} ${p.equipoVisita} ${p.estadio} ${p.ciudad}"
                     data-grupo="${p.grupo}" data-pais="${p.pais}">
                <div class="match-head">
                    <span class="grupo-chip">GRUPO ${p.grupo}</span>
                    <span class="fechachip"><svg><use href="#i-cal"/></svg>
                        <span class="fecha" data-fecha="${p.fecha}">${p.fecha}</span></span>
                </div>
                <div class="teams">
                    <div class="team">
                        <span class="fwrap"><img class="flag" data-name="${p.equipoLocal}" alt="${p.equipoLocal}" loading="lazy"></span>
                        <strong>${p.equipoLocal}</strong>
                    </div>
                    <span class="vs">VS</span>
                    <div class="team">
                        <span class="fwrap"><img class="flag" data-name="${p.equipoVisita}" alt="${p.equipoVisita}" loading="lazy"></span>
                        <strong>${p.equipoVisita}</strong>
                    </div>
                </div>
                <div class="venue">
                    <span><svg><use href="#i-stadium"/></svg> ${p.estadio}</span>
                    <span><svg><use href="#i-pin"/></svg> ${p.ciudad}, ${p.pais}</span>
                </div>
                <a class="btn-card" href="${pageContext.request.contextPath}/mashup?codigoPartido=${p.codigo}">
                    Ver estadio y comprar <svg><use href="#i-arrow"/></svg></a>
            </article>
        </c:forEach>
    </div>
    <div class="nores" id="nores">No hay partidos que coincidan con tu busqueda.</div>

    <footer class="foot">
        <span>TicketPremium · Copa Mundial FIFA 2026&trade;</span>
        <span>Arquitectura de Software · Grupo 6 · ESPE — Josue Marin · Mikaela Salcedo · Elkin Pabon</span>
    </footer>
</div>

<script>
// banderas reales: flagcdn (ISO 3166), nombres tal como estan en la BD
const FLAGS={
 "Mexico":"mx","Sudafrica":"za","Corea del Sur":"kr","Chequia":"cz",
 "Canada":"ca","Suiza":"ch","Qatar":"qa","Bosnia y Herzegovina":"ba",
 "Brasil":"br","Marruecos":"ma","Haiti":"ht","Escocia":"gb-sct",
 "Estados Unidos":"us","Paraguay":"py","Australia":"au","Turquia":"tr",
 "Alemania":"de","Curazao":"cw","Costa de Marfil":"ci","Ecuador":"ec",
 "Paises Bajos":"nl","Japon":"jp","Tunez":"tn","Suecia":"se",
 "Belgica":"be","Egipto":"eg","Iran":"ir","Nueva Zelanda":"nz",
 "Espana":"es","Cabo Verde":"cv","Arabia Saudi":"sa","Uruguay":"uy",
 "Francia":"fr","Senegal":"sn","Noruega":"no","Irak":"iq",
 "Argentina":"ar","Argelia":"dz","Austria":"at","Jordania":"jo",
 "Portugal":"pt","Uzbekistan":"uz","Colombia":"co","Republica Democratica del Congo":"cd",
 "Inglaterra":"gb-eng","Croacia":"hr","Ghana":"gh","Panama":"pa"};

document.querySelectorAll('img.flag').forEach(img=>{
  const code=FLAGS[(img.dataset.name||'').trim()];
  if(code){ img.src='https://flagcdn.com/w160/'+code+'.png'; }
  else { img.parentElement.style.display='none'; }
});

const MESES=['ene','feb','mar','abr','may','jun','jul','ago','sep','oct','nov','dic'];
document.querySelectorAll('.fecha').forEach(el=>{
  const f=el.dataset.fecha||'';
  const m=f.match(/(\d{4})-(\d{2})-(\d{2})[ T](\d{2}):(\d{2})/);
  if(m) el.textContent=parseInt(m[3])+' '+MESES[parseInt(m[2])-1]+' · '+m[4]+':'+m[5];
});

function filtrar(){
  const q=document.getElementById('fBuscar').value.toLowerCase();
  const g=document.getElementById('fGrupo').value;
  const pais=document.getElementById('fPais').value;
  let visibles=0;
  document.querySelectorAll('.match-card').forEach(card=>{
    const show=(!q || card.dataset.texto.toLowerCase().includes(q))
            && (!g || card.dataset.grupo===g)
            && (!pais || card.dataset.pais===pais);
    card.style.display=show?'':'none';
    if(show) visibles++;
  });
  document.getElementById('nores').style.display=visibles?'none':'block';
}

// entrada escalonada de tarjetas al hacer scroll
const io=new IntersectionObserver(es=>{
  es.forEach((e,i)=>{ if(e.isIntersecting){ setTimeout(()=>e.target.classList.add('in'), (i%6)*70); io.unobserve(e.target); } });
},{threshold:.08});
document.querySelectorAll('.match-card').forEach(c=>io.observe(c));

// contadores animados del hero
document.querySelectorAll('.stat b').forEach(el=>{
  const fin=parseInt(el.dataset.n)||0; const t0=performance.now(), dur=1300;
  function paso(t){ const k=Math.min(1,(t-t0)/dur); el.textContent=Math.round(fin*(1-Math.pow(1-k,3))); if(k<1) requestAnimationFrame(paso); }
  requestAnimationFrame(paso);
});

// tilt 3D sutil del balon con el mouse
const hero=document.getElementById('heroTilt'), bw=document.getElementById('ballwrap');
if(hero && matchMedia('(pointer:fine)').matches){
  hero.addEventListener('mousemove',e=>{
    const r=hero.getBoundingClientRect();
    const x=(e.clientX-r.left)/r.width-.5, y=(e.clientY-r.top)/r.height-.5;
    bw.style.transform='rotateY('+(x*9)+'deg) rotateX('+(-y*7)+'deg)';
  });
  hero.addEventListener('mouseleave',()=>{ bw.style.transform='rotateY(0) rotateX(0)'; });
}
</script>
</body>
</html>
