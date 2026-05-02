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
    <title>Conversiones de Temperatura - CONUNI</title>
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
                <path d="M15 13V5c0-1.66-1.34-3-3-3S9 3.34 9 5v8c-1.21.91-2 2.37-2 4 0 2.76 2.24 5 5 5s5-2.24 5-5c0-1.63-.79-3.09-2-4zm-4-8c0-.55.45-1 1-1s1 .45 1 1h-1v1h1v2h-1v1h1v2h-2V5z"/>
            </svg>
            <div>
                <h2>Conversiones de Temperatura</h2>
                <p>Convierte entre Celsius, Fahrenheit y Kelvin.</p>
            </div>
        </div>

        <form action="${pageContext.request.contextPath}/temperatura" method="post">
            <label for="operacion">Conversion:</label>
            <select id="operacion" name="operacion" required>
                <option value="celsiusAFahrenheit" <%= "celsiusAFahrenheit".equals(operacionSeleccionada) ? "selected" : "" %>>Celsius a Fahrenheit</option>
                <option value="fahrenheitACelsius" <%= "fahrenheitACelsius".equals(operacionSeleccionada) ? "selected" : "" %>>Fahrenheit a Celsius</option>
                <option value="celsiusAKelvin"     <%= "celsiusAKelvin".equals(operacionSeleccionada)     ? "selected" : "" %>>Celsius a Kelvin</option>
                <option value="kelvinACelsius"     <%= "kelvinACelsius".equals(operacionSeleccionada)     ? "selected" : "" %>>Kelvin a Celsius</option>
                <option value="fahrenheitAKelvin"  <%= "fahrenheitAKelvin".equals(operacionSeleccionada)  ? "selected" : "" %>>Fahrenheit a Kelvin</option>
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
