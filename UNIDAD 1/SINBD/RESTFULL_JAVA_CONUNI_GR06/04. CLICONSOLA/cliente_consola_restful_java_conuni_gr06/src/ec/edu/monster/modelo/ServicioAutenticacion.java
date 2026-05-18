package ec.edu.monster.modelo;

public class ServicioAutenticacion {

    private final ClienteRest clienteRest = new ClienteRest();

    public boolean iniciarSesion(String usuario, String contrasena) throws Exception {
        String cuerpo = "{\"usuario\":\"" + escapar(usuario) + "\",\"contrasena\":\"" + escapar(contrasena) + "\"}";
        String respuesta = clienteRest.post("/sesion", cuerpo);
        return ClienteRest.extraerBooleano(respuesta, "valido");
    }

    private static String escapar(String texto) {
        if (texto == null) return "";
        return texto.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
