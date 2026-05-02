<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="ec.edu.monster.modelo.Resultado" %>
<%
    Resultado resultado = (Resultado) request.getAttribute("resultado");
    String operacionSeleccionada = (String) request.getAttribute("operacionSeleccionada");
    String valorIngresado = (String) request.getAttribute("valorIngresado");
%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Conversiones de Longitud - CONUNI</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/estilo.css">
</head>
<body>
    <div class="encabezado">
        <h1>
            <img class="logo" src="${pageContext.request.contextPath}/img/moster.webp" alt="Logo CONUNI">
            Cliente Web CONUNI
        </h1>
        <div>
            <a href="${pageContext.request.contextPath}/vista/menu.jsp">Menu</a>
            &nbsp;|&nbsp;
            <a href="${pageContext.request.contextPath}/cerrarSesion">Cerrar Sesion</a>
        </div>
    </div>

    <div class="contenedor">
        <div class="conversion-encabezado">
            <svg viewBox="0 0 24 24" fill="#1f3a5f" width="64" height="64" style="border-radius:12px;border:3px solid #ffd966;padding:6px;background:#fffbe6;">
                <path d="M21 6H3c-.55 0-1 .45-1 1v10c0 .55.45 1 1 1h18c.55 0 1-.45 1-1V7c0-.55-.45-1-1-1zm-1 10H4V8h2v4h2V8h2v4h2V8h2v4h2V8h2v4h2v4z"/>
            </svg>
            <div>
                <h2>Conversiones de Longitud</h2>
                <p>Convierte entre metros, pies, kilometros, millas, pulgadas y mas.</p>
            </div>
        </div>

        <form action="${pageContext.request.contextPath}/longitud" method="post">
            <label for="operacion">Conversion:</label>
            <select id="operacion" name="operacion" required>
                <option value="metrosAPies"         <%= "metrosAPies".equals(operacionSeleccionada)         ? "selected" : "" %>>Metros a Pies</option>
                <option value="kilometrosAMillas"   <%= "kilometrosAMillas".equals(operacionSeleccionada)   ? "selected" : "" %>>Kilometros a Millas</option>
                <option value="centimetrosAPulgadas"<%= "centimetrosAPulgadas".equals(operacionSeleccionada)? "selected" : "" %>>Centimetros a Pulgadas</option>
                <option value="yardasAMetros"       <%= "yardasAMetros".equals(operacionSeleccionada)       ? "selected" : "" %>>Yardas a Metros</option>
                <option value="milimetrosAPulgadas" <%= "milimetrosAPulgadas".equals(operacionSeleccionada) ? "selected" : "" %>>Milimetros a Pulgadas</option>
            </select>

            <label for="valor">Valor:</label>
            <input type="number" step="any" id="valor" name="valor"
                   value="<%= valorIngresado != null ? valorIngresado : "" %>" required>

            <button type="submit">Convertir</button>
        </form>

        <% if (resultado != null) { %>
            <% if (resultado.isExito()) { %>
                <div class="mensaje-exito">Resultado: <%= resultado.getValor() %></div>
            <% } else { %>
                <div class="mensaje-error"><%= resultado.getMensaje() %></div>
            <% } %>
        <% } %>
    </div>
</body>
</html>
