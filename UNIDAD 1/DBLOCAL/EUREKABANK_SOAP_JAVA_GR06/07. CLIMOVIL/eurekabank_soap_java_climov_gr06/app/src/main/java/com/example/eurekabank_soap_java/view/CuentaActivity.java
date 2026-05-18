package com.example.eurekabank_soap_java.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.eurekabank_soap_java.R;
import com.example.eurekabank_soap_java.controller.CuentaController;
import com.example.eurekabank_soap_java.service.CuentaService;

public class CuentaActivity extends AppCompatActivity {
    private EditText etCuenta;
    private EditText etMonto;
    private Button btnProcesar;
    private Button btnRegresar;
    private CuentaController cuentaController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cuenta);

        etCuenta = findViewById(R.id.etCuenta);
        etMonto = findViewById(R.id.etMonto);
        btnProcesar = findViewById(R.id.btnProcesar);
        btnRegresar = findViewById(R.id.btnRegresar);

        cuentaController = new CuentaController(new CuentaService());

        btnProcesar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                procesarCuenta();
            }
        });

        btnRegresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(CuentaActivity.this, MovimientosActivity.class));
                finish();
            }
        });
    }

    private void procesarCuenta() {
        String numeroCuenta = etCuenta.getText().toString().trim();
        String montoStr = etMonto.getText().toString().trim();

        if (numeroCuenta.isEmpty()) {
            Toast.makeText(this, "Por favor ingrese un número de cuenta", Toast.LENGTH_SHORT).show();
            return;
        }

        if (montoStr.isEmpty()) {
            Toast.makeText(this, "Por favor ingrese un monto", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            double monto = Double.parseDouble(montoStr);

            cuentaController.procesarCuenta(numeroCuenta, monto, new CuentaController.CuentaCallback() {
                @Override
                public void onCuentaSuccess(boolean result) {
                    runOnUiThread(() -> {
                        String mensaje = result ? "Operación exitosa" : "Operación fallida";
                        Toast.makeText(CuentaActivity.this, mensaje, Toast.LENGTH_SHORT).show();
                        if(result){
                            startActivity(new Intent(CuentaActivity.this, MovimientosActivity.class));
                            finish();
                        }
                    });
                }

                @Override
                public void onCuentaError(String errorMessage) {
                    runOnUiThread(() -> {
                        Toast.makeText(CuentaActivity.this, "Error: " + errorMessage, Toast.LENGTH_LONG).show();
                    });
                }
            });

        } catch (NumberFormatException e) {
            Toast.makeText(this, "Monto inválido", Toast.LENGTH_SHORT).show();
        }
    }
}