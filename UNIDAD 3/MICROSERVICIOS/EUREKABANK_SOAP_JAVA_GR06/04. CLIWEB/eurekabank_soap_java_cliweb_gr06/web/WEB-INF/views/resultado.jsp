<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="ec.edu.monster.cliweb.ws.Resultado" %>
<% Resultado r = (Resultado) request.getAttribute("resultado");
   String accion = String.valueOf(request.getAttribute("accion"));
   boolean ok = r != null && r.isExito(); %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>EUREKABANK - Resultado</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/styles.css?v=4">
</head>
<body>
<div class="wrap">
    <div class="card">
        <div class="topbar">
            <div class="brand">
                <img src="${pageContext.request.contextPath}/img/moster.webp" alt="Monster">
                <h1>EUREKABANK GR06</h1>
            </div>
            <span class="user">Usuario: <strong>${sessionScope.usuario}</strong></span>
        </div>
        <h2>Resultado de la operación: <%= accion %></h2>

        <div class="alert <%= ok ? "ok" : "err" %>">
            <%= r != null ? r.getMensaje() : "Sin respuesta del servidor." %>
        </div>

        <% if (r != null && ("saldo".equals(accion) || ok)) { %>
            <div class="table-scroll">
                <table>
                    <tr><th>Cuenta</th><td><%= request.getAttribute("cuenta") %></td></tr>
                    <tr><th>Saldo</th><td><strong><%= String.format("%.2f", r.getSaldo()) %></strong></td></tr>
                </table>
            </div>
        <% } %>

        <a class="link" href="${pageContext.request.contextPath}/menu">&larr; Volver al menú</a>
    </div>
</div>
</body>
</html>
