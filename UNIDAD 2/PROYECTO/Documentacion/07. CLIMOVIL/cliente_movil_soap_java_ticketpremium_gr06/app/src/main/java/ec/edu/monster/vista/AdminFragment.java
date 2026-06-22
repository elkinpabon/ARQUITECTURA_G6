package ec.edu.monster.vista;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import ec.edu.monster.R;

/**
 * OBSOLETO (contrato FIFA 2026): la administracion de partidos/localidades
 * se realiza desde el cliente web. Se conserva la clase para no romper el
 * modulo; ya no se registra en las pestanas de MainActivity.
 */
public class AdminFragment extends Fragment {

    @Override
    public View onCreateView(@NonNull LayoutInflater inf, ViewGroup c, Bundle s) {
        return inf.inflate(R.layout.fragment_admin, c, false);
    }
}
