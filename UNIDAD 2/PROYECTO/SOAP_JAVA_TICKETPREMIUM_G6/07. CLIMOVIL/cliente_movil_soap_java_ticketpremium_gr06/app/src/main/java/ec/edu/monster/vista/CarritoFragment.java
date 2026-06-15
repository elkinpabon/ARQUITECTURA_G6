package ec.edu.monster.vista;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import ec.edu.monster.R;
import ec.edu.monster.modelo.Carrito;
import ec.edu.monster.modelo.ComprobanteCompra;
import ec.edu.monster.modelo.Cuota;
import ec.edu.monster.modelo.Factura;
import ec.edu.monster.modelo.ItemCarrito;
import ec.edu.monster.modelo.Sesion;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Pestana "Carrito": lineas reservadas + checkout CONTADO o CREDITO
 * (entrada, numero de cuotas y tasa de interes mensual en %).
 */
public class CarritoFragment extends Fragment {

    private static final BigDecimal IVA = new BigDecimal("0.15");

    private final List<ItemCarrito> data = new ArrayList<>();
    private final Adapter adapter = new Adapter();

    private TextView txtVacio, lblSub, lblIva, lblTot;
    private RadioGroup rgTipoPago;
    private View layCredito;
    private TextInputEditText edtEntrada, edtCuotas, edtTasa;
    private MaterialButton btnPagar, btnVaciar;

    @Override
    public View onCreateView(@NonNull LayoutInflater inf, ViewGroup c, Bundle s) {
        View v = inf.inflate(R.layout.fragment_carrito, c, false);
        txtVacio   = v.findViewById(R.id.txtVacio);
        lblSub     = v.findViewById(R.id.lblSubtotal);
        lblIva     = v.findViewById(R.id.lblIva);
        lblTot     = v.findViewById(R.id.lblTotal);
        rgTipoPago = v.findViewById(R.id.rgTipoPago);
        layCredito = v.findViewById(R.id.layCredito);
        edtEntrada = v.findViewById(R.id.edtEntrada);
        edtCuotas  = v.findViewById(R.id.edtCuotas);
        edtTasa    = v.findViewById(R.id.edtTasa);
        btnPagar   = v.findViewById(R.id.btnPagar);
        btnVaciar  = v.findViewById(R.id.btnVaciar);

        RecyclerView rv = v.findViewById(R.id.rvCarrito);
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        rv.setAdapter(adapter);

        rgTipoPago.setOnCheckedChangeListener((g, id) ->
                layCredito.setVisibility(id == R.id.rbCredito ? View.VISIBLE : View.GONE));
        btnVaciar.setOnClickListener(view -> vaciar());
        btnPagar.setOnClickListener(view -> confirmarPago());

        refrescar();
        return v;
    }

    @Override public void onResume() {
        super.onResume();
        refrescar();
    }

    private ec.edu.monster.controlador.TicketController ctrl() {
        return ((MainActivity) requireActivity()).ctrl;
    }

    // ============================================================================
    // Refresco de lista y totales
    // ============================================================================
    private void refrescar() {
        data.clear();
        data.addAll(Carrito.items());
        adapter.notifyDataSetChanged();
        txtVacio.setVisibility(data.isEmpty() ? View.VISIBLE : View.GONE);
        btnVaciar.setVisibility(data.isEmpty() ? View.GONE : View.VISIBLE);

        BigDecimal sub = Carrito.subtotal();
        BigDecimal iva = sub.multiply(IVA).setScale(2, RoundingMode.HALF_UP);
        lblSub.setText(Moneda.fmt(sub));
        lblIva.setText(Moneda.fmt(iva));
        lblTot.setText(Moneda.fmt(sub.add(iva)));
    }

    // ============================================================================
    // Quitar / vaciar (libera los asientos en el servidor)
    // ============================================================================
    private void quitar(ItemCarrito it) {
        ctrl().liberarAsiento(it.getIdSeccion(), it.getFila(), it.getAsientos(), (r, err) -> {
            if (!isAdded()) return;
            if (err != null) { Ui.error(requireContext(), err); return; }
            Carrito.quitar(it.getIdSeccion(), it.getFila(), it.getAsientos());
            refrescar();
        });
    }

    private void vaciar() {
        ctrl().liberarMisReservas((r, err) -> {
            if (!isAdded()) return;
            if (err != null) { Ui.error(requireContext(), err); return; }
            Carrito.limpiar();
            refrescar();
            Ui.toast(requireContext(), "Carrito vaciado");
        });
    }

    // ============================================================================
    // Checkout
    // ============================================================================
    private void confirmarPago() {
        if (Carrito.numItems() == 0) {
            Ui.toast(requireContext(), getString(R.string.carrito_vacio));
            return;
        }
        boolean credito = rgTipoPago.getCheckedRadioButtonId() == R.id.rbCredito;
        String resumen = "Asientos: " + Carrito.numItems()
                + "\nTotal estimado: " + lblTot.getText()
                + "\nPago: " + (credito ? "CREDITO" : "CONTADO");
        if (credito) {
            resumen += "\nEntrada: $ " + texto(edtEntrada, "0")
                    + "  |  Cuotas: " + texto(edtCuotas, "1")
                    + "  |  Tasa: " + texto(edtTasa, "0") + "% mensual";
        }
        new AlertDialog.Builder(requireContext())
                .setTitle("Confirmar compra")
                .setMessage(resumen)
                .setNegativeButton(R.string.boton_cancelar, null)
                .setPositiveButton(R.string.boton_pagar, (d, w) -> pagar(credito))
                .show();
    }

    private void pagar(boolean credito) {
        String tipoPago = credito ? "CREDITO" : "CONTADO";
        int numCuotas = 0;
        BigDecimal tasa = BigDecimal.ZERO;
        BigDecimal entrada = BigDecimal.ZERO;

        if (credito) {
            try {
                numCuotas = Integer.parseInt(texto(edtCuotas, "0"));
                // el usuario digita % (ej 2) -> el servidor espera decimal (0.02)
                tasa = new BigDecimal(texto(edtTasa, "0"))
                        .divide(new BigDecimal("100"), 6, RoundingMode.HALF_UP);
                entrada = new BigDecimal(texto(edtEntrada, "0"));
            } catch (Exception e) {
                Ui.error(requireContext(), "Revisa entrada, cuotas y tasa: deben ser numeros validos");
                return;
            }
            if (numCuotas <= 0) {
                Ui.error(requireContext(), "El numero de cuotas debe ser mayor a cero");
                return;
            }
        }

        final List<ItemCarrito> items = Carrito.items();
        btnPagar.setEnabled(false);
        ctrl().registrarCompra(items, tipoPago, numCuotas, tasa, entrada, (r, err) -> {
            if (!isAdded()) return;
            btnPagar.setEnabled(true);
            if (err != null) { Ui.error(requireContext(), err); return; }
            if (r == null || !r.isExito()) {
                Ui.error(requireContext(), r != null && r.getMensaje() != null
                        ? r.getMensaje() : "No se pudo registrar la compra");
                return;
            }
            Factura f = r.getFactura();
            if (f != null) {
                ((MainActivity) requireActivity()).comprobantes
                        .put(f.getIdFactura(), construirComprobante(items, f));
            }
            Carrito.limpiar();
            refrescar();
            mostrarResultado(f, r.getMensaje());
        });
    }

    private void mostrarResultado(Factura f, String mensaje) {
        StringBuilder sb = new StringBuilder();
        if (mensaje != null && !mensaje.isEmpty()) sb.append(mensaje).append("\n\n");
        if (f != null) {
            sb.append("Factura #").append(f.getIdFactura()).append("\n");
            sb.append("Fecha: ").append(f.getFecha()).append("\n");
            sb.append("Subtotal: ").append(Moneda.fmt(f.getSubtotal())).append("\n");
            sb.append("IVA: ").append(Moneda.fmt(f.getIva())).append("\n");
            sb.append("Total: ").append(Moneda.fmt(f.getTotal())).append("\n");
            sb.append("Tipo de pago: ").append(f.getTipoPago()).append("\n");
            if (f.esCredito()) {
                sb.append("Entrada: ").append(Moneda.fmt(f.getEntrada())).append("\n");
                sb.append("Monto financiado: ").append(Moneda.fmt(f.getMontoFinanciado())).append("\n");
                sb.append("Cuotas: ").append(f.getNumCuotas()).append("\n");
                if (!f.getAmortizacion().isEmpty()) {
                    sb.append("\nTABLA DE AMORTIZACION\n");
                    sb.append(String.format(Locale.US, "%-3s %-11s %9s %8s %9s %9s%n",
                            "#", "Vence", "Cuota", "Interes", "Capital", "Saldo"));
                    for (Cuota c : f.getAmortizacion()) {
                        sb.append(String.format(Locale.US, "%-3d %-11s %9.2f %8.2f %9.2f %9.2f%n",
                                c.getNumCuota(),
                                c.getFechaVencimiento(),
                                c.getCuota(), c.getInteres(),
                                c.getAbonoCapital(), c.getSaldoFinal()));
                    }
                }
            }
        }
        TextView txt = new TextView(requireContext());
        txt.setText(sb.toString());
        txt.setTextSize(12);
        txt.setTypeface(android.graphics.Typeface.MONOSPACE);
        txt.setTextColor(0xFF1F2937);
        int pad = (int) (16 * getResources().getDisplayMetrics().density);
        txt.setPadding(pad, pad, pad, pad);
        android.widget.ScrollView sc = new android.widget.ScrollView(requireContext());
        sc.addView(txt);

        new AlertDialog.Builder(requireContext())
                .setTitle(getString(R.string.msg_compra_exito))
                .setView(sc)
                .setPositiveButton("OK", null)
                .show();
    }

    private ComprobanteCompra construirComprobante(List<ItemCarrito> items, Factura f) {
        ComprobanteCompra comp = new ComprobanteCompra();
        comp.setIdFactura(f.getIdFactura());
        comp.setCodigoR(generarCodigoR());
        comp.setFecha(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")));
        comp.setUsuario(Sesion.nombre());
        if (!items.isEmpty()) {
            ItemCarrito p = items.get(0);
            String desc = p.getDescripcionPartido();
            if (items.size() > 1) desc += " (+" + (items.size() - 1) + " mas)";
            comp.setPartido(desc);
            comp.setLocalidad(p.getCategoria() + " / " + p.getCodigoSeccion());
        }
        int cant = 0;
        for (ItemCarrito it : items) cant += it.getCantidad();
        comp.setCantidad(cant);
        comp.setSubtotal(f.getSubtotal());
        comp.setIva(f.getIva());
        comp.setTotal(f.getTotal());
        return comp;
    }

    private static String generarCodigoR() {
        String fecha = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss"));
        int rand = ThreadLocalRandom.current().nextInt(1000, 10000);
        return "R-" + fecha + "-" + rand;
    }

    private static String texto(TextInputEditText e, String def) {
        String t = e.getText() == null ? "" : e.getText().toString().trim();
        return t.isEmpty() ? def : t;
    }

    // ============================================================================
    // Adapter
    // ============================================================================
    private class Adapter extends RecyclerView.Adapter<VH> {
        @NonNull @Override public VH onCreateViewHolder(@NonNull ViewGroup p, int t) {
            return new VH(LayoutInflater.from(p.getContext()).inflate(R.layout.row_carrito, p, false));
        }
        @Override public void onBindViewHolder(@NonNull VH h, int pos) {
            ItemCarrito it = data.get(pos);
            h.txtPartido.setText(it.getDescripcionPartido());
            h.txtUbicacion.setText(it.getCategoria() + "  |  Seccion " + it.getCodigoSeccion()
                    + "  |  Fila " + it.getFila() + "  Asiento " + it.getAsientos());
            h.txtPrecio.setText(Moneda.fmt(it.totalLinea()));
            h.btnQuitar.setOnClickListener(v -> quitar(it));
        }
        @Override public int getItemCount() { return data.size(); }
    }

    static class VH extends RecyclerView.ViewHolder {
        TextView txtPartido, txtUbicacion, txtPrecio;
        MaterialButton btnQuitar;
        VH(View v) {
            super(v);
            txtPartido   = v.findViewById(R.id.txtPartido);
            txtUbicacion = v.findViewById(R.id.txtUbicacion);
            txtPrecio    = v.findViewById(R.id.txtPrecio);
            btnQuitar    = v.findViewById(R.id.btnQuitar);
        }
    }
}
