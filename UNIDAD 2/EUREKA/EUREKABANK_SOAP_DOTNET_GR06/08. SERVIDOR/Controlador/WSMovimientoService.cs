using SERVIDOR.Modelo;
using SERVIDOR.Servicio;

namespace SERVIDOR.Controlador
{
    public class WSMovimientoService : IWSMovimiento
    {
        private readonly MovimientoService movimientoService = new MovimientoService();

        public List<MovimientoModel> ListarMovimientos(string cuenta)
        {
            return movimientoService.ListarMovimientos(cuenta);
        }
    }
}
