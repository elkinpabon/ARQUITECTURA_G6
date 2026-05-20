package ec.edu.monster.vista;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import ec.edu.monster.R;
import ec.edu.monster.controlador.TicketController;
import ec.edu.monster.modelo.Localidad;
import ec.edu.monster.modelo.Resultado;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class AdminLocalidadesActivity extends AppCompatActivity {

    private TicketController ctrl;
    private int codigoPartido;
    private final List<Localidad> data = new ArrayList<>();
    private final Adapter adapter = new Adapter();

    @Override
    protected void onCreate(Bundle s) {
        super.onCreate(s);
        setContentView(R.layout.activity_admin_localidades);
        ctrl = new TicketController(this);
        codigoPartido = getIntent().getIntExtra("codigoPartido", -1);
        String titulo = getIntent().getStringExtra("titulo");

        Toolbar tb = findViewById(R.id.toolbar);
        tb.setTitle("Localidades");
        tb.setSubtitle(titulo);
        tb.setSubtitleTextColor(0xFFCCD4E4);
        setSupportActionBar(tb);
        tb.setNavigationOnClickListener(v -> finish());

        RecyclerView rv = findViewById(R.id.rvLocalidades);
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setAdapter(adapter);

        FloatingActionButton fab = findViewById(R.id.fabNuevo);
        fab.setOnClickListener(v -> dialogo(null));

        cargar();
    }

    private void cargar() {
        ctrl.localidadesAdmin(codigoPartido, (lista, err) -> {
            if (err != null) { Ui.error(this, err); return; }
            data.clear();
            if (lista != null) data.addAll(lista);
            adapter.notifyDataSetChanged();
        });
    }

    private void dialogo(Localidad editar) {
        View form = LayoutInflater.from(this).inflate(R.layout.dialog_localidad, null);
        TextInputEditText edtC = form.findViewById(R.id.edtCodigo);
        TextInputEditText edtD = form.findViewById(R.id.edtDispo);
        TextInputEditText edtP = form.findViewById(R.id.edtPrecio);
        if (editar != null) {
            edtC.setText(editar.getCodigoLocalidad());
            edtC.setEnabled(false);
            edtD.setText(String.valueOf(editar.getDisponibilidad()));
            edtP.setText(editar.getPrecio() == null ? "" : editar.getPrecio().toPlainString());
        }
        new AlertDialog.Builder(this)
                .setTitle(editar == null ? "Nueva localidad" : "Editar #" + editar.getId())
                .setView(form)
                .setNegativeButton("Cancelar", null)
                .setPositiveButton("Guardar", (d, w) -> {
                    try {
                        String cod = String.valueOf(edtC.getText()).trim();
                        int disp   = Integer.parseInt(String.valueOf(edtD.getText()).trim());
                        BigDecimal precio = new BigDecimal(String.valueOf(edtP.getText()).trim());
                        TicketController.Callback<Resultado> cb = (r, err) -> {
                            if (err != null) { Ui.error(this, err); return; }
                            Ui.toast(this, r.getMensaje());
                            if (r.isExito()) cargar();
                        };
                        if (editar == null) ctrl.registrarLocalidad(codigoPartido, cod, disp, precio, cb);
                        else                ctrl.actualizarLocalidad(editar.getId(), disp, precio, cb);
                    } catch (NumberFormatException nfe) {
                        Ui.error(this, "Disponibilidad o precio invalidos");
                    }
                })
                .show();
    }

    private void confirmarEliminar(Localidad l) {
        new AlertDialog.Builder(this)
                .setTitle("Eliminar localidad")
                .setMessage("Eliminar " + l.getCodigoLocalidad() + " (id=" + l.getId() + ")?")
                .setNegativeButton("Cancelar", null)
                .setPositiveButton("Eliminar", (d, w) -> ctrl.eliminarLocalidad(l.getId(), (r, err) -> {
                    if (err != null) { Ui.error(this, err); return; }
                    Ui.toast(this, r.getMensaje());
                    if (r.isExito()) cargar();
                }))
                .show();
    }

    // ---------- Adapter ----------
    private class Adapter extends RecyclerView.Adapter<VH> {
        @NonNull @Override public VH onCreateViewHolder(@NonNull ViewGroup p, int t) {
            return new VH(LayoutInflater.from(p.getContext()).inflate(R.layout.row_admin_localidad, p, false));
        }
        @Override public void onBindViewHolder(@NonNull VH h, int pos) {
            Localidad l = data.get(pos);
            h.txtCodigo.setText(l.getCodigoLocalidad());
            h.txtPrecio.setText(Moneda.fmt(l.getPrecio()));
            h.txtDispo.setText("Disponibilidad: " + l.getDisponibilidad());
            h.btnEditar.setOnClickListener(v -> dialogo(l));
            h.btnEliminar.setOnClickListener(v -> confirmarEliminar(l));
        }
        @Override public int getItemCount() { return data.size(); }
    }

    static class VH extends RecyclerView.ViewHolder {
        TextView txtCodigo, txtPrecio, txtDispo;
        MaterialButton btnEditar, btnEliminar;
        VH(View v) {
            super(v);
            txtCodigo   = v.findViewById(R.id.txtCodigo);
            txtPrecio   = v.findViewById(R.id.txtPrecio);
            txtDispo    = v.findViewById(R.id.txtDispo);
            btnEditar   = v.findViewById(R.id.btnEditar);
            btnEliminar = v.findViewById(R.id.btnEliminar);
        }
    }
}
