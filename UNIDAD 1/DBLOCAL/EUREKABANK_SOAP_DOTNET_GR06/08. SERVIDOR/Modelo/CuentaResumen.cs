using System.Runtime.Serialization;

namespace SERVIDOR.Modelo
{
    [DataContract]
    public class CuentaResumen
    {
        [DataMember]
        public string CodigoCuenta { get; set; }

        [DataMember]
        public string Moneda { get; set; }

        [DataMember]
        public double Saldo { get; set; }

        [DataMember]
        public string Estado { get; set; }

        [DataMember]
        public string CodigoCliente { get; set; }

        [DataMember]
        public string NombreCliente { get; set; }

        public CuentaResumen()
        {
            CodigoCuenta = string.Empty;
            Moneda = string.Empty;
            Estado = string.Empty;
            CodigoCliente = string.Empty;
            NombreCliente = string.Empty;
        }
    }
}
