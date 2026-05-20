package ec.edu.monster.vista;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import ec.edu.monster.R;
import ec.edu.monster.controlador.TicketController;
import ec.edu.monster.modelo.Partido;
import ec.edu.monster.modelo.Resultado;
import java.util.ArrayList;
import java.util.List;

public class AdminFragment extends Fragment {

    private SwipeRefreshLayout swipe;
    private final List<Partido> data = new ArrayList<>();
    private final Adapter adapter = new Adapter();

    @Override
    public View onCreateView(@NonNull LayoutInflater inf, ViewGroup c, Bundle s) {
        View v = inf.inflate(R.layout.fragment_admin, c, false);
        swipe = v.findViewById(R.id.swipe);
        RecyclerView rv = v.findViewById(R.id.rvPartidos);
        FloatingActionButton fab = v.findViewById(R.id.fabNuevo);
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        rv.setAdapter(adapter);
        swipe.setOnRefreshListener(this::cargar);
        fab.setOnClickListener(view -> dialogoPartido(null));
        cargar();
        return v;
    }

    private TicketController ctrl() { return ((MainActivity) requireActivity()).ctrl; }

    private void cargar() {
        swipe.setRefreshing(true);
        ctrl().partidos((lista, err) -> {
            swipe.setRefreshing(false);
            if (err != null) { Ui.error(requireContext(), err); return; }
            data.clear();
            if (lista != null) data.addAll(lista);
            adapter.notifyDataSetChanged();
        });
    }

    // ============================================================================
    // Crear / Editar
    // ============================================================================
    private void dialogoPartido(Partido editar) {
        View form = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_partido, null);
        TextInputEditText edtL = form.findViewById(R.id.edtLocal);
        TextInputEditText edtV = form.findViewById(R.id.edtVisita);
        TextInputEditText edtF = form.findViewById(R.id.edtFecha);
        TextInputEditText edtP = form.findViewById(R.id.edtLugar);
        if (editar != null) {
            edtL.setText(editar.getEquipoLocal());
            edtV.setText(editar.getEquipoVisita());
            String f = editar.getFecha();
            if (f != null && f.endsWith(".0")) f = f.substring(0, f.length() - 2);
            edtF.setText(f);
            edtP.setText(editar.getLugar());
        }
        new AlertDialog.Builder(requireContext())
                .setTitle(editar == null ? "Nuevo partido" : "Editar partido #" + editar.getCodigo())
                .setView(form)
                .setNegativeButton("Cancelar", null)
                .setPositiveButton("Guardar", (d, w) -> {
                    String local  = String.valueOf(edtL.getText()).trim();
                    String visita = String.valueOf(edtV.getText()).trim();
                    String fecha  = String.valueOf(edtF.getText()).trim();
                    String lugar  = String.valueOf(edtP.getText()).trim();
                    TicketController.Callback<Resultado> cb = (r, err) -> {
                        if (err != null) { Ui.error(requireContext(), err); return; }
                        Ui.toast(requireContext(), r.getMensaje());
                        if (r.isExito()) cargar();
                    };
                    if (editar == null) ctrl().registrarPartido(local, visita, fecha, lugar, cb);
                    else                ctrl().actualizarPartido(editar.getCodigo(), local, visita, fecha, lugar, cb);
                })
                .show();
    }

    private void confirmarEliminar(Partido p) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Eliminar partido")
                .setMessage("Eliminar #" + p.getCodigo() + " (" + p.getEquipoLocal()
                        + " vs " + p.getEquipoVisita() + ")?")
                .setNegativeButton("Cancelar", null)
                .setPositiveButton("Eliminar", (d, w) -> ctrl().eliminarPartido(p.getCodigo(), (r, err) -> {
                    if (err != null) { Ui.error(requireContext(), err); return; }
                    Ui.toast(requireContext(), r.getMensaje());
                    if (r.isExito()) cargar();
                }))
                .show();
    }

    // ---------- Adapter ----------
    private class Adapter extends RecyclerView.Adapter<VH> {
        @NonNull @Override public VH onCreateViewHolder(@NonNull ViewGroup p, int t) {
            return new VH(LayoutInflater.from(p.getContext()).inflate(R.layout.row_admin_partido, p, false));
        }
        @Override public void onBindViewHolder(@NonNull VH h, int pos) {
            Partido p = data.get(pos);
            h.txtTitulo.setText("[" + p.getCodigo() + "] " + p.getEquipoLocal() + " vs " + p.getEquipoVisita());
            h.txtSub.setText(p.getFecha() + "  -  " + p.getLugar());
            h.btnLocalidades.setOnClickListener(v -> abrirLocalidades(p));
            h.btnEditar.setOnClickListener(v -> dialogoPartido(p));
            h.btnEliminar.setOnClickListener(v -> confirmarEliminar(p));
        }
        @Override public int getItemCount() { return data.size(); }
    }

    private void abrirLocalidades(Partido p) {
        Intent i = new Intent(requireContext(), AdminLocalidadesActivity.class);
        i.putExtra("codigoPartido", p.getCodigo());
        i.putExtra("titulo", p.getEquipoLocal() + " vs " + p.getEquipoVisita());
        startActivity(i);
    }

    static class VH extends RecyclerView.ViewHolder {
        TextView txtTitulo, txtSub;
        MaterialButton btnLocalidades, btnEditar, btnEliminar;
        VH(View v) {
            super(v);
            txtTitulo      = v.findViewById(R.id.txtTitulo);
            txtSub         = v.findViewById(R.id.txtSub);
            btnLocalidades = v.findViewById(R.id.btnLocalidades);
            btnEditar      = v.findViewById(R.id.btnEditar);
            btnEliminar    = v.findViewById(R.id.btnEliminar);
        }
    }
}
