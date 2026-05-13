using System.ServiceModel;
using System.ServiceModel.Channels;
using Ec.Edu.Monster.Contrato;

namespace Ec.Edu.Monster.Utilidades;

public class ClienteSoap
{
    public string DireccionServicio { get; set; } = ConstantesSoap.DireccionServicio;

    public T Ejecutar<T>(Func<IConuniServicio, T> operacion)
    {
        var binding = new BasicHttpBinding(BasicHttpSecurityMode.None);
        var factory = new ChannelFactory<IConuniServicio>(binding, new EndpointAddress(DireccionServicio));
        var cliente = factory.CreateChannel();

        try
        {
            var resultado = operacion(cliente);
            ((IClientChannel)cliente).Close();
            factory.Close();
            return resultado;
        }
        catch
        {
            ((IClientChannel)cliente).Abort();
            factory.Abort();
            throw;
        }
    }
}
