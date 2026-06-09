using System.Runtime.Serialization;

namespace SERVIDOR.Modelo
{
    [DataContract]
    public class Resultado
    {
        [DataMember]
        public bool Exitoso { get; set; }

        [DataMember]
        public string Mensaje { get; set; }

        [DataMember]
        public double Saldo { get; set; }

        public Resultado()
        {
            Exitoso = false;
            Mensaje = string.Empty;
            Saldo = 0.0;
        }

        public static Resultado Ok(string mensaje, double saldo = 0.0)
        {
            return new Resultado { Exitoso = true, Mensaje = mensaje, Saldo = saldo };
        }

        public static Resultado Error(string mensaje)
        {
            return new Resultado { Exitoso = false, Mensaje = mensaje, Saldo = 0.0 };
        }
    }
}
