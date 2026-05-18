using Ec.Edu.Monster.Modelo;

namespace Ec.Edu.Monster.Servicio;

public class ServicioAutenticacion
{
    public Resultado Validar(Credencial credencial)
    {
        if (credencial is null)
        {
            return new Resultado { Exito = false, Mensaje = "Credencial invalida" };
        }

        var exito = credencial.Usuario.Equals("MONSTER", StringComparison.OrdinalIgnoreCase)
                    && credencial.Contrasena == "MONSTER9";

        return new Resultado
        {
            Exito = exito,
            Mensaje = exito ? "Sesion iniciada" : "Credenciales invalidas"
        };
    }
}
