package ec.edu.monster.controller;

import ec.edu.monster.view.CuentaView;
import ec.edu.monster.ws.CuentaService;

import javax.swing.JOptionPane;
import java.awt.Color;
import javax.swing.UIManager;

public class CuentaController {

    private CuentaService cuentaService = new CuentaService();
    
    public CuentaController() {
        this.cuentaService= new CuentaService();
    }

    public void procesarDeposito(String cuenta, String monto, CuentaView cuentaView) {

        // Validaciones de campos vacíos
        if (cuenta.isEmpty() || monto.isEmpty()) {
            mostrarMensaje("Por favor, complete todos los campos.", "Campos Vacíos");
            return;
        }

        // Validar si el monto es numérico
        if (!esNumeroValido(monto)) {
            mostrarMensaje("El monto debe ser un número válido.", "Error en el Monto");
            return;
        }

        // Realizar depósito
        boolean exito = cuentaService.realizarDeposito(cuenta, monto);
        if (exito) {
            mostrarMensaje("Depósito realizado exitosamente.", "Éxito");
            cuentaView.dispose();
        } else {
            mostrarMensaje("No se pudo realizar el depósito. Verifique los datos ingresados.", "Error en el Depósito");
        }
    }

    private void mostrarMensaje(String mensaje, String titulo) {
        // Personalizar ventana de diálogo
        UIManager.put("OptionPane.background", new Color(0, 102, 204));
        UIManager.put("Panel.background", new Color(0, 102, 204));
        UIManager.put("OptionPane.messageForeground", Color.WHITE);
        JOptionPane.showMessageDialog(null, mensaje, titulo, JOptionPane.INFORMATION_MESSAGE);
    }

    private boolean esNumeroValido(String monto) {
        try {
            double valor = Double.parseDouble(monto);
            return valor > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
