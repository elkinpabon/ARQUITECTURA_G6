package ec.edu.monster.controlador;

import ec.edu.monster.dto.Peticiones;
import ec.edu.monster.modelo.ClienteResumen;
import ec.edu.monster.modelo.Resultado;
import ec.edu.monster.servicio.CuentaService;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import java.util.List;

/** /api/clientes — listar y registrar clientes (admin). */
@Path("clientes")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ClienteResource {

    private final CuentaService cuentaService = new CuentaService();

    @GET
    public List<ClienteResumen> listar() {
        return cuentaService.listarClientes();
    }

    @POST
    public Resultado registrar(Peticiones.NuevoCliente c) {
        return cuentaService.registrarCliente(c.getPaterno(), c.getMaterno(),
                c.getNombre(), c.getDni(), c.getCiudad(), c.getDireccion(),
                c.getTelefono(), c.getEmail());
    }
}
