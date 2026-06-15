package com.example.eurekabank_restful_java.view;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.eurekabank_restful_java.R;
import com.example.eurekabank_restful_java.controlador.BancoController;
import com.example.eurekabank_restful_java.modelo.ClienteResumen;
import com.example.eurekabank_restful_java.modelo.CuentaResumen;
import com.example.eurekabank_restful_java.modelo.Resultado;
import com.example.eurekabank_restful_java.rest.Async;

import java.util.ArrayList;
import java.util.List;

/** Panel principal — UI pulida; admin elige cliente con Spinner legible. */
public class CuentaActivity extends AppCompatActivity {

    private static final int BG     = Color.parseColor("#0F172A");
    private static final int CARD   = Color.parseColor("#1E293B");
    private static final int FIELD  = Color.parseColor("#0F1B30");
    private static final int ACCENT = Color.parseColor("#38BDF8");
    private static final int TEXT   = Color.parseColor("#E2E8F0");
    private static final int SUB    = Color.parseColor("#94A3B8");
    private static final int BORDER = Color.parseColor("#334155");
    private static final int DARKTX = Color.parseColor("#04263A");

    private final BancoController ctrl = new BancoController();
    private TextView tvCuentas;
    private Spinner spCuenta, spMoneda, spClientes;
    private EditText etMonto;

    @Override protected void onCreate(Bundle b) {
        super.onCreate(b);
        final boolean admin = ctrl.getSesion().isAdmin();

        ScrollView sv = new ScrollView(this);
        sv.setBackgroundColor(BG);
        LinearLayout L = new LinearLayout(this);
        L.setOrientation(LinearLayout.VERTICAL);
        sv.addView(L);

        // ===== Header band =====
        LinearLayout band = new LinearLayout(this);
        band.setOrientation(LinearLayout.HORIZONTAL);
        band.setGravity(android.view.Gravity.CENTER_VERTICAL);
        GradientDrawable bg = new GradientDrawable(
                GradientDrawable.Orientation.LEFT_RIGHT,
                new int[]{Color.parseColor("#0EA5E9"), Color.parseColor("#1E293B")});
        band.setBackground(bg);
        band.setPadding(40, 56, 40, 36);
        ImageView logo = new ImageView(this);
        try { logo.setImageResource(R.drawable.logo_login); } catch (Exception ignore) { }
        logo.setLayoutParams(new LinearLayout.LayoutParams(120, 120));
        band.addView(logo);
        LinearLayout htext = new LinearLayout(this);
        htext.setOrientation(LinearLayout.VERTICAL);
        htext.setPadding(28, 0, 0, 0);
        TextView title = new TextView(this);
        title.setText("EUREKABANK GR06");
        title.setTextColor(Color.WHITE);
        title.setTextSize(21);
        title.setTypeface(null, Typeface.BOLD);
        TextView who = new TextView(this);
        who.setText(ctrl.getSesion().getUsuario()
                + (admin ? "  ·  ADMIN" : "  ·  Cliente"));
        who.setTextColor(Color.parseColor("#DBEAFE"));
        htext.addView(title);
        htext.addView(who);
        band.addView(htext);
        L.addView(band);

        LinearLayout body = new LinearLayout(this);
        body.setOrientation(LinearLayout.VERTICAL);
        body.setPadding(32, 24, 32, 56);
        L.addView(body);

        // ===== (Admin) Seleccionar cliente =====
        if (admin) {
            LinearLayout c = card();
            c.addView(label("1. Seleccionar cliente"));
            spClientes = field(new Spinner(this));
            c.addView(spClientes);
            c.addView(btnPrim("Ver cuentas", () -> {
                Object o = spClientes.getSelectedItem();
                if (o instanceof ClienteResumen)
                    recargar(((ClienteResumen) o).getCodigo());
                else toast("Selecciona un cliente.");
            }));
            body.addView(c);
            cargarClientes();

            LinearLayout a = card();
            a.addView(label("Administración"));
            a.addView(btnGhost("Registrar cliente", this::dlgRegCliente));
            a.addView(btnGhost("Registrar cuenta", this::dlgRegCuenta));
            a.addView(btnGhost("Eliminar cuenta", this::dlgEliminarCuenta));
            body.addView(a);
        }

        // ===== Cuentas =====
        LinearLayout cc = card();
        cc.addView(label(admin ? "2. Cuentas" : "Mis cuentas"));
        tvCuentas = new TextView(this);
        tvCuentas.setTextColor(TEXT);
        tvCuentas.setTextSize(15);
        tvCuentas.setLineSpacing(6f, 1f);
        cc.addView(tvCuentas);
        body.addView(cc);

        // ===== Operar =====
        LinearLayout op = card();
        op.addView(label("Operar"));
        op.addView(sub("Cuenta"));
        spCuenta = field(new Spinner(this));
        op.addView(spCuenta);
        op.addView(sub("Monto"));
        etMonto = new EditText(this);
        etMonto.setHint("0.00");
        etMonto.setText("");
        etMonto.setTextColor(TEXT);
        etMonto.setHintTextColor(SUB);
        etMonto.setInputType(InputType.TYPE_CLASS_NUMBER
                | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        etMonto.setBackground(fieldBg());
        etMonto.setPadding(28, 26, 28, 26);
        op.addView(etMonto);
        op.addView(sub("Moneda del monto"));
        spMoneda = field(new Spinner(this));
        spMoneda.setAdapter(adapter(java.util.Arrays.asList(
                "Dólares (preferente)", "Soles")));
        op.addView(spMoneda);

        op.addView(btnPrim("Consultar saldo", () -> op("saldo")));
        if (admin) op.addView(btnPrim("Depositar", () -> op("depositar")));
        op.addView(btnPrim("Retirar", () -> op("retirar")));
        op.addView(btnPrim("Transferir", () -> op("transferir")));
        op.addView(btnGhost("Ver movimientos", () -> {
            String cu = cuentaSel();
            if (cu == null) { toast("Selecciona una cuenta."); return; }
            Intent i = new Intent(this, MovimientosActivity.class);
            i.putExtra("cuenta", cu);
            startActivity(i);
        }));
        body.addView(op);

        body.addView(btnGhost("Actualizar saldos", () -> recargar(clienteActual())));
        Button salir = btnPrim("Cerrar sesión", () -> {
            ctrl.logout();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });
        ((GradientDrawable) salir.getBackground()).setColor(Color.parseColor("#7F1D1D"));
        salir.setTextColor(Color.WHITE);
        body.addView(salir);

        setContentView(sv);
        pintarCuentas();
    }

    /* ================= estilos ================= */

    private LinearLayout card() {
        LinearLayout c = new LinearLayout(this);
        c.setOrientation(LinearLayout.VERTICAL);
        GradientDrawable g = new GradientDrawable();
        g.setColor(CARD);
        g.setCornerRadius(34f);
        g.setStroke(2, BORDER);
        c.setBackground(g);
        c.setPadding(34, 30, 34, 30);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.setMargins(0, 12, 0, 12);
        c.setLayoutParams(lp);
        return c;
    }

    private TextView label(String s) {
        TextView t = new TextView(this);
        t.setText(s); t.setTextColor(ACCENT);
        t.setTextSize(17); t.setTypeface(null, Typeface.BOLD);
        t.setPadding(0, 0, 0, 14);
        return t;
    }

    private TextView sub(String s) {
        TextView t = new TextView(this);
        t.setText(s); t.setTextColor(SUB); t.setTextSize(13);
        t.setPadding(2, 16, 0, 6);
        return t;
    }

    private GradientDrawable fieldBg() {
        GradientDrawable g = new GradientDrawable();
        g.setColor(FIELD);
        g.setCornerRadius(18f);
        g.setStroke(2, BORDER);
        return g;
    }

    /** Envuelve un Spinner con fondo de campo y padding. */
    private Spinner field(Spinner sp) {
        sp.setBackground(fieldBg());
        sp.setPadding(24, 24, 24, 24);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.setMargins(0, 4, 0, 4);
        sp.setLayoutParams(lp);
        return sp;
    }

    /** Adapter con texto LEGIBLE: claro en el campo, oscuro en el desplegable. */
    private <T> ArrayAdapter<T> adapter(List<T> items) {
        return new ArrayAdapter<T>(this, android.R.layout.simple_spinner_item, items) {
            @Override public View getView(int p, View cv, ViewGroup pa) {
                TextView v = (TextView) super.getView(p, cv, pa);
                v.setTextColor(TEXT);
                v.setTextSize(15);
                return v;
            }
            @Override public View getDropDownView(int p, View cv, ViewGroup pa) {
                TextView v = (TextView) super.getDropDownView(p, cv, pa);
                v.setTextColor(Color.parseColor("#0F172A"));
                v.setBackgroundColor(Color.WHITE);
                v.setPadding(36, 34, 36, 34);
                return v;
            }
        };
    }

    private Button base(String txt, final Runnable r) {
        Button b = new Button(this);
        b.setText(txt);
        b.setAllCaps(false);
        b.setTextSize(15);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, 132);
        lp.setMargins(0, 12, 0, 0);
        b.setLayoutParams(lp);
        b.setOnClickListener(v -> r.run());
        return b;
    }

    private Button btnPrim(String txt, Runnable r) {
        Button b = base(txt, r);
        GradientDrawable g = new GradientDrawable();
        g.setColor(ACCENT);
        g.setCornerRadius(20f);
        b.setBackground(g);
        b.setTextColor(DARKTX);
        b.setTypeface(null, Typeface.BOLD);
        return b;
    }

    private Button btnGhost(String txt, Runnable r) {
        Button b = base(txt, r);
        GradientDrawable g = new GradientDrawable();
        g.setColor(FIELD);
        g.setCornerRadius(20f);
        g.setStroke(2, ACCENT);
        b.setBackground(g);
        b.setTextColor(ACCENT);
        return b;
    }

    /* ================= datos ================= */

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

    private void cargarClientes() {
        Async.run(ctrl::listarClientes,
            list -> spClientes.setAdapter(adapter(list)),
            e -> toast("Error cargando clientes: " + e.getMessage()));
    }

    private void pintarCuentas() {
        List<CuentaResumen> ct = ctrl.getCuentas();
        StringBuilder sb = new StringBuilder();
        List<String> codigos = new ArrayList<>();
        double tot = 0;
        if (ct != null) {
            for (CuentaResumen c : ct) {
                sb.append("• ").append(c.getCodigoCuenta()).append("\n   ")
                  .append(String.format("%,.2f", c.getSaldo())).append(" ")
                  .append("02".equals(c.getMoneda()) ? "Dólares" : "Soles")
                  .append("  ·  ").append(c.getEstado()).append("\n\n");
                codigos.add(c.getCodigoCuenta());
                tot += c.getSaldo();
            }
        }
        if (codigos.isEmpty()) sb.append("(sin cuentas)\n\n");
        sb.append("SALDO TOTAL:  ").append(String.format("%,.2f", tot));
        tvCuentas.setText(sb.toString());
        spCuenta.setAdapter(adapter(codigos));
    }

    private void toast(String m) { Toast.makeText(this, m, Toast.LENGTH_LONG).show(); }

    private void recargar(final String criterio) {
        Async.run(() -> { ctrl.cargarCuentas(criterio); return null; },
            x -> pintarCuentas(), e -> toast("Error: " + e.getMessage()));
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
            final EditText dst = dlgField("Cuenta destino");
            new AlertDialog.Builder(this).setTitle("Transferir").setView(dst)
                .setPositiveButton("Enviar", (d, w) -> Async.run(
                    () -> ctrl.transferir(c, dst.getText().toString().trim(), monto, mon),
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

    /* ================= diálogos admin ================= */

    /** EditText legible para diálogos: fondo claro, texto oscuro, hint gris. */
    private EditText dlgField(String hint) {
        EditText e = new EditText(this);
        e.setHint(hint);
        e.setTextColor(Color.parseColor("#0F172A"));
        e.setHintTextColor(Color.parseColor("#64748B"));
        GradientDrawable g = new GradientDrawable();
        g.setColor(Color.WHITE);
        g.setCornerRadius(16f);
        g.setStroke(2, BORDER);
        e.setBackground(g);
        e.setPadding(28, 24, 28, 24);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.setMargins(0, 10, 0, 0);
        e.setLayoutParams(lp);
        return e;
    }

    private EditText campo(LinearLayout l, String hint) {
        EditText e = dlgField(hint); l.addView(e); return e;
    }

    private void dlgRegCliente() {
        LinearLayout f = new LinearLayout(this);
        f.setOrientation(LinearLayout.VERTICAL);
        f.setPadding(48, 24, 48, 0);
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
                r -> { toast(r.getMensaje()); cargarClientes(); },
                e -> toast("Error: " + e.getMessage())))
            .setNegativeButton("Cancelar", null).show();
    }

    private void dlgRegCuenta() {
        LinearLayout f = new LinearLayout(this);
        f.setOrientation(LinearLayout.VERTICAL);
        f.setPadding(48, 24, 48, 0);
        final EditText cli = campo(f, "Código cliente");
        String actual = clienteActual();
        if (actual != null) cli.setText(actual);   // cliente cargado prefijado
        final Spinner mon = new Spinner(this);
        mon.setAdapter(adapter(java.util.Arrays.asList("Dólares", "Soles")));
        f.addView(mon);
        new AlertDialog.Builder(this).setTitle("Registrar cuenta").setView(f)
            .setPositiveButton("Crear", (d, w) -> Async.run(
                () -> ctrl.registrarCuenta(cli.getText().toString().trim(),
                        mon.getSelectedItemPosition() == 1 ? "01" : "02"),
                r -> { toast(r.getMensaje()); recargar(clienteActual()); },
                e -> toast("Error: " + e.getMessage())))
            .setNegativeButton("Cancelar", null).show();
    }

    private void dlgEliminarCuenta() {
        java.util.List<com.example.eurekabank_restful_java.modelo.CuentaResumen>
                ct = ctrl.getCuentas();
        if (ct != null && !ct.isEmpty()) {
            final java.util.List<String> codigos = new java.util.ArrayList<>();
            final String[] items = new String[ct.size()];
            for (int i = 0; i < ct.size(); i++) {
                com.example.eurekabank_restful_java.modelo.CuentaResumen c = ct.get(i);
                codigos.add(c.getCodigoCuenta());
                items[i] = c.getCodigoCuenta() + "  ·  "
                        + String.format("%,.2f", c.getSaldo()) + " "
                        + ("02".equals(c.getMoneda()) ? "Dólares" : "Soles")
                        + "  ·  " + c.getEstado();
            }
            final int[] sel = {0};
            new AlertDialog.Builder(this)
                .setTitle("Eliminar cuenta de " + ct.get(0).getNombreCliente())
                .setSingleChoiceItems(items, 0, (d, w) -> sel[0] = w)
                .setPositiveButton("Eliminar", (d, w) -> {
                    final String cod = codigos.get(sel[0]);
                    Async.run(() -> ctrl.eliminarCuenta(cod),
                        r -> { toast(r.getMensaje()); recargar(clienteActual()); },
                        e -> toast("Error: " + e.getMessage()));
                })
                .setNegativeButton("Cancelar", null).show();
            return;
        }
        final EditText cta = dlgField("Código de cuenta a eliminar");
        new AlertDialog.Builder(this).setTitle("Eliminar cuenta").setView(cta)
            .setMessage("Borra la cuenta y sus movimientos.")
            .setPositiveButton("Eliminar", (d, w) -> Async.run(
                () -> ctrl.eliminarCuenta(cta.getText().toString().trim()),
                r -> toast(r.getMensaje()), e -> toast("Error: " + e.getMessage())))
            .setNegativeButton("Cancelar", null).show();
    }
}
