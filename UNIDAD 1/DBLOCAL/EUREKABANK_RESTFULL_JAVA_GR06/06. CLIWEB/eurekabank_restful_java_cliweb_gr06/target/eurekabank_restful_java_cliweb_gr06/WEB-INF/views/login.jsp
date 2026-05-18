<%@ page contentType="text/html;charset=UTF-8" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>EUREKABANK - Iniciar sesión</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/styles.css?v=4">
</head>
<body>
<div class="login-wrap">
    <div class="card login-split">
        <div class="login-art"
             style="background-image:url('${pageContext.request.contextPath}/img/login.jpg');">
            <div class="art-brand">
                <img src="${pageContext.request.contextPath}/img/moster.webp" alt="Monster">
                <h1>EUREKABANK GR06</h1>
                <p>Banca RESTFULL &middot; Cliente Web Java</p>
            </div>
        </div>
        <div class="login-form">
            <h2>Iniciar sesión</h2>
            <p class="sub">Ingresa tus credenciales para continuar</p>

            <% if (request.getAttribute("error") != null) { %>
                <div class="alert err"><%= request.getAttribute("error") %></div>
            <% } %>

            <form method="post" action="${pageContext.request.contextPath}/login" autocomplete="off">
                <label for="usuario">Usuario</label>
                <input type="text" id="usuario" name="usuario" required autofocus
                       autocapitalize="off" autocorrect="off" spellcheck="false">
                <label for="clave">Clave</label>
                <input type="password" id="clave" name="clave" required
                       autocapitalize="off" autocorrect="off" spellcheck="false">
                <button type="submit">Ingresar</button>
            </form>
        </div>
    </div>
</div>
</body>
</html>
