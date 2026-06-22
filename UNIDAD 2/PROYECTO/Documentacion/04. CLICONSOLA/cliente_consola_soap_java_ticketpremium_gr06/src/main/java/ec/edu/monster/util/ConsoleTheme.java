package ec.edu.monster.util;

/** Helpers visuales para la interfaz de consola. */
public final class ConsoleTheme {

    private static final boolean ENABLED = System.getenv("NO_COLOR") == null;

    private static final String RESET = "\u001B[0m";
    private static final String BOLD = "\u001B[1m";
    private static final String CYAN = "\u001B[36m";
    private static final String BLUE = "\u001B[34m";
    private static final String GREEN = "\u001B[32m";
    private static final String YELLOW = "\u001B[33m";
    private static final String RED = "\u001B[31m";
    private static final String GRAY = "\u001B[90m";

    public String line() {
        return repeat("=", 84);
    }

    public String title(String text) {
        return wrap(BOLD + CYAN, text);
    }

    public String primary(String text) {
        return wrap(BOLD + BLUE, text);
    }

    public String success(String text) {
        return wrap(BOLD + GREEN, text);
    }

    public String warning(String text) {
        return wrap(BOLD + YELLOW, text);
    }

    public String danger(String text) {
        return wrap(BOLD + RED, text);
    }

    public String muted(String text) {
        return wrap(GRAY, text);
    }

    public String label(String text) {
        return wrap(BOLD, text);
    }

    public String chip(String text) {
        return wrap(BOLD + BLUE, "[" + text + "]");
    }

    private String wrap(String code, String text) {
        if (!ENABLED) return text;
        return code + text + RESET;
    }

    private String repeat(String s, int count) {
        return s.repeat(Math.max(0, count));
    }
}
