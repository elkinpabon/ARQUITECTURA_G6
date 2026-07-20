package ec.edu.monster.controlador;

import ec.edu.monster.dto.Peticiones;
import ec.edu.monster.modelo.Resultado;
import ec.edu.monster.servicio.LoginService;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

/** /api/login — autenticación y cliente asociado al usuario. */
@Path("login")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class LoginResource {

    private final LoginService loginService = new LoginService();

    @POST
    public Resultado iniciarSesion(Peticiones.Login req) {
        boolean ok = req != null
                && loginService.login(req.getUsuario(), req.getClave());
        return ok ? Resultado.ok("Sesión iniciada.", 0)
                  : Resultado.error("Usuario o clave inválidos.");
    }

    /** Código de cliente del usuario, o "" si es admin/sin cliente. */
    @GET
    @Path("cliente/{usuario}")
    @Produces(MediaType.TEXT_PLAIN)
    public String clienteDeUsuario(@PathParam("usuario") String usuario) {
        return loginService.clienteDe(usuario);
    }
}
