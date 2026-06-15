package ec.edu.monster.vista;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import ec.edu.monster.R;
import ec.edu.monster.controlador.TicketController;

public class LoginActivity extends AppCompatActivity {

    private TicketController ctrl;

    @Override
    protected void onCreate(Bundle s) {
        super.onCreate(s);
        setContentView(R.layout.activity_login);
        ctrl = new TicketController(this);

        TextInputEditText edtUsuario  = findViewById(R.id.edtUsuario);
        TextInputEditText edtClave    = findViewById(R.id.edtClave);
        MaterialButton    btnLogin    = findViewById(R.id.btnLogin);
        ProgressBar       prg         = findViewById(R.id.prgLogin);

        btnLogin.setOnClickListener(v -> {
            String usuario = String.valueOf(edtUsuario.getText()).trim();
            String clave   = String.valueOf(edtClave.getText()).trim();
            if (usuario.isEmpty() || clave.isEmpty()) {
                Ui.toast(this, "Usuario y contrasena son obligatorios");
                return;
            }

            prg.setVisibility(View.VISIBLE);
            btnLogin.setEnabled(false);
            ctrl.login(usuario, clave, (r, err) -> {
                prg.setVisibility(View.GONE);
                btnLogin.setEnabled(true);
                if (err != null) {
                    Ui.error(this, "Error conectando con el servidor:\n" + err);
                    return;
                }
                if (r != null && r.isExito()) {
                    startActivity(new Intent(this, MainActivity.class));
                    finish();
                } else {
                    Ui.error(this, r != null && r.getMensaje() != null
                            ? r.getMensaje() : getString(R.string.msg_login_error));
                }
            });
        });
    }
}
