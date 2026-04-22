package ec.edu.monster.controlador;

import ec.edu.monster.modelo.Resultado;
import ec.edu.monster.modelo.ServicioMasa;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet(name = "ServletMasa", urlPatterns = {"/masa"})
public class ServletMasa extends HttpServlet {

    private final ServicioMasa servicioMasa = new ServicioMasa();

    @Override
    protected void doGet(HttpServletRequest peticion, HttpServletResponse respuesta)
            throws ServletException, IOException {
        peticion.getRequestDispatcher("/vista/masa.jsp").forward(peticion, respuesta);
    }

    @Override
    protected void doPost(HttpServletRequest peticion, HttpServletResponse respuesta)
            throws ServletException, IOException {
        String operacion = peticion.getParameter("operacion");
        String valorTexto = peticion.getParameter("valor");

        Resultado resultado;
        try {
            double valor = Double.parseDouble(valorTexto);
            double convertido;
            switch (operacion) {
                case "kilogramosALibras":
                    convertido = servicioMasa.kilogramosALibras(valor);
                    break;
                case "gramosAOnzas":
                    convertido = servicioMasa.gramosAOnzas(valor);
                    break;
                case "toneladasAKilogramos":
                    convertido = servicioMasa.toneladasAKilogramos(valor);
                    break;
                case "librasAOnzas":
                    convertido = servicioMasa.librasAOnzas(valor);
                    break;
                case "miligramosAGramos":
                    convertido = servicioMasa.miligramosAGramos(valor);
                    break;
                default:
                    throw new IllegalArgumentException("Operacion desconocida: " + operacion);
            }
            resultado = Resultado.ok(String.valueOf(convertido));
        } catch (NumberFormatException excepcion) {
            resultado = Resultado.error("El valor ingresado no es un numero valido.");
        } catch (Exception excepcion) {
            resultado = Resultado.error("Error al invocar el servicio: " + excepcion.getMessage());
        }

        peticion.setAttribute("resultado", resultado);
        peticion.setAttribute("operacionSeleccionada", operacion);
        peticion.setAttribute("valorIngresado", valorTexto);
        peticion.getRequestDispatcher("/vista/masa.jsp").forward(peticion, respuesta);
    }
}
