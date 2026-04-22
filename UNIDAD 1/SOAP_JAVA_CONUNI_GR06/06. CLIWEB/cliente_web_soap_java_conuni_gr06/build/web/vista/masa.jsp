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
    <title>Conversiones de Masa - CONUNI</title>
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
                <path d="M12 3C9.24 3 7 5.24 7 8c0 .85.21 1.65.58 2.35L2 21h20l-5.58-10.65c.37-.7.58-1.5.58-2.35 0-2.76-2.24-5-5-5zm0 2c1.66 0 3 1.34 3 3s-1.34 3-3 3-3-1.34-3-3 1.34-3 3-3z"/>
            </svg>
            <div>
                <h2>Conversiones de Masa</h2>
                <p>Convierte entre kilogramos, libras, gramos, onzas, toneladas y mas.</p>
            </div>
        </div>

        <form action="${pageContext.request.contextPath}/masa" method="post">
            <label for="operacion">Conversion:</label>
            <select id="operacion" name="operacion" required>
                <option value="kilogramosALibras"    <%= "kilogramosALibras".equals(operacionSeleccionada)    ? "selected" : "" %>>Kilogramos a Libras</option>
                <option value="gramosAOnzas"         <%= "gramosAOnzas".equals(operacionSeleccionada)         ? "selected" : "" %>>Gramos a Onzas</option>
                <option value="toneladasAKilogramos" <%= "toneladasAKilogramos".equals(operacionSeleccionada) ? "selected" : "" %>>Toneladas a Kilogramos</option>
                <option value="librasAOnzas"         <%= "librasAOnzas".equals(operacionSeleccionada)         ? "selected" : "" %>>Libras a Onzas</option>
                <option value="miligramosAGramos"    <%= "miligramosAGramos".equals(operacionSeleccionada)    ? "selected" : "" %>>Miligramos a Gramos</option>
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
