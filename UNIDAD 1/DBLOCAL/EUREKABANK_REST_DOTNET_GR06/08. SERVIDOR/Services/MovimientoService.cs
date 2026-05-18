using SERVIDOR.Data;
using SERVIDOR.Models;

namespace SERVIDOR.Services;

public class MovimientoService
{
    private readonly MovimientoDAO _movimientoDAO;

    public MovimientoService(MovimientoDAO movimientoDAO)
    {
        _movimientoDAO = movimientoDAO;
    }

    public List<MovimientoModel> ListarMovimientos(string codigoCuenta)
    {
        if (string.IsNullOrWhiteSpace(codigoCuenta))
            return new List<MovimientoModel>();

        try
        {
            return _movimientoDAO.ListarPorCuenta(codigoCuenta.Trim());
        }
        catch (Exception e)
        {
            Console.WriteLine($"Error listando movimientos: {e.Message}");
            return new List<MovimientoModel>();
        }
    }
}
