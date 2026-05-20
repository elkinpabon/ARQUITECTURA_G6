package ec.edu.monster.vista;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.google.android.material.button.MaterialButton;
import ec.edu.monster.R;
import ec.edu.monster.modelo.ComprobanteCompra;
import ec.edu.monster.modelo.Factura;
import ec.edu.monster.modelo.Localidad;
import ec.edu.monster.modelo.Partido;
import ec.edu.monster.modelo.Resultado;
import ec.edu.monster.modelo.Sesion;
import ec.edu.monster.servicio.GeneradorComprobantePDF;
import java.io.File;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class ComprarFragment extends Fragment {

    private static final BigDecimal IVA = new BigDecimal("0.15");

    private Spinner    spnPartido;
    private RecyclerView rv;
    private EditText   edtCantidad;
    private TextView   lblSub, lblIva, lblTot;
    private MaterialButton btnComprar;
    private SwipeRefreshLayout swipe;

    private final List<Partido> partidos = new ArrayList<>();
    private final LocalidadAdapter localidadAdapter = new LocalidadAdapter(l -> recalcular());

    @Override
    public View onCreateView(@NonNull LayoutInflater inf, ViewGroup c, Bundle s) {
        View v = inf.inflate(R.layout.fragment_comprar, c, false);
        spnPartido  = v.findViewById(R.id.spnPartido);
        rv          = v.findViewById(R.id.rvLocalidades);
        edtCantidad = v.findViewById(R.id.edtCantidad);
        lblSub      = v.findViewById(R.id.lblSubtotal);
        lblIva      = v.findViewById(R.id.lblIva);
        lblTot      = v.findViewById(R.id.lblTotal);
        btnComprar  = v.findViewById(R.id.btnComprar);
        swipe       = v.findViewById(R.id.swipe);

        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        rv.setAdapter(localidadAdapter);

        spnPartido.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(AdapterView<?> a, View view, int i, long id) { cargarLocalidades(); }
            @Override public void onNothingSelected(AdapterView<?> a) { }
        });
        edtCantidad.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int st, int c, int a) { }
            @Override public void onTextChanged(CharSequence s, int st, int b, int c) { }
            @Override public void afterTextChanged(Editable s) { recalcular(); }
        });
        btnComprar.setOnClickListener(view -> confirmarCompra());
        swipe.setOnRefreshListener(this::cargarPartidos);

        cargarPartidos();
        return v;
    }

    // ============================================================================
    // Carga de datos
    // ============================================================================
    private void cargarPartidos() {
        swipe.setRefreshing(true);
        ctrl().partidos((data, err) -> {
            swipe.setRefreshing(false);
            if (err != null) { Ui.error(requireContext(), err); return; }
            partidos.clear();
            partidos.addAll(data == null ? new ArrayList<>() : data);
            List<String> labels = new ArrayList<>();
            for (Partido p : partidos) labels.add(p.toString());
            ArrayAdapter<String> ad = new ArrayAdapter<>(requireContext(),
                    android.R.layout.simple_spinner_dropdown_item, labels);
            spnPartido.setAdapter(ad);
            if (!partidos.isEmpty()) cargarLocalidades();
        });
    }

    private void cargarLocalidades() {
        int pos = spnPartido.getSelectedItemPosition();
        if (pos < 0 || pos >= partidos.size()) return;
        ctrl().localidades(partidos.get(pos).getCodigo(), (data, err) -> {
            if (err != null) { Ui.error(requireContext(), err); return; }
            localidadAdapter.setData(data);
            recalcular();
        });
    }

    // ============================================================================
    // Calculo
    // ============================================================================
    private void recalcular() {
        Localidad sel = localidadAdapter.seleccionada();
        int cant = leerCantidad();
        if (sel == null) {
            lblSub.setText("$ 0.00"); lblIva.setText("$ 0.00"); lblTot.setText("$ 0.00");
            return;
        }
        BigDecimal sub = sel.getPrecio().multiply(BigDecimal.valueOf(cant));
        BigDecimal iva = sub.multiply(IVA).setScale(2, RoundingMode.HALF_UP);
        BigDecimal tot = sub.add(iva);
        lblSub.setText(Moneda.fmt(sub));
        lblIva.setText(Moneda.fmt(iva));
        lblTot.setText(Moneda.fmt(tot));
    }

    private int leerCantidad() {
        try {
            int n = Integer.parseInt(String.valueOf(edtCantidad.getText()).trim());
            return Math.max(1, n);
        } catch (Exception e) { return 1; }
    }

    // ============================================================================
    // Compra
    // ============================================================================
    private void confirmarCompra() {
        int pos = spnPartido.getSelectedItemPosition();
        if (pos < 0 || pos >= partidos.size()) { Ui.toast(requireContext(), "Selecciona un partido"); return; }
        Partido part = partidos.get(pos);
        Localidad loc = localidadAdapter.seleccionada();
        if (loc == null) { Ui.toast(requireContext(), "Selecciona una localidad"); return; }
        int cant = leerCantidad();

        new AlertDialog.Builder(requireContext())
                .setTitle("Confirmar compra")
                .setMessage("Partido: " + part + "\nLocalidad: " + loc.getCodigoLocalidad()
                        + "\nCantidad: " + cant + "\nTotal: " + lblTot.getText())
                .setNegativeButton("Cancelar", null)
                .setPositiveButton("Comprar", (d, w) -> ejecutarCompra(part, loc, cant))
                .show();
    }

    private void ejecutarCompra(Partido part, Localidad loc, int cant) {
        btnComprar.setEnabled(false);
        ctrl().comprar(part.getCodigo(), loc.getCodigoLocalidad(), cant, (r, err) -> {
            btnComprar.setEnabled(true);
            if (err != null) { Ui.error(requireContext(), err); return; }
            if (r == null || !r.isExito()) {
                Ui.error(requireContext(), r != null ? r.getMensaje() : "Error en la compra");
                return;
            }
            Factura f = r.getFactura();
            ComprobanteCompra comp = construirComprobante(part, loc, cant, f);
            ((MainActivity) requireActivity()).comprobantes.put(f.getIdFactura(), comp);

            new AlertDialog.Builder(requireContext())
                    .setTitle("Compra exitosa")
                    .setMessage("Comprobante: " + comp.getCodigoR()
                            + "\nFactura #" + f.getIdFactura()
                            + "\nTotal: " + Moneda.fmt(f.getTotal()))
                    .setPositiveButton("Descargar PDF", (d, w) -> generarPDF(comp))
                    .setNegativeButton("Cerrar", null)
                    .show();
            cargarLocalidades();
        });
    }

    private ComprobanteCompra construirComprobante(Partido p, Localidad l, int cant, Factura f) {
        ComprobanteCompra c = new ComprobanteCompra();
        c.setIdFactura(f.getIdFactura());
        c.setCodigoR(generarCodigoR());
        c.setFecha(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")));
        c.setUsuario(Sesion.nombre());
        c.setPartido(p.getEquipoLocal() + " vs " + p.getEquipoVisita());
        c.setLocalidad(l.getCodigoLocalidad());
        c.setCantidad(cant);
        c.setSubtotal(f.getSubtotal());
        c.setIva(f.getIva());
        c.setTotal(f.getTotal());
        return c;
    }

    private static String generarCodigoR() {
        String fecha = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss"));
        int rand = ThreadLocalRandom.current().nextInt(1000, 10000);
        return "R-" + fecha + "-" + rand;
    }

    private void generarPDF(ComprobanteCompra comp) {
        try {
            File pdf = GeneradorComprobantePDF.comprobante(requireContext(), comp);
            Uri uri = GeneradorComprobantePDF.uriCompartible(requireContext(), pdf);
            Intent ver = new Intent(Intent.ACTION_VIEW)
                    .setDataAndType(uri, "application/pdf")
                    .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            Intent share = new Intent(Intent.ACTION_SEND)
                    .setType("application/pdf")
                    .putExtra(Intent.EXTRA_STREAM, uri)
                    .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            Intent chooser = Intent.createChooser(ver, "Comprobante " + comp.getCodigoR())
                    .putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{share});
            startActivity(chooser);
        } catch (Exception ex) {
            Ui.error(requireContext(), "No se pudo generar el PDF:\n" + ex.getMessage());
        }
    }

    private ec.edu.monster.controlador.TicketController ctrl() {
        return ((MainActivity) requireActivity()).ctrl;
    }
}
