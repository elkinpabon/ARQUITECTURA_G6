package com.example.eurekabank_soap_java.view;

import android.os.Bundle;
import android.view.Gravity;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.eurekabank_soap_java.controlador.BancoController;
import com.example.eurekabank_soap_java.modelo.Movimiento;
import com.example.eurekabank_soap_java.soap.Async;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/** Estado de cuenta: ingresos/egresos + botón "ojito" de conversión. */
public class MovimientosActivity extends AppCompatActivity {

    private static final Set<String> INGRESOS =
            new HashSet<>(java.util.Arrays.asList("001", "003", "005", "008"));
    private final BancoController ctrl = new BancoController();
    private LinearLayout cont;

    @Override protected void onCreate(Bundle b) {
        super.onCreate(b);
        final String cuenta = getIntent().getStringExtra("cuenta");

        ScrollView sv = new ScrollView(this);
        cont = new LinearLayout(this);
        cont.setOrientation(LinearLayout.VERTICAL);
        cont.setPadding(40, 60, 40, 40);
        sv.addView(cont);
        setContentView(sv);

        TextView t = new TextView(this);
        t.setText("Estado de cuenta " + cuenta);
        t.setTextSize(20);
        cont.addView(t);

        Async.run(() -> ctrl.movimientos(cuenta),
            this::pintar,
            e -> Toast.makeText(this, "Error: " + e.getMessage(),
                    Toast.LENGTH_LONG).show());
    }

    private void pintar(List<Movimiento> ms) {
        if (ms == null || ms.isEmpty()) {
            TextView v = new TextView(this);
            v.setText("Sin movimientos o sin acceso.");
            cont.addView(v);
            return;
        }
        double tin = 0, tout = 0;
        for (final Movimiento m : ms) {
            boolean in = INGRESOS.contains(m.getTipoCodigo());
            if (in) tin += m.getImporte(); else tout += m.getImporte();
            TextView v = new TextView(this);
            v.setPadding(0, 16, 0, 4);
            v.setText("#" + m.getNumero() + "  " + m.getFecha() + "\n"
                    + m.getTipoDescripcion() + "\n"
                    + (in ? "INGRESO +" : "EGRESO -")
                    + String.format("%,.2f", m.getImporte())
                    + (m.getCuentaReferencia() == null ? ""
                       : "   Ref: " + m.getCuentaReferencia()));
            cont.addView(v);
            if (m.tieneConversion()) {
                Button ojo = new Button(this);
                ojo.setText("👁 Ver conversión");
                ojo.setOnClickListener(x -> {
                    double io = m.getImporteOrigen() == null ? 0 : m.getImporteOrigen();
                    String mo = "02".equals(m.getMonedaOrigen()) ? "Dólares" : "Soles";
                    Toast.makeText(this,
                        String.format("%,.2f %s  ×  tasa %s  =  %,.2f (moneda de la cuenta)",
                            io, mo, String.valueOf(m.getTasaAplicada()), m.getImporte()),
                        Toast.LENGTH_LONG).show();
                });
                cont.addView(ojo);
            }
        }
        TextView tot = new TextView(this);
        tot.setGravity(Gravity.END);
        tot.setPadding(0, 24, 0, 0);
        tot.setText(String.format("TOTALES  Ingresos %,.2f   Egresos %,.2f   Neto %,.2f",
                tin, tout, tin - tout));
        cont.addView(tot);

        Button volver = new Button(this);
        volver.setText("Volver");
        volver.setOnClickListener(x -> finish());
        cont.addView(volver);
    }
}
