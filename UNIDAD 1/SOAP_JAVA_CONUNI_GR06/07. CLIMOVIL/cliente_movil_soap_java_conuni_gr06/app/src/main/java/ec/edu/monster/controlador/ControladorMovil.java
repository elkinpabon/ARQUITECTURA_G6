package ec.edu.monster.controlador;

import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Helper que ejecuta una llamada SOAP (bloqueante) en un hilo secundario
 * y entrega el resultado en el hilo principal (UI thread).
 *
 * Android prohibe operaciones de red en el UI thread (NetworkOnMainThreadException),
 * por eso TODAS las Activities consumen el modelo a traves de este controlador.
 */
public final class ControladorMovil {

    private static final ExecutorService EJECUTOR = Executors.newCachedThreadPool();
    private static final Handler HILO_PRINCIPAL = new Handler(Looper.getMainLooper());

    private ControladorMovil() {}

    /** Callback con dos ramas: exito (resultado T) o fallo (excepcion). */
    public interface Callback<T> {
        void onExito(T resultado);
        void onError(Exception ex);
    }

    /** Ejecuta {@code tarea} en background y entrega el resultado al UI thread. */
    public static <T> void ejecutar(Callable<T> tarea, Callback<T> callback) {
        EJECUTOR.submit(() -> {
            try {
                final T resultado = tarea.call();
                HILO_PRINCIPAL.post(() -> callback.onExito(resultado));
            } catch (Exception ex) {
                HILO_PRINCIPAL.post(() -> callback.onError(ex));
            }
        });
    }
}
