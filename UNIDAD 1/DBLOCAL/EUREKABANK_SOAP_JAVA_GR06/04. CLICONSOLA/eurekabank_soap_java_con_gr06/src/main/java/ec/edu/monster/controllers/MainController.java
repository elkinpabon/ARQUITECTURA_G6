package ec.edu.monster.controllers;

import ec.edu.monster.ws.CuentaService;
import ec.edu.monster.ws.LoginService;
import ec.edu.monster.ws.MovimientoModel;
import ec.edu.monster.ws.MovimientoService;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

public class MainController {
    private LoginService loginService = new LoginService();
    private CuentaService cuentaService = new CuentaService();
    private MovimientoService movimientoService = new MovimientoService();
    private String usuarioActual = null;

    public String getUsuarioActual() {
        return usuarioActual;
    }

    public boolean iniciarSesion(String username, String password) {
        // La clave viaja en texto plano; el SERVIDOR aplica SHA1 contra la tabla usuario.
        if (loginService.auth(username, password)) {
            usuarioActual = username;
            return true;
        }
        return false;
    }

    public void cerrarSesion() {
        usuarioActual = null;
    }

    public boolean realizarDeposito(String cuenta, String monto) {
        return cuentaService.realizarDeposito(cuenta, monto);
    }

    public String verMovimientos(String cuenta) {
        try {
            List<MovimientoModel> movimientos = movimientoService.obtenerMovimientos(cuenta);

            if (movimientos == null || movimientos.isEmpty()) {
                return "No se encontraron movimientos para la cuenta: " + cuenta;
            }

            StringBuilder resultado = new StringBuilder();
            resultado.append("\n===== MOVIMIENTOS DE LA CUENTA ").append(cuenta).append(" =====\n");
            resultado.append(String.format("%-10s %-20s %-15s %-20s %-15s %-15s%n",
                    "Nro", "Fecha", "Tipo Mov.", "Descripción", "Importe", "Cta Referencia"));
            resultado.append("------------------------------------------------------------------------\n");

            for (MovimientoModel mov : movimientos) {
                resultado.append(String.format("%-10d %-20s %-15s %-20s %-15.2f %-15s%n",
                        mov.getNumeroMovimiento(),
                        mov.getFechaMovimiento(),
                        mov.getCodigoTipoMovimiento(),
                        mov.getTipoDescripcion(),
                        mov.getImporteMovimiento(),
                        mov.getCuentaReferencia() != null ? mov.getCuentaReferencia() : "N/A"));
            }

            // Calcular totales
            double totalIngresos = movimientos.stream()
                    .filter(m -> !m.getTipoDescripcion().equals("Retiro"))
                    .mapToDouble(MovimientoModel::getImporteMovimiento)
                    .sum();

            double totalEgresos = movimientos.stream()
                    .filter(m -> m.getTipoDescripcion().equals("Retiro"))
                    .mapToDouble(MovimientoModel::getImporteMovimiento)
                    .sum();

            resultado.append("\nRESUMEN:\n");
            resultado.append(String.format("Total Ingresos: %.2f%n", totalIngresos));
            resultado.append(String.format("Total Egresos (Retiros): %.2f%n", Math.abs(totalEgresos)));
            resultado.append(String.format("Saldo Neto: %.2f%n", totalIngresos + totalEgresos));

            return resultado.toString();
        } catch (Exception e) {
            return "Error al obtener los movimientos: " + e.getMessage();
        }
    }

    private String hashPassword(String input) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hashBytes = digest.digest(input.getBytes());
        StringBuilder hexString = new StringBuilder();
        for (byte b : hashBytes) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }
}
