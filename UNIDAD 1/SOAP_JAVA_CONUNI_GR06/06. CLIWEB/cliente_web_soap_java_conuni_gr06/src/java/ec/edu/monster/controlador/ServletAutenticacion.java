package ec.edu.monster.controlador;

import ec.edu.monster.modelo.ServicioAutenticacion;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebServlet(name = "ServletAutenticacion", urlPatterns = {"/autenticacion"})
public class ServletAutenticacion extends HttpServlet {

    private final ServicioAutenticacion servicioAutenticacion = new ServicioAutenticacion();

    @Override
    protected void doGet(HttpServletRequest peticion, HttpServletResponse respuesta)
            throws ServletException, IOException {
        peticion.getRequestDispatcher("/vista/iniciarSesion.jsp").forward(peticion, respuesta);
    }

    @Override
    protected void doPost(HttpServletRequest peticion, HttpServletResponse respuesta)
            throws ServletException, IOException {
        String usuario = peticion.getParameter("usuario");
        String contrasena = peticion.getParameter("contrasena");

        try {
            boolean valido = servicioAutenticacion.iniciarSesion(usuario, contrasena);
            if (valido) {
                HttpSession sesion = peticion.getSession(true);
                sesion.setAttribute("usuario", usuario);
                respuesta.sendRedirect(peticion.getContextPath() + "/vista/menu.jsp");
                return;
            }
            peticion.setAttribute("mensajeError", "Usuario o contrasena incorrectos.");
        } catch (Exception excepcion) {
            peticion.setAttribute("mensajeError",
                    "No se pudo conectar con el servidor SOAP: " + excepcion.getMessage());
        }
        peticion.getRequestDispatcher("/vista/iniciarSesion.jsp").forward(peticion, respuesta);
    }
}
