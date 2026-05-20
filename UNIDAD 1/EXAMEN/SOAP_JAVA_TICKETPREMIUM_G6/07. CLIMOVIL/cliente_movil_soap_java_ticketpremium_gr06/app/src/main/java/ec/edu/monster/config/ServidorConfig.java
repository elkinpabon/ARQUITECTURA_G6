package ec.edu.monster.config;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * URL base del servidor SOAP. Persiste en SharedPreferences para que la
 * pantalla de login pueda cambiarla sin recompilar.
 *
 * Default: 10.0.2.2 = el localhost del host visto desde el emulador AVD.
 */
public final class ServidorConfig {

    private static final String PREFS = "servidor_prefs";
    private static final String KEY   = "servidor.base";
    public static final String DEFAULT =
            "http://10.0.2.2:8080/servidor_soap_java_federacion_gr06";

    private ServidorConfig() { }

    public static String base(Context ctx) {
        SharedPreferences p = ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        String v = p.getString(KEY, DEFAULT);
        return v == null || v.isEmpty() ? DEFAULT : v.replaceAll("/+$", "");
    }

    public static void guardar(Context ctx, String base) {
        if (base == null || base.trim().isEmpty()) return;
        SharedPreferences p = ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        p.edit().putString(KEY, base.trim()).apply();
    }

    public static String endpoint(Context ctx)  { return base(ctx) + "/WSFederacion"; }
    public static String namespace()            { return "http://ws.monster.edu.ec/"; }
}
