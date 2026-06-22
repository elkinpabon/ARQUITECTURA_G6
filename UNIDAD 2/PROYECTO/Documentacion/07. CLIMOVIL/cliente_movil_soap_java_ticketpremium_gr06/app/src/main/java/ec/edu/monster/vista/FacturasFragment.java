package ec.edu.monster.vista;

import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;
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
import ec.edu.monster.modelo.Cuota;
import ec.edu.monster.modelo.DetalleFactura;
import ec.edu.monster.modelo.Factura;
import ec.edu.monster.modelo.Sesion;
import ec.edu.monster.servicio.GeneradorComprobantePDF;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Pestana "Mis compras": facturas del usuario (el admin ve todas).
 * Detalle = verComprobante (boletos + amortizacion si es credito).
 */
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

    private ec.edu.monster.controlador.TicketController ctrl() {
        return ((MainActivity) requireActivity()).ctrl;
    }

    private void cargar() {
        swipe.setRefreshing(true);
        ctrl().misFacturas((lista, err) -> {
            if (!isAdded()) return;
            swipe.setRefreshing(false);
            if (err != null) { Ui.error(requireContext(), err); return; }
            data.clear();
            if (lista != null) data.addAll(lista);
            adapter.notifyDataSetChanged();
        });
    }

    // ============================================================================
    // Detalle (verComprobante)
    // ============================================================================
    private void verDetalle(Factura cab) {
        ctrl().verComprobante(cab.getIdFactura(), (f, err) -> {
            if (!isAdded()) return;
            if (err != null) { Ui.error(requireContext(), err); return; }
            if (f == null || f.getIdFactura() == 0) {
                Ui.error(requireContext(), "No se pudo obtener el comprobante");
                return;
            }
            mostrarDetalle(f);
        });
    }

    private void mostrarDetalle(Factura f) {
        StringBuilder sb = new StringBuilder();
        sb.append("Factura #").append(f.getIdFactura()).append("\n");
        sb.append("Fecha: ").append(f.getFecha()).append("\n");
        sb.append("Tipo de pago: ").append(f.getTipoPago()).append("\n\n");

        sb.append("BOLETOS\n");
        if (f.getDetalles().isEmpty()) {
            sb.append("(sin detalle)\n");
        } else {
            for (DetalleFactura d : f.getDetalles()) {
                sb.append("- ").append(vacio(d.getDescripcionPartido(), "Partido " + d.getCodigoPartido()))
                  .append("\n  ").append(d.getCategoria());
                if (d.getFila() != null && !d.getFila().isEmpty()) {
                    sb.append("  Fila ").append(d.getFila());
                }
                if (d.getAsientos() != null && !d.getAsientos().isEmpty()) {
                    sb.append("  Asientos ").append(d.getAsientos());
                }
                sb.append("\n  ").append(d.getCantidad()).append(" x ")
                  .append(Moneda.fmt(d.getPrecioUnitario()))
                  .append(" = ").append(Moneda.fmt(d.getTotal())).append("\n");
            }
        }

        sb.append("\nSubtotal: ").append(Moneda.fmt(f.getSubtotal())).append("\n");
        sb.append("IVA: ").append(Moneda.fmt(f.getIva())).append("\n");
        sb.append("TOTAL: ").append(Moneda.fmt(f.getTotal())).append("\n");

        if (f.esCredito()) {
            sb.append("\nCREDITO\n");
            sb.append("Entrada: ").append(Moneda.fmt(f.getEntrada())).append("\n");
            sb.append("Monto financiado: ").append(Moneda.fmt(f.getMontoFinanciado())).append("\n");
            sb.append("Cuotas: ").append(f.getNumCuotas()).append("\n");
            if (!f.getAmortizacion().isEmpty()) {
                sb.append("\nTABLA DE AMORTIZACION\n");
                sb.append(String.format(Locale.US, "%-3s %-11s %9s %8s %9s %9s%n",
                        "#", "Vence", "Cuota", "Interes", "Capital", "Saldo"));
                for (Cuota cu : f.getAmortizacion()) {
                    sb.append(String.format(Locale.US, "%-3d %-11s %9.2f %8.2f %9.2f %9.2f%n",
                            cu.getNumCuota(), cu.getFechaVencimiento(),
                            cu.getCuota(), cu.getInteres(),
                            cu.getAbonoCapital(), cu.getSaldoFinal()));
                }
            }
        }

        TextView txt = new TextView(requireContext());
        txt.setText(sb.toString());
        txt.setTextSize(12);
        txt.setTypeface(Typeface.MONOSPACE);
        txt.setTextColor(0xFF1F2937);
        int pad = (int) (16 * getResources().getDisplayMetrics().density);
        txt.setPadding(pad, pad, pad, pad);
        ScrollView sc = new ScrollView(requireContext());
        sc.addView(txt);

        new AlertDialog.Builder(requireContext())
                .setTitle("Comprobante #" + f.getIdFactura())
                .setView(sc)
                .setPositiveButton("OK", null)
                .show();
    }

    private static String vacio(String s, String def) {
        return s == null || s.trim().isEmpty() ? def : s;
    }

    // ============================================================================
    // PDF (solo compras generadas en esta sesion)
    // ============================================================================
    private void descargar(Factura f) {
        ComprobanteCompra comp = ((MainActivity) requireActivity()).comprobantes.get(f.getIdFactura());
        if (comp == null) {
            Ui.error(requireContext(),
                    "Solo puedes descargar el PDF de facturas generadas en esta sesion.\n"
                  + "Usa el boton Detalle para ver el comprobante completo.");
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
            String tipo = f.getTipoPago() == null || f.getTipoPago().isEmpty()
                    ? "" : "   Pago: " + f.getTipoPago();
            h.txtDetalle.setText("Subtotal: " + Moneda.fmt(f.getSubtotal())
                    + "   IVA: " + Moneda.fmt(f.getIva()) + tipo);

            // Solo el admin ve el nombre del cliente que compro la factura
            if (Sesion.isAdmin()) {
                String nombre = f.getUsuarioNombre();
                if (nombre == null || nombre.isEmpty()) nombre = "id=" + f.getIdUsuario();
                h.txtUsuario.setText("Cliente: " + nombre);
                h.txtUsuario.setVisibility(View.VISIBLE);
            } else {
                h.txtUsuario.setVisibility(View.GONE);
            }

            h.btnDetalle.setOnClickListener(v -> verDetalle(f));
            h.btnPdf.setOnClickListener(v -> descargar(f));
        }
        @Override public int getItemCount() { return data.size(); }
    }

    static class VH extends RecyclerView.ViewHolder {
        TextView txtFactura, txtFecha, txtTotal, txtDetalle, txtUsuario;
        MaterialButton btnDetalle, btnPdf;
        VH(View v) {
            super(v);
            txtFactura = v.findViewById(R.id.txtFactura);
            txtFecha   = v.findViewById(R.id.txtFecha);
            txtTotal   = v.findViewById(R.id.txtTotal);
            txtDetalle = v.findViewById(R.id.txtDetalle);
            txtUsuario = v.findViewById(R.id.txtUsuario);
            btnDetalle = v.findViewById(R.id.btnDetalleFactura);
            btnPdf     = v.findViewById(R.id.btnPdf);
        }
    }
}
