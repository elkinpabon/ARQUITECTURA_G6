<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    if (session.getAttribute("usuario") == null) {
        response.sendRedirect(request.getContextPath() + "/vista/iniciarSesion.jsp");
        return;
    }
%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Menu Principal - CONUNI</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/estilo.css">
</head>
<body>
    <div class="encabezado">
        <h1>
            <img class="logo" src="${pageContext.request.contextPath}/img/moster.webp" alt="Logo CONUNI">
            Cliente Web CONUNI
        </h1>
        <div>
            <span>Bienvenido, <%= session.getAttribute("usuario") %></span>
            &nbsp;|&nbsp;
            <a href="${pageContext.request.contextPath}/cerrarSesion">Cerrar Sesion</a>
        </div>
    </div>

    <div class="contenedor">
        <h2>Menu de Conversiones</h2>
        <p>Elige una categoria para realizar conversiones de unidades.</p>

        <div class="menu-opciones">
            <a href="${pageContext.request.contextPath}/longitud">
                <svg class="icono" viewBox="0 0 24 24" fill="currentColor">
                    <path d="M21 6H3c-.55 0-1 .45-1 1v10c0 .55.45 1 1 1h18c.55 0 1-.45 1-1V7c0-.55-.45-1-1-1zm-1 10H4V8h2v4h2V8h2v4h2V8h2v4h2V8h2v4h2v4z"/>
                </svg>
                Longitud
            </a>
            <a href="${pageContext.request.contextPath}/masa">
                <svg class="icono" viewBox="0 0 24 24" fill="currentColor">
                    <path d="M12 3C9.24 3 7 5.24 7 8c0 .85.21 1.65.58 2.35L2 21h20l-5.58-10.65c.37-.7.58-1.5.58-2.35 0-2.76-2.24-5-5-5zm0 2c1.66 0 3 1.34 3 3s-1.34 3-3 3-3-1.34-3-3 1.34-3 3-3z"/>
                </svg>
                Masa
            </a>
            <a href="${pageContext.request.contextPath}/temperatura">
                <svg class="icono" viewBox="0 0 24 24" fill="currentColor">
                    <path d="M15 13V5c0-1.66-1.34-3-3-3S9 3.34 9 5v8c-1.21.91-2 2.37-2 4 0 2.76 2.24 5 5 5s5-2.24 5-5c0-1.63-.79-3.09-2-4zm-4-8c0-.55.45-1 1-1s1 .45 1 1h-1v1h1v2h-1v1h1v2h-2V5z"/>
                </svg>
                Temperatura
            </a>
        </div>
    </div>
</body>
</html>
