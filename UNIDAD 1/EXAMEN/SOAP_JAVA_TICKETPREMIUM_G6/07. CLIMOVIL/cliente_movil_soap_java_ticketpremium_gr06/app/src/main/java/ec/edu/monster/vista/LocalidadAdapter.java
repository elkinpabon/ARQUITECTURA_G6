package ec.edu.monster.vista;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import ec.edu.monster.R;
import ec.edu.monster.modelo.Localidad;
import java.util.ArrayList;
import java.util.List;

/** Lista de localidades con seleccion unica + callback al cambiar. */
class LocalidadAdapter extends RecyclerView.Adapter<LocalidadAdapter.VH> {

    interface OnSeleccion { void onLocalidad(Localidad l); }

    private final List<Localidad> data = new ArrayList<>();
    private int selectedPos = -1;
    private final OnSeleccion cb;

    LocalidadAdapter(OnSeleccion cb) { this.cb = cb; }

    void setData(List<Localidad> nuevos) {
        data.clear();
        if (nuevos != null) data.addAll(nuevos);
        selectedPos = -1;
        notifyDataSetChanged();
    }

    Localidad seleccionada() {
        return selectedPos >= 0 && selectedPos < data.size() ? data.get(selectedPos) : null;
    }

    @NonNull @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_localidad, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int pos) {
        Localidad l = data.get(pos);
        h.txtNombre.setText(l.getCodigoLocalidad());
        h.txtDispo.setText(String.valueOf(l.getDisponibilidad()));
        h.txtPrecio.setText(Moneda.fmt(l.getPrecio()));
        h.itemView.setBackgroundColor(pos == selectedPos ? 0xFFFFF3D6 : 0xFFFFFFFF);
        h.itemView.setOnClickListener(v -> {
            int prev = selectedPos;
            selectedPos = h.getAdapterPosition();
            if (prev >= 0) notifyItemChanged(prev);
            notifyItemChanged(selectedPos);
            if (cb != null) cb.onLocalidad(seleccionada());
        });
    }

    @Override public int getItemCount() { return data.size(); }

    static class VH extends RecyclerView.ViewHolder {
        final TextView txtNombre, txtDispo, txtPrecio;
        VH(View v) {
            super(v);
            txtNombre = v.findViewById(R.id.txtNombre);
            txtDispo  = v.findViewById(R.id.txtDispo);
            txtPrecio = v.findViewById(R.id.txtPrecio);
        }
    }
}
