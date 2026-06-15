package ec.edu.monster.vista;

import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import com.google.android.material.button.MaterialButton;
import ec.edu.monster.R;
import ec.edu.monster.controlador.TicketController;
import ec.edu.monster.modelo.Asiento;
import ec.edu.monster.modelo.Carrito;
import ec.edu.monster.modelo.ItemCarrito;
import ec.edu.monster.modelo.Localidad;
import ec.edu.monster.modelo.Seccion;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Mapa de asientos estilo StubHub: el usuario elige categoria (CAT1-4) y
 * seccion; la grilla pinta cada asiento segun su estado:
 *   verde = libre, ambar = reservado (otro usuario), rojo = ocupado,
 *   azul = reservado por mi (en el carrito).
 * Tocar un asiento libre lo reserva y lo agrega al carrito; tocar uno mio lo
 * libera. La grilla se refresca cada 5 segundos via asientosNoLibres.
 */
public class AsientosActivity extends AppCompatActivity {

    public static final String EXTRA_CODIGO  = "codigoPartido";
    public static final String EXTRA_TITULO  = "tituloPartido";
    public static final String EXTRA_DETALLE = "detallePartido";

    private static final long REFRESH_MS = 5000;

    private TicketController ctrl;
    private int codigoPartido;
    private String tituloPartido = "";

    private Spinner spnCategoria, spnSeccion;
    private TextView lblPrecio, lblCarrito;
    private GridLayout grid;
    private ProgressBar prg;
    private LinearLayout layLeyenda;

    private final List<Localidad> localidades = new ArrayList<>();
    private final List<Seccion> secciones = new ArrayList<>();
    private Localidad locSel;
    private Seccion secSel;

    /** Estado de los asientos NO libres: clave "fila|asiento" -> RESERVADO/OCUPADO. */
    private final Map<String, String> noLibres = new HashMap<>();

    private final Handler handler = new Handler(Looper.getMainLooper());
    private final Runnable tick = new Runnable() {
        @Override public void run() {
            refrescarAsientos(false);
            handler.postDelayed(this, REFRESH_MS);
        }
    };

    @Override
    protected void onCreate(Bundle s) {
        super.onCreate(s);
        setContentView(R.layout.activity_asientos);
        ctrl = new TicketController(this);

        codigoPartido = getIntent().getIntExtra(EXTRA_CODIGO, 0);
        tituloPartido = getIntent().getStringExtra(EXTRA_TITULO);
        if (tituloPartido == null) tituloPartido = "Partido";
        String detalle = getIntent().getStringExtra(EXTRA_DETALLE);

        TextView txtPartido = findViewById(R.id.txtPartido);
        TextView txtDetalle = findViewById(R.id.txtDetallePartido);
        txtPartido.setText(tituloPartido);
        txtDetalle.setText(detalle == null ? "" : detalle);

        spnCategoria = findViewById(R.id.spnCategoria);
        spnSeccion   = findViewById(R.id.spnSeccion);
        lblPrecio    = findViewById(R.id.lblPrecio);
        lblCarrito   = findViewById(R.id.lblCarrito);
        grid         = findViewById(R.id.gridAsientos);
        prg          = findViewById(R.id.prgAsientos);
        layLeyenda   = findViewById(R.id.layLeyenda);

        MaterialButton btnRefrescar = findViewById(R.id.btnRefrescar);
        MaterialButton btnIrCarrito = findViewById(R.id.btnIrCarrito);
        btnRefrescar.setOnClickListener(v -> refrescarAsientos(true));
        btnIrCarrito.setOnClickListener(v -> finish());

        construirLeyenda();

        spnCategoria.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(AdapterView<?> a, View view, int pos, long id) {
                if (pos >= 0 && pos < localidades.size()) {
                    locSel = localidades.get(pos);
                    lblPrecio.setText(Moneda.fmt(locSel.getPrecio()));
                    cargarSecciones(locSel.getId());
                }
            }
            @Override public void onNothingSelected(AdapterView<?> a) { }
        });

        spnSeccion.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(AdapterView<?> a, View view, int pos, long id) {
                if (pos >= 0 && pos < secciones.size()) {
                    secSel = secciones.get(pos);
                    noLibres.clear();
                    construirGrilla();
                    refrescarAsientos(true);
                }
            }
            @Override public void onNothingSelected(AdapterView<?> a) { }
        });

        actualizarResumenCarrito();
        cargarLocalidades();
    }

    @Override protected void onResume() {
        super.onResume();
        actualizarResumenCarrito();
        handler.postDelayed(tick, REFRESH_MS);
    }

    @Override protected void onPause() {
        super.onPause();
        handler.removeCallbacks(tick);
    }

    // ============================================================================
    // Carga de catalogos
    // ============================================================================
    private void cargarLocalidades() {
        ctrl.localidades(codigoPartido, (lista, err) -> {
            if (isFinishing()) return;
            if (err != null) { Ui.error(this, err); return; }
            localidades.clear();
            if (lista != null) localidades.addAll(lista);
            List<String> labels = new ArrayList<>();
            for (Localidad l : localidades) {
                labels.add(l.getCategoria() + "  -  " + Moneda.fmt(l.getPrecio())
                        + "  (disp: " + l.getDisponibilidad() + ")");
            }
            spnCategoria.setAdapter(new ArrayAdapter<>(this,
                    android.R.layout.simple_spinner_dropdown_item, labels));
            if (localidades.isEmpty()) Ui.toast(this, "No hay localidades para este partido");
        });
    }

    private void cargarSecciones(int idLocalidad) {
        ctrl.secciones(idLocalidad, (lista, err) -> {
            if (isFinishing()) return;
            if (err != null) { Ui.error(this, err); return; }
            secciones.clear();
            if (lista != null) secciones.addAll(lista);
            List<String> labels = new ArrayList<>();
            for (Seccion sc : secciones) labels.add(sc.toString());
            spnSeccion.setAdapter(new ArrayAdapter<>(this,
                    android.R.layout.simple_spinner_dropdown_item, labels));
            if (secciones.isEmpty()) {
                secSel = null;
                grid.removeAllViews();
                Ui.toast(this, "No hay secciones para esta categoria");
            }
        });
    }

    // ============================================================================
    // Grilla de asientos
    // ============================================================================
    private void construirGrilla() {
        grid.removeAllViews();
        if (secSel == null) return;
        int filas = Math.max(1, secSel.getNumFilas());
        int cols  = Math.max(1, secSel.getAsientosPorFila());
        grid.setColumnCount(cols + 1); // +1 para la etiqueta de fila

        int cell  = dp(34);
        int margen = dp(2);

        for (int f = 1; f <= filas; f++) {
            // Etiqueta de fila
            TextView lbl = new TextView(this);
            lbl.setText("F" + f);
            lbl.setTextSize(11);
            lbl.setTypeface(Typeface.DEFAULT_BOLD);
            lbl.setTextColor(ContextCompat.getColor(this, R.color.text_secondary));
            lbl.setGravity(Gravity.CENTER);
            GridLayout.LayoutParams lpLbl = new GridLayout.LayoutParams();
            lpLbl.width = dp(34);
            lpLbl.height = cell;
            lpLbl.setMargins(margen, margen, margen + dp(4), margen);
            grid.addView(lbl, lpLbl);

            for (int a = 1; a <= cols; a++) {
                final String fila = "F" + f;
                final String asiento = String.valueOf(a);

                TextView seat = new TextView(this);
                seat.setText(asiento);
                seat.setTextSize(10);
                seat.setGravity(Gravity.CENTER);
                seat.setTextColor(Color.WHITE);
                seat.setTag(fila + "|" + asiento);
                seat.setOnClickListener(v -> tocarAsiento(fila, asiento));

                GridLayout.LayoutParams lp = new GridLayout.LayoutParams();
                lp.width = cell;
                lp.height = cell;
                lp.setMargins(margen, margen, margen, margen);
                grid.addView(seat, lp);
            }
        }
        pintarGrilla();
    }

    private void pintarGrilla() {
        if (secSel == null) return;
        for (int i = 0; i < grid.getChildCount(); i++) {
            View v = grid.getChildAt(i);
            Object tag = v.getTag();
            if (!(tag instanceof String)) continue;
            String[] partes = ((String) tag).split("\\|");
            if (partes.length != 2) continue;
            String fila = partes[0], asiento = partes[1];

            int color;
            if (Carrito.contiene(secSel.getIdSeccion(), fila, asiento)) {
                color = ContextCompat.getColor(this, R.color.seat_mio);
            } else {
                String estado = noLibres.get(fila + "|" + asiento);
                if (estado == null) {
                    color = ContextCompat.getColor(this, R.color.seat_libre);
                } else if ("OCUPADO".equalsIgnoreCase(estado)) {
                    color = ContextCompat.getColor(this, R.color.seat_ocupado);
                } else {
                    color = ContextCompat.getColor(this, R.color.seat_reservado);
                }
            }
            GradientDrawable d = new GradientDrawable();
            d.setColor(color);
            d.setCornerRadius(dp(4));
            v.setBackground(d);
        }
    }

    private void refrescarAsientos(boolean mostrarCarga) {
        if (secSel == null) return;
        final int idSeccion = secSel.getIdSeccion();
        if (mostrarCarga) prg.setVisibility(View.VISIBLE);
        ctrl.asientosNoLibres(idSeccion, (lista, err) -> {
            if (isFinishing()) return;
            prg.setVisibility(View.GONE);
            if (secSel == null || secSel.getIdSeccion() != idSeccion) return;
            if (err != null) {
                if (mostrarCarga) Ui.toast(this, "No se pudo actualizar: " + err);
                return;
            }
            noLibres.clear();
            if (lista != null) {
                for (Asiento a : lista) {
                    noLibres.put(a.getFila() + "|" + a.getAsiento(), a.getEstado());
                }
            }
            pintarGrilla();
        });
    }

    // ============================================================================
    // Reservar / liberar
    // ============================================================================
    private void tocarAsiento(String fila, String asiento) {
        if (secSel == null || locSel == null) return;
        final int idSeccion = secSel.getIdSeccion();

        if (Carrito.contiene(idSeccion, fila, asiento)) {
            // Asiento mio -> liberar
            ctrl.liberarAsiento(idSeccion, fila, asiento, (r, err) -> {
                if (isFinishing()) return;
                if (err != null) { Ui.error(this, err); return; }
                Carrito.quitar(idSeccion, fila, asiento);
                noLibres.remove(fila + "|" + asiento);
                actualizarResumenCarrito();
                pintarGrilla();
                Ui.toast(this, "Asiento " + fila + "-" + asiento + " liberado");
            });
            return;
        }

        String estado = noLibres.get(fila + "|" + asiento);
        if (estado != null) {
            Ui.toast(this, "Asiento " + fila + "-" + asiento + " no disponible (" + estado + ")");
            return;
        }

        // Libre -> reservar y agregar al carrito
        ctrl.reservarAsiento(idSeccion, fila, asiento, (r, err) -> {
            if (isFinishing()) return;
            if (err != null) { Ui.error(this, err); return; }
            if (r == null || !r.isExito()) {
                Ui.toast(this, r != null && r.getMensaje() != null && !r.getMensaje().isEmpty()
                        ? r.getMensaje() : "No se pudo reservar el asiento");
                refrescarAsientos(false);
                return;
            }
            ItemCarrito it = new ItemCarrito();
            it.setCodigoPartido(codigoPartido);
            it.setIdSeccion(idSeccion);
            it.setCantidad(1);
            it.setFila(fila);
            it.setAsientos(asiento);
            it.setDescripcionPartido(tituloPartido);
            it.setCategoria(locSel.getCategoria());
            it.setCodigoSeccion(secSel.getCodigoSeccion());
            it.setPrecioUnitario(locSel.getPrecio());
            Carrito.agregar(it);
            actualizarResumenCarrito();
            pintarGrilla();
            Ui.toast(this, "Asiento " + fila + "-" + asiento + " agregado al carrito");
        });
    }

    private void actualizarResumenCarrito() {
        lblCarrito.setText("Carrito: " + Carrito.numItems() + " asiento(s)  |  "
                + Moneda.fmt(Carrito.subtotal()));
    }

    // ============================================================================
    // Leyenda
    // ============================================================================
    private void construirLeyenda() {
        layLeyenda.removeAllViews();
        agregarLeyenda(R.color.seat_libre,     getString(R.string.leyenda_libre));
        agregarLeyenda(R.color.seat_reservado, getString(R.string.leyenda_reservado));
        agregarLeyenda(R.color.seat_ocupado,   getString(R.string.leyenda_ocupado));
        agregarLeyenda(R.color.seat_mio,       getString(R.string.leyenda_mio));
    }

    private void agregarLeyenda(int colorRes, String texto) {
        View chip = new View(this);
        GradientDrawable d = new GradientDrawable();
        d.setColor(ContextCompat.getColor(this, colorRes));
        d.setCornerRadius(dp(3));
        chip.setBackground(d);
        LinearLayout.LayoutParams lpChip = new LinearLayout.LayoutParams(dp(14), dp(14));
        lpChip.setMargins(0, 0, dp(4), 0);
        layLeyenda.addView(chip, lpChip);

        TextView txt = new TextView(this);
        txt.setText(texto);
        txt.setTextSize(11);
        txt.setTextColor(ContextCompat.getColor(this, R.color.text_secondary));
        LinearLayout.LayoutParams lpTxt = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lpTxt.setMargins(0, 0, dp(12), 0);
        layLeyenda.addView(txt, lpTxt);
    }

    private int dp(int v) {
        return (int) (v * getResources().getDisplayMetrics().density + 0.5f);
    }
}
