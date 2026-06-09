<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="ec.edu.monster.cliweb.ws.CuentaResumen" %>
<%@ page import="ec.edu.monster.cliweb.ws.ClienteResumen" %>
<%@ page import="ec.edu.monster.cliweb.util.Moneda" %>
<%
    List<CuentaResumen> cuentas =
            (List<CuentaResumen>) session.getAttribute("cuentasCliente");
    String clienteBuscado = (String) session.getAttribute("clienteBuscado");
    String cuentasMsg = (String) session.getAttribute("cuentasMsg");
    if (clienteBuscado == null) clienteBuscado = "";
    Boolean esAdminObj = (Boolean) session.getAttribute("esAdmin");
    boolean esAdmin = Boolean.TRUE.equals(esAdminObj);
    List<ClienteResumen> todosClientes =
            (List<ClienteResumen>) request.getAttribute("todosClientes");
    boolean hayCuentas = cuentas != null && !cuentas.isEmpty();
    String nombreCliente = null, codigoCliente = null;
    double totalSaldo = 0;
    if (hayCuentas) {
        nombreCliente = cuentas.get(0).getNombreCliente();
        codigoCliente = cuentas.get(0).getCodigoCliente();
        for (CuentaResumen c : cuentas) totalSaldo += c.getSaldo();
    }
%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>EUREKABANK - Menú</title>
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
            <span class="user">Usuario: <strong>${sessionScope.usuario}</strong> &middot;
                <a class="link" href="${pageContext.request.contextPath}/logout">Salir</a></span>
        </div>
        <h2>Operaciones bancarias
            <% if (esAdmin) { %>&nbsp;·&nbsp;<small style="color:#38bdf8;">ADMIN</small><% } %>
        </h2>

        <% if (esAdmin) { %>
            <%-- Solo 'monster' puede buscar cualquier cliente --%>
            <form method="post" action="${pageContext.request.contextPath}/cuentas">
                <fieldset class="fieldset">
                    <legend>1. Seleccionar cliente</legend>
                    <label for="cliente">Cliente registrado</label>
                    <select id="cliente" name="cliente" required autofocus>
                        <option value="">— Selecciona un cliente —</option>
                        <% if (todosClientes != null) {
                               for (ClienteResumen cl : todosClientes) {
                                   boolean sel = cl.getCodigo().equals(clienteBuscado); %>
                            <option value="<%= cl.getCodigo() %>" <%= sel ? "selected" : "" %>>
                                <%= cl.getCodigo() %> &middot; DNI <%= cl.getDni() %>
                                &middot; <%= cl.getNombre() %></option>
                        <%     }
                           } %>
                    </select>
                    <button type="submit">Ver cuentas</button>
                </fieldset>
            </form>

            <details style="margin-bottom:16px;">
                <summary style="cursor:pointer;color:#38bdf8;font-weight:600;
                    padding:8px 0;">+ Registrar cliente / cuenta (admin)</summary>

                <form method="post" action="${pageContext.request.contextPath}/admin">
                    <fieldset class="fieldset">
                        <legend>Registrar cliente</legend>
                        <label for="nombre">Nombre</label>
                        <input type="text" id="nombre" name="nombre" required>
                        <label for="paterno">Apellido paterno</label>
                        <input type="text" id="paterno" name="paterno" required>
                        <label for="materno">Apellido materno</label>
                        <input type="text" id="materno" name="materno">
                        <label for="dni">DNI</label>
                        <input type="text" id="dni" name="dni" required>
                        <label for="ciudad">Ciudad</label>
                        <input type="text" id="ciudad" name="ciudad" value="QUITO">
                        <label for="direccion">Dirección</label>
                        <input type="text" id="direccion" name="direccion">
                        <label for="telefono">Teléfono</label>
                        <input type="text" id="telefono" name="telefono">
                        <label for="email">Email</label>
                        <input type="text" id="email" name="email">
                        <button type="submit" name="accion" value="nuevoCliente">Crear cliente</button>
                    </fieldset>
                </form>

                <form method="post" action="${pageContext.request.contextPath}/admin">
                    <fieldset class="fieldset">
                        <legend>Registrar cuenta</legend>
                        <label for="cliCta">Cliente</label>
                        <select id="cliCta" name="cliente" required>
                            <option value="">— Selecciona un cliente —</option>
                            <% if (todosClientes != null) {
                                   for (ClienteResumen cl : todosClientes) { %>
                                <option value="<%= cl.getCodigo() %>">
                                    <%= cl.getCodigo() %> &middot; <%= cl.getNombre() %></option>
                            <%     }
                               } %>
                        </select>
                        <label for="monCta">Moneda de la cuenta</label>
                        <select id="monCta" name="moneda">
                            <option value="02" selected>Dólares</option>
                            <option value="01">Soles</option>
                        </select>
                        <button type="submit" name="accion" value="nuevaCuenta">Crear cuenta</button>
                    </fieldset>
                </form>

                <form method="post" action="${pageContext.request.contextPath}/admin"
                      onsubmit="return confirm('¿Eliminar la cuenta y TODOS sus movimientos? Esta acción no se puede deshacer.');">
                    <fieldset class="fieldset">
                        <legend>Eliminar cuenta</legend>
                        <label for="ctaDel">Código de cuenta a eliminar</label>
                        <input type="text" id="ctaDel" name="cuenta"
                               placeholder="Ej: 00900031" pattern="[0-9]{8}"
                               title="8 dígitos" required>
                        <p class="hint" style="text-align:left;margin-top:6px;">
                            Borra la cuenta y sus movimientos. La cuenta MASTER
                            <code>00900000</code> está protegida.</p>
                        <button type="submit" name="accion" value="eliminarCuenta"
                                style="background:#f87171;color:#3f0d0d;">Eliminar cuenta</button>
                    </fieldset>
                </form>
            </details>
        <% } %>

        <% if (cuentasMsg != null) { %>
            <div class="alert err"><%= cuentasMsg %></div>
        <% } %>

        <% if (hayCuentas) { %>
            <%-- Resumen: cuentas del cliente y cuánto tiene --%>
            <div class="client-head">
                <div class="who"><%= nombreCliente %>
                    <small>Cliente <%= codigoCliente %> &middot;
                        <%= cuentas.size() %> cuenta(s)</small>
                </div>
                <div class="total-box">
                    <div class="lbl">Saldo total</div>
                    <div class="amt"><%= String.format("%,.2f", totalSaldo) %></div>
                    <a class="link" style="margin-top:6px;"
                       href="${pageContext.request.contextPath}/menu?refrescar=1">&#8635; Actualizar saldos</a>
                </div>
            </div>

            <div class="acct-grid">
                <% for (CuentaResumen c : cuentas) {
                       boolean activo = "ACTIVO".equalsIgnoreCase(c.getEstado()); %>
                    <div class="acct">
                        <div class="code">CTA <%= c.getCodigoCuenta() %></div>
                        <div class="bal"><%= String.format("%,.2f", c.getSaldo()) %></div>
                        <div class="meta">
                            <span><%= Moneda.nombre(c.getMoneda()) %></span>
                            <span class="badge <%= activo ? "activo" : "otro" %>">
                                <%= c.getEstado() %></span>
                        </div>
                    </div>
                <% } %>
            </div>

            <%-- Paso 2: elegir cuenta y operar --%>
            <form method="post" action="${pageContext.request.contextPath}/cuenta">
                <fieldset class="fieldset">
                    <legend><%= esAdmin ? "2. Operar cuenta" : "Operar cuenta" %></legend>

                    <label for="cuenta">Cuenta</label>
                    <select id="cuenta" name="cuenta" required>
                        <% for (CuentaResumen c : cuentas) {
                               String et = c.getCodigoCuenta()
                                   + "  ·  " + String.format("%,.2f", c.getSaldo())
                                   + " " + Moneda.nombre(c.getMoneda())
                                   + "  ·  " + c.getEstado(); %>
                            <option value="<%= c.getCodigoCuenta() %>"><%= et %></option>
                        <% } %>
                    </select>

                    <label for="monto">Monto (depósito / retiro / transferencia)</label>
                    <input type="text" id="monto" name="monto" placeholder="100.00">

                    <label for="moneda">Moneda del monto</label>
                    <select id="moneda" name="moneda">
                        <option value="02" selected>Dólares (preferente)</option>
                        <option value="01">Soles</option>
                    </select>
                    <p class="hint" style="text-align:left;margin-top:6px;">
                        Se convierte automáticamente a la moneda de la cuenta afectada
                        (1 USD = 3.75 Soles).</p>

                    <div class="btn-row" style="margin-top:20px;">
                        <% if (esAdmin) { %>
                            <button type="submit" name="accion" value="depositar"
                                    formaction="${pageContext.request.contextPath}/cuenta">Depositar</button>
                        <% } %>
                        <button type="submit" name="accion" value="retirar"
                                formaction="${pageContext.request.contextPath}/cuenta">Retirar</button>
                    </div>
                    <button type="submit" name="accion" value="saldo"
                            formaction="${pageContext.request.contextPath}/cuenta">Consultar saldo</button>
                    <button type="submit"
                            formaction="${pageContext.request.contextPath}/movimientos">Ver / imprimir movimientos</button>
                </fieldset>

                <fieldset class="fieldset">
                    <legend>Transferir a otra cuenta</legend>
                    <label for="cuentaDestino">Cuenta destino (de cualquier cliente del banco)</label>
                    <input type="text" id="cuentaDestino" name="cuentaDestino"
                           placeholder="Ej: 00200002" pattern="[0-9]{8}"
                           title="8 dígitos">
                    <p class="hint" style="text-align:left;margin-top:8px;">
                        Usa el <strong>Monto</strong> de arriba. Origen = la cuenta seleccionada.
                        Se registrará el movimiento en ambas cuentas.</p>
                    <button type="submit" name="accion" value="transferir"
                            formaction="${pageContext.request.contextPath}/cuenta">Transferir</button>
                </fieldset>
            </form>
        <% } else if (cuentasMsg == null) { %>
            <div class="alert">Busca un cliente por su código o DNI para ver sus cuentas y saldos.</div>
        <% } %>
    </div>
</div>
</body>
</html>
