package ec.edu.monster.vista;

import ec.edu.monster.controlador.TicketController;
import ec.edu.monster.modelo.LineaCarrito;
import ec.edu.monster.util.ConsoleTheme;
import ec.edu.monster.util.Moneda;
import ec.edu.monster.ws.Asiento;
import ec.edu.monster.ws.Cuenta;
import ec.edu.monster.ws.Cuota;
import ec.edu.monster.ws.DetalleFactura;
import ec.edu.monster.ws.Estadio;
import ec.edu.monster.ws.Factura;
import ec.edu.monster.ws.ItemCarrito;
import ec.edu.monster.ws.Localidad;
import ec.edu.monster.ws.Movimiento;
import ec.edu.monster.ws.Partido;
import ec.edu.monster.ws.Resultado;
import ec.edu.monster.ws.ResumenLocalidad;
import ec.edu.monster.ws.Seccion;
import ec.edu.monster.ws.Seleccion;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/** Cliente consola TicketPremium FIFA 2026 (MVC + SOAP). */
public class ConsolaApp {

    private final Scanner in = new Scanner(System.in);
    private final TicketController ctrl = new TicketController();
    private final ConsoleTheme ui = new ConsoleTheme();
    private final List<LineaCarrito> carrito = new ArrayList<>();

    public static void main(String[] args) {
        new ConsolaApp().run();
    }

    private void run() {
        banner();
        while (true) {
            if (!login()) {
                if (!confirmar("Reintentar inicio de sesion? [s/n]:")) {
                    break;
                }
                continue;
            }
            menu();
            cerrarSesionLimpia();
        }
        println();
        println(ui.muted("Sesion cerrada. Hasta pronto."));
    }

    private void cerrarSesionLimpia() {
        try {
            ctrl.liberarMisReservas();
        } catch (Exception ignored) {
            // si el servidor no responde, las reservas expiran solas en 10 min
        }
        carrito.clear();
        ctrl.logout();
    }

    private void banner() {
        println(ui.line());
        println(ui.title("TICKETPREMIUM FIFA 2026 | CLIENTE CONSOLA GR06"));
        println(ui.muted("MVC + SOAP | Venta de boletos del Mundial 2026"));
        println(ui.muted("Servidor: " + ec.edu.monster.config.ServidorConfig.base()));
        println(ui.line());
    }

    // ================================================================= LOGIN
    private boolean login() {
        println();
        println(ui.primary("[ ACCESO ]"));
        println(ui.muted("Credenciales de prueba:"));
        println("  " + ui.success("ADMIN") + "  monster / monster9");
        println("  " + ui.chip("CLIENTE") + " josue, mikaela, elkin / admin2002");
        println();

        String usuario = leerTexto("Usuario");
        String clave = leerTexto("Contrasena");

        try {
            if (ctrl.login(usuario, clave)) {
                println(ui.success("Bienvenido, " + ctrl.getSesion().getNombre()
                        + " [" + ctrl.getSesion().getUsuario().getRol() + "]"));
                return true;
            }
            println(ui.warning("Credenciales invalidas."));
        } catch (Exception e) {
            println(ui.danger("Error contactando el servidor: " + e.getMessage()));
        }
        return false;
    }

    // ================================================================= MENU
    private void menu() {
        boolean salir = false;
        while (!salir) {
            println();
            println(ui.line());
            println(ui.primary("MENU PRINCIPAL"));
            println(ui.muted("Sesion: " + ctrl.getSesion().getNombre()
                    + " | Rol: " + ctrl.getSesion().getUsuario().getRol()
                    + " | Carrito: " + carrito.size() + " boleto(s)"));
            println(ui.line());

            println(menuItem("1", "Ver partidos del Mundial"));
            println(ui.success(menuItem("2", "Comprar boletos (reserva de asientos)")));
            println(menuItem("3", "Mis compras (facturas y comprobantes)"));
            println(menuItem("4", "Mi cuenta (saldo y movimientos)"));
            println(menuItem("5", "Ver carrito"));
            println(menuItem("6", "Vaciar mis reservas"));
            if (ctrl.getSesion().isAdmin()) {
                println(menuItem("7", "Administracion (ADMIN)"));
            }
            println(menuItem("0", "Cerrar sesion y salir"));

            String op = leerTexto("Opcion");
            switch (op) {
                case "1" -> mostrarPartidos();
                case "2" -> flujoCompra();
                case "3" -> misCompras();
                case "4" -> miCuenta();
                case "5" -> verCarrito();
                case "6" -> vaciarReservas();
                case "7" -> { if (ctrl.getSesion().isAdmin()) menuAdmin(); else println(ui.warning("Opcion invalida.")); }
                case "0" -> salir = true;
                default -> println(ui.warning("Opcion invalida."));
            }
        }
    }

    // ================================================================= PARTIDOS
    private List<Partido> mostrarPartidos() {
        try {
            List<Partido> partidos = ctrl.partidosDisponibles();
            renderPartidos("PARTIDOS DEL MUNDIAL FIFA 2026", partidos);
            return partidos;
        } catch (Exception e) {
            println(ui.danger("Error: " + e.getMessage()));
            return new ArrayList<>();
        }
    }

    private void renderPartidos(String titulo, List<Partido> partidos) {
        List<String[]> rows = new ArrayList<>();
        for (Partido p : partidos) {
            rows.add(new String[]{
                String.valueOf(p.getCodigo()),
                p.getGrupo(),
                p.getEquipoLocal() + " vs " + p.getEquipoVisita(),
                p.getFecha(),
                p.getEstadio(),
                p.getCiudad() + ", " + p.getPais()
            });
        }
        renderTable(titulo, new String[]{"COD", "GRUPO", "PARTIDO", "FECHA", "ESTADIO", "CIUDAD"}, rows);
        if (partidos.isEmpty()) {
            println(ui.muted("No hay partidos disponibles en este momento."));
        }
    }

    // ================================================================= COMPRA
    private void flujoCompra() {
        boolean seguir = true;
        while (seguir) {
            if (!agregarBoletos()) break;
            println();
            if (!confirmar("Deseas agregar boletos de otro partido/seccion? [s/n]:")) {
                seguir = false;
            }
        }
        if (!carrito.isEmpty()) {
            println();
            if (confirmar("Deseas finalizar la compra ahora (checkout)? [s/n]:")) {
                checkout();
            } else {
                println(ui.muted("Tu carrito se conserva. Recuerda: las reservas expiran en 10 minutos."));
            }
        }
    }

    /** Elige partido -> categoria -> seccion -> asientos. Devuelve false si el usuario cancelo. */
    private boolean agregarBoletos() {
        List<Partido> partidos = mostrarPartidos();
        if (partidos.isEmpty()) return false;

        int codigoPartido = leerEntero("Codigo de partido (0 para cancelar)");
        if (codigoPartido == 0) return false;
        Partido partido = partidos.stream().filter(p -> p.getCodigo() == codigoPartido).findFirst().orElse(null);
        if (partido == null) {
            println(ui.warning("Codigo de partido invalido."));
            return true;
        }
        String partidoDesc = partido.getEquipoLocal() + " vs " + partido.getEquipoVisita();

        // ---- categorias (localidades)
        List<Localidad> categorias;
        try {
            categorias = ctrl.categoriasDe(codigoPartido);
        } catch (Exception e) {
            println(ui.danger("Error: " + e.getMessage()));
            return false;
        }
        List<String[]> rows = new ArrayList<>();
        for (Localidad l : categorias) {
            rows.add(new String[]{String.valueOf(l.getId()), l.getCategoria(),
                    Moneda.fmt(l.getPrecio()), String.valueOf(l.getDisponibilidad())});
        }
        renderTable("CATEGORIAS | " + partidoDesc, new String[]{"ID", "CATEGORIA", "PRECIO", "STOCK"}, rows);
        if (categorias.isEmpty()) {
            println(ui.muted("Este partido no tiene localidades disponibles."));
            return true;
        }

        int idLocalidad = leerEntero("ID de categoria (0 para cancelar)");
        if (idLocalidad == 0) return false;
        Localidad categoria = categorias.stream().filter(l -> l.getId() == idLocalidad).findFirst().orElse(null);
        if (categoria == null) {
            println(ui.warning("ID de categoria invalido."));
            return true;
        }

        // ---- secciones
        List<Seccion> secciones;
        try {
            secciones = ctrl.seccionesDe(idLocalidad);
        } catch (Exception e) {
            println(ui.danger("Error: " + e.getMessage()));
            return false;
        }
        rows = new ArrayList<>();
        for (Seccion s : secciones) {
            rows.add(new String[]{String.valueOf(s.getIdSeccion()), s.getCodigoSeccion(),
                    String.valueOf(s.getNumFilas()), String.valueOf(s.getAsientosPorFila())});
        }
        renderTable("SECCIONES | " + categoria.getCategoria(),
                new String[]{"ID", "SECCION", "FILAS", "ASIENTOS/FILA"}, rows);
        if (secciones.isEmpty()) {
            println(ui.muted("Esta categoria no tiene secciones."));
            return true;
        }

        int idSeccion = leerEntero("ID de seccion (0 para cancelar)");
        if (idSeccion == 0) return false;
        Seccion seccion = secciones.stream().filter(s -> s.getIdSeccion() == idSeccion).findFirst().orElse(null);
        if (seccion == null) {
            println(ui.warning("ID de seccion invalido."));
            return true;
        }

        // ---- asientos
        elegirAsientos(partido, categoria, seccion);
        return true;
    }

    private void elegirAsientos(Partido partido, Localidad categoria, Seccion seccion) {
        String partidoDesc = partido.getEquipoLocal() + " vs " + partido.getEquipoVisita();
        String seccionLabel = categoria.getCategoria() + " / " + seccion.getCodigoSeccion();
        boolean elegir = true;
        while (elegir) {
            Map<String, String> estados = mapaEstados(seccion.getIdSeccion());
            if (estados == null) return;
            dibujarMapa(seccion, estados, seccionLabel);

            println(ui.muted("Ingresa fila y asiento separados por coma (ej. 3,7) o 0 para terminar."));
            String entrada = leerTexto("Asiento");
            if (entrada.equals("0")) break;
            String[] partes = entrada.split("[,;\\s]+");
            if (partes.length != 2) {
                println(ui.warning("Formato invalido. Usa fila,asiento (ej. 3,7)."));
                continue;
            }
            int numFila, numAsiento;
            try {
                numFila = Integer.parseInt(partes[0].trim().toUpperCase().replace("F", ""));
                numAsiento = Integer.parseInt(partes[1].trim());
            } catch (NumberFormatException e) {
                println(ui.warning("Formato invalido. Usa numeros: fila,asiento (ej. 3,7)."));
                continue;
            }
            if (numFila < 1 || numFila > seccion.getNumFilas()
                    || numAsiento < 1 || numAsiento > seccion.getAsientosPorFila()) {
                println(ui.warning("Fila o asiento fuera de rango."));
                continue;
            }
            String fila = "F" + numFila;
            String asiento = String.valueOf(numAsiento);

            try {
                Resultado r = ctrl.reservarAsiento(seccion.getIdSeccion(), fila, asiento);
                if (r.isExito()) {
                    carrito.add(new LineaCarrito(partido.getCodigo(), partidoDesc, seccion.getIdSeccion(),
                            seccionLabel, categoria.getCategoria(), categoria.getPrecio(), 1, fila, asiento));
                    println(ui.success("Asiento " + fila + "-" + asiento
                            + " reservado y agregado al carrito (expira en 10 min)."));
                } else {
                    println(ui.warning(r.getMensaje()));
                }
            } catch (Exception e) {
                println(ui.danger("Error reservando asiento: " + e.getMessage()));
            }
            println();
            elegir = confirmar("Reservar otro asiento en esta seccion? [s/n]:");
        }
    }

    private Map<String, String> mapaEstados(int idSeccion) {
        try {
            Map<String, String> estados = new HashMap<>();
            for (Asiento a : ctrl.asientosNoLibres(idSeccion)) {
                estados.put(a.getFila() + "|" + a.getAsiento(), a.getEstado());
            }
            return estados;
        } catch (Exception e) {
            println(ui.danger("Error consultando asientos: " + e.getMessage()));
            return null;
        }
    }

    private void dibujarMapa(Seccion seccion, Map<String, String> estados, String etiqueta) {
        println();
        println(ui.label("MAPA DE ASIENTOS | " + etiqueta));
        println(ui.muted("Leyenda: . = libre | R = reservado | O = ocupado"));
        int filas = seccion.getNumFilas();
        int porFila = seccion.getAsientosPorFila();

        StringBuilder header = new StringBuilder("      ");
        for (int a = 1; a <= porFila; a++) {
            header.append(String.format("%3d", a));
        }
        println(ui.primary(header.toString()));

        for (int f = 1; f <= filas; f++) {
            StringBuilder row = new StringBuilder(String.format("  F%-3d", f));
            for (int a = 1; a <= porFila; a++) {
                String estado = estados.get("F" + f + "|" + a);
                char c = '.';
                if (estado != null) {
                    c = estado.toUpperCase().startsWith("OCUP") ? 'O' : 'R';
                }
                row.append("  ").append(c);
            }
            println(row.toString());
        }
    }

    // ================================================================= CARRITO / CHECKOUT
    private void verCarrito() {
        if (carrito.isEmpty()) {
            println(ui.muted("El carrito esta vacio."));
            return;
        }
        List<String[]> rows = new ArrayList<>();
        BigDecimal subtotal = BigDecimal.ZERO;
        int i = 1;
        for (LineaCarrito l : carrito) {
            rows.add(new String[]{String.valueOf(i++), l.getPartidoDesc(), l.getSeccionLabel(),
                    l.getFila() + "-" + l.getAsientos(), Moneda.fmt(l.getPrecio())});
            subtotal = subtotal.add(l.getTotal());
        }
        renderTable("CARRITO DE COMPRA", new String[]{"#", "PARTIDO", "SECCION", "ASIENTO", "PRECIO"}, rows);
        println(ui.primary("Subtotal (sin IVA): " + Moneda.fmt(subtotal)));

        println();
        println(menuItem("1", "Finalizar compra (checkout)"));
        println(menuItem("2", "Quitar un boleto"));
        println(menuItem("0", "Volver"));
        String op = leerTexto("Opcion");
        switch (op) {
            case "1" -> checkout();
            case "2" -> quitarDelCarrito();
            default -> { }
        }
    }

    private void quitarDelCarrito() {
        int idx = leerEntero("Numero de linea a quitar (0 para cancelar)");
        if (idx <= 0 || idx > carrito.size()) return;
        LineaCarrito l = carrito.get(idx - 1);
        try {
            ctrl.liberarAsiento(l.getIdSeccion(), l.getFila(), l.getAsientos());
        } catch (Exception e) {
            println(ui.warning("No se pudo liberar el asiento en el servidor: " + e.getMessage()));
        }
        carrito.remove(idx - 1);
        println(ui.success("Boleto quitado del carrito y reserva liberada."));
    }

    private void vaciarReservas() {
        try {
            Resultado r = ctrl.liberarMisReservas();
            println(r.isExito() ? ui.success(r.getMensaje()) : ui.warning(r.getMensaje()));
        } catch (Exception e) {
            println(ui.danger("Error: " + e.getMessage()));
        }
        carrito.clear();
        println(ui.muted("Carrito local vaciado."));
    }

    private void checkout() {
        if (carrito.isEmpty()) {
            println(ui.muted("El carrito esta vacio."));
            return;
        }
        println();
        println(ui.primary("[ CHECKOUT ]"));
        println(menuItem("1", "CONTADO"));
        println(menuItem("2", "CREDITO (cuotas con amortizacion francesa)"));
        String op = leerTexto("Tipo de pago");

        String tipoPago;
        int numCuotas = 0;
        BigDecimal entrada = BigDecimal.ZERO;
        BigDecimal tasa = BigDecimal.ZERO;

        if (op.equals("2")) {
            tipoPago = "CREDITO";
            entrada = leerBigDecimal("Entrada (USD, 0 si no aplica)");
            numCuotas = leerEntero("Numero de cuotas");
            BigDecimal tasaPct = leerBigDecimal("Tasa de interes % mensual (ej. 2)");
            tasa = tasaPct.divide(BigDecimal.valueOf(100), 6, RoundingMode.HALF_UP);
        } else if (op.equals("1")) {
            tipoPago = "CONTADO";
        } else {
            println(ui.warning("Opcion invalida, se cancela el checkout."));
            return;
        }

        List<ItemCarrito> items = new ArrayList<>();
        for (LineaCarrito l : carrito) {
            ItemCarrito it = new ItemCarrito();
            it.setCodigoPartido(l.getCodigoPartido());
            it.setIdSeccion(l.getIdSeccion());
            it.setCantidad(l.getCantidad());
            it.setFila(l.getFila() == null ? "" : l.getFila());
            it.setAsientos(l.getAsientos() == null ? "" : l.getAsientos());
            items.add(it);
        }

        try {
            Resultado r = ctrl.comprar(items, tipoPago, numCuotas, tasa, entrada);
            if (r.isExito()) {
                println(ui.success(r.getMensaje()));
                carrito.clear();
                Factura f = r.getFactura();
                if (f != null) {
                    Factura completa = ctrl.comprobante(f.getIdFactura());
                    mostrarFactura(completa != null ? completa : f);
                }
            } else {
                println(ui.warning(r.getMensaje()));
            }
        } catch (Exception e) {
            println(ui.danger("Error al finalizar la compra: " + e.getMessage()));
        }
    }

    // ================================================================= FACTURAS
    private void misCompras() {
        try {
            boolean admin = ctrl.getSesion().isAdmin();
            List<Factura> facturas = ctrl.misFacturas();
            renderFacturas(admin ? "MIS FACTURAS" : "MIS COMPRAS", facturas, false);
            if (facturas.isEmpty()) {
                println(ui.muted("Todavia no tienes facturas registradas."));
                return;
            }
            int id = leerEntero("Numero de factura a visualizar (0 para volver)");
            if (id == 0) return;
            Factura f = ctrl.comprobante(id);
            if (f == null) {
                println(ui.warning("No se pudo recuperar el comprobante."));
            } else {
                mostrarFactura(f);
            }
        } catch (Exception e) {
            println(ui.danger("Error: " + e.getMessage()));
        }
    }

    private void renderFacturas(String titulo, List<Factura> facturas, boolean conUsuario) {
        List<String[]> rows = new ArrayList<>();
        for (Factura f : facturas) {
            if (conUsuario) {
                rows.add(new String[]{String.valueOf(f.getIdFactura()), f.getFecha(), f.getUsuarioNombre(),
                        f.getTipoPago(), Moneda.fmt(f.getTotal())});
            } else {
                rows.add(new String[]{String.valueOf(f.getIdFactura()), f.getFecha(),
                        f.getTipoPago(), Moneda.fmt(f.getTotal())});
            }
        }
        renderTable(titulo, conUsuario
                ? new String[]{"#", "FECHA", "CLIENTE", "PAGO", "TOTAL"}
                : new String[]{"#", "FECHA", "PAGO", "TOTAL"}, rows);
    }

    private void mostrarFactura(Factura f) {
        println();
        println(ui.line());
        println(ui.title("COMPROBANTE DE COMPRA | FACTURA #" + f.getIdFactura()));
        println(ui.muted("Fecha: " + f.getFecha()
                + " | Cliente: " + (f.getUsuarioNombre() == null ? ctrl.getSesion().getNombre() : f.getUsuarioNombre())
                + " | Pago: " + f.getTipoPago()
                + (f.getMoneda() == null ? "" : " | Moneda: " + f.getMoneda())));
        println(ui.line());

        List<String[]> rows = new ArrayList<>();
        if (f.getDetalles() != null) {
            for (DetalleFactura d : f.getDetalles()) {
                rows.add(new String[]{
                    d.getDescripcionPartido(),
                    d.getCategoria(),
                    (d.getFila() == null ? "" : d.getFila()) + " / " + (d.getAsientos() == null ? "" : d.getAsientos()),
                    String.valueOf(d.getCantidad()),
                    Moneda.fmt(d.getPrecioUnitario()),
                    Moneda.fmt(d.getTotal())
                });
            }
        }
        renderTable("BOLETOS", new String[]{"PARTIDO", "CATEGORIA", "FILA/ASIENTOS", "CANT", "P.UNIT", "TOTAL"}, rows);

        println(ui.primary("Subtotal: " + Moneda.fmt(f.getSubtotal())
                + " | IVA: " + Moneda.fmt(f.getIva())
                + " | TOTAL: " + Moneda.fmt(f.getTotal())));

        if ("CREDITO".equalsIgnoreCase(f.getTipoPago())) {
            println();
            println(ui.label("FINANCIAMIENTO (CREDITO)"));
            BigDecimal tasaPct = f.getTasaInteres() == null
                    ? BigDecimal.ZERO : f.getTasaInteres().multiply(BigDecimal.valueOf(100));
            println("Entrada: " + Moneda.fmt(f.getEntrada())
                    + " | Monto financiado: " + Moneda.fmt(f.getMontoFinanciado())
                    + " | Cuotas: " + f.getNumCuotas()
                    + " | Tasa: " + tasaPct.stripTrailingZeros().toPlainString() + "% mensual");

            List<String[]> amort = new ArrayList<>();
            if (f.getAmortizacion() != null) {
                for (Cuota q : f.getAmortizacion()) {
                    amort.add(new String[]{
                        String.valueOf(q.getNumCuota()),
                        q.getFechaVencimiento(),
                        Moneda.fmt(q.getSaldoInicial()),
                        Moneda.fmt(q.getCuota()),
                        Moneda.fmt(q.getInteres()),
                        Moneda.fmt(q.getAbonoCapital()),
                        Moneda.fmt(q.getSaldoFinal())
                    });
                }
            }
            renderTable("TABLA DE AMORTIZACION (SISTEMA FRANCES)",
                    new String[]{"#", "VENCIMIENTO", "SALDO INI", "CUOTA", "INTERES", "CAPITAL", "SALDO FIN"}, amort);
        }
        println(ui.line());
    }

    // ================================================================= CUENTA
    private void miCuenta() {
        try {
            Cuenta c = ctrl.miCuenta();
            println();
            println(ui.label("MI CUENTA"));
            if (c == null) {
                println(ui.muted("No tienes una cuenta registrada."));
                return;
            }
            println("Numero de cuenta: " + c.getNumero());
            println(ui.primary("Saldo disponible: " + Moneda.fmt(c.getSaldo())));

            List<Movimiento> movs = ctrl.misMovimientos();
            List<String[]> rows = new ArrayList<>();
            for (Movimiento m : movs) {
                rows.add(new String[]{String.valueOf(m.getIdMovimiento()), m.getFecha(), m.getTipo(),
                        Moneda.fmt(m.getMonto()), m.getDescripcion(),
                        m.getIdFactura() > 0 ? "#" + m.getIdFactura() : "-"});
            }
            renderTable("MOVIMIENTOS", new String[]{"ID", "FECHA", "TIPO", "MONTO", "DESCRIPCION", "FACT"}, rows);
            if (movs.isEmpty()) {
                println(ui.muted("Sin movimientos registrados."));
            }
        } catch (Exception e) {
            println(ui.danger("Error: " + e.getMessage()));
        }
    }

    // ================================================================= ADMIN
    private void menuAdmin() {
        boolean volver = false;
        while (!volver) {
            println();
            println(ui.line());
            println(ui.primary("ADMINISTRACION (ADMIN)"));
            println(ui.line());
            println(ui.label("PARTIDOS"));
            println(menuItem("1", "Listar todos los partidos"));
            println(menuItem("2", "Registrar partido"));
            println(menuItem("3", "Actualizar partido"));
            println(menuItem("4", "Eliminar partido"));
            println();
            println(ui.label("LOCALIDADES"));
            println(menuItem("5", "Listar localidades de un partido"));
            println(menuItem("6", "Registrar localidad"));
            println(menuItem("7", "Actualizar localidad"));
            println(menuItem("8", "Eliminar localidad"));
            println();
            println(ui.label("REPORTES"));
            println(menuItem("9", "Resumen de ventas por partido"));
            println(menuItem("10", "Todas las facturas"));
            println();
            println(menuItem("0", "Volver al menu principal"));

            String op = leerTexto("Opcion");
            try {
                switch (op) {
                    case "1" -> renderPartidos("TODOS LOS PARTIDOS", ctrl.todosPartidos());
                    case "2" -> registrarPartido();
                    case "3" -> actualizarPartido();
                    case "4" -> eliminarPartido();
                    case "5" -> localidadesAdmin();
                    case "6" -> registrarLocalidad();
                    case "7" -> actualizarLocalidad();
                    case "8" -> eliminarLocalidad();
                    case "9" -> resumenVentas();
                    case "10" -> todasFacturas();
                    case "0" -> volver = true;
                    default -> println(ui.warning("Opcion invalida."));
                }
            } catch (Exception e) {
                println(ui.danger("Error: " + e.getMessage()));
            }
        }
    }

    private void mostrarSelecciones() {
        List<String[]> rows = new ArrayList<>();
        for (Seleccion s : ctrl.selecciones()) {
            rows.add(new String[]{String.valueOf(s.getIdSeleccion()), s.getNombre(), s.getGrupo()});
        }
        renderTable("SELECCIONES", new String[]{"ID", "SELECCION", "GRUPO"}, rows);
    }

    private void mostrarEstadios() {
        List<String[]> rows = new ArrayList<>();
        for (Estadio e : ctrl.estadios()) {
            rows.add(new String[]{String.valueOf(e.getIdEstadio()), e.getNombreOficial(), e.getCiudad()});
        }
        renderTable("ESTADIOS", new String[]{"ID", "ESTADIO", "CIUDAD"}, rows);
    }

    private void registrarPartido() {
        println(ui.primary("REGISTRAR PARTIDO"));
        mostrarSelecciones();
        int idLocal = leerEntero("ID seleccion local");
        int idVisita = leerEntero("ID seleccion visita");
        mostrarEstadios();
        int idEstadio = leerEntero("ID estadio");
        String fecha = leerTexto("Fecha (yyyy-MM-dd HH:mm:ss)");
        String grupo = leerTexto("Grupo (ej. A)");
        resultado(ctrl.registrarPartido(idLocal, idVisita, idEstadio, fecha, grupo));
    }

    private void actualizarPartido() {
        println(ui.primary("ACTUALIZAR PARTIDO"));
        renderPartidos("TODOS LOS PARTIDOS", ctrl.todosPartidos());
        int codigo = leerEntero("Codigo de partido a actualizar");
        mostrarSelecciones();
        int idLocal = leerEntero("ID seleccion local");
        int idVisita = leerEntero("ID seleccion visita");
        mostrarEstadios();
        int idEstadio = leerEntero("ID estadio");
        String fecha = leerTexto("Fecha (yyyy-MM-dd HH:mm:ss)");
        String grupo = leerTexto("Grupo (ej. A)");
        resultado(ctrl.actualizarPartido(codigo, idLocal, idVisita, idEstadio, fecha, grupo));
    }

    private void eliminarPartido() {
        println(ui.primary("ELIMINAR PARTIDO"));
        int codigo = leerEntero("Codigo de partido");
        if (confirmar("Confirmas eliminar el partido " + codigo + "? [s/n]:")) {
            resultado(ctrl.eliminarPartido(codigo));
        }
    }

    private void localidadesAdmin() {
        int codigoPartido = leerEntero("Codigo de partido");
        List<Localidad> locales = ctrl.localidadesAdmin(codigoPartido);
        List<String[]> rows = new ArrayList<>();
        for (Localidad l : locales) {
            rows.add(new String[]{String.valueOf(l.getId()), l.getCategoria(),
                    Moneda.fmt(l.getPrecio()), String.valueOf(l.getDisponibilidad())});
        }
        renderTable("LOCALIDADES (ADMIN)", new String[]{"ID", "CATEGORIA", "PRECIO", "DISPONIBILIDAD"}, rows);
        if (locales.isEmpty()) {
            println(ui.muted("No existen localidades para este partido."));
        }
    }

    private void registrarLocalidad() {
        println(ui.primary("REGISTRAR LOCALIDAD"));
        int codigoPartido = leerEntero("Codigo de partido");
        String categoria = leerTexto("Categoria (CAT1/CAT2/CAT3/CAT4)");
        int disponibilidad = leerEntero("Disponibilidad");
        BigDecimal precio = leerBigDecimal("Precio");
        resultado(ctrl.registrarLocalidad(codigoPartido, categoria, disponibilidad, precio));
    }

    private void actualizarLocalidad() {
        println(ui.primary("ACTUALIZAR LOCALIDAD"));
        int idLocalidad = leerEntero("ID de localidad");
        int disponibilidad = leerEntero("Disponibilidad");
        BigDecimal precio = leerBigDecimal("Precio");
        resultado(ctrl.actualizarLocalidad(idLocalidad, disponibilidad, precio));
    }

    private void eliminarLocalidad() {
        println(ui.primary("ELIMINAR LOCALIDAD"));
        int idLocalidad = leerEntero("ID de localidad");
        if (confirmar("Confirmas eliminar la localidad " + idLocalidad + "? [s/n]:")) {
            resultado(ctrl.eliminarLocalidad(idLocalidad));
        }
    }

    private void resumenVentas() {
        int codigoPartido = leerEntero("Codigo de partido");
        List<ResumenLocalidad> filas = ctrl.resumenVentas(codigoPartido);
        List<String[]> rows = new ArrayList<>();
        BigDecimal total = BigDecimal.ZERO;
        int vendidos = 0;
        for (ResumenLocalidad r : filas) {
            rows.add(new String[]{r.getLocalidad(), String.valueOf(r.getVendidos()), Moneda.fmt(r.getTotalRecaudado())});
            vendidos += r.getVendidos();
            if (r.getTotalRecaudado() != null) total = total.add(r.getTotalRecaudado());
        }
        renderTable("RESUMEN DE VENTAS", new String[]{"LOCALIDAD", "VENDIDOS", "RECAUDADO"}, rows);
        if (filas.isEmpty()) {
            println(ui.muted("Sin ventas para ese partido."));
        } else {
            println(ui.primary("Total vendidos: " + vendidos + " | Recaudado: " + Moneda.fmt(total)));
        }
    }

    private void todasFacturas() {
        List<Factura> facturas = ctrl.todasFacturas();
        renderFacturas("TODAS LAS FACTURAS", facturas, true);
        if (facturas.isEmpty()) {
            println(ui.muted("No hay facturas registradas."));
            return;
        }
        int id = leerEntero("Numero de factura a visualizar (0 para volver)");
        if (id == 0) return;
        Factura f = ctrl.comprobante(id);
        if (f == null) {
            println(ui.warning("No se pudo recuperar el comprobante."));
        } else {
            mostrarFactura(f);
        }
    }

    // ================================================================= helpers
    private void resultado(Resultado r) {
        if (r.isExito()) {
            println(ui.success(r.getMensaje()));
        } else {
            println(ui.warning(r.getMensaje()));
        }
    }

    private String leerTexto(String etiqueta) {
        System.out.print(ui.muted(etiqueta + ": "));
        return in.nextLine().trim();
    }

    private int leerEntero(String etiqueta) {
        while (true) {
            try {
                return Integer.parseInt(leerTexto(etiqueta));
            } catch (NumberFormatException e) {
                println(ui.warning("Ingresa un numero valido."));
            }
        }
    }

    private BigDecimal leerBigDecimal(String etiqueta) {
        while (true) {
            try {
                return new BigDecimal(leerTexto(etiqueta));
            } catch (Exception e) {
                println(ui.warning("Ingresa un decimal valido."));
            }
        }
    }

    private boolean confirmar(String prompt) {
        System.out.print(ui.muted(prompt + " "));
        String r = in.nextLine().trim();
        return r.equalsIgnoreCase("s") || r.equalsIgnoreCase("si");
    }

    private void renderTable(String titulo, String[] headers, List<String[]> rows) {
        println();
        println(ui.label(titulo));

        int cols = headers.length;
        int[] widths = new int[cols];
        for (int i = 0; i < cols; i++) {
            widths[i] = headers[i].length();
        }
        for (String[] row : rows) {
            for (int i = 0; i < cols && i < row.length; i++) {
                widths[i] = Math.max(widths[i], row[i] == null ? 0 : row[i].length());
            }
        }

        println(border(widths));
        println(row(headers, widths, true));
        println(border(widths));
        if (rows.isEmpty()) {
            println(row(emptyRow(cols, "Sin datos"), widths, false));
        } else {
            for (String[] row : rows) {
                println(row(row, widths, false));
            }
        }
        println(border(widths));
    }

    private String[] emptyRow(int cols, String value) {
        String[] row = new String[cols];
        row[0] = value;
        for (int i = 1; i < cols; i++) row[i] = "";
        return row;
    }

    private String border(int[] widths) {
        StringBuilder sb = new StringBuilder();
        sb.append("+");
        for (int width : widths) {
            sb.append("-".repeat(width + 2)).append("+");
        }
        return sb.toString();
    }

    private String row(String[] values, int[] widths, boolean header) {
        StringBuilder sb = new StringBuilder();
        sb.append("|");
        for (int i = 0; i < widths.length; i++) {
            String value = i < values.length && values[i] != null ? values[i] : "";
            sb.append(' ')
              .append(pad(value, widths[i]))
              .append(' ')
              .append("|");
        }
        String line = sb.toString();
        return header ? ui.primary(line) : line;
    }

    private String pad(String value, int width) {
        String trimmed = value;
        if (trimmed.length() > width) {
            trimmed = trimmed.substring(0, Math.max(0, width - 3)) + "...";
        }
        return String.format("%-" + width + "s", trimmed);
    }

    private String menuItem(String key, String text) {
        return "  " + ui.chip(key) + "  " + text;
    }

    private void println() {
        System.out.println();
    }

    private void println(String s) {
        System.out.println(s);
    }
}
