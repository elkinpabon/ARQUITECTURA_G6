package ec.edu.monster.vista;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import ec.edu.monster.R;
import ec.edu.monster.modelo.Partido;
import java.util.ArrayList;
import java.util.List;

/**
 * Pestana "Partidos": lista de partidos disponibles del Mundial 2026.
 * Al tocar un partido se abre AsientosActivity (categoria + seccion + mapa).
 */
public class ComprarFragment extends Fragment {

    private final List<Partido> data = new ArrayList<>();
    private SwipeRefreshLayout swipe;
    private final Adapter adapter = new Adapter();

    @Override
    public View onCreateView(@NonNull LayoutInflater inf, ViewGroup c, Bundle s) {
        View v = inf.inflate(R.layout.fragment_comprar, c, false);
        swipe = v.findViewById(R.id.swipe);
        RecyclerView rv = v.findViewById(R.id.rvPartidos);
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        rv.setAdapter(adapter);
        swipe.setOnRefreshListener(this::cargar);
        cargar();
        return v;
    }

    private ec.edu.monster.controlador.TicketController ctrl() {
        return ((MainActivity) requireActivity()).ctrl;
    }

    private void cargar() {
        swipe.setRefreshing(true);
        ctrl().partidos((lista, err) -> {
            if (!isAdded()) return;
            swipe.setRefreshing(false);
            if (err != null) { Ui.error(requireContext(), err); return; }
            data.clear();
            if (lista != null) data.addAll(lista);
            adapter.notifyDataSetChanged();
        });
    }

    private void abrirAsientos(Partido p) {
        Intent i = new Intent(requireContext(), AsientosActivity.class);
        i.putExtra(AsientosActivity.EXTRA_CODIGO, p.getCodigo());
        i.putExtra(AsientosActivity.EXTRA_TITULO, p.descripcion());
        i.putExtra(AsientosActivity.EXTRA_DETALLE,
                "Grupo " + (p.getGrupo() == null ? "-" : p.getGrupo())
                        + "  |  " + p.getFecha() + "\n" + p.lugarCompleto());
        startActivity(i);
    }

    // ---------- Adapter ----------
    private class Adapter extends RecyclerView.Adapter<VH> {
        @NonNull @Override public VH onCreateViewHolder(@NonNull ViewGroup p, int t) {
            return new VH(LayoutInflater.from(p.getContext()).inflate(R.layout.row_partido, p, false));
        }
        @Override public void onBindViewHolder(@NonNull VH h, int pos) {
            Partido p = data.get(pos);
            h.txtEquipos.setText(p.descripcion());
            h.txtGrupoFecha.setText("Grupo " + (p.getGrupo() == null || p.getGrupo().isEmpty() ? "-" : p.getGrupo())
                    + "  |  " + p.getFecha());
            h.txtEstadio.setText(p.lugarCompleto());
            h.txtGrupo.setText(p.getGrupo() == null || p.getGrupo().isEmpty() ? "-" : p.getGrupo());
            h.itemView.setOnClickListener(v -> abrirAsientos(p));
        }
        @Override public int getItemCount() { return data.size(); }
    }

    static class VH extends RecyclerView.ViewHolder {
        TextView txtEquipos, txtGrupoFecha, txtEstadio, txtGrupo;
        VH(View v) {
            super(v);
            txtEquipos    = v.findViewById(R.id.txtEquipos);
            txtGrupoFecha = v.findViewById(R.id.txtGrupoFecha);
            txtEstadio    = v.findViewById(R.id.txtEstadio);
            txtGrupo      = v.findViewById(R.id.txtGrupo);
        }
    }
}
