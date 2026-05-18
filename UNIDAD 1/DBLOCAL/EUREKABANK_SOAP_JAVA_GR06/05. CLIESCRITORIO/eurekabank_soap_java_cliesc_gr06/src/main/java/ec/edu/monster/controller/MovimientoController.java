package ec.edu.monster.controller;

import ec.edu.monster.view.MovimientoView;
import ec.edu.monster.ws.MovimientoModel;
import ec.edu.monster.ws.MovimientoService;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class MovimientoController {

    private MovimientoService movimientoService;

    public MovimientoController() {
        this.movimientoService = new MovimientoService();
    }

    /**
     * Método para cargar y mostrar los movimientos en la vista proporcionada.
     *
     * @param cuenta          Número de cuenta a buscar.
     * @param movimientoView  Instancia de la vista donde se mostrarán los resultados.
     */
    public void cargarMovimientos(String cuenta, MovimientoView movimientoView) {
        try {
            List<MovimientoModel> movimientos = movimientoService.obtenerMovimientos(cuenta);

            JPanel panelResultados = new JPanel();
            panelResultados.setLayout(new BoxLayout(panelResultados, BoxLayout.Y_AXIS));
            panelResultados.setBackground(Color.WHITE);

            if (movimientos == null || movimientos.isEmpty()) {
                JLabel noResultados = new JLabel("No se encontraron movimientos para la cuenta: " + cuenta);
                noResultados.setFont(new Font("Arial", Font.BOLD, 16));
                noResultados.setForeground(Color.RED);
                panelResultados.add(noResultados);
            } else {
                // Crear celdas para cada movimiento
                for (MovimientoModel mov : movimientos) {
                    JPanel celda = movimientoView.crearCelda(
                            "Cuenta: "+mov.getCodigoCuenta(),
                            "Fecha: " + mov.getFechaMovimiento(),
                            "Movimiento: " + mov.getNumeroMovimiento(),
                            
                            "Descripción: " + mov.getTipoDescripcion(),
                            "Tipo Mov.: " + mov.getCodigoTipoMovimiento(),
                            "Importe: $" + String.format("%.2f", mov.getImporteMovimiento())
                    );
                    panelResultados.add(celda);
                }

                // Resumen de totales
                double totalIngresos = movimientos.stream()
                        .filter(m -> !m.getTipoDescripcion().equals("Retiro"))
                        .mapToDouble(MovimientoModel::getImporteMovimiento)
                        .sum();

                double totalEgresos = movimientos.stream()
                        .filter(m -> m.getTipoDescripcion().equals("Retiro"))
                        .mapToDouble(MovimientoModel::getImporteMovimiento)
                        .sum();

                JPanel resumenPanel = new JPanel(new GridLayout(3, 1));
                resumenPanel.setBackground(new Color(255, 255, 255));
                resumenPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

                JLabel totalIngresosLabel = new JLabel("Total Ingresos: $" + String.format("%.2f", totalIngresos));
                JLabel totalEgresosLabel = new JLabel("Total Egresos (Retiros): $" + String.format("%.2f", Math.abs(totalEgresos)));
                JLabel saldoNetoLabel = new JLabel("Saldo Neto: $" + String.format("%.2f", totalIngresos + totalEgresos));

                resumenPanel.add(totalIngresosLabel);
                resumenPanel.add(totalEgresosLabel);
                resumenPanel.add(saldoNetoLabel);

                panelResultados.add(resumenPanel);
            }

            // Reemplazar el contenido del JScrollPane con los nuevos resultados
            movimientoView.getjScrollPane1().setViewportView(panelResultados);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(movimientoView,
                    "Error al obtener los movimientos: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}
