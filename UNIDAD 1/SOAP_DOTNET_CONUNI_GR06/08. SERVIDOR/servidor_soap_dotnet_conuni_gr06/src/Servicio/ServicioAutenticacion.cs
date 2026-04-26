using Ec.Edu.Monster.Modelo;

namespace Ec.Edu.Monster.Servicio;

public class ServicioAutenticacion
{
    public bool Validar(Credencial credencial)
    {
        if (credencial is null)
        {
            return false;
        }

        return credencial.Usuario.Equals("MONSTER", StringComparison.OrdinalIgnoreCase)
               && credencial.Contrasena == "MONSTER9";
    }
}
