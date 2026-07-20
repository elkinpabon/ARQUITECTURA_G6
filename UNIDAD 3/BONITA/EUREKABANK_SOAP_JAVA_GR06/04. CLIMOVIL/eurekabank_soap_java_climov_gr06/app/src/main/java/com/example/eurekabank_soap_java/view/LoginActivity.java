package com.example.eurekabank_soap_java.view;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.eurekabank_soap_java.R;
import com.example.eurekabank_soap_java.controlador.BancoController;
import com.example.eurekabank_soap_java.soap.Async;

/** Vista de login (usa el layout activity_login existente). */
public class LoginActivity extends AppCompatActivity {

    private final BancoController ctrl = new BancoController();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Diferenciar este cliente: Banca SOAP · Java · Móvil
        TextView tvWelcome = findViewById(R.id.tvWelcome);
        if (tvWelcome != null) {
            tvWelcome.setText("EUREKABANK GR06\n"
                    + "Banca SOAP · Cliente Móvil (Java)");
        }

        final EditText user = findViewById(R.id.etUsername);
        final EditText pass = findViewById(R.id.etPassword);
        final Button btn = findViewById(R.id.btnLogin);

        btn.setOnClickListener(v -> {
            final String u = user.getText().toString().trim();
            final String p = pass.getText().toString().trim();
            if (u.isEmpty() || p.isEmpty()) {
                Toast.makeText(this, "Ingrese usuario y clave",
                        Toast.LENGTH_SHORT).show();
                return;
            }
            btn.setEnabled(false);
            Async.run(() -> ctrl.login(u, p),
                ok -> {
                    btn.setEnabled(true);
                    if (ok) {
                        startActivity(new Intent(this, CuentaActivity.class));
                        finish();
                    } else {
                        Toast.makeText(this, "Usuario o clave inválidos.",
                                Toast.LENGTH_LONG).show();
                    }
                },
                e -> {
                    btn.setEnabled(true);
                    Toast.makeText(this, "Error de conexión: " + e.getMessage(),
                            Toast.LENGTH_LONG).show();
                });
        });
    }
}
