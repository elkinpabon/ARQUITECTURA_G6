using System.ServiceModel;

namespace SERVIDOR.Controlador
{
    [ServiceContract(Namespace = "http://ws.monster.edu.ec/")]
    public interface IWSLogin
    {
        [OperationContract]
        bool IniciarSesion(string usuario, string clave);

        [OperationContract]
        string ClienteDeUsuario(string usuario);
    }
}
