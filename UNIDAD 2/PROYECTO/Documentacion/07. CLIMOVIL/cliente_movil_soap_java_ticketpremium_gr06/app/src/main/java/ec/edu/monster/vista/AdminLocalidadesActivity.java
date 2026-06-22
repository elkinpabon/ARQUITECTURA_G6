package ec.edu.monster.vista;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

/**
 * OBSOLETO (contrato FIFA 2026): el CRUD de localidades se realiza desde el
 * cliente web. Se conserva la clase para no romper el modulo; se cierra de
 * inmediato si llegara a abrirse.
 */
public class AdminLocalidadesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle s) {
        super.onCreate(s);
        finish();
    }
}
