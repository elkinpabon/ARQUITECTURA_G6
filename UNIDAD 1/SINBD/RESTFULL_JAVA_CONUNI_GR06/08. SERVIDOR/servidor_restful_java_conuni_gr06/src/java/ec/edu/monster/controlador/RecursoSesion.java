package ec.edu.monster.controlador;

import ec.edu.monster.modelo.PeticionSesion;
import ec.edu.monster.modelo.RespuestaSesion;
import ec.edu.monster.servicio.ServicioAutenticacion;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

/**
 * Recurso JAX-RS para autenticacion. Equivale a la operacion SOAP iniciarSesion.
 * Devuelve 200 con {valido:true} si las credenciales coinciden o 401 con
 * {valido:false} si no.
 */
@Path("/sesion")
public class RecursoSesion {

    private final ServicioAutenticacion servicioAutenticacion = new ServicioAutenticacion();

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response iniciarSesion(PeticionSesion peticion) {
        if (peticion == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new RespuestaSesion(false, "Cuerpo vacio"))
                    .build();
        }
        boolean valido = servicioAutenticacion.autenticar(peticion.getUsuario(), peticion.getContrasena());
        if (valido) {
            return Response.ok(new RespuestaSesion(true, "Credenciales validas")).build();
        }
        return Response.status(Response.Status.UNAUTHORIZED)
                .entity(new RespuestaSesion(false, "Usuario o contrasena invalidos"))
                .build();
    }
}
