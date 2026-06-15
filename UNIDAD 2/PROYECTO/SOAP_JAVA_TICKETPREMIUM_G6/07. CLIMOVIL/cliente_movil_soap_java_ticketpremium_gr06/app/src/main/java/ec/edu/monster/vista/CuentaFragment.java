package ec.edu.monster.vista;

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
import ec.edu.monster.modelo.Cuenta;
import ec.edu.monster.modelo.Movimiento;
import java.util.ArrayList;
import java.util.List;

/**
 * Pestana "Mi cuenta": numero de cuenta, saldo (deuda por creditos)
 * y movimientos (compras al contado y creditos).
 */
public class CuentaFragment extends Fragment {

    private SwipeRefreshLayout swipe;
    private TextView txtNumero, txtSaldo;
    private final List<Movimiento> data = new ArrayList<>();
    private final Adapter adapter = new Adapter();

    @Override
    public View onCreateView(@NonNull LayoutInflater inf, ViewGroup c, Bundle s) {
        View v = inf.inflate(R.layout.fragment_cuenta, c, false);
        swipe     = v.findViewById(R.id.swipe);
        txtNumero = v.findViewById(R.id.txtNumero);
        txtSaldo  = v.findViewById(R.id.txtSaldo);
        RecyclerView rv = v.findViewById(R.id.rvMovimientos);
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        rv.setAdapter(adapter);
        swipe.setOnRefreshListener(this::cargar);
        cargar();
        return v;
    }

    @Override public void onResume() { super.onResume(); cargar(); }

    private ec.edu.monster.controlador.TicketController ctrl() {
        return ((MainActivity) requireActivity()).ctrl;
    }

    private void cargar() {
        swipe.setRefreshing(true);
        ctrl().miCuenta((cta, err) -> {
            if (!isAdded()) return;
            if (err != null) {
                swipe.setRefreshing(false);
                Ui.error(requireContext(), err);
                return;
            }
            mostrarCuenta(cta);
            cargarMovimientos();
        });
    }

    private void mostrarCuenta(Cuenta cta) {
        if (cta == null || cta.getNumero() == null || cta.getNumero().isEmpty()) {
            txtNumero.setText("-");
            txtSaldo.setText(Moneda.fmt(null));
            return;
        }
        txtNumero.setText(cta.getNumero());
        txtSaldo.setText(Moneda.fmt(cta.getSaldo()));
    }

    private void cargarMovimientos() {
        ctrl().misMovimientos((lista, err) -> {
            if (!isAdded()) return;
            swipe.setRefreshing(false);
            if (err != null) { Ui.error(requireContext(), err); return; }
            data.clear();
            if (lista != null) data.addAll(lista);
            adapter.notifyDataSetChanged();
        });
    }

    // ---------- Adapter ----------
    private class Adapter extends RecyclerView.Adapter<VH> {
        @NonNull @Override public VH onCreateViewHolder(@NonNull ViewGroup p, int t) {
            return new VH(LayoutInflater.from(p.getContext()).inflate(R.layout.row_movimiento, p, false));
        }
        @Override public void onBindViewHolder(@NonNull VH h, int pos) {
            Movimiento m = data.get(pos);
            String tipo = m.getTipo() == null ? "" : m.getTipo().replace('_', ' ');
            h.txtTipo.setText(tipo + (m.getIdFactura() > 0 ? "  (Factura #" + m.getIdFactura() + ")" : ""));
            h.txtDescripcion.setText(m.getDescripcion());
            h.txtFecha.setText(m.getFecha());
            h.txtMonto.setText(Moneda.fmt(m.getMonto()));
        }
        @Override public int getItemCount() { return data.size(); }
    }

    static class VH extends RecyclerView.ViewHolder {
        TextView txtTipo, txtDescripcion, txtFecha, txtMonto;
        VH(View v) {
            super(v);
            txtTipo        = v.findViewById(R.id.txtTipo);
            txtDescripcion = v.findViewById(R.id.txtDescripcion);
            txtFecha       = v.findViewById(R.id.txtFecha);
            txtMonto       = v.findViewById(R.id.txtMonto);
        }
    }
}
