package ec.edu.monster.vista;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.button.MaterialButton;
import ec.edu.monster.R;
import ec.edu.monster.modelo.Partido;
import ec.edu.monster.modelo.ResumenLocalidad;
import ec.edu.monster.modelo.Sesion;
import ec.edu.monster.servicio.GeneradorComprobantePDF;
import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class ReporteFragment extends Fragment {

    private Spinner spn;
    private RecyclerView rv;
    private MaterialButton btnPdf;

    private final List<Partido> partidos = new ArrayList<>();
    private List<ResumenLocalidad> ultimo = new ArrayList<>();
    private String headerPartido = "";
    private final Adapter adapter = new Adapter();

    @Override
    public View onCreateView(@NonNull LayoutInflater inf, ViewGroup c, Bundle s) {
        View v = inf.inflate(R.layout.fragment_reporte, c, false);
        spn = v.findViewById(R.id.spnPartido);
        rv  = v.findViewById(R.id.rvReporte);
        MaterialButton btnGen = v.findViewById(R.id.btnGenerar);
        btnPdf = v.findViewById(R.id.btnPdf);

        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        rv.setAdapter(adapter);

        btnGen.setOnClickListener(view -> generar());
        btnPdf.setOnClickListener(view -> descargarPDF());

        ((MainActivity) requireActivity()).ctrl.partidos((data, err) -> {
            if (err != null) { Ui.error(requireContext(), err); return; }
            partidos.clear();
            partidos.addAll(data == null ? new ArrayList<>() : data);
            List<String> labels = new ArrayList<>();
            for (Partido p : partidos) labels.add(p.toString());
            spn.setAdapter(new ArrayAdapter<>(requireContext(),
                    android.R.layout.simple_spinner_dropdown_item, labels));
        });
        return v;
    }

    private void generar() {
        int pos = spn.getSelectedItemPosition();
        if (pos < 0 || pos >= partidos.size()) return;
        Partido sel = partidos.get(pos);
        headerPartido = sel.toString();
        ((MainActivity) requireActivity()).ctrl.resumenVentas(sel.getCodigo(), (lista, err) -> {
            if (err != null) { Ui.error(requireContext(), err); return; }
            ultimo = lista == null ? new ArrayList<>() : lista;
            adapter.notifyDataSetChanged();
            btnPdf.setEnabled(!ultimo.isEmpty());
        });
    }

    private void descargarPDF() {
        try {
            File pdf = GeneradorComprobantePDF.reporteVentas(
                    requireContext(), headerPartido, ultimo, Sesion.nombre());
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
            return new VH(LayoutInflater.from(p.getContext()).inflate(R.layout.row_resumen, p, false));
        }
        @Override public void onBindViewHolder(@NonNull VH h, int pos) {
            if (ultimo.isEmpty()) return;
            ResumenLocalidad r = ultimo.get(pos);
            h.txtLocalidad.setText(r.getLocalidad());
            h.txtVendidos.setText(String.valueOf(r.getVendidos()));
            h.txtTotal.setText(Moneda.fmt(r.getTotalRecaudado()));
        }
        @Override public int getItemCount() { return ultimo.size(); }
    }

    static class VH extends RecyclerView.ViewHolder {
        TextView txtLocalidad, txtVendidos, txtTotal;
        VH(View v) {
            super(v);
            txtLocalidad = v.findViewById(R.id.txtLocalidad);
            txtVendidos  = v.findViewById(R.id.txtVendidos);
            txtTotal     = v.findViewById(R.id.txtTotal);
        }
    }
}
