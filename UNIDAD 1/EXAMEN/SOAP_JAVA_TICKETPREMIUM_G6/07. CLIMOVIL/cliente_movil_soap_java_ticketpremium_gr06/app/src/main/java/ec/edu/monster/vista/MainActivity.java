package ec.edu.monster.vista;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import ec.edu.monster.R;
import ec.edu.monster.controlador.TicketController;
import ec.edu.monster.modelo.ComprobanteCompra;
import ec.edu.monster.modelo.Sesion;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    public TicketController ctrl;

    /** Comprobantes generados en esta sesion (clave = idFactura). */
    public final Map<Integer, ComprobanteCompra> comprobantes = new HashMap<>();

    @Override
    protected void onCreate(Bundle s) {
        super.onCreate(s);
        setContentView(R.layout.activity_main);
        ctrl = new TicketController(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setSubtitle("Hola " + Sesion.nombre() + "  [" + (Sesion.isAdmin() ? "ADMIN" : "CLIENTE") + "]");
        toolbar.setSubtitleTextColor(0xFFCCD4E4);
        setSupportActionBar(toolbar);
        toolbar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.action_logout) { cerrarSesion(); return true; }
            return false;
        });

        boolean admin = Sesion.isAdmin();
        List<TabInfo> tabs = new ArrayList<>();
        tabs.add(new TabInfo(getString(R.string.tab_comprar),  ComprarFragment::new));
        tabs.add(new TabInfo(getString(R.string.tab_facturas), FacturasFragment::new));
        if (admin) {
            tabs.add(new TabInfo(getString(R.string.tab_reporte), ReporteFragment::new));
            tabs.add(new TabInfo(getString(R.string.tab_admin),   AdminFragment::new));
        }

        ViewPager2 pager = findViewById(R.id.pager);
        TabLayout  tabLayout = findViewById(R.id.tabs);
        pager.setAdapter(new TabsAdapter(this, tabs));
        pager.setOffscreenPageLimit(tabs.size());
        new TabLayoutMediator(tabLayout, pager,
                (tab, pos) -> tab.setText(tabs.get(pos).titulo)).attach();
    }

    private void cerrarSesion() {
        ctrl.logout();
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }

    // ----- Adapter -----
    private interface FragSupplier { Fragment get(); }
    private static class TabInfo {
        final String titulo;
        final FragSupplier supplier;
        TabInfo(String t, FragSupplier s) { titulo = t; supplier = s; }
    }
    private static class TabsAdapter extends FragmentStateAdapter {
        private final List<TabInfo> tabs;
        TabsAdapter(FragmentActivity a, List<TabInfo> tabs) { super(a); this.tabs = tabs; }
        @NonNull @Override public Fragment createFragment(int pos) { return tabs.get(pos).supplier.get(); }
        @Override public int getItemCount() { return tabs.size(); }
    }
}
