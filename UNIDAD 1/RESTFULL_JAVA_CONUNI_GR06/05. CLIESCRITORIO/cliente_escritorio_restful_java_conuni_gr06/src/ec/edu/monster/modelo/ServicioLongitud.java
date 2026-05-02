package ec.edu.monster.modelo;

public class ServicioLongitud {

    private final ClienteRest clienteRest = new ClienteRest();

    public double metrosAPies(double metros) throws Exception {
        return invocar("/longitud/metros-a-pies", metros);
    }

    public double kilometrosAMillas(double kilometros) throws Exception {
        return invocar("/longitud/kilometros-a-millas", kilometros);
    }

    public double centimetrosAPulgadas(double centimetros) throws Exception {
        return invocar("/longitud/centimetros-a-pulgadas", centimetros);
    }

    public double yardasAMetros(double yardas) throws Exception {
        return invocar("/longitud/yardas-a-metros", yardas);
    }

    public double milimetrosAPulgadas(double milimetros) throws Exception {
        return invocar("/longitud/milimetros-a-pulgadas", milimetros);
    }

    private double invocar(String ruta, double valor) throws Exception {
        String respuesta = clienteRest.get(ruta, valor);
        return ClienteRest.extraerNumero(respuesta, "valor");
    }
}
