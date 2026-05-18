package ec.edu.monster.vista;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.card.MaterialCardView;

import ec.edu.monster.R;

/**
 * Menu principal con tarjetas grandes por categoria.
 */
public class MenuActivity extends AppCompatActivity {

    public static final String EXTRA_USUARIO = "extra_usuario";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.accionCerrarSesion) {
                cerrarSesion();
                return true;
            }
            return false;
        });

        String usuario = getIntent().getStringExtra(EXTRA_USUARIO);
        if (usuario == null) usuario = "";

        TextView lblUsuario = findViewById(R.id.lblUsuario);
        lblUsuario.setText(getString(R.string.etiqueta_usuario_activo, usuario));

        MaterialCardView cardLongitud = findViewById(R.id.cardLongitud);
        MaterialCardView cardMasa = findViewById(R.id.cardMasa);
        MaterialCardView cardTemperatura = findViewById(R.id.cardTemperatura);

        cardLongitud.setOnClickListener(v -> abrir(LongitudActivity.class));
        cardMasa.setOnClickListener(v -> abrir(MasaActivity.class));
        cardTemperatura.setOnClickListener(v -> abrir(TemperaturaActivity.class));
    }

    private void abrir(Class<?> destino) {
        startActivity(new Intent(this, destino));
    }

    private void cerrarSesion() {
        Intent i = new Intent(this, LoginActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
        finish();
    }
}
