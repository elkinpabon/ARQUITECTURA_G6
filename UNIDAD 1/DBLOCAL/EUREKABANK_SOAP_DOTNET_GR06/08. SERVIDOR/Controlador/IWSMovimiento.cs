using System.Collections.Generic;
using System.ServiceModel;
using SERVIDOR.Modelo;

namespace SERVIDOR.Controlador
{
    [ServiceContract(Namespace = "http://ws.monster.edu.ec/")]
    public interface IWSMovimiento
    {
        [OperationContract]
        List<MovimientoModel> ListarMovimientos(string cuenta);
    }
}
