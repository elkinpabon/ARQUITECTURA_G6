using SERVIDOR.Modelo;
using SERVIDOR.Persistencia;

namespace SERVIDOR.Servicio
{
    public class MovimientoService
    {
        private readonly MovimientoDAO movimientoDAO = new MovimientoDAO();

        public List<MovimientoModel> ListarMovimientos(string codigoCuenta)
        {
            if (string.IsNullOrWhiteSpace(codigoCuenta))
                return new List<MovimientoModel>();

            try
            {
                return movimientoDAO.ListarPorCuenta(codigoCuenta.Trim());
            }
            catch
            {
                return new List<MovimientoModel>();
            }
        }
    }
}
