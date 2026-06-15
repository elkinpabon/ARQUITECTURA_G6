package ec.edu.monster.servicio;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.nio.charset.StandardCharsets;

/** Utilidad de seguridad: hash SHA-1 en hexadecimal (formato de la tabla usuario). */
public final class SeguridadUtil {

    private SeguridadUtil() {
    }

    /** Devuelve el SHA-1 del texto en hexadecimal minuscula (40 caracteres). */
    public static String sha1(String texto) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            byte[] hash = md.digest(texto.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder(hash.length * 2);
            for (byte b : hash) {
                sb.append(Character.forDigit((b >> 4) & 0xF, 16));
                sb.append(Character.forDigit(b & 0xF, 16));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-1 no disponible", e);
        }
    }
}
