using System.Runtime.Serialization;

namespace SERVIDOR.Modelo
{
    [DataContract]
    public class Usuario
    {
        [DataMember]
        public string CodigoEmpleado { get; set; }

        [DataMember]
        public string UsuarioNombre { get; set; }

        [DataMember]
        public string Clave { get; set; }

        [DataMember]
        public string Estado { get; set; }

        [DataMember]
        public string ClienteCodigo { get; set; }

        public Usuario()
        {
            CodigoEmpleado = string.Empty;
            UsuarioNombre = string.Empty;
            Clave = string.Empty;
            Estado = string.Empty;
            ClienteCodigo = string.Empty;
        }
    }
}
