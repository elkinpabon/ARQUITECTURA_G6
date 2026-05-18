using System.Runtime.Serialization;

namespace SERVIDOR.Modelo
{
    [DataContract]
    public class MovimientoModel
    {
        [DataMember]
        public string CodigoCuenta { get; set; }

        [DataMember]
        public int NumeroMovimiento { get; set; }

        [DataMember]
        public string FechaMovimiento { get; set; }

        [DataMember]
        public string CodigoEmpleado { get; set; }

        [DataMember]
        public string CodigoTipoMovimiento { get; set; }

        [DataMember]
        public string TipoDescripcion { get; set; }

        [DataMember]
        public double ImporteMovimiento { get; set; }

        [DataMember]
        public string CuentaReferencia { get; set; }

        [DataMember]
        public string MonedaOrigen { get; set; }

        [DataMember]
        public double? ImporteOrigen { get; set; }

        [DataMember]
        public double? TasaAplicada { get; set; }

        public MovimientoModel()
        {
            CodigoCuenta = string.Empty;
            FechaMovimiento = string.Empty;
            CodigoEmpleado = string.Empty;
            CodigoTipoMovimiento = string.Empty;
            TipoDescripcion = string.Empty;
            CuentaReferencia = string.Empty;
            MonedaOrigen = string.Empty;
        }
    }
}
