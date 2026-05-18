package com.example.eurekabank_soap_java.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.eurekabank_soap_java.R;
import com.example.eurekabank_soap_java.controller.MovimientoController;
import com.example.eurekabank_soap_java.models.MovimientoModel;
import com.example.eurekabank_soap_java.service.MovementService;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MovimientosActivity extends AppCompatActivity {
    private EditText etCuenta;
    private Button btnBuscar;
    private FloatingActionButton btnAgregar;
    private LinearLayout movimientosContainer;
    private TextView tvNoMovimientos;
    private MovimientoController movimientoController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movimientos);

        // Initialize views
        etCuenta = findViewById(R.id.etCuenta);
        btnBuscar = findViewById(R.id.btnBuscar);
        btnAgregar = findViewById(R.id.btnAgregar);
        movimientosContainer = findViewById(R.id.movimientosContainer);

        // Add a no movements view
        tvNoMovimientos = new TextView(this);
        tvNoMovimientos.setText("No se encontraron movimientos para esta cuenta");
        tvNoMovimientos.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        tvNoMovimientos.setTextSize(16);
        tvNoMovimientos.setPadding(0, 32, 0, 32);
        tvNoMovimientos.setVisibility(View.GONE);

        // Add the no movements view to the container
        movimientosContainer.addView(tvNoMovimientos);

        // Initialize controller
        movimientoController = new MovimientoController(new MovementService());

        // Set up search button click listener
        btnBuscar.setOnClickListener(v -> buscarMovimientos());

        btnAgregar.setOnClickListener(v -> {
            startActivity(new Intent(MovimientosActivity.this, CuentaActivity.class));
            finish();
        });
    }

    private void buscarMovimientos() {
        String numeroCuenta = etCuenta.getText().toString().trim();

        if (numeroCuenta.isEmpty()) {
            Toast.makeText(this, "Por favor ingrese un número de cuenta",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        // Clear previous results
        movimientosContainer.removeAllViews();
        // Re-add the no movements view
        movimientosContainer.addView(tvNoMovimientos);
        // Hide the no movements view initially
        tvNoMovimientos.setVisibility(View.GONE);

        movimientoController.obtenerMovimientos(numeroCuenta, new MovimientoController.MovimientoCallback() {
            @Override
            public void onMovimientosSuccess(List<MovimientoModel> movimientos) {
                runOnUiThread(() -> {
                    // Clear previous views
                    movimientosContainer.removeAllViews();

                    if (movimientos == null || movimientos.isEmpty()) {
                        // Show no movements view
                        tvNoMovimientos.setVisibility(View.VISIBLE);
                        movimientosContainer.addView(tvNoMovimientos);
                        return;
                    }

                    // Populate movimientos
                    for (MovimientoModel movimiento : movimientos) {
                        View movimientoView = LayoutInflater.from(MovimientosActivity.this)
                                .inflate(R.layout.item_movimiento, movimientosContainer, false);

                        // Bind data to views (same as before)
                        TextView tvCuenta = movimientoView.findViewById(R.id.tvCuenta);
                        TextView tvFecha = movimientoView.findViewById(R.id.tvFecha);
                        TextView tvTipo = movimientoView.findViewById(R.id.tvTipo);
                        TextView tvAccion = movimientoView.findViewById(R.id.tvAccion);
                        TextView tvImporte = movimientoView.findViewById(R.id.tvImporte);
                        TextView tvNumMov = movimientoView.findViewById(R.id.tvNumMovimiento);

                        // Set account number
                        tvCuenta.setText(String.format("Cuenta: %s",
                                movimiento.getCodigoCuenta()));

                        // More robust date parsing
                        try {
                            // Try multiple date formats
                            SimpleDateFormat[] possibleFormats = {
                                    new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()),
                                    new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()),
                                    new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault())
                            };

                            Date parsedDate = null;
                            for (SimpleDateFormat format : possibleFormats) {
                                try {
                                    parsedDate = format.parse(movimiento.getFechaMovimiento());
                                    if (parsedDate != null) {
                                        break;
                                    }
                                } catch (Exception ignored) {}
                            }

                            if (parsedDate != null) {
                                SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                                String formattedDate = outputFormat.format(parsedDate);
                                tvFecha.setText(String.format("Fecha: %s", formattedDate));
                            } else {
                                // If no parsing works, use the original date
                                tvFecha.setText(String.format("Fecha: %s", movimiento.getFechaMovimiento()));
                            }
                        } catch (Exception e) {
                            // Fallback to original date if parsing fails
                            tvFecha.setText(String.format("Fecha: %s", movimiento.getFechaMovimiento()));
                        }

                        tvNumMov.setText(String.format("Movimiento: %s",
                                movimiento.getNumeroMovimiento()));

                        // Set transaction type (you might want to map these codes)
                        tvTipo.setText(String.format("Tipo: %s",
                                movimiento.getDescTipoMovimiento()));

                        // Determine action (credit/debit) - you might want to customize this logic
                        String accion = movimiento.getImporteMovimiento() >= 0 ? "Crédito" : "Débito";
                        tvAccion.setText(String.format("Acción: %s", accion));

                        // Format amount
                        tvImporte.setText(String.format(Locale.getDefault(),
                                "Importe: $%.2f",
                                Math.abs(movimiento.getImporteMovimiento())));

                        // Add to container
                        movimientosContainer.addView(movimientoView);
                    }
                });
            }

            @Override
            public void onMovimientosError(String errorMessage) {
                runOnUiThread(() -> {
                    // Clear previous views
                    movimientosContainer.removeAllViews();

                    // Show no movements view with error message
                    tvNoMovimientos.setText("Error al obtener movimientos: " + errorMessage);
                    tvNoMovimientos.setVisibility(View.VISIBLE);
                    movimientosContainer.addView(tvNoMovimientos);

                    Toast.makeText(MovimientosActivity.this,
                            "Error al obtener movimientos: " + errorMessage,
                            Toast.LENGTH_LONG).show();
                });
            }
        });
    }
}