<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="jakarta.tags.core"%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>TicketPremium | Acceso</title>
    <script src="${pageContext.request.contextPath}/assets/js/theme.js?v=3"></script>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/app.css?v=3">
    <style>
        .theme-fixed{ position:fixed; top:14px; right:14px; z-index:200; }
    </style>
</head>
<body>
<button class="theme-toggle solo theme-fixed" type="button" onclick="toggleTheme()" title="Cambiar tema claro/oscuro">
    <svg class="t-moon" viewBox="0 0 24 24"><path d="M21 12.8A9 9 0 1 1 11.2 3 7 7 0 0 0 21 12.8Z"/></svg>
    <svg class="t-sun" viewBox="0 0 24 24"><circle cx="12" cy="12" r="4"/><path d="M12 2v2M12 20v2M4.9 4.9l1.4 1.4M17.7 17.7l1.4 1.4M2 12h2M20 12h2M4.9 19.1l1.4-1.4M17.7 6.3l1.4-1.4"/></svg>
</button>
<div class="auth-layout">
    <section class="auth-hero">
        <div class="auth-hero-inner">
            <div class="brand-mark"><span class="brand-dot"></span> TICKETPREMIUM</div>
            <img src="${pageContext.request.contextPath}/assets/images/moster.png"
                 alt="Monster mascot" class="auth-hero-image">
            <h1 class="hero-title">Sistema web de venta de boletos.</h1>
            <p class="hero-copy">Compra boletos y revisa tus compras en un solo lugar.</p>
        </div>
    </section>

    <section class="auth-panel">
        <div class="card auth-card">
            <img src="${pageContext.request.contextPath}/assets/images/moster.png"
                 alt="Monster" class="auth-card-logo">
            <div class="section-eyebrow">Acceso al sistema</div>
            <h2 class="section-title">Iniciar sesion</h2>
            <p class="section-copy">Ingresa con tus credenciales para acceder al panel de operaciones.</p>

            <c:if test="${not empty error}">
                <div class="alert alert-error">${error}</div>
            </c:if>

            <form method="post" action="${pageContext.request.contextPath}/login" class="form-grid compact-form">
                <c:if test="${not empty next}"><input type="hidden" name="next" value="${next}"></c:if>
                <div class="field">
                    <label>Usuario</label>
                    <input type="text" name="usuario" autocomplete="username" placeholder="Ej. monster" required>
                </div>
                <div class="field">
                    <label>Contrasena</label>
                    <input type="password" name="contrasena" autocomplete="current-password" placeholder="********" required>
                </div>
                <div class="btn-row">
                    <button type="submit" class="btn">Ingresar</button>
                    <a class="btn-secondary" href="${pageContext.request.contextPath}/partidos">Ver partidos sin iniciar sesion</a>
                </div>
            </form>

            <div class="divider"></div>

            <div class="mini-list">
                <div class="mini-row">
                    <div>
                        <strong>Admin demo</strong>
                        <span>monster / monster9</span>
                    </div>
                    <span class="chip-danger">ADMIN</span>
                </div>
                <div class="mini-row">
                    <div>
                        <strong>Clientes demo</strong>
                        <span>josue, mikaela, elkin / admin2002</span>
                    </div>
                    <span class="chip-success">CLIENTE</span>
                </div>
            </div>
        </div>
    </section>
</div>
</body>
</html>
