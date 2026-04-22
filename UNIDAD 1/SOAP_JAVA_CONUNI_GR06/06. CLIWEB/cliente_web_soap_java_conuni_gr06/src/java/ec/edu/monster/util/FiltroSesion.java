package ec.edu.monster.util;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

/**
 * Filtro que bloquea el acceso a las vistas de conversion si el usuario no tiene sesion activa.
 */
@WebFilter(filterName = "FiltroSesion", urlPatterns = {"/longitud", "/masa", "/temperatura"})
public class FiltroSesion implements Filter {

    @Override
    public void doFilter(ServletRequest peticion, ServletResponse respuesta, FilterChain cadena)
            throws IOException, ServletException {
        HttpServletRequest peticionHttp = (HttpServletRequest) peticion;
        HttpServletResponse respuestaHttp = (HttpServletResponse) respuesta;

        HttpSession sesion = peticionHttp.getSession(false);
        boolean autenticado = (sesion != null && sesion.getAttribute("usuario") != null);

        if (autenticado) {
            cadena.doFilter(peticion, respuesta);
        } else {
            respuestaHttp.sendRedirect(peticionHttp.getContextPath() + "/vista/iniciarSesion.jsp");
        }
    }
}
