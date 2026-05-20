package ec.edu.monster.vista;

import android.content.Intent;
import android.net.Uri;
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
import com.google.android.material.button.MaterialButton;
import ec.edu.monster.R;
import ec.edu.monster.modelo.ComprobanteCompra;
import ec.edu.monster.modelo.Factura;
import ec.edu.monster.servicio.GeneradorComprobantePDF;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FacturasFragment extends Fragment {

    private final List<Factura> data = new ArrayList<>();
    private SwipeRefreshLayout swipe;
    private final Adapter adapter = new Adapter();

    @Override
    public View onCreateView(@NonNull LayoutInflater inf, ViewGroup c, Bundle s) {
        View v = inf.inflate(R.layout.fragment_facturas, c, false);
        swipe = v.findViewById(R.id.swipe);
        RecyclerView rv = v.findViewById(R.id.rvFacturas);
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        rv.setAdapter(adapter);
        swipe.setOnRefreshListener(this::cargar);
        cargar();
        return v;
    }

    @Override public void onResume() { super.onResume(); cargar(); }

    private void cargar() {
        swipe.setRefreshing(true);
        ((MainActivity) requireActivity()).ctrl.misFacturas((lista, err) -> {
            swipe.setRefreshing(false);
            if (err != null) { Ui.error(requireContext(), err); return; }
            data.clear();
            if (lista != null) data.addAll(lista);
            adapter.notifyDataSetChanged();
        });
    }

    private void descargar(Factura f) {
        ComprobanteCompra comp = ((MainActivity) requireActivity()).comprobantes.get(f.getIdFactura());
        if (comp == null) {
            Ui.error(requireContext(),
                    "Solo puedes descargar el PDF de facturas generadas en esta sesion.\n"
                  + "Realiza una compra nueva para obtener el comprobante completo.");
            return;
        }
        try {
            File pdf = GeneradorComprobantePDF.comprobante(requireContext(), comp);
            Uri uri = GeneradorComprobantePDF.uriCompartible(requireContext(), pdf);
            startActivity(new Intent(Intent.ACTION_VIEW)
                    .setDataAndType(uri, "application/pdf")
                    .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION));
        } catch (Exception ex) {
            Ui.error(requireContext(), "No se pudo generar el PDF:\n" + ex.getMessage());
        }
    }

    // ---------- Adapter inner ----------
    private class Adapter extends RecyclerView.Adapter<VH> {
        @NonNull @Override public VH onCreateViewHolder(@NonNull ViewGroup p, int t) {
            return new VH(LayoutInflater.from(p.getContext()).inflate(R.layout.row_factura, p, false));
        }
        @Override public void onBindViewHolder(@NonNull VH h, int pos) {
            Factura f = data.get(pos);
            h.txtFactura.setText("Factura #" + f.getIdFactura());
            h.txtFecha.setText(f.getFecha());
            h.txtTotal.setText(Moneda.fmt(f.getTotal()));
            h.txtDetalle.setText("Subtotal: " + Moneda.fmt(f.getSubtotal())
                    + "   IVA: " + Moneda.fmt(f.getIva()));
            h.btnPdf.setOnClickListener(v -> descargar(f));
        }
        @Override public int getItemCount() { return data.size(); }
    }

    static class VH extends RecyclerView.ViewHolder {
        TextView txtFactura, txtFecha, txtTotal, txtDetalle;
        MaterialButton btnPdf;
        VH(View v) {
            super(v);
            txtFactura = v.findViewById(R.id.txtFactura);
            txtFecha   = v.findViewById(R.id.txtFecha);
            txtTotal   = v.findViewById(R.id.txtTotal);
            txtDetalle = v.findViewById(R.id.txtDetalle);
            btnPdf     = v.findViewById(R.id.btnPdf);
        }
    }
}
