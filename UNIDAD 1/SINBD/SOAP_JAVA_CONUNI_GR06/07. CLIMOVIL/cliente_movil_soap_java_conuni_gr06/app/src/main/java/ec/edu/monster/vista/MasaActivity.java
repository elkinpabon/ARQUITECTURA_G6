package ec.edu.monster.vista;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;

import ec.edu.monster.R;
import ec.edu.monster.controlador.ControladorMovil;
import ec.edu.monster.modelo.FormatoConversion;
import ec.edu.monster.modelo.Resultado;
import ec.edu.monster.modelo.ServicioMasa;

public class MasaActivity extends AppCompatActivity {

    private static final String[] OPERACIONES = {
            "kilogramosALibras",
            "gramosAOnzas",
            "toneladasAKilogramos",
            "librasAOnzas",
            "miligramosAGramos"
    };

    private final ServicioMasa servicio = new ServicioMasa();

    private Spinner spOperacion;
    private TextInputEditText txtValor;
    private MaterialButton btnConvertir;
    private MaterialButton btnLimpiar;
    private TextView lblResultado;
    private ProgressBar progreso;
    private View raiz;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_masa);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        raiz = findViewById(R.id.raiz);
        spOperacion = findViewById(R.id.spOperacion);
        txtValor = findViewById(R.id.txtValor);
        btnConvertir = findViewById(R.id.btnConvertir);
        btnLimpiar = findViewById(R.id.btnLimpiar);
        lblResultado = findViewById(R.id.lblResultado);
        progreso = findViewById(R.id.progreso);

        String[] etiquetas = {
                getString(R.string.op_kg_libras),
                getString(R.string.op_g_onzas),
                getString(R.string.op_t_kg),
                getString(R.string.op_lb_oz),
                getString(R.string.op_mg_g)
        };
        spOperacion.setAdapter(new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_dropdown_item, etiquetas));

        btnConvertir.setOnClickListener(v -> convertir());
        btnLimpiar.setOnClickListener(v -> limpiar());
    }

    private void convertir() {
        final String texto = txtValor.getText() == null ? "" : txtValor.getText().toString().trim();
        final double valor;
        try {
            valor = Double.parseDouble(texto.replace(',', '.'));
        } catch (NumberFormatException ex) {
            Snackbar.make(raiz, R.string.msg_valor_invalido, Snackbar.LENGTH_SHORT).show();
            return;
        }

        final int op = spOperacion.getSelectedItemPosition();
        final String operacion = OPERACIONES[op];

        mostrarProgreso(true);
        ControladorMovil.ejecutar(
                () -> {
                    double r;
                    switch (op) {
                        case 0: r = servicio.kilogramosALibras(valor); break;
                        case 1: r = servicio.gramosAOnzas(valor); break;
                        case 2: r = servicio.toneladasAKilogramos(valor); break;
                        case 3: r = servicio.librasAOnzas(valor); break;
                        case 4: r = servicio.miligramosAGramos(valor); break;
                        default: throw new IllegalStateException("Operacion no soportada");
                    }
                    String[] u = FormatoConversion.unidades(operacion);
                    return Resultado.ok(FormatoConversion.formatear(valor, u[0], r, u[1]));
                },
                new ControladorMovil.Callback<Resultado>() {
                    @Override
                    public void onExito(Resultado r) {
                        mostrarProgreso(false);
                        lblResultado.setText(r.isExito() ? r.getValor() : r.getMensaje());
                    }

                    @Override
                    public void onError(Exception ex) {
                        mostrarProgreso(false);
                        Snackbar.make(raiz,
                                getString(R.string.msg_error_servicio, ex.getMessage()),
                                Snackbar.LENGTH_LONG).show();
                    }
                });
    }

    private void limpiar() {
        txtValor.setText("");
        lblResultado.setText(R.string.placeholder_resultado);
        spOperacion.setSelection(0);
    }

    private void mostrarProgreso(boolean visible) {
        progreso.setVisibility(visible ? View.VISIBLE : View.GONE);
        btnConvertir.setEnabled(!visible);
        btnLimpiar.setEnabled(!visible);
    }
}
