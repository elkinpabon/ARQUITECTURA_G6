package ec.edu.monster.controlador;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebServlet(name = "ServletCerrarSesion", urlPatterns = {"/cerrarSesion"})
public class ServletCerrarSesion extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest peticion, HttpServletResponse respuesta)
            throws ServletException, IOException {
        HttpSession sesion = peticion.getSession(false);
        if (sesion != null) {
            sesion.invalidate();
        }
        respuesta.sendRedirect(peticion.getContextPath() + "/vista/iniciarSesion.jsp");
    }
}
