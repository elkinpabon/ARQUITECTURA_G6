<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="jakarta.tags.core"%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>TicketPremium | Mapa del estadio (Mashup)</title>
    <script src="${pageContext.request.contextPath}/assets/js/theme.js?v=3"></script>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/app.css?v=3">
    <style>
        .mashup{ display:grid; grid-template-columns:1.45fr 1fr; gap:16px; align-items:start; }
        @media (max-width:1080px){ .mashup{ grid-template-columns:1fr; } }

        /* ------- mapa del estadio ------- */
        #stadiumWrap{ width:100%; background:#0c1730; border:1px solid var(--border);
                      border-radius:12px; padding:4px; }
        #stadiumWrap svg{ width:100%; height:auto; display:block; }
        .zone path{ transition:filter .12s, opacity .12s; cursor:pointer; }
        .zone:hover path{ filter:brightness(1.22) saturate(1.15); }
        .zone.sel path{ stroke:#ffd34d; stroke-width:2.5; filter:brightness(1.18) drop-shadow(0 0 6px rgba(255,211,77,.85)); }
        .zone text{ pointer-events:none; }
        .legend{ display:flex; gap:14px; flex-wrap:wrap; font-size:12px; margin:10px 2px; }
        .legend i{ width:12px; height:12px; border-radius:3px; display:inline-block; margin-right:5px; vertical-align:middle; }
        #mapa{ width:100%; height:190px; border:0; border-radius:10px; margin-top:10px; }
        .slabel{ font-size:11px; color:var(--muted); }
        .cartpill{ background:#ffc940; color:#093868; border-radius:20px; padding:4px 12px; font-weight:700; }

        /* ------- panel derecho: anuncios estilo marketplace ------- */
        .cards{ display:flex; flex-direction:column; gap:10px; max-height:72vh; overflow:auto; padding:2px; }
        .tcard{ display:flex; gap:12px; align-items:center; background:var(--surface); border:1px solid var(--border); border-radius:10px;
                padding:8px; cursor:pointer; transition:box-shadow .12s, transform .12s; }
        .tcard:hover{ box-shadow:0 6px 18px rgba(20,40,90,.14); transform:translateY(-1px); border-color:var(--primary); }
        .tcard.sel{ outline:2px solid #ffc940; }
        .thumb{ width:128px; min-width:128px; height:78px; border-radius:8px; overflow:hidden; background:#0d1f44; }
        .thumb svg{ width:100%; height:100%; display:block; }
        .tinfo{ flex:1; min-width:0; }
        .tinfo .t1{ font-weight:700; font-size:13.5px; color:var(--text); }
        .tinfo .t2{ font-size:11.5px; color:var(--muted); margin-top:2px; }
        .tprice{ text-align:right; white-space:nowrap; }
        .tprice .p{ font-weight:800; font-size:16px; color:var(--text); }
        .tprice .pu{ font-size:10.5px; color:var(--muted); }
        .deal{ display:inline-block; margin-top:4px; font-size:10.5px; font-weight:700; color:#fff;
               border-radius:10px; padding:2px 8px; }
        .deal.d1{ background:#0e9f6e; } .deal.d2{ background:#31c48d; } .deal.d3{ background:#76a9fa; }
        .catdot{ width:9px; height:9px; border-radius:50%; display:inline-block; margin-right:5px; vertical-align:middle; }

        /* ------- detalle de la seccion (foto grande + asientos) ------- */
        #detailView{ display:none; }
        .bigview{ border-radius:12px; overflow:hidden; background:#0d1f44; position:relative; }
        .bigview svg{ width:100%; height:auto; display:block; }
        .bigview .cap{ position:absolute; left:10px; bottom:8px; background:rgba(9,16,34,.72); color:#e8eefb;
                       font-size:11.5px; padding:4px 10px; border-radius:9px; backdrop-filter:blur(2px); }
        .backbtn{ background:none; border:none; color:var(--primary); font-weight:700; cursor:pointer; font-size:12.5px; padding:0; margin-bottom:8px; }
        .seatmap{ display:flex; flex-direction:column; gap:3px; overflow:auto; max-height:40vh; padding:8px;
                  background:var(--surface-2); border:1px solid var(--border); border-radius:8px; margin-top:10px; }
        .seatrow{ display:flex; gap:3px; align-items:center; } .seatrow .rl{ width:34px; font-size:10px; color:var(--muted); }
        .seat{ width:26px; height:24px; border:none; border-radius:6px 6px 3px 3px; font-size:9px; cursor:pointer; color:#10241f; }
        .seat.free{ background:#2ecc71; } .seat.res{ background:#f1c40f; cursor:not-allowed; }
        .seat.mine{ background:#3b82f6; color:#fff; }
        .seat.occ{ background:#e74c3c; color:#fff; cursor:not-allowed; }

        .nav-links{ display:flex; gap:2px; align-items:center; flex-wrap:wrap; }
        .nav-links a{ color:rgba(255,255,255,.85); text-decoration:none; font-size:.88rem; font-weight:600;
            padding:8px 12px; border-radius:6px; }
        .nav-links a:hover{ background:rgba(255,255,255,.12); color:#fff; }
        .live{ display:inline-flex; align-items:center; gap:6px; font-size:.78rem; font-weight:800;
            border-radius:999px; padding:4px 12px; background:rgba(255,255,255,.14); color:#fff; }
        .live .dot{ width:8px; height:8px; border-radius:50%; background:#9aa3b5; }
        .live.on .dot{ background:#2ecc71; box-shadow:0 0 6px #2ecc71; animation:pulse 1.6s infinite; }
        @keyframes pulse{ 0%,100%{opacity:1} 50%{opacity:.4} }

        /* indicador de carga */
        .loader{ display:flex; flex-direction:column; align-items:center; justify-content:center;
                 gap:14px; padding:70px 0; color:var(--muted); font-size:.92rem; font-weight:600; }
        .loader.dark{ color:#9fb3d8; min-height:340px; }
        .spin{ width:38px; height:38px; border-radius:50%;
               border:4px solid var(--border); border-top-color:var(--primary);
               animation:rot .9s linear infinite; }
        .loader.dark .spin{ border-color:#22375f; border-top-color:#9fb3d8; }
        @keyframes rot{ to{ transform:rotate(360deg); } }

        /* modal "inicia sesion para reservar" */
        .modal-back{ display:none; position:fixed; inset:0; background:rgba(9,16,34,.62); z-index:1000;
            align-items:center; justify-content:center; backdrop-filter:blur(3px); }
        .modal-back.open{ display:flex; }
        .modal{ background:var(--surface); border:1px solid var(--border); border-radius:14px; padding:28px;
            width:min(420px, calc(100% - 32px));
            text-align:center; box-shadow:0 24px 70px rgba(0,0,0,.35); animation:pop .18s ease; }
        @keyframes pop{ from{ transform:scale(.92); opacity:0 } to{ transform:scale(1); opacity:1 } }
        .modal .mico{ font-size:46px; line-height:1; margin-bottom:10px; }
        .modal h3{ margin:0 0 8px; font-size:1.2rem; color:var(--text); }
        .modal p{ margin:0 0 18px; color:var(--muted); font-size:.92rem; }
        .modal .mbtns{ display:flex; gap:10px; justify-content:center; flex-wrap:wrap; }
    </style>
</head>
<body>
<div class="shell">
    <header class="card topbar">
        <div class="topbar-left">
            <div class="brand-mark"><span class="brand-dot"></span> TICKETPREMIUM · FIFA 2026</div>
            <nav class="nav-links">
                <a href="${pageContext.request.contextPath}/partidos">Partidos</a>
                <a href="${pageContext.request.contextPath}/compras">Mis compras</a>
                <a href="${pageContext.request.contextPath}/cuenta">Mi cuenta</a>
            </nav>
            <div class="app-meta" id="infoPartido">Cargando...</div>
        </div>
        <div class="topbar-right">
            <button class="theme-toggle" type="button" onclick="toggleTheme()" title="Cambiar tema claro/oscuro">
                <svg class="t-moon" viewBox="0 0 24 24"><path d="M21 12.8A9 9 0 1 1 11.2 3 7 7 0 0 0 21 12.8Z"/></svg>
                <svg class="t-sun" viewBox="0 0 24 24"><circle cx="12" cy="12" r="4"/><path d="M12 2v2M12 20v2M4.9 4.9l1.4 1.4M17.7 17.7l1.4 1.4M2 12h2M20 12h2M4.9 19.1l1.4-1.4M17.7 6.3l1.4-1.4"/></svg>
            </button>
            <span class="live" id="liveBadge"><span class="dot"></span><span id="liveTxt">CONECTANDO</span></span>
            <c:choose>
                <c:when test="${logueado}">
                    <span class="cartpill">Carrito: <span id="cartCount">0</span></span>
                    <a class="btn-secondary" href="${pageContext.request.contextPath}/home?partido=${codigoPartido}">Ir al carrito / pagar</a>
                </c:when>
                <c:otherwise>
                    <span class="cartpill" style="display:none">Carrito: <span id="cartCount">0</span></span>
                    <a class="btn-secondary" href="${pageContext.request.contextPath}/login?next=%2Fmashup%3FcodigoPartido%3D${codigoPartido}">Iniciar sesion para comprar</a>
                </c:otherwise>
            </c:choose>
        </div>
    </header>

    <div id="flash"></div>

    <div class="mashup">
        <section class="card">
            <h2 class="panel-title">Selecciona tu zona</h2>
            <p class="panel-subtitle">Plano del estadio en dos niveles. Pasa el mouse para resaltar y haz clic en una
               <strong>seccion</strong> para ver la vista desde el asiento y elegir tus lugares.</p>
            <div id="stadiumWrap">
                <div class="loader dark"><div class="spin"></div>Cargando el estadio y los datos del partido...</div>
            </div>
            <div class="legend" id="legend"></div>
            <iframe id="mapa" title="Ubicacion" src="about:blank"></iframe>
            <div class="slabel" id="estadioNota"></div>
        </section>

        <section class="card">
            <div id="listView">
                <h2 class="panel-title" id="adsTitle">Anuncios</h2>
                <p class="slabel" style="margin-bottom:8px;">Cada anuncio incluye la <strong>vista real desde la seccion</strong>. Haz clic para elegir asientos.</p>
                <div class="cards" id="cards">
                    <div class="loader"><div class="spin"></div>Cargando anuncios...</div>
                </div>
            </div>
            <div id="detailView">
                <button class="backbtn" onclick="volver()">&#8592; Volver a los anuncios</button>
                <h2 class="panel-title" id="secTitle">Asientos</h2>
                <p class="slabel" id="secSub"></p>
                <div class="bigview" id="bigview"></div>
                <div class="legend" style="margin-top:10px;">
                    <span><i style="background:#2ecc71"></i>Libre</span>
                    <span><i style="background:#3b82f6"></i>Tu reserva</span>
                    <span><i style="background:#f1c40f"></i>Reservado</span>
                    <span><i style="background:#e74c3c"></i>Ocupado</span>
                </div>
                <p class="slabel"><strong>1 clic</strong> = reservar &middot; <strong>doble clic</strong> en tu reserva (azul) = quitarla.</p>
                <div id="seatPanel"></div>
            </div>
        </section>
    </div>
</div>

<div class="modal-back" id="loginModal" onclick="if(event.target===this) cerrarModal()">
    <div class="modal">
        <div class="mico" style="display:flex; justify-content:center;">
            <svg width="52" height="52" viewBox="0 0 24 24" fill="none" stroke="#0f4c81" stroke-width="1.8"
                 stroke-linecap="round" stroke-linejoin="round" style="background:#eaf2ff; border-radius:16px; padding:10px;">
                <rect x="4" y="11" width="16" height="10" rx="2"/>
                <path d="M8 11V7a4 4 0 0 1 8 0v4"/><circle cx="12" cy="16" r="1.6"/>
            </svg>
        </div>
        <h3>Inicia sesion para reservar</h3>
        <p>Puedes explorar el estadio, los precios y la disponibilidad libremente,
           pero para <strong>reservar asientos</strong> y comprar necesitas una cuenta.</p>
        <div class="mbtns">
            <a class="btn" id="modalLoginBtn" href="#">Iniciar sesion</a>
            <button class="btn-secondary" type="button" onclick="cerrarModal()">Seguir mirando</button>
        </div>
    </div>
</div>

<script>
const ctx='${pageContext.request.contextPath}';
const codigoPartido=${codigoPartido};
const logueado=${logueado};   // ver es publico; reservar exige sesion
// paleta por categoria (CAT1 violeta premium -> CAT4 ambar)
const PAL=['#8b7cf6','#5ea8f7','#45cfae','#f2a65a','#f48fb1','#80deea'];
const COORDS={
 "Estadio Azteca":[19.3029,-99.1505],"Estadio Akron":[20.6819,-103.4626],"Estadio BBVA":[25.6694,-100.2447],
 "Mercedes-Benz Stadium":[33.7553,-84.4006],"SoFi Stadium":[33.9535,-118.3392],"BMO Field":[43.6332,-79.4185],
 "Levi's Stadium":[37.4030,-121.9700],"BC Place":[49.2768,-123.1119],"Gillette Stadium":[42.0909,-71.2643],
 "Lumen Field":[47.5952,-122.3316],"MetLife Stadium":[40.8135,-74.0745],"Lincoln Financial Field":[39.9008,-75.1675],
 "Hard Rock Stadium":[25.9580,-80.2389],"NRG Stadium":[29.6847,-95.4107],"Arrowhead Stadium":[39.0489,-94.4839],
 "AT&T Stadium":[32.7473,-97.0945]};

// geometria del bowl: anillo bajo (premium) y anillo alto
const CX=440, CY=290;
const RING={ lower:{rx1:205,ry1:142,rx2:272,ry2:198}, upper:{rx1:288,ry1:212,rx2:358,ry2:266} };
// arcos por categoria: [0]=laterales bajos N/S, [1]=fondos bajos O/E, [2]=laterales altos, [3]=fondos altos
const ARCS=[
 {ring:'lower', arcs:[[-128,-52],[52,128]]},
 {ring:'lower', arcs:[[142,218],[-38,38]]},
 {ring:'upper', arcs:[[-138,-42],[42,138]]},
 {ring:'upper', arcs:[[138,222],[-42,42]]}
];

let DATA=null, FLAT=[], secSel=null, pollTimer=null, mias=new Set();
let ws=null, wsOk=false;

/* ---- WebSocket de asientos en tiempo real (con fallback a polling) ---- */
function setLive(on){
  const b=document.getElementById('liveBadge');
  b.classList.toggle('on',on);
  document.getElementById('liveTxt').textContent=on?'EN VIVO':'POLLING';
}
function connectWS(){
  try{
    const url=(location.protocol==='https:'?'wss://':'ws://')+location.host+ctx+'/ws-asientos';
    ws=new WebSocket(url);
    ws.onopen=()=>{ wsOk=true; setLive(true); if(secSel) ws.send(String(secSel.idSeccion)); };
    ws.onmessage=e=>{
      try{ const d=JSON.parse(e.data);
        if(secSel && d.idSeccion===secSel.idSeccion) applySeats(d.asientos);
      }catch(_){}
    };
    ws.onclose=()=>{ wsOk=false; setLive(false); setTimeout(connectWS,3000); };
    ws.onerror=()=>{ try{ws.close();}catch(_){} };
  }catch(e){ wsOk=false; setLive(false); }
}

function flash(m,ok){ document.getElementById('flash').innerHTML='<div class="alert '+(ok?'alert-success':'alert-error')+'">'+m+'</div>'; }
function pt(a,rx,ry){ const r=a*Math.PI/180; return [CX+rx*Math.cos(r), CY+ry*Math.sin(r)]; }
function sideOf(a){ a=((a%360)+360)%360; if(a>=315||a<45) return 'E'; if(a<135) return 'S'; if(a<225) return 'O'; return 'N'; }
const SIDE_NAME={N:'Norte',S:'Sur',E:'Este',O:'Oeste'};

function cargar(){
  fetch(ctx+'/mashup-data?codigoPartido='+codigoPartido).then(r=>r.json()).then(d=>{
    DATA=d;
    document.getElementById('infoPartido').textContent=d.partido+' · '+d.estadio+' · '+d.ciudad+' · '+d.fecha;
    document.getElementById('estadioNota').textContent='Sede: '+d.estadio+' ('+d.ciudad+', '+d.pais+')';
    const c=COORDS[d.estadio];
    if(c){const[la,lo]=c; document.getElementById('mapa').src='https://www.openstreetmap.org/export/embed.html?bbox='
      +(lo-0.012)+'%2C'+(la-0.008)+'%2C'+(lo+0.012)+'%2C'+(la+0.008)+'&layer=mapnik&marker='+la+'%2C'+lo;}
    layout(d.categorias||[]);
    renderLegend(d.categorias); buildStadium(); buildCards();
  }).catch(e=>{
    flash('No se pudo cargar: '+e,false);
    document.getElementById('stadiumWrap').innerHTML='<div class="loader dark">No se pudo cargar el estadio. Intenta recargar la pagina.</div>';
    document.getElementById('cards').innerHTML='<div class="loader">No se pudieron cargar los anuncios.</div>';
  });
}

/* asigna a cada seccion su anillo, arco y angulos dentro del bowl */
function layout(cats){
  FLAT=[];
  cats.forEach((cat,ci)=>{
    const cfg=ARCS[ci%ARCS.length], n=cat.secciones.length;
    const nA=Math.ceil(n/2), nB=n-nA;
    cat.secciones.forEach((s,j)=>{
      const enA=j<nA, arc=cfg.arcs[enA?0:1], k=enA?j:j-nA, m=enA?nA:Math.max(nB,1);
      const span=(arc[1]-arc[0])/m, a1=arc[0]+k*span, a2=a1+span;
      const mid=(a1+a2)/2;
      FLAT.push({idSeccion:s.idSeccion,codigo:s.codigo,numFilas:s.numFilas,asientosPorFila:s.asientosPorFila,
        categoria:cat.categoria,precio:cat.precio,catIdx:ci,ring:cfg.ring,a1:a1,a2:a2,
        side:sideOf(mid),deck:(cfg.ring==='lower'?'bajo':'alto')});
    });
  });
}

function renderLegend(cats){
  document.getElementById('legend').innerHTML=(cats||[]).map((c,i)=>
    '<span><i style="background:'+PAL[i%PAL.length]+'"></i>'+c.categoria+' · $'+c.precio+'</span>').join('');
}

/* trazo de una seccion: sector anular muestreando los dos arcos */
function secPath(a1,a2,R,gap){
  a1+=gap; a2-=gap;
  const n=Math.max(2,Math.ceil((a2-a1)/9));
  let p='M';
  for(let i=0;i<=n;i++){ const a=a1+(a2-a1)*i/n; const[x,y]=pt(a,R.rx1,R.ry1); p+=(i?' L':' ')+x.toFixed(1)+' '+y.toFixed(1); }
  for(let i=n;i>=0;i--){ const a=a1+(a2-a1)*i/n; const[x,y]=pt(a,R.rx2,R.ry2); p+=' L'+x.toFixed(1)+' '+y.toFixed(1); }
  return p+' Z';
}

function buildStadium(){
  let svg='<svg viewBox="0 0 880 580" xmlns="http://www.w3.org/2000/svg">';
  // muro exterior + explanada (colores planos)
  svg+='<ellipse cx="'+CX+'" cy="'+CY+'" rx="372" ry="278" fill="#13203a" stroke="#27406f" stroke-width="3"/>';
  svg+='<ellipse cx="'+CX+'" cy="'+CY+'" rx="362" ry="269" fill="none" stroke="#1c2f55" stroke-width="1.5"/>';
  // pasillo entre anillos
  svg+='<ellipse cx="'+CX+'" cy="'+CY+'" rx="280" ry="205" fill="none" stroke="#22375f" stroke-width="9" opacity=".85"/>';
  // pasillo interior (entre nivel bajo y cancha)
  svg+='<ellipse cx="'+CX+'" cy="'+CY+'" rx="198" ry="136" fill="#11203d" stroke="#22375f" stroke-width="2"/>';

  // esquinas decorativas del anillo bajo (vomitorios / escaleras)
  [[-52,-38],[38,52],[128,142],[218,232]].forEach(g=>{
    svg+='<path d="'+secPath(g[0],g[1],RING.lower,1)+'" fill="#16233f" stroke="#22375f" stroke-width="1"/>';
    const[mx,my]=pt((g[0]+g[1])/2,(RING.lower.rx1+RING.lower.rx2)/2,(RING.lower.ry1+RING.lower.ry2)/2);
    svg+='<text x="'+mx+'" y="'+(my+3)+'" text-anchor="middle" font-size="9" fill="#42598a">&#9650;</text>';
  });

  // cancha
  svg+='<rect x="300" y="202" width="280" height="176" rx="5" fill="#1f7a43" stroke="#e9f5ec" stroke-width="2"/>';
  for(let i=1;i<7;i++){ if(i%2) svg+='<rect x="'+(300+i*40)+'" y="202" width="40" height="176" fill="#ffffff" opacity="0.045"/>'; }
  svg+='<line x1="440" y1="202" x2="440" y2="378" stroke="#e9f5ec" stroke-width="2"/>';
  svg+='<circle cx="440" cy="290" r="28" fill="none" stroke="#e9f5ec" stroke-width="2"/>';
  svg+='<circle cx="440" cy="290" r="2.6" fill="#e9f5ec"/>';
  svg+='<rect x="300" y="245" width="38" height="90" fill="none" stroke="#e9f5ec" stroke-width="2"/>';
  svg+='<rect x="542" y="245" width="38" height="90" fill="none" stroke="#e9f5ec" stroke-width="2"/>';
  svg+='<rect x="300" y="266" width="15" height="48" fill="none" stroke="#e9f5ec" stroke-width="1.4"/>';
  svg+='<rect x="565" y="266" width="15" height="48" fill="none" stroke="#e9f5ec" stroke-width="1.4"/>';

  // secciones
  FLAT.forEach((s,i)=>{
    const R=RING[s.ring];
    const[cxs,cys]=pt((s.a1+s.a2)/2,(R.rx1+R.rx2)/2,(R.ry1+R.ry2)/2);
    svg+='<g class="zone" id="zone-'+i+'" onclick="selZone('+i+')">'
       +'<path d="'+secPath(s.a1,s.a2,R,1.4)+'" fill="'+PAL[s.catIdx%PAL.length]+'" stroke="#0d1830" stroke-width="1.4"/>'
       +'<rect x="'+(cxs-24)+'" y="'+(cys-11)+'" width="48" height="15" rx="7.5" fill="#ffffff" opacity=".92"/>'
       +'<text x="'+cxs+'" y="'+(cys+1)+'" text-anchor="middle" font-size="10.5" font-weight="800" fill="#1a2238">$'+s.precio+'</text>'
       +'<text x="'+cxs+'" y="'+(cys+13)+'" text-anchor="middle" font-size="8" font-weight="600" fill="#0f1a33">'+s.codigo+'</text>'
       +'</g>';
  });

  // puertas y brujula
  [['NORTE',CX,26],['SUR',CX,572]].forEach(t=>svg+='<text x="'+t[1]+'" y="'+t[2]+'" text-anchor="middle" fill="#7e95c4" font-size="14" font-weight="800" letter-spacing="3">'+t[0]+'</text>');
  svg+='<text x="858" y="295" text-anchor="end" fill="#7e95c4" font-size="14" font-weight="800" letter-spacing="3">ESTE</text>';
  svg+='<text x="22" y="295" text-anchor="start" fill="#7e95c4" font-size="14" font-weight="800" letter-spacing="3">OESTE</text>';
  svg+='</svg>';
  document.getElementById('stadiumWrap').innerHTML=svg;
}

/* ============ "FOTO": vista desde la seccion (render en perspectiva) ============ */
function viewSVG(s,suf){
  const lateral=(s.side==='N'||s.side==='S');
  const upper=(s.deck==='alto');
  // marco de la perspectiva (trapecio de la cancha)
  const fy=upper?150:142, ny=upper?218:252;                  // y lejos / cerca
  const fx0=upper?120:96, fx1=upper?320:344;                 // borde lejano
  const nx0=upper?-12:-34, nx1=upper?452:474;                // borde cercano
  const seatTop=upper?218:252;
  function P(u,v){ const xf=fx0+(fx1-fx0)*u, xn=nx0+(nx1-nx0)*u;
    return [(xf*(1-v)+xn*v).toFixed(1), (fy+(ny-fy)*v).toFixed(1)]; }
  function poly(pts,attrs){ return '<polygon points="'+pts.map(p=>p.join(',')).join(' ')+'" '+attrs+'/>'; }
  function line(u1,v1,u2,v2,w){ const a=P(u1,v1), b=P(u2,v2);
    return '<line x1="'+a[0]+'" y1="'+a[1]+'" x2="'+b[0]+'" y2="'+b[1]+'" stroke="#eef7f0" stroke-width="'+(w||1.6)+'" opacity=".92"/>'; }

  let g='<svg viewBox="0 0 440 260" xmlns="http://www.w3.org/2000/svg" preserveAspectRatio="xMidYMid slice">';
  g+='<defs>'
   +'<linearGradient id="sky'+suf+'" x1="0" y1="0" x2="0" y2="1">'
   +'<stop offset="0%" stop-color="#0a1733"/><stop offset="100%" stop-color="#23406e"/></linearGradient>'
   +'<linearGradient id="grass'+suf+'" x1="0" y1="0" x2="0" y2="1">'
   +'<stop offset="0%" stop-color="#34a862"/><stop offset="100%" stop-color="#1e7a44"/></linearGradient>'
   +'<radialGradient id="glow'+suf+'" cx="50%" cy="50%" r="50%">'
   +'<stop offset="0%" stop-color="#ffe9a8" stop-opacity=".9"/><stop offset="100%" stop-color="#ffe9a8" stop-opacity="0"/></radialGradient>'
   +'<pattern id="crowd'+suf+'" width="9" height="7" patternUnits="userSpaceOnUse">'
   +'<rect width="9" height="7" fill="#2b3d63"/>'
   +'<circle cx="2" cy="2" r="1.2" fill="#46608f"/><circle cx="6.5" cy="3.5" r="1.2" fill="#9aa7c4"/>'
   +'<circle cx="4" cy="5.5" r="1.1" fill="#5d76a3"/><circle cx="8" cy="1.5" r="1" fill="#3a517d"/>'
   +'</pattern></defs>';
  // cielo + techo del estadio
  g+='<rect width="440" height="120" fill="url(#sky'+suf+')"/>';
  g+='<path d="M0 78 Q220 50 440 78 L440 96 Q220 70 0 96 Z" fill="#0a1428"/>';
  g+='<circle cx="70" cy="62" r="26" fill="url(#glow'+suf+')"/><circle cx="370" cy="62" r="26" fill="url(#glow'+suf+')"/>';
  g+='<circle cx="70" cy="62" r="3.5" fill="#fff3c4"/><circle cx="370" cy="62" r="3.5" fill="#fff3c4"/>';
  // tribuna de enfrente (multitud)
  g+='<path d="M0 96 Q220 70 440 96 L440 '+fy+' L0 '+fy+' Z" fill="url(#crowd'+suf+')"/>';
  g+='<line x1="0" y1="118" x2="440" y2="112" stroke="#1c2c4e" stroke-width="2"/>';
  // cancha en perspectiva
  g+=poly([P(0,0),P(1,0),P(1,1),P(0,1)],'fill="url(#grass'+suf+')" stroke="#eef7f0" stroke-width="2"');
  for(let i=0;i<7;i++){ if(i%2) g+=poly([P(i/7,0),P((i+1)/7,0),P((i+1)/7,1),P(i/7,1)],'fill="#ffffff" opacity=".05"'); }
  const cc=P(.5,.5), crx=lateral?40:34, cry=upper?11:14;
  if(lateral){
    g+=line(.5,0,.5,1,1.8);                                       // medio campo vertical
    g+='<ellipse cx="'+cc[0]+'" cy="'+cc[1]+'" rx="'+crx+'" ry="'+cry+'" fill="none" stroke="#eef7f0" stroke-width="1.7" opacity=".92"/>';
    g+=poly([P(0,.2),P(.16,.2),P(.16,.8),P(0,.8)],'fill="none" stroke="#eef7f0" stroke-width="1.6" opacity=".92"');   // area izq
    g+=poly([P(.84,.2),P(1,.2),P(1,.8),P(.84,.8)],'fill="none" stroke="#eef7f0" stroke-width="1.6" opacity=".92"');   // area der
  } else {
    g+=line(0,.5,1,.5,1.8);                                       // medio campo horizontal (al fondo)
    g+='<ellipse cx="'+cc[0]+'" cy="'+cc[1]+'" rx="'+crx+'" ry="'+cry+'" fill="none" stroke="#eef7f0" stroke-width="1.7" opacity=".92"/>';
    g+=poly([P(.26,.74),P(.74,.74),P(.74,1),P(.26,1)],'fill="none" stroke="#eef7f0" stroke-width="1.7" opacity=".95"'); // area cercana
    g+=poly([P(.33,0),P(.67,0),P(.67,.16),P(.33,.16)],'fill="none" stroke="#eef7f0" stroke-width="1.3" opacity=".85"'); // area lejana
    const ga=P(.42,1), gb=P(.58,1);                                // arco cercano
    g+='<line x1="'+ga[0]+'" y1="'+ga[1]+'" x2="'+ga[0]+'" y2="'+(ga[1]-14)+'" stroke="#f4f9f5" stroke-width="2"/>'
     +'<line x1="'+gb[0]+'" y1="'+gb[1]+'" x2="'+gb[0]+'" y2="'+(gb[1]-14)+'" stroke="#f4f9f5" stroke-width="2"/>'
     +'<line x1="'+ga[0]+'" y1="'+(ga[1]-14)+'" x2="'+gb[0]+'" y2="'+(gb[1]-14)+'" stroke="#f4f9f5" stroke-width="2"/>';
  }
  // primer plano: filas de asientos delante (tu tribuna)
  const rows=upper?3:2;
  for(let r=0;r<rows;r++){ const y=seatTop+ r*((260-seatTop)/rows);
    g+='<rect x="0" y="'+y+'" width="440" height="'+((260-seatTop)/rows)+'" fill="'+(r%2?'#101d3a':'#0c1730')+'"/>';
    for(let x=8;x<440;x+=22) g+='<rect x="'+x+'" y="'+(y+3)+'" width="15" height="6" rx="3" fill="#1d3158"/>';
  }
  // un par de espectadores en silueta
  g+='<circle cx="58" cy="'+(seatTop+6)+'" r="8" fill="#0a1326"/><rect x="44" y="'+(seatTop+12)+'" width="28" height="14" rx="6" fill="#0a1326"/>';
  g+='<circle cx="392" cy="'+(seatTop+8)+'" r="7" fill="#0a1326"/><rect x="380" y="'+(seatTop+13)+'" width="25" height="13" rx="6" fill="#0a1326"/>';
  if(upper) g+='<rect x="0" y="'+(seatTop-5)+'" width="440" height="4" rx="2" fill="#33507f"/>';   // baranda nivel alto
  g+='</svg>';
  return g;
}

/* ============ tarjetas de anuncios (panel derecho) ============ */
function dealOf(p){
  const ps=FLAT.map(f=>f.precio), mx=Math.max.apply(null,ps), mn=Math.min.apply(null,ps);
  const sc=8.0+1.9*((mx-p)/((mx-mn)||1));
  return {sc:sc.toFixed(1), txt:(sc>=9.3?'Increible':(sc>=8.6?'Genial':'Bueno')), cls:(sc>=9.3?'d1':(sc>=8.6?'d2':'d3'))};
}

function buildCards(){
  document.getElementById('adsTitle').textContent=FLAT.length+' anuncios';
  document.getElementById('cards').innerHTML=FLAT.map((s,i)=>{
    const d=dealOf(s.precio);
    return '<div class="tcard" id="card-'+i+'" onclick="selZone('+i+')">'
      +'<div class="thumb">'+viewSVG(s,'c'+i)+'</div>'
      +'<div class="tinfo"><div class="t1">Seccion '+s.codigo+' · '+SIDE_NAME[s.side]+'</div>'
      +'<div class="t2"><span class="catdot" style="background:'+PAL[s.catIdx%PAL.length]+'"></span>'
      +s.categoria+' · Nivel '+s.deck+' · '+s.numFilas+' filas</div>'
      +'<span class="deal '+d.cls+'">'+d.txt+' '+d.sc+'</span></div>'
      +'<div class="tprice"><div class="p">$'+s.precio+'</div><div class="pu">c/u + IVA</div></div></div>';
  }).join('');
}

/* ============ seleccion de zona / detalle ============ */
function selZone(i){
  document.querySelectorAll('.zone').forEach(z=>z.classList.remove('sel'));
  document.querySelectorAll('.tcard').forEach(c=>c.classList.remove('sel'));
  const z=document.getElementById('zone-'+i); if(z) z.classList.add('sel');
  const c=document.getElementById('card-'+i); if(c) c.classList.add('sel');
  const s=FLAT[i]; secSel=s; mias=new Set();
  document.getElementById('listView').style.display='none';
  document.getElementById('detailView').style.display='block';
  document.getElementById('secTitle').textContent='Seccion '+s.codigo+' · '+s.categoria;
  document.getElementById('secSub').textContent='Lado '+SIDE_NAME[s.side]+' · Nivel '+s.deck+' · $'+s.precio
    +' c/u · '+s.numFilas+'F x '+s.asientosPorFila+' asientos';
  const camIco='<svg width="13" height="13" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" '
    +'stroke-linecap="round" stroke-linejoin="round" style="vertical-align:-2px"><path d="M4 7h3l2-2h6l2 2h3v13H4z"/>'
    +'<circle cx="12" cy="13" r="3.5"/></svg>';
  document.getElementById('bigview').innerHTML=viewSVG(s,'big')
    +'<div class="cap">'+camIco+' Vista desde la seccion '+s.codigo+' ('+SIDE_NAME[s.side]+', nivel '+s.deck+')</div>';
  renderGrid(s);
  // tiempo real: suscribirse por WebSocket; el polling queda solo de respaldo
  if(wsOk && ws && ws.readyState===1){ ws.send(String(s.idSeccion)); }
  if(pollTimer) clearInterval(pollTimer);
  pollTimer=setInterval(()=>{ if(!wsOk && secSel) refreshSeats(secSel.idSeccion); },5000);
}

function volver(){
  document.getElementById('detailView').style.display='none';
  document.getElementById('listView').style.display='block';
  document.querySelectorAll('.zone').forEach(z=>z.classList.remove('sel'));
  if(pollTimer) clearInterval(pollTimer);
  secSel=null;
}

function renderGrid(s){
  let h='<div class="seatmap" id="seatmap">';
  for(let r=1;r<=s.numFilas;r++){ h+='<div class="seatrow"><span class="rl">F'+r+'</span>';
    for(let a=1;a<=s.asientosPorFila;a++){
      h+='<button class="seat free" id="ms-F'+r+'-'+a+'" onclick="clickSeat(\'F'+r+'\','+a+')" ondblclick="dblSeat(\'F'+r+'\','+a+')">'+a+'</button>'; }
    h+='</div>'; }
  h+='</div>'; document.getElementById('seatPanel').innerHTML=h;
  refreshSeats(s.idSeccion);
}

function applySeats(arr){
  document.querySelectorAll('#seatmap .seat').forEach(b=>{b.className='seat free'; b.disabled=false;});
  (arr||[]).forEach(x=>{const el=document.getElementById('ms-'+x.fila+'-'+x.asiento); if(!el) return;
    const key=x.fila+'|'+x.asiento;
    if(x.estado==='OCUPADO'){ el.className='seat occ'; el.disabled=true; }
    else if(mias.has(key)){ el.className='seat mine'; }
    else { el.className='seat res'; el.disabled=true; } });
}

function refreshSeats(id){
  fetch(ctx+'/asientos-data?idSeccion='+id).then(r=>r.json()).then(applySeats).catch(()=>{});
}

function clickSeat(f,a){ const el=document.getElementById('ms-'+f+'-'+a); if(el && el.classList.contains('free')) reservar(f,a); }
function dblSeat(f,a){ const el=document.getElementById('ms-'+f+'-'+a); if(el && el.classList.contains('mine')) liberar(f,a); }

function abrirModal(){
  document.getElementById('modalLoginBtn').href=ctx+'/login?next='+encodeURIComponent('/mashup?codigoPartido='+codigoPartido);
  document.getElementById('loginModal').classList.add('open');
}
function cerrarModal(){ document.getElementById('loginModal').classList.remove('open'); }

function reservar(f,a){
  if(!logueado){ abrirModal(); return; }
  if(!secSel) return;
  const body=new URLSearchParams({idSeccion:secSel.idSeccion,fila:f,asiento:a,codigoPartido:codigoPartido,
    categoria:secSel.categoria,precio:secSel.precio,partidoDesc:DATA.partido,seccionLabel:secSel.categoria+' / '+secSel.codigo});
  fetch(ctx+'/reservar-json',{method:'POST',headers:{'Content-Type':'application/x-www-form-urlencoded'},body}).then(r=>r.json()).then(res=>{
    if(res.ok){ mias.add(f+'|'+a); const el=document.getElementById('ms-'+f+'-'+a); if(el){el.className='seat mine'; el.disabled=false;}
      document.getElementById('cartCount').textContent=res.carritoCount; flash(res.mensaje+' (doble clic para quitar)',true); }
    else flash(res.mensaje,false);
  }).catch(e=>flash('Error: '+e,false));
}

function liberar(f,a){
  if(!secSel) return;
  const body=new URLSearchParams({idSeccion:secSel.idSeccion,fila:f,asiento:a});
  fetch(ctx+'/liberar-json',{method:'POST',headers:{'Content-Type':'application/x-www-form-urlencoded'},body}).then(r=>r.json()).then(res=>{
    if(res.ok){ mias.delete(f+'|'+a); const el=document.getElementById('ms-'+f+'-'+a); if(el){el.className='seat free'; el.disabled=false;}
      document.getElementById('cartCount').textContent=res.carritoCount; flash('Reserva del asiento '+f+'-'+a+' liberada.',true); }
    else flash(res.mensaje,false);
  }).catch(e=>flash('Error: '+e,false));
}

cargar();
connectWS();
</script>
</body>
</html>
