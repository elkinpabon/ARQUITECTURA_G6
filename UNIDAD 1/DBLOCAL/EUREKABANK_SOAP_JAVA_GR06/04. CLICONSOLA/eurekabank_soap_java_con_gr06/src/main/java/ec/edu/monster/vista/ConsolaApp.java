package ec.edu.monster.vista;

import ec.edu.monster.config.ServidorConfig;
import ec.edu.monster.controlador.BancoController;
import ec.edu.monster.util.Moneda;
import ec.edu.monster.ws.ClienteResumen;
import ec.edu.monster.ws.CuentaResumen;
import ec.edu.monster.ws.MovimientoModel;
import ec.edu.monster.ws.Resultado;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

/** Vista de consola (MVC) — misma funcionalidad que el cliente web. */
public class ConsolaApp {

    private static final Scanner sc = new Scanner(System.in);
    private static final BancoController ctrl = new BancoController();
    private static final Set<String> INGRESOS = Set.of("001", "003", "005", "008");

    public static void main(String[] args) {
        System.out.println("====== EUREKABANK GR06 — Cliente Consola ======");
        System.out.println("Servidor: " + ServidorConfig.base());
        while (true) {
            if (!ctrl.getSesion().activa()) {
                if (!login()) { System.out.println("Hasta luego."); return; }
            } else {
                menuPrincipal();
            }
        }
    }

    /* ---------- helpers de entrada ---------- */

    private static String pedir(String etiqueta) {
        System.out.print(etiqueta + ": ");
        return sc.nextLine().trim();
    }

    private static int opcion() {
        System.out.print("Opción: ");
        String s = sc.nextLine().trim();
        try { return Integer.parseInt(s); } catch (NumberFormatException e) { return -1; }
    }

    private static String pedirMoneda() {
        System.out.println("Moneda del monto: 1) Dólares (preferente)  2) Soles");
        String s = pedir("Opción [1]");
        return "2".equals(s) ? "01" : "02";
    }

    private static void msg(Resultado r) {
        System.out.println((r.isExito() ? "[OK] " : "[ERROR] ") + r.getMensaje());
    }

    /* ---------- login ---------- */

    private static boolean login() {
        System.out.println("\n--- Iniciar sesión ---  (0 = salir)");
        String u = pedir("Usuario");
        if ("0".equals(u)) return false;
        String c = pedir("Clave");
        try {
            if (ctrl.login(u, c)) {
                System.out.println(">> Bienvenido " + u
                        + (ctrl.getSesion().isAdmin() ? " [ADMIN]" : "") + "\n");
            } else {
                System.out.println(">> Usuario o clave inválidos.\n");
            }
        } catch (Exception e) {
            System.out.println(">> Error contactando el servidor: " + e.getMessage());
        }
        return true;
    }

    /* ---------- menú principal ---------- */

    private static void menuPrincipal() {
        boolean admin = ctrl.getSesion().isAdmin();
        System.out.println("\n===== MENÚ" + (admin ? " (ADMIN)" : "")
                + " — " + ctrl.getSesion().getUsuario() + " =====");
        if (admin) {
            System.out.println("1. Buscar cliente y ver cuentas");
            System.out.println("2. Operar / movimientos (cuentas cargadas)");
            System.out.println("3. Registrar cliente");
            System.out.println("4. Registrar cuenta");
            System.out.println("5. Eliminar cuenta");
            System.out.println("9. Cerrar sesión");
            switch (opcion()) {
                case 1 -> buscarCliente();
                case 2 -> operarMenu();
                case 3 -> registrarCliente();
                case 4 -> registrarCuenta();
                case 5 -> eliminarCuenta();
                case 9 -> ctrl.logout();
                default -> System.out.println("Opción inválida.");
            }
        } else {
            mostrarCuentas();
            System.out.println("1. Operar / movimientos");
            System.out.println("2. Actualizar saldos");
            System.out.println("9. Cerrar sesión");
            switch (opcion()) {
                case 1 -> operarMenu();
                case 2 -> { ctrl.cargarCuentas(null);
                            System.out.println(">> Saldos actualizados."); }
                case 9 -> ctrl.logout();
                default -> System.out.println("Opción inválida.");
            }
        }
    }

    private static void buscarCliente() {
        List<ClienteResumen> cls = ctrl.listarClientes();
        System.out.println("\n--- Clientes registrados ---");
        for (ClienteResumen c : cls) {
            System.out.println("  " + c.getCodigo() + " · DNI " + c.getDni()
                    + " · " + c.getNombre());
        }
        String cod = pedir("Código de cliente");
        ctrl.cargarCuentas(cod);
        mostrarCuentas();
    }

    private static void mostrarCuentas() {
        List<CuentaResumen> ct = ctrl.getCuentas();
        if (ct == null || ct.isEmpty()) {
            System.out.println("\n(No hay cuentas cargadas. Busca un cliente.)");
            return;
        }
        System.out.println("\n--- Cuentas de " + ct.get(0).getNombreCliente()
                + " (cliente " + ct.get(0).getCodigoCliente() + ") ---");
        for (CuentaResumen c : ct) {
            System.out.printf("  %s | %,.2f %s | %s%n",
                    c.getCodigoCuenta(), c.getSaldo(),
                    Moneda.nombre(c.getMoneda()), c.getEstado());
        }
        System.out.printf("  SALDO TOTAL: %,.2f%n", ctrl.saldoTotal());
    }

    /* ---------- operar ---------- */

    private static void operarMenu() {
        if (ctrl.getCuentas() == null || ctrl.getCuentas().isEmpty()) {
            System.out.println(">> No hay cuentas. Busca un cliente primero.");
            return;
        }
        mostrarCuentas();
        String cuenta = pedir("Cuenta a operar");
        System.out.println("1. Consultar saldo");
        if (ctrl.getSesion().isAdmin()) System.out.println("2. Depositar");
        System.out.println("3. Retirar");
        System.out.println("4. Transferir");
        System.out.println("5. Ver movimientos");
        try {
            switch (opcion()) {
                case 1 -> msg(ctrl.consultarSaldo(cuenta));
                case 2 -> {
                    String m = pedir("Monto");
                    msg(ctrl.depositar(cuenta, m, pedirMoneda()));
                }
                case 3 -> {
                    String m = pedir("Monto");
                    msg(ctrl.retirar(cuenta, m, pedirMoneda()));
                }
                case 4 -> {
                    String dst = pedir("Cuenta destino");
                    String m = pedir("Monto");
                    msg(ctrl.transferir(cuenta, dst, m, pedirMoneda()));
                }
                case 5 -> verMovimientos(cuenta);
                default -> System.out.println("Opción inválida.");
            }
            if (ctrl.getSesion().isAdmin()) ctrl.cargarCuentas(
                    ctrl.getCuentas().isEmpty() ? null
                    : ctrl.getCuentas().get(0).getCodigoCliente());
            else ctrl.cargarCuentas(null);
        } catch (Exception e) {
            System.out.println(">> Error: " + e.getMessage());
        }
    }

    private static void verMovimientos(String cuenta) {
        List<MovimientoModel> ms = ctrl.movimientos(cuenta);
        if (ms == null || ms.isEmpty()) {
            System.out.println(">> Sin movimientos o sin acceso.");
            return;
        }
        System.out.println("\n--- Estado de cuenta " + cuenta
                + " (ordenado por fecha, descendente) ---");
        System.out.printf("%-4s %-12s %-20s %-8s %12s %12s%n",
                "#", "Fecha", "Tipo", "Mov.", "Crédito", "Débito");
        double tin = 0, tout = 0;
        for (MovimientoModel m : ms) {
            boolean in = INGRESOS.contains(m.getCodigoTipoMovimiento());
            double imp = m.getImporteMovimiento();
            if (in) tin += imp; else tout += imp;
            System.out.printf("%-4d %-12s %-20s %-8s %12s %12s%n",
                    m.getNumeroMovimiento(), m.getFechaMovimiento(),
                    m.getTipoDescripcion(),
                    in ? "CRÉDITO" : "DÉBITO",
                    in ? String.format("%,.2f", imp) : "",
                    in ? "" : String.format("%,.2f", imp));
            if (m.getMonedaOrigen() != null && !m.getMonedaOrigen().isBlank()) {
                System.out.printf("      [conversión] %,.2f %s  x tasa %s  =  %,.2f%n",
                        m.getImporteOrigen() == null ? 0 : m.getImporteOrigen(),
                        Moneda.nombre(m.getMonedaOrigen()),
                        m.getTasaAplicada(), imp);
            }
        }
        System.out.printf("TOTALES  Créditos: %,.2f   Débitos: %,.2f   Neto: %,.2f%n",
                tin, tout, tin - tout);

        if ("S".equalsIgnoreCase(pedir(
                "¿Exportar estado de cuenta a HTML (imprimible a PDF)? (S/N)"))) {
            String titular = "-", moneda = "-";
            for (ec.edu.monster.ws.CuentaResumen c : ctrl.getCuentas()) {
                if (cuenta.equals(c.getCodigoCuenta())) {
                    titular = c.getNombreCliente();
                    moneda = c.getMoneda();
                    break;
                }
            }
            try {
                String ruta = ec.edu.monster.util.ExportHtml
                        .estadoCuenta(cuenta, titular, moneda, ms);
                System.out.println(">> Exportado: " + ruta);
                System.out.println(">> Ábrelo en el navegador y usa "
                        + "'Imprimir > Guardar como PDF'.");
            } catch (Exception e) {
                System.out.println(">> Error exportando: " + e.getMessage());
            }
        }
    }

    /* ---------- admin ---------- */

    private static void registrarCliente() {
        System.out.println("\n--- Registrar cliente ---");
        Resultado r = ctrl.registrarCliente(
                pedir("Apellido paterno"), pedir("Apellido materno"),
                pedir("Nombre"), pedir("DNI"), pedir("Ciudad"),
                pedir("Dirección"), pedir("Teléfono"), pedir("Email"));
        msg(r);
    }

    private static void registrarCuenta() {
        System.out.println("\n--- Registrar cuenta ---");
        List<CuentaResumen> ct = ctrl.getCuentas();
        String sugerido = (ct != null && !ct.isEmpty())
                ? ct.get(0).getCodigoCliente() : null;
        if (sugerido != null) {
            System.out.println("Cliente cargado: " + sugerido + " — "
                    + ct.get(0).getNombreCliente());
        }
        String cli = pedir("Código de cliente"
                + (sugerido != null ? " [Enter = " + sugerido + "]" : ""));
        if (cli.isEmpty() && sugerido != null) cli = sugerido;
        System.out.println("Moneda: 1) Dólares  2) Soles");
        String mon = "2".equals(pedir("Opción [1]")) ? "01" : "02";
        Resultado r = ctrl.registrarCuenta(cli, mon);
        msg(r);
        if (r.isExito() && cli.equals(sugerido)) ctrl.cargarCuentas(cli);
    }

    private static void eliminarCuenta() {
        System.out.println("\n--- Eliminar cuenta ---");
        List<CuentaResumen> ct = ctrl.getCuentas();
        String cta;
        if (ct != null && !ct.isEmpty()) {
            System.out.println("Cuentas de " + ct.get(0).getNombreCliente() + ":");
            for (int i = 0; i < ct.size(); i++) {
                CuentaResumen c = ct.get(i);
                System.out.printf("  %d) %s  %,.2f %s  %s%n", i + 1,
                        c.getCodigoCuenta(), c.getSaldo(),
                        Moneda.nombre(c.getMoneda()), c.getEstado());
            }
            String sel = pedir("Número de la cuenta a eliminar (o código)");
            try {
                int idx = Integer.parseInt(sel);
                cta = (idx >= 1 && idx <= ct.size())
                        ? ct.get(idx - 1).getCodigoCuenta() : sel;
            } catch (NumberFormatException e) {
                cta = sel;
            }
        } else {
            cta = pedir("Código de cuenta a eliminar");
        }
        if ("S".equalsIgnoreCase(pedir("¿Confirmar borrado de " + cta
                + " y sus movimientos? (S/N)"))) {
            Resultado r = ctrl.eliminarCuenta(cta);
            msg(r);
            String cli = (ct != null && !ct.isEmpty())
                    ? ct.get(0).getCodigoCliente() : null;
            if (r.isExito() && cli != null) ctrl.cargarCuentas(cli);
        } else {
            System.out.println(">> Cancelado.");
        }
    }
}
