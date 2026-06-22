package ec.edu.monster.vista;

import android.content.Context;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;

/** Helpers UI compartidos por las pantallas. */
final class Ui {

    private Ui() { }

    static void toast(Context ctx, String msg) {
        Toast.makeText(ctx, msg, Toast.LENGTH_SHORT).show();
    }

    static void error(Context ctx, String msg) {
        new AlertDialog.Builder(ctx)
                .setTitle("Error")
                .setMessage(msg)
                .setPositiveButton("OK", null)
                .show();
    }

    static void info(Context ctx, String titulo, String msg) {
        new AlertDialog.Builder(ctx)
                .setTitle(titulo)
                .setMessage(msg)
                .setPositiveButton("OK", null)
                .show();
    }
}
