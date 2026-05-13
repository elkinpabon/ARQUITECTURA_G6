package ec.edu.monster.vista;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;

import ec.edu.monster.R;
import ec.edu.monster.controlador.ControladorMovil;
import ec.edu.monster.modelo.ServicioAutenticacion;

/**
 * Pantalla de inicio de sesion.
 * - Habilita/deshabilita el boton segun los campos.
 * - Muestra errores con Snackbar (no Toast) para mejor UX.
 * - Pulsar "Done" en el teclado dispara el login.
 */
public class LoginActivity extends AppCompatActivity {

    private final ServicioAutenticacion servicioAutenticacion = new ServicioAutenticacion();

    private TextInputEditText txtUsuario;
    private TextInputEditText txtContrasena;
    private MaterialButton btnIngresar;
    private ProgressBar progreso;
    private View raiz;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        raiz = findViewById(R.id.raiz);
        txtUsuario = findViewById(R.id.txtUsuario);
        txtContrasena = findViewById(R.id.txtContrasena);
        btnIngresar = findViewById(R.id.btnIngresar);
        progreso = findViewById(R.id.progreso);

        btnIngresar.setEnabled(false);
        TextWatcher watcher = new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int a, int b, int c) {}
            @Override public void onTextChanged(CharSequence s, int a, int b, int c) {}
            @Override public void afterTextChanged(Editable s) {
                btnIngresar.setEnabled(camposCompletos());
            }
        };
        txtUsuario.addTextChangedListener(watcher);
        txtContrasena.addTextChangedListener(watcher);

        txtContrasena.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE && camposCompletos()) {
                intentarIngresar();
                return true;
            }
            return false;
        });

        btnIngresar.setOnClickListener(v -> intentarIngresar());
    }

    private boolean camposCompletos() {
        return !TextUtils.isEmpty(textoDe(txtUsuario)) && !TextUtils.isEmpty(textoDe(txtContrasena));
    }

    private String textoDe(TextInputEditText et) {
        return et.getText() == null ? "" : et.getText().toString().trim();
    }

    private void intentarIngresar() {
        final String usuario = textoDe(txtUsuario);
        final String contrasena = txtContrasena.getText() == null ? "" : txtContrasena.getText().toString();

        if (TextUtils.isEmpty(usuario) || TextUtils.isEmpty(contrasena)) {
            snack(getString(R.string.msg_campos_vacios), true);
            return;
        }

        mostrarProgreso(true);
        ControladorMovil.ejecutar(
                () -> servicioAutenticacion.iniciarSesion(usuario, contrasena),
                new ControladorMovil.Callback<Boolean>() {
                    @Override
                    public void onExito(Boolean ok) {
                        mostrarProgreso(false);
                        if (Boolean.TRUE.equals(ok)) {
                            Intent i = new Intent(LoginActivity.this, MenuActivity.class);
                            i.putExtra(MenuActivity.EXTRA_USUARIO, usuario);
                            startActivity(i);
                            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                            finish();
                        } else {
                            snack(getString(R.string.msg_credenciales_invalidas), true);
                        }
                    }

                    @Override
                    public void onError(Exception ex) {
                        mostrarProgreso(false);
                        snack(getString(R.string.msg_error_conexion, ex.getMessage()), true);
                    }
                });
    }

    private void mostrarProgreso(boolean visible) {
        progreso.setVisibility(visible ? View.VISIBLE : View.GONE);
        btnIngresar.setEnabled(!visible && camposCompletos());
    }

    private void snack(String mensaje, boolean esError) {
        Snackbar sb = Snackbar.make(raiz, mensaje, Snackbar.LENGTH_LONG);
        if (esError) {
            sb.setBackgroundTint(getColor(R.color.md_theme_error));
            sb.setTextColor(getColor(R.color.md_theme_onError));
        }
        sb.show();
    }
}
