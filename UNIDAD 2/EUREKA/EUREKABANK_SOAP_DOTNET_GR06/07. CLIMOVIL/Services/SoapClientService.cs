using System.Text;
using System.Globalization;

namespace CLIMOVIL.Services
{
    public class SoapClientService
    {
        private readonly string _baseUrl;

        public SoapClientService()
        {
            _baseUrl = Constantes.BaseUrl.TrimEnd('/');
        }

        public bool IniciarSesion(string usuario, string clave)
        {
            string soap = $@"<soap:Envelope xmlns:xsi=""http://www.w3.org/2001/XMLSchema-instance"" xmlns:xsd=""http://www.w3.org/2001/XMLSchema"" xmlns:soap=""http://schemas.xmlsoap.org/soap/envelope/"">
  <soap:Body><IniciarSesion xmlns=""http://ws.monster.edu.ec/""><usuario>{Esc(usuario)}</usuario><clave>{Esc(clave)}</clave></IniciarSesion></soap:Body></soap:Envelope>";
            return Post(_baseUrl + "/WSLogin.asmx", "IniciarSesion", soap).Contains("<IniciarSesionResult>true</IniciarSesionResult>");
        }

        public string ClienteDeUsuario(string usuario)
        {
            string soap = $@"<soap:Envelope xmlns:xsi=""http://www.w3.org/2001/XMLSchema-instance"" xmlns:xsd=""http://www.w3.org/2001/XMLSchema"" xmlns:soap=""http://schemas.xmlsoap.org/soap/envelope/"">
  <soap:Body><ClienteDeUsuario xmlns=""http://ws.monster.edu.ec/""><usuario>{Esc(usuario)}</usuario></ClienteDeUsuario></soap:Body></soap:Envelope>";
            return Extract(Post(_baseUrl + "/WSLogin.asmx", "ClienteDeUsuario", soap), "ClienteDeUsuarioResult");
        }

        public List<ClienteResumen> ListarClientes()
        {
            string soap = @"<soap:Envelope xmlns:xsi=""http://www.w3.org/2001/XMLSchema-instance"" xmlns:xsd=""http://www.w3.org/2001/XMLSchema"" xmlns:soap=""http://schemas.xmlsoap.org/soap/envelope/"">
  <soap:Body><ListarClientes xmlns=""http://ws.monster.edu.ec/"" /></soap:Body></soap:Envelope>";
            return ParseClientes(Post(_baseUrl + "/WSCuenta.asmx", "ListarClientes", soap));
        }

        public Resultado Depositar(string cuenta, string monto, string moneda)
        {
            string soap = $@"<soap:Envelope xmlns:xsi=""http://www.w3.org/2001/XMLSchema-instance"" xmlns:xsd=""http://www.w3.org/2001/XMLSchema"" xmlns:soap=""http://schemas.xmlsoap.org/soap/envelope/"">
  <soap:Body><Depositar xmlns=""http://ws.monster.edu.ec/""><cuenta>{Esc(cuenta)}</cuenta><monto>{Esc(NormalizeNumber(monto))}</monto><moneda>{Esc(moneda)}</moneda></Depositar></soap:Body></soap:Envelope>";
            return ParseResultado(Post(_baseUrl + "/WSCuenta.asmx", "Depositar", soap), "DepositarResult");
        }

        public Resultado Retirar(string cuenta, string monto, string moneda)
        {
            string soap = $@"<soap:Envelope xmlns:xsi=""http://www.w3.org/2001/XMLSchema-instance"" xmlns:xsd=""http://www.w3.org/2001/XMLSchema"" xmlns:soap=""http://schemas.xmlsoap.org/soap/envelope/"">
  <soap:Body><Retirar xmlns=""http://ws.monster.edu.ec/""><cuenta>{Esc(cuenta)}</cuenta><monto>{Esc(NormalizeNumber(monto))}</monto><moneda>{Esc(moneda)}</moneda></Retirar></soap:Body></soap:Envelope>";
            return ParseResultado(Post(_baseUrl + "/WSCuenta.asmx", "Retirar", soap), "RetirarResult");
        }

        public Resultado ConsultarSaldo(string cuenta)
        {
            string soap = $@"<soap:Envelope xmlns:xsi=""http://www.w3.org/2001/XMLSchema-instance"" xmlns:xsd=""http://www.w3.org/2001/XMLSchema"" xmlns:soap=""http://schemas.xmlsoap.org/soap/envelope/"">
  <soap:Body><ConsultarSaldo xmlns=""http://ws.monster.edu.ec/""><cuenta>{Esc(cuenta)}</cuenta></ConsultarSaldo></soap:Body></soap:Envelope>";
            return ParseResultado(Post(_baseUrl + "/WSCuenta.asmx", "ConsultarSaldo", soap), "ConsultarSaldoResult");
        }

        public Resultado Transferir(string origen, string destino, string monto, string moneda)
        {
            string soap = $@"<soap:Envelope xmlns:xsi=""http://www.w3.org/2001/XMLSchema-instance"" xmlns:xsd=""http://www.w3.org/2001/XMLSchema"" xmlns:soap=""http://schemas.xmlsoap.org/soap/envelope/"">
  <soap:Body><Transferir xmlns=""http://ws.monster.edu.ec/""><origen>{Esc(origen)}</origen><destino>{Esc(destino)}</destino><monto>{Esc(NormalizeNumber(monto))}</monto><moneda>{Esc(moneda)}</moneda></Transferir></soap:Body></soap:Envelope>";
            return ParseResultado(Post(_baseUrl + "/WSCuenta.asmx", "Transferir", soap), "TransferirResult");
        }

        public List<CuentaResumen> ListarCuentasPorCliente(string cliente)
        {
            string soap = $@"<soap:Envelope xmlns:xsi=""http://www.w3.org/2001/XMLSchema-instance"" xmlns:xsd=""http://www.w3.org/2001/XMLSchema"" xmlns:soap=""http://schemas.xmlsoap.org/soap/envelope/"">
  <soap:Body><ListarCuentasPorCliente xmlns=""http://ws.monster.edu.ec/""><cliente>{Esc(cliente)}</cliente></ListarCuentasPorCliente></soap:Body></soap:Envelope>";
            return ParseCuentas(Post(_baseUrl + "/WSCuenta.asmx", "ListarCuentasPorCliente", soap));
        }

        public Resultado RegistrarCliente(string paterno, string materno, string nombre, string dni, string ciudad, string direccion, string telefono, string email)
        {
            string soap = $@"<soap:Envelope xmlns:xsi=""http://www.w3.org/2001/XMLSchema-instance"" xmlns:xsd=""http://www.w3.org/2001/XMLSchema"" xmlns:soap=""http://schemas.xmlsoap.org/soap/envelope/"">
  <soap:Body><RegistrarCliente xmlns=""http://ws.monster.edu.ec/""><paterno>{Esc(paterno)}</paterno><materno>{Esc(materno)}</materno><nombre>{Esc(nombre)}</nombre><dni>{Esc(dni)}</dni><ciudad>{Esc(ciudad)}</ciudad><direccion>{Esc(direccion)}</direccion><telefono>{Esc(telefono)}</telefono><email>{Esc(email)}</email></RegistrarCliente></soap:Body></soap:Envelope>";
            return ParseResultado(Post(_baseUrl + "/WSCuenta.asmx", "RegistrarCliente", soap), "RegistrarClienteResult");
        }

        public Resultado RegistrarCuenta(string cliente, string moneda)
        {
            string soap = $@"<soap:Envelope xmlns:xsi=""http://www.w3.org/2001/XMLSchema-instance"" xmlns:xsd=""http://www.w3.org/2001/XMLSchema"" xmlns:soap=""http://schemas.xmlsoap.org/soap/envelope/"">
  <soap:Body><RegistrarCuenta xmlns=""http://ws.monster.edu.ec/""><cliente>{Esc(cliente)}</cliente><moneda>{Esc(moneda)}</moneda></RegistrarCuenta></soap:Body></soap:Envelope>";
            return ParseResultado(Post(_baseUrl + "/WSCuenta.asmx", "RegistrarCuenta", soap), "RegistrarCuentaResult");
        }

        public Resultado EliminarCuenta(string cuenta)
        {
            string soap = $@"<soap:Envelope xmlns:xsi=""http://www.w3.org/2001/XMLSchema-instance"" xmlns:xsd=""http://www.w3.org/2001/XMLSchema"" xmlns:soap=""http://schemas.xmlsoap.org/soap/envelope/"">
  <soap:Body><EliminarCuenta xmlns=""http://ws.monster.edu.ec/""><cuenta>{Esc(cuenta)}</cuenta></EliminarCuenta></soap:Body></soap:Envelope>";
            return ParseResultado(Post(_baseUrl + "/WSCuenta.asmx", "EliminarCuenta", soap), "EliminarCuentaResult");
        }

        public List<MovimientoModel> ListarMovimientos(string cuenta)
        {
            string soap = $@"<soap:Envelope xmlns:xsi=""http://www.w3.org/2001/XMLSchema-instance"" xmlns:xsd=""http://www.w3.org/2001/XMLSchema"" xmlns:soap=""http://schemas.xmlsoap.org/soap/envelope/"">
  <soap:Body><ListarMovimientos xmlns=""http://ws.monster.edu.ec/""><cuenta>{Esc(cuenta)}</cuenta></ListarMovimientos></soap:Body></soap:Envelope>";
            return ParseMovimientos(Post(_baseUrl + "/WSMovimiento.asmx", "ListarMovimientos", soap));
        }

        private string Post(string url, string action, string soap)
        {
            using var client = new HttpClient();
            client.Timeout = TimeSpan.FromSeconds(30);

            var content = new StringContent(soap, Encoding.UTF8, "text/xml");
            client.DefaultRequestHeaders.TryAddWithoutValidation("SOAPAction", $"\"http://ws.monster.edu.ec/{action}\"");

            var resp = client.PostAsync(url, content).GetAwaiter().GetResult();
            var body = resp.Content.ReadAsStringAsync().GetAwaiter().GetResult();
            if (string.IsNullOrWhiteSpace(body))
            {
                throw new InvalidOperationException($"El servidor SOAP no devolvio respuesta para {action}.");
            }

            return body;
        }

        private string Esc(string s) => string.IsNullOrEmpty(s) ? "" : s.Replace("&", "&amp;").Replace("<", "&lt;").Replace(">", "&gt;");

        private string NormalizeNumber(string s) => string.IsNullOrWhiteSpace(s) ? "" : s.Trim().Replace(',', '.');

        private string Extract(string resp, string tag)
        {
            string s = $"<{tag}>", e = $"</{tag}>";
            int si = resp.IndexOf(s, StringComparison.OrdinalIgnoreCase);
            if (si < 0) return "";
            si += s.Length;
            int ei = resp.IndexOf(e, si, StringComparison.OrdinalIgnoreCase);
            return ei < 0 ? "" : resp.Substring(si, ei - si);
        }

        private Resultado ParseResultado(string resp, string tag)
        {
            if (resp.Contains("<Fault>", StringComparison.OrdinalIgnoreCase) || resp.Contains("<soap:Fault>", StringComparison.OrdinalIgnoreCase))
            {
                return new Resultado { Exitoso = false, Mensaje = FirstNonEmpty(resp, "faultstring", "faultcode", "Mensaje") };
            }

            int si = resp.IndexOf($"<{tag}>", StringComparison.OrdinalIgnoreCase);
            int ei = resp.IndexOf($"</{tag}>", si < 0 ? 0 : si, StringComparison.OrdinalIgnoreCase);
            if (si < 0 || ei < 0 || ei <= si)
            {
                return new Resultado { Exitoso = false, Mensaje = "Respuesta invalida" };
            }

            string inner = resp.Substring(si, ei - si + $"</{tag}>".Length);
            return new Resultado
            {
                Exitoso = inner.Contains("<Exitoso>true</Exitoso>") || inner.Contains("<Exito>true</Exito>"),
                Mensaje = FirstNonEmpty(inner, "Mensaje", "mensaje"),
                Saldo = TryParseDouble(FirstNonEmpty(inner, "Saldo", "saldo"))
            };
        }

        private List<ClienteResumen> ParseClientes(string resp)
        {
            var list = new List<ClienteResumen>();
            int p = 0;
            while ((p = resp.IndexOf("<ClienteResumen>", p, StringComparison.OrdinalIgnoreCase)) >= 0)
            {
                int ep = resp.IndexOf("</ClienteResumen>", p, StringComparison.OrdinalIgnoreCase);
                if (ep < 0) break;
                string item = resp.Substring(p, ep - p + 17);
                list.Add(new ClienteResumen
                {
                    Codigo = FirstNonEmpty(item, "Codigo", "codigo"),
                    Dni = FirstNonEmpty(item, "Dni", "dni"),
                    Nombre = FirstNonEmpty(item, "Nombre", "nombre")
                });
                p = ep + 17;
            }
            return list;
        }

        private List<CuentaResumen> ParseCuentas(string resp)
        {
            var list = new List<CuentaResumen>();
            int p = 0;
            while ((p = resp.IndexOf("<CuentaResumen>", p)) >= 0)
            {
                int ep = resp.IndexOf("</CuentaResumen>", p);
                if (ep < 0) break;
                string item = resp.Substring(p, ep - p + 16);
                list.Add(new CuentaResumen
                {
                    CodigoCuenta = FirstNonEmpty(item, "CodigoCuenta", "codigoCuenta"),
                    Moneda = FirstNonEmpty(item, "Moneda", "moneda"),
                    Saldo = TryParseDouble(FirstNonEmpty(item, "Saldo", "saldo")),
                    Estado = FirstNonEmpty(item, "Estado", "estado"),
                    CodigoCliente = FirstNonEmpty(item, "CodigoCliente", "codigoCliente"),
                    NombreCliente = FirstNonEmpty(item, "NombreCliente", "nombreCliente")
                });
                p = ep + 16;
            }
            return list;
        }

        private List<MovimientoModel> ParseMovimientos(string resp)
        {
            var list = new List<MovimientoModel>();
            int p = 0;
            while ((p = resp.IndexOf("<MovimientoModel>", p, StringComparison.OrdinalIgnoreCase)) >= 0)
            {
                int ep = resp.IndexOf("</MovimientoModel>", p, StringComparison.OrdinalIgnoreCase);
                if (ep < 0) break;
                string item = resp.Substring(p, ep - p + 18);
                list.Add(new MovimientoModel
                {
                    CodigoCuenta = FirstNonEmpty(item, "CodigoCuenta", "codigoCuenta"),
                    NumeroMovimiento = int.TryParse(FirstNonEmpty(item, "NumeroMovimiento", "numeroMovimiento"), out var n) ? n : 0,
                    FechaMovimiento = FirstNonEmpty(item, "FechaMovimiento", "fechaMovimiento"),
                    CodigoEmpleado = FirstNonEmpty(item, "CodigoEmpleado", "codigoEmpleado"),
                    TipoDescripcion = FirstNonEmpty(item, "TipoDescripcion", "tipoDescripcion"),
                    CodigoTipoMovimiento = FirstNonEmpty(item, "CodigoTipoMovimiento", "codigoTipoMovimiento"),
                    CuentaReferencia = FirstNonEmpty(item, "CuentaReferencia", "cuentaReferencia"),
                    MonedaOrigen = FirstNonEmpty(item, "MonedaOrigen", "monedaOrigen"),
                    ImporteMovimiento = TryParseDouble(FirstNonEmpty(item, "ImporteMovimiento", "importeMovimiento")),
                    ImporteOrigen = TryParseNullableDouble(FirstNonEmpty(item, "ImporteOrigen", "importeOrigen")),
                    TasaAplicada = TryParseNullableDouble(FirstNonEmpty(item, "TasaAplicada", "tasaAplicada"))
                });
                p = ep + 18;
            }
            return list;
        }

        private string FirstNonEmpty(string source, params string[] tags)
        {
            foreach (var tag in tags)
            {
                var value = Extract(source, tag);
                if (!string.IsNullOrWhiteSpace(value)) return value;
            }
            return string.Empty;
        }

        private double TryParseDouble(string value)
            => double.TryParse(value, NumberStyles.Any, CultureInfo.InvariantCulture, out var s) ? s : 0;

        private double? TryParseNullableDouble(string value)
            => double.TryParse(value, NumberStyles.Any, CultureInfo.InvariantCulture, out var s) ? s : null;
    }

    public class Resultado { public bool Exitoso { get; set; } public string Mensaje { get; set; } = ""; public double Saldo { get; set; } }
    public class ClienteResumen
    {
        public string Codigo { get; set; } = "";
        public string Dni { get; set; } = "";
        public string Nombre { get; set; } = "";
        public string DisplayText => string.IsNullOrWhiteSpace(Dni)
            ? $"{Codigo} | {Nombre}"
            : $"{Codigo} | {Dni} | {Nombre}";
    }
    public class CuentaResumen
    {
        public string CodigoCuenta { get; set; } = "";
        public string Moneda { get; set; } = "";
        public double Saldo { get; set; }
        public string Estado { get; set; } = "";
        public string CodigoCliente { get; set; } = "";
        public string NombreCliente { get; set; } = "";
        public string MonedaNombre => Moneda == "01" ? "Soles" : Moneda == "02" ? "Dólares" : Moneda;
        public string DisplayText => $"{CodigoCuenta} | {MonedaNombre} | {Saldo:F2} | {Estado}";
    }
    public class MovimientoModel
    {
        private static readonly HashSet<string> Ingresos = new(StringComparer.OrdinalIgnoreCase) { "001", "003", "005", "008" };

        public string CodigoCuenta { get; set; } = "";
        public int NumeroMovimiento { get; set; }
        public string FechaMovimiento { get; set; } = "";
        public string CodigoEmpleado { get; set; } = "";
        public string TipoDescripcion { get; set; } = "";
        public string CodigoTipoMovimiento { get; set; } = "";
        public string CuentaReferencia { get; set; } = "";
        public string MonedaOrigen { get; set; } = "";
        public double ImporteMovimiento { get; set; }
        public double? ImporteOrigen { get; set; }
        public double? TasaAplicada { get; set; }
        public bool EsIngreso => Ingresos.Contains(CodigoTipoMovimiento);
        public string MonedaOrigenNombre => MonedaOrigen == "02" ? "Dólares" : MonedaOrigen == "01" ? "Soles" : MonedaOrigen;
        public string DisplayText
        {
            get
            {
                var sign = EsIngreso ? "+" : "-";
                var extra = string.IsNullOrWhiteSpace(CuentaReferencia) ? string.Empty : $"\nRef: {CuentaReferencia}";
                var conv = ImporteOrigen.HasValue && TasaAplicada.HasValue
                    ? $"\nConv: {ImporteOrigen.Value:F2} {MonedaOrigenNombre} x {TasaAplicada.Value:F4}"
                    : string.Empty;
                return $"#{NumeroMovimiento} | {FechaMovimiento}\n{TipoDescripcion}\n{sign} {ImporteMovimiento:F2}{extra}{conv}";
            }
        }
    }
}
