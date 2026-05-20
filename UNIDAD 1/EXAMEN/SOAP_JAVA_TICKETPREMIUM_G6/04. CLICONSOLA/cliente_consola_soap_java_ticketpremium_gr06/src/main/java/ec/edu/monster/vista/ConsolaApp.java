package ec.edu.monster.vista;

import ec.edu.monster.controlador.TicketController;
import ec.edu.monster.util.ConsoleTheme;
import ec.edu.monster.util.Moneda;
import ec.edu.monster.ws.Factura;
import ec.edu.monster.ws.Localidad;
import ec.edu.monster.ws.Partido;
import ec.edu.monster.ws.Resultado;
import ec.edu.monster.ws.ResumenLocalidad;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ThreadLocalRandom;

/** Cliente consola TicketPremium con experiencia visual mejorada. */
public class ConsolaApp {

    private final Scanner in = new Scanner(System.in);
    private final TicketController ctrl = new TicketController();
    private final ConsoleTheme ui = new ConsoleTheme();
    private final List<EntradaConsola> entradas = new ArrayList<>();

    public static void main(String[] args) {
        new ConsolaApp().run();
    }

    private void run() {
        banner();
        while (true) {
            if (!login()) {
                if (!confirmar("Reintentar inicio de sesion? [s/n]: ")) {
                    break;
                }
                continue;
            }
            menu();
            entradas.clear();
            ctrl.logout();
        }
        println();
        println(ui.muted("Sesion cerrada. Hasta pronto."));
    }

    private void banner() {
        println(ui.line());
        println(ui.title("TICKETPREMIUM GR06 | CLIENTE CONSOLA"));
        println(ui.muted("MVC + SOAP | Venta de boletos y administracion"));
        println(ui.muted("Servidor: " + ec.edu.monster.config.ServidorConfig.base()));
        println(ui.line());
    }

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
                println(ui.success("Bienvenido, " + ctrl.getSesion().getNombre() + " [" + ctrl.getSesion().getUsuario().getRol() + "]"));
                return true;
            }
            println(ui.warning("Credenciales invalidas."));
        } catch (Exception e) {
            println(ui.danger("Error contactando el servidor: " + e.getMessage()));
        }
        return false;
    }

    private void menu() {
        boolean salir = false;
        while (!salir) {
            println();
            println(ui.line());
            println(ui.primary("MENU PRINCIPAL"));
            println(ui.muted("Sesion activa: " + ctrl.getSesion().getNombre() + " | Rol: " + ctrl.getSesion().getUsuario().getRol()));
            println(ui.line());

            println(ui.label("OPERACIONES DE CLIENTE"));
            println(menuItem("1", "Listar partidos disponibles"));
            println(menuItem("2", "Listar localidades por partido"));
            println(ui.success(menuItem("3", "Registrar venta / comprar boletos")));
            println(menuItem("4", "Mis facturas"));
            println(menuItem("5", "Reporte de entradas / mis boletos"));

            if (ctrl.getSesion().isAdmin()) {
                println();
                println(ui.label("ADMINISTRACION"));
                println(menuItem("6", "Resumen ventas por partido"));
                println(menuItem("7", "Listar todas las localidades por partido"));
                println(menuItem("8", "Registrar partido"));
                println(menuItem("9", "Actualizar partido"));
                println(menuItem("10", "Eliminar partido"));
                println(menuItem("11", "Registrar localidad"));
                println(menuItem("12", "Actualizar localidad"));
                println(menuItem("13", "Eliminar localidad"));
            }

            println();
            println(menuItem("0", "Cerrar sesion"));

            String op = leerTexto("Opcion");
            switch (op) {
                case "1" -> mostrarPartidos();
                case "2" -> mostrarLocalidades();
                case "3" -> registrarVenta();
                case "4" -> mostrarFacturas();
                case "5" -> mostrarEntradas();
                case "6" -> mostrarReporte();
                case "7" -> mostrarTodasLasLocalidades();
                case "8" -> registrarPartido();
                case "9" -> actualizarPartido();
                case "10" -> eliminarPartido();
                case "11" -> registrarLocalidad();
                case "12" -> actualizarLocalidad();
                case "13" -> eliminarLocalidad();
                case "0" -> salir = true;
                default -> println(ui.warning("Opcion invalida."));
            }
        }
    }

    private void mostrarPartidos() {
        try {
            List<Partido> partidos = ctrl.partidosDisponibles();
            List<String[]> rows = new ArrayList<>();
            for (Partido p : partidos) {
                rows.add(new String[]{String.valueOf(p.getCodigo()), p.getEquipoLocal(), p.getEquipoVisita(), p.getFecha(), p.getLugar()});
            }
            renderTable("PARTIDOS DISPONIBLES", new String[]{"COD", "LOCAL", "VISITA", "FECHA", "LUGAR"}, rows);
            if (partidos.isEmpty()) {
                println(ui.muted("No hay partidos disponibles en este momento."));
            }
        } catch (Exception e) {
            println(ui.danger("Error: " + e.getMessage()));
        }
    }

    private void mostrarLocalidades() {
        int codigoPartido = leerEntero("Codigo de partido");
        mostrarLocalidadesDe(codigoPartido, false);
    }

    private void registrarVenta() {
        int codigoPartido = leerEntero("Codigo de partido");
        String codigoLocalidad = leerTexto("Codigo localidad (GENERAL/TRIBUNA/PALCO/PREFERENCIA)");
        int cantidad = leerEntero("Cantidad");
        try {
            Resultado r = ctrl.comprar(codigoPartido, codigoLocalidad, cantidad);
            if (r.isExito() && r.getFactura() != null) {
                Factura f = r.getFactura();
                println(ui.success(r.getMensaje()));
                EntradaConsola entrada = crearEntrada(codigoPartido, codigoLocalidad, cantidad, f);
                entradas.add(entrada);
                mostrarComprobanteTextual(entrada);
                println(ui.primary("Factura #" + f.getIdFactura() + " | "
                        + "Subtotal " + Moneda.fmt(f.getSubtotal()) + " | "
                        + "IVA " + Moneda.fmt(f.getIva()) + " | "
                        + "Total " + Moneda.fmt(f.getTotal())));
            } else {
                println(ui.warning(r.getMensaje()));
            }
        } catch (Exception e) {
            println(ui.danger("Error registrando la compra: " + e.getMessage()));
        }
    }

    private void mostrarComprobanteTextual(EntradaConsola entrada) {
        println();
        println(ui.label("COMPROBANTE DE COMPRA"));
        println(ui.line());
        println("Codigo R: " + entrada.codigoR);
        println("Factura: #" + entrada.idFactura);
        println("Fecha: " + entrada.fecha);
        println("Cliente: " + entrada.cliente);
        println("Partido: " + entrada.partido);
        println("Localidad: " + entrada.localidad);
        println("Cantidad: " + entrada.cantidad);
        println("Subtotal: " + Moneda.fmt(entrada.subtotal));
        println("IVA: " + Moneda.fmt(entrada.iva));
        println("Total: " + Moneda.fmt(entrada.total));
        println("Entrada simulada: " + entrada.codigoR);
        mostrarQrSimulado(entrada.codigoR + "-" + entrada.idFactura);
        println(ui.line());
    }

    private void mostrarEntradas() {
        if (entradas.isEmpty()) {
            println(ui.muted("Todavia no tienes entradas registradas en esta sesion."));
            return;
        }

        List<String[]> rows = new ArrayList<>();
        for (EntradaConsola e : entradas) {
            rows.add(new String[]{String.valueOf(e.idFactura), e.codigoR, e.fecha, e.partido, e.localidad, Moneda.fmt(e.total)});
        }
        renderTable("REPORTE DE ENTRADAS", new String[]{"FACT", "CODIGO R", "FECHA", "PARTIDO", "LOCALIDAD", "TOTAL"}, rows);

        int facturaId = leerEntero("Factura a visualizar (0 para volver)");
        if (facturaId == 0) {
            return;
        }

        EntradaConsola entrada = buscarEntrada(facturaId);
        if (entrada == null) {
            println(ui.warning("No existe una entrada con esa factura en esta sesion."));
            return;
        }

        mostrarComprobanteTextual(entrada);
    }

    private void mostrarQrSimulado(String seed) {
        println("QR SIMULADO");
        int size = 11;
        int hash = seed.hashCode();
        for (int fila = 0; fila < size; fila++) {
            StringBuilder line = new StringBuilder();
            for (int col = 0; col < size; col++) {
                if (esMarcoQr(fila, col, size)) {
                    line.append("##");
                    continue;
                }
                int bit = Math.abs(hash + fila * 31 + col * 17 + fila * col * 7);
                line.append((bit % 3 == 0) ? "  " : "##");
            }
            println(line.toString());
        }
    }

    private boolean esMarcoQr(int fila, int col, int size) {
        return enCuadro(fila, col, 0, 0)
                || enCuadro(fila, col, 0, size - 3)
                || enCuadro(fila, col, size - 3, 0)
                || enCuadro(fila, col, size - 3, size - 3);
    }

    private boolean enCuadro(int fila, int col, int inicioFila, int inicioCol) {
        return fila >= inicioFila && fila < inicioFila + 3 && col >= inicioCol && col < inicioCol + 3;
    }

    private void mostrarFacturas() {
        try {
            List<Factura> facturas = ctrl.misFacturas();
            List<String[]> rows = new ArrayList<>();
            for (Factura f : facturas) {
                rows.add(new String[]{String.valueOf(f.getIdFactura()), f.getFecha(), Moneda.fmt(f.getSubtotal()), Moneda.fmt(f.getIva()), Moneda.fmt(f.getTotal())});
            }
            renderTable("MIS FACTURAS", new String[]{"#", "FECHA", "SUBTOTAL", "IVA", "TOTAL"}, rows);
            if (facturas.isEmpty()) {
                println(ui.muted("Todavia no tienes facturas registradas."));
            }
        } catch (Exception e) {
            println(ui.danger("Error: " + e.getMessage()));
        }
    }

    private void mostrarReporte() {
        if (!ctrl.getSesion().isAdmin()) {
            println(ui.warning("El reporte solo esta disponible para ADMIN."));
            return;
        }
        int codigoPartido = leerEntero("Codigo de partido");
        try {
            List<ResumenLocalidad> filas = ctrl.resumenVentas(codigoPartido);
            List<String[]> rows = new ArrayList<>();
            BigDecimal total = BigDecimal.ZERO;
            int vendidos = 0;
            for (ResumenLocalidad r : filas) {
                rows.add(new String[]{r.getLocalidad(), String.valueOf(r.getVendidos()), Moneda.fmt(r.getTotalRecaudado())});
                vendidos += r.getVendidos();
                total = total.add(r.getTotalRecaudado());
            }
            renderTable("RESUMEN DE VENTAS", new String[]{"LOCALIDAD", "VENDIDOS", "RECAUDADO"}, rows);
            if (filas.isEmpty()) {
                println(ui.muted("Sin ventas para ese partido."));
            } else {
                println(ui.primary("Total vendidos: " + vendidos + " | Recaudado: " + Moneda.fmt(total)));
            }
        } catch (Exception e) {
            println(ui.danger("Error: " + e.getMessage()));
        }
    }

    private void mostrarTodasLasLocalidades() {
        int codigoPartido = leerEntero("Codigo de partido");
        try {
            List<Localidad> locales = ctrl.localidadesAdmin(codigoPartido);
            List<String[]> rows = new ArrayList<>();
            for (Localidad l : locales) {
                rows.add(new String[]{String.valueOf(l.getId()), l.getCodigoLocalidad(), String.valueOf(l.getDisponibilidad()), Moneda.fmt(l.getPrecio())});
            }
            renderTable("LOCALIDADES ADMIN", new String[]{"ID", "LOCALIDAD", "DISPONIBILIDAD", "PRECIO"}, rows);
            if (locales.isEmpty()) {
                println(ui.muted("No existen localidades para este partido."));
            }
        } catch (Exception e) {
            println(ui.danger("Error: " + e.getMessage()));
        }
    }

    private void registrarPartido() {
        println(ui.primary("REGISTRAR PARTIDO"));
        String local = leerTexto("Equipo local");
        String visita = leerTexto("Equipo visita");
        String fecha = leerTexto("Fecha (yyyy-MM-dd HH:mm:ss)");
        String lugar = leerTexto("Lugar");
        resultado(ctrl.registrarPartido(local, visita, fecha, lugar));
    }

    private void actualizarPartido() {
        println(ui.primary("ACTUALIZAR PARTIDO"));
        int codigo = leerEntero("Codigo");
        String local = leerTexto("Equipo local");
        String visita = leerTexto("Equipo visita");
        String fecha = leerTexto("Fecha (yyyy-MM-dd HH:mm:ss)");
        String lugar = leerTexto("Lugar");
        resultado(ctrl.actualizarPartido(codigo, local, visita, fecha, lugar));
    }

    private void eliminarPartido() {
        println(ui.primary("ELIMINAR PARTIDO"));
        int codigo = leerEntero("Codigo");
        resultado(ctrl.eliminarPartido(codigo));
    }

    private void registrarLocalidad() {
        println(ui.primary("REGISTRAR LOCALIDAD"));
        int codigoPartido = leerEntero("Codigo partido");
        String codigoLocalidad = leerTexto("Codigo localidad");
        int disponibilidad = leerEntero("Disponibilidad");
        BigDecimal precio = leerBigDecimal("Precio");
        resultado(ctrl.registrarLocalidad(codigoPartido, codigoLocalidad, disponibilidad, precio));
    }

    private void actualizarLocalidad() {
        println(ui.primary("ACTUALIZAR LOCALIDAD"));
        int idLocalidad = leerEntero("ID localidad");
        int disponibilidad = leerEntero("Disponibilidad");
        BigDecimal precio = leerBigDecimal("Precio");
        resultado(ctrl.actualizarLocalidad(idLocalidad, disponibilidad, precio));
    }

    private void eliminarLocalidad() {
        println(ui.primary("ELIMINAR LOCALIDAD"));
        int idLocalidad = leerEntero("ID localidad");
        resultado(ctrl.eliminarLocalidad(idLocalidad));
    }

    private void resultado(Resultado r) {
        if (r.isExito()) {
            println(ui.success(r.getMensaje()));
        } else {
            println(ui.warning(r.getMensaje()));
        }
    }

    private void mostrarLocalidadesDe(int codigoPartido, boolean admin) {
        try {
            List<Localidad> locales = admin ? ctrl.localidadesAdmin(codigoPartido) : ctrl.localidadesDe(codigoPartido);
            List<String[]> rows = new ArrayList<>();
            for (Localidad l : locales) {
                rows.add(new String[]{String.valueOf(l.getId()), l.getCodigoLocalidad(), String.valueOf(l.getDisponibilidad()), Moneda.fmt(l.getPrecio())});
            }
            renderTable(admin ? "LOCALIDADES ADMIN" : "LOCALIDADES DISPONIBLES", new String[]{"ID", "LOCALIDAD", "DISPONIBILIDAD", "PRECIO"}, rows);
            if (locales.isEmpty()) {
                println(ui.muted("Sin resultados para el partido seleccionado."));
            }
        } catch (Exception e) {
            println(ui.danger("Error: " + e.getMessage()));
        }
    }

    private String codigoRecibo() {
        String fecha = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss"));
        int random = ThreadLocalRandom.current().nextInt(1000, 10000);
        return "R-" + fecha + "-" + random;
    }

    private String descripcionPartido(int codigoPartido) {
        try {
            for (Partido p : ctrl.partidosDisponibles()) {
                if (p.getCodigo() == codigoPartido) {
                    return p.getEquipoLocal() + " vs " + p.getEquipoVisita();
                }
            }
        } catch (Exception ignored) {
            // Si el listado falla, mostramos el identificador para no bloquear el comprobante.
        }
        return "Partido #" + codigoPartido;
    }

    private EntradaConsola crearEntrada(int codigoPartido, String codigoLocalidad, int cantidad, Factura factura) {
        EntradaConsola e = new EntradaConsola();
        e.idFactura = factura.getIdFactura();
        e.codigoR = codigoRecibo();
        e.fecha = factura.getFecha();
        e.cliente = ctrl.getSesion().getNombre();
        e.partido = descripcionPartido(codigoPartido);
        e.localidad = codigoLocalidad;
        e.cantidad = cantidad;
        e.subtotal = factura.getSubtotal();
        e.iva = factura.getIva();
        e.total = factura.getTotal();
        return e;
    }

    private EntradaConsola buscarEntrada(int facturaId) {
        for (EntradaConsola e : entradas) {
            if (e.idFactura == facturaId) {
                return e;
            }
        }
        return null;
    }

    private static final class EntradaConsola {
        private int idFactura;
        private String codigoR;
        private String fecha;
        private String cliente;
        private String partido;
        private String localidad;
        private int cantidad;
        private BigDecimal subtotal;
        private BigDecimal iva;
        private BigDecimal total;
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
            sb.append(repeat("-", width + 2)).append("+");
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

    private String repeat(String s, int count) {
        return s.repeat(Math.max(0, count));
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
