using System.Runtime.Serialization;

namespace SERVIDOR.Modelo
{
    [DataContract]
    public class ClienteResumen
    {
        [DataMember]
        public string Codigo { get; set; }

        [DataMember]
        public string Dni { get; set; }

        [DataMember]
        public string Nombre { get; set; }

        public ClienteResumen()
        {
            Codigo = string.Empty;
            Dni = string.Empty;
            Nombre = string.Empty;
        }
    }
}
