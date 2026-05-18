package com.example.eurekabank_soap_java.view;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.eurekabank_soap_java.controlador.BancoController;
import com.example.eurekabank_soap_java.modelo.CuentaResumen;
import com.example.eurekabank_soap_java.modelo.Resultado;
import com.example.eurekabank_soap_java.soap.Async;

import java.util.ArrayList;
import java.util.List;

/** Panel principal (UI programática) — misma funcionalidad que el cliente web. */
public class CuentaActivity extends AppCompatActivity {

    private final BancoController ctrl = new BancoController();
    private TextView tvCuentas;
    private Spinner spCuenta, spMoneda;
    private EditText etMonto, etCliente;

    @Override protected void onCreate(Bundle b) {
        super.onCreate(b);
        boolean admin = ctrl.getSesion().isAdmin();

        ScrollView sv = new ScrollView(this);
        LinearLayout L = new LinearLayout(this);
        L.setOrientation(LinearLayout.VERTICAL);
        L.setPadding(40, 60, 40, 40);
        sv.addView(L);

        TextView head = new TextView(this);
        head.setText("Usuario: " + ctrl.getSesion().getUsuario()
                + (admin ? "  [ADMIN]" : ""));
        head.setTextSize(18);
        L.addView(head);

        if (admin) {
            etCliente = new EditText(this);
            etCliente.setHint("Código o DNI del cliente");
            L.addView(etCliente);
            addBtn(L, "Ver cuentas", () -> recargar(etCliente.getText().toString().trim()));
            addBtn(L, "Registrar cliente", this::dlgRegCliente);
            addBtn(L, "Registrar cuenta", this::dlgRegCuenta);
            addBtn(L, "Eliminar cuenta", this::dlgEliminarCuenta);
        }

        tvCuentas = new TextView(this);
        tvCuentas.setPadding(0, 24, 0, 24);
        L.addView(tvCuentas);

        spCuenta = new Spinner(this);
        L.addView(labeled(L, "Cuenta:"));
        L.addView(spCuenta);

        etMonto = new EditText(this);
        etMonto.setHint("Monto");
        etMonto.setInputType(InputType.TYPE_CLASS_NUMBER
                | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        L.addView(etMonto);

        spMoneda = new Spinner(this);
        spMoneda.setAdapter(new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item,
                new String[]{"Dólares (preferente)", "Soles"}));
        L.addView(labeled(L, "Moneda del monto:"));
        L.addView(spMoneda);

        addBtn(L, "Consultar saldo", () -> op("saldo"));
        if (admin) addBtn(L, "Depositar", () -> op("depositar"));
        addBtn(L, "Retirar", () -> op("retirar"));
        addBtn(L, "Transferir", () -> op("transferir"));
        addBtn(L, "Ver movimientos", () -> {
            String c = cuentaSel();
            if (c == null) return;
            Intent i = new Intent(this, MovimientosActivity.class);
            i.putExtra("cuenta", c);
            startActivity(i);
        });
        addBtn(L, "Actualizar saldos", () -> recargar(clienteActual()));
        addBtn(L, "Cerrar sesión", () -> {
            ctrl.logout();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });

        setContentView(sv);
        pintarCuentas();              // cliente: ya cargó en login
    }

    /* ---------- helpers UI ---------- */

    private TextView labeled(LinearLayout l, String s) {
        TextView t = new TextView(this); t.setText(s); return t;
    }

    private void addBtn(LinearLayout l, String txt, Runnable r) {
        Button b = new Button(this);
        b.setText(txt);
        b.setOnClickListener(v -> r.run());
        l.addView(b);
    }

    private String monedaSel() {
        return spMoneda.getSelectedItemPosition() == 1 ? "01" : "02";
    }

    private String cuentaSel() {
        Object o = spCuenta.getSelectedItem();
        return o == null ? null : o.toString();
    }

    private String clienteActual() {
        List<CuentaResumen> ct = ctrl.getCuentas();
        return (ct != null && !ct.isEmpty()) ? ct.get(0).getCodigoCliente() : null;
    }

    private void pintarCuentas() {
        List<CuentaResumen> ct = ctrl.getCuentas();
        StringBuilder sb = new StringBuilder();
        List<String> codigos = new ArrayList<>();
        double tot = 0;
        if (ct != null) {
            for (CuentaResumen c : ct) {
                sb.append(c.getCodigoCuenta()).append(" | ")
                  .append(String.format("%,.2f", c.getSaldo())).append(" ")
                  .append("02".equals(c.getMoneda()) ? "Dólares" : "Soles")
                  .append(" | ").append(c.getEstado()).append("\n");
                codigos.add(c.getCodigoCuenta());
                tot += c.getSaldo();
            }
        }
        sb.append("SALDO TOTAL: ").append(String.format("%,.2f", tot));
        tvCuentas.setText(sb.toString());
        spCuenta.setAdapter(new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, codigos));
    }

    private void toast(String m) {
        Toast.makeText(this, m, Toast.LENGTH_LONG).show();
    }

    /* ---------- acciones ---------- */

    private void recargar(final String criterio) {
        Async.run(() -> { ctrl.cargarCuentas(criterio); return null; },
            x -> pintarCuentas(),
            e -> toast("Error: " + e.getMessage()));
    }

    private void resultado(Resultado r) {
        toast((r.isExito() ? "OK: " : "Error: ") + r.getMensaje());
        recargar(clienteActual());
    }

    private void op(final String tipo) {
        final String c = cuentaSel();
        if (c == null) { toast("Selecciona una cuenta."); return; }
        final String monto = etMonto.getText().toString().trim();
        final String mon = monedaSel();
        if ("transferir".equals(tipo)) {
            final EditText dst = new EditText(this);
            dst.setHint("Cuenta destino");
            new AlertDialog.Builder(this).setTitle("Transferir")
                .setView(dst)
                .setPositiveButton("Enviar", (d, w) ->
                    Async.run(() -> ctrl.transferir(c,
                            dst.getText().toString().trim(), monto, mon),
                        this::resultado, e -> toast("Error: " + e.getMessage())))
                .setNegativeButton("Cancelar", null).show();
            return;
        }
        Async.run(() -> {
                switch (tipo) {
                    case "saldo":     return ctrl.consultarSaldo(c);
                    case "depositar": return ctrl.depositar(c, monto, mon);
                    case "retirar":   return ctrl.retirar(c, monto, mon);
                    default:          return new Resultado(false, "Acción inválida");
                }
            }, this::resultado, e -> toast("Error: " + e.getMessage()));
    }

    /* ---------- diálogos admin ---------- */

    private EditText campo(LinearLayout l, String hint) {
        EditText e = new EditText(this); e.setHint(hint); l.addView(e); return e;
    }

    private void dlgRegCliente() {
        LinearLayout f = new LinearLayout(this);
        f.setOrientation(LinearLayout.VERTICAL);
        final EditText nom = campo(f, "Nombre"), pat = campo(f, "Ap. paterno"),
                mat = campo(f, "Ap. materno"), dni = campo(f, "DNI"),
                ciu = campo(f, "Ciudad"), dir = campo(f, "Dirección"),
                tel = campo(f, "Teléfono"), ema = campo(f, "Email");
        new AlertDialog.Builder(this).setTitle("Registrar cliente").setView(f)
            .setPositiveButton("Crear", (d, w) -> Async.run(
                () -> ctrl.registrarCliente(pat.getText().toString(),
                        mat.getText().toString(), nom.getText().toString(),
                        dni.getText().toString(), ciu.getText().toString(),
                        dir.getText().toString(), tel.getText().toString(),
                        ema.getText().toString()),
                r -> toast(r.getMensaje()), e -> toast("Error: " + e.getMessage())))
            .setNegativeButton("Cancelar", null).show();
    }

    private void dlgRegCuenta() {
        LinearLayout f = new LinearLayout(this);
        f.setOrientation(LinearLayout.VERTICAL);
        final EditText cli = campo(f, "Código cliente");
        final Spinner mon = new Spinner(this);
        mon.setAdapter(new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item,
                new String[]{"Dólares", "Soles"}));
        f.addView(mon);
        new AlertDialog.Builder(this).setTitle("Registrar cuenta").setView(f)
            .setPositiveButton("Crear", (d, w) -> Async.run(
                () -> ctrl.registrarCuenta(cli.getText().toString().trim(),
                        mon.getSelectedItemPosition() == 1 ? "01" : "02"),
                r -> toast(r.getMensaje()), e -> toast("Error: " + e.getMessage())))
            .setNegativeButton("Cancelar", null).show();
    }

    private void dlgEliminarCuenta() {
        final EditText cta = new EditText(this);
        cta.setHint("Código de cuenta a eliminar");
        new AlertDialog.Builder(this).setTitle("Eliminar cuenta").setView(cta)
            .setMessage("Borra la cuenta y sus movimientos.")
            .setPositiveButton("Eliminar", (d, w) -> Async.run(
                () -> ctrl.eliminarCuenta(cta.getText().toString().trim()),
                r -> toast(r.getMensaje()), e -> toast("Error: " + e.getMessage())))
            .setNegativeButton("Cancelar", null).show();
    }
}
