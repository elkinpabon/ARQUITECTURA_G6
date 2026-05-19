using System.Net;
using System.Text;

namespace CLIWEB.Services
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
            string resp = Post($"{_baseUrl}/WSLogin.asmx", "IniciarSesion", soap);
            return resp.Contains("<IniciarSesionResult>true</IniciarSesionResult>");
        }

        public string ClienteDeUsuario(string usuario)
        {
            string soap = $@"<soap:Envelope xmlns:xsi=""http://www.w3.org/2001/XMLSchema-instance"" xmlns:xsd=""http://www.w3.org/2001/XMLSchema"" xmlns:soap=""http://schemas.xmlsoap.org/soap/envelope/"">
  <soap:Body><ClienteDeUsuario xmlns=""http://ws.monster.edu.ec/""><usuario>{Esc(usuario)}</usuario></ClienteDeUsuario></soap:Body></soap:Envelope>";
            string resp = Post($"{_baseUrl}/WSLogin.asmx", "ClienteDeUsuario", soap);
            return Extract(resp, "ClienteDeUsuarioResult");
        }

        public Resultado Depositar(string cuenta, string monto, string moneda)
        {
            string soap = $@"<soap:Envelope xmlns:xsi=""http://www.w3.org/2001/XMLSchema-instance"" xmlns:xsd=""http://www.w3.org/2001/XMLSchema"" xmlns:soap=""http://schemas.xmlsoap.org/soap/envelope/"">
  <soap:Body><Depositar xmlns=""http://ws.monster.edu.ec/""><cuenta>{Esc(cuenta)}</cuenta><monto>{Esc(monto)}</monto><moneda>{Esc(moneda)}</moneda></Depositar></soap:Body></soap:Envelope>";
            string resp = Post($"{_baseUrl}/WSCuenta.asmx", "Depositar", soap);
            return ParseResultado(resp, "DepositarResult");
        }

        public Resultado Retirar(string cuenta, string monto, string moneda)
        {
            string soap = $@"<soap:Envelope xmlns:xsi=""http://www.w3.org/2001/XMLSchema-instance"" xmlns:xsd=""http://www.w3.org/2001/XMLSchema"" xmlns:soap=""http://schemas.xmlsoap.org/soap/envelope/"">
  <soap:Body><Retirar xmlns=""http://ws.monster.edu.ec/""><cuenta>{Esc(cuenta)}</cuenta><monto>{Esc(monto)}</monto><moneda>{Esc(moneda)}</moneda></Retirar></soap:Body></soap:Envelope>";
            string resp = Post($"{_baseUrl}/WSCuenta.asmx", "Retirar", soap);
            return ParseResultado(resp, "RetirarResult");
        }

        public Resultado ConsultarSaldo(string cuenta)
        {
            string soap = $@"<soap:Envelope xmlns:xsi=""http://www.w3.org/2001/XMLSchema-instance"" xmlns:xsd=""http://www.w3.org/2001/XMLSchema"" xmlns:soap=""http://schemas.xmlsoap.org/soap/envelope/"">
  <soap:Body><ConsultarSaldo xmlns=""http://ws.monster.edu.ec/""><cuenta>{Esc(cuenta)}</cuenta></ConsultarSaldo></soap:Body></soap:Envelope>";
            string resp = Post($"{_baseUrl}/WSCuenta.asmx", "ConsultarSaldo", soap);
            return ParseResultado(resp, "ConsultarSaldoResult");
        }

        public Resultado Transferir(string origen, string destino, string monto, string moneda)
        {
            string soap = $@"<soap:Envelope xmlns:xsi=""http://www.w3.org/2001/XMLSchema-instance"" xmlns:xsd=""http://www.w3.org/2001/XMLSchema"" xmlns:soap=""http://schemas.xmlsoap.org/soap/envelope/"">
  <soap:Body><Transferir xmlns=""http://ws.monster.edu.ec/""><origen>{Esc(origen)}</origen><destino>{Esc(destino)}</destino><monto>{Esc(monto)}</monto><moneda>{Esc(moneda)}</moneda></Transferir></soap:Body></soap:Envelope>";
            string resp = Post($"{_baseUrl}/WSCuenta.asmx", "Transferir", soap);
            return ParseResultado(resp, "TransferirResult");
        }

        public List<CuentaResumen> ListarCuentasPorCliente(string cliente)
        {
            string soap = $@"<soap:Envelope xmlns:xsi=""http://www.w3.org/2001/XMLSchema-instance"" xmlns:xsd=""http://www.w3.org/2001/XMLSchema"" xmlns:soap=""http://schemas.xmlsoap.org/soap/envelope/"">
  <soap:Body><ListarCuentasPorCliente xmlns=""http://ws.monster.edu.ec/""><cliente>{Esc(cliente)}</cliente></ListarCuentasPorCliente></soap:Body></soap:Envelope>";
            string resp = Post($"{_baseUrl}/WSCuenta.asmx", "ListarCuentasPorCliente", soap);
            return ParseCuentas(resp);
        }

        public List<ClienteResumen> ListarClientes()
        {
            string soap = @"<soap:Envelope xmlns:xsi=""http://www.w3.org/2001/XMLSchema-instance"" xmlns:xsd=""http://www.w3.org/2001/XMLSchema"" xmlns:soap=""http://schemas.xmlsoap.org/soap/envelope/"">
  <soap:Body><ListarClientes xmlns=""http://ws.monster.edu.ec/"" /></soap:Body></soap:Envelope>";
            string resp = Post($"{_baseUrl}/WSCuenta.asmx", "ListarClientes", soap);
            return ParseClientes(resp);
        }

        public Resultado RegistrarCliente(string paterno, string materno, string nombre, string dni, string ciudad, string direccion, string telefono, string email)
        {
            string soap = $@"<soap:Envelope xmlns:xsi=""http://www.w3.org/2001/XMLSchema-instance"" xmlns:xsd=""http://www.w3.org/2001/XMLSchema"" xmlns:soap=""http://schemas.xmlsoap.org/soap/envelope/"">
  <soap:Body><RegistrarCliente xmlns=""http://ws.monster.edu.ec/""><paterno>{Esc(paterno)}</paterno><materno>{Esc(materno)}</materno><nombre>{Esc(nombre)}</nombre><dni>{Esc(dni)}</dni><ciudad>{Esc(ciudad)}</ciudad><direccion>{Esc(direccion)}</direccion><telefono>{Esc(telefono)}</telefono><email>{Esc(email)}</email></RegistrarCliente></soap:Body></soap:Envelope>";
            string resp = Post($"{_baseUrl}/WSCuenta.asmx", "RegistrarCliente", soap);
            return ParseResultado(resp, "RegistrarClienteResult");
        }

        public Resultado RegistrarCuenta(string cliente, string moneda)
        {
            string soap = $@"<soap:Envelope xmlns:xsi=""http://www.w3.org/2001/XMLSchema-instance"" xmlns:xsd=""http://www.w3.org/2001/XMLSchema"" xmlns:soap=""http://schemas.xmlsoap.org/soap/envelope/"">
  <soap:Body><RegistrarCuenta xmlns=""http://ws.monster.edu.ec/""><cliente>{Esc(cliente)}</cliente><moneda>{Esc(moneda)}</moneda></RegistrarCuenta></soap:Body></soap:Envelope>";
            string resp = Post($"{_baseUrl}/WSCuenta.asmx", "RegistrarCuenta", soap);
            return ParseResultado(resp, "RegistrarCuentaResult");
        }

        public Resultado EliminarCuenta(string cuenta)
        {
            string soap = $@"<soap:Envelope xmlns:xsi=""http://www.w3.org/2001/XMLSchema-instance"" xmlns:xsd=""http://www.w3.org/2001/XMLSchema"" xmlns:soap=""http://schemas.xmlsoap.org/soap/envelope/"">
  <soap:Body><EliminarCuenta xmlns=""http://ws.monster.edu.ec/""><cuenta>{Esc(cuenta)}</cuenta></EliminarCuenta></soap:Body></soap:Envelope>";
            string resp = Post($"{_baseUrl}/WSCuenta.asmx", "EliminarCuenta", soap);
            return ParseResultado(resp, "EliminarCuentaResult");
        }

        public List<MovimientoModel> ListarMovimientos(string cuenta)
        {
            string soap = $@"<soap:Envelope xmlns:xsi=""http://www.w3.org/2001/XMLSchema-instance"" xmlns:xsd=""http://www.w3.org/2001/XMLSchema"" xmlns:soap=""http://schemas.xmlsoap.org/soap/envelope/"">
  <soap:Body><ListarMovimientos xmlns=""http://ws.monster.edu.ec/""><cuenta>{Esc(cuenta)}</cuenta></ListarMovimientos></soap:Body></soap:Envelope>";
            string resp = Post($"{_baseUrl}/WSMovimiento.asmx", "ListarMovimientos", soap);
            return ParseMovimientos(resp);
        }

        private string Post(string url, string action, string soap)
        {
            using var client = new HttpClient();
            client.Timeout = TimeSpan.FromSeconds(30);
            var content = new StringContent(soap, Encoding.UTF8, "text/xml");
            client.DefaultRequestHeaders.TryAddWithoutValidation("SOAPAction", $"\"http://ws.monster.edu.ec/{action}\"");
            var resp = client.PostAsync(url, content).GetAwaiter().GetResult();
            resp.EnsureSuccessStatusCode();
            var body = resp.Content.ReadAsStringAsync().GetAwaiter().GetResult();
            if (body.Contains("<soap:Fault>") || body.Contains("<Fault>"))
            {
                throw new InvalidOperationException("El servidor SOAP devolvió un fault.");
            }
            return body;
        }

        private string Esc(string s) => string.IsNullOrEmpty(s) ? "" : s.Replace("&", "&amp;").Replace("<", "&lt;").Replace(">", "&gt;");

        private string Extract(string resp, string tag)
        {
            string s = $"<{tag}>", e = $"</{tag}>";
            int si = resp.IndexOf(s);
            if (si < 0) return "";
            si += s.Length;
            int ei = resp.IndexOf(e, si);
            return ei < 0 ? "" : resp.Substring(si, ei - si);
        }

        private Resultado ParseResultado(string resp, string tag)
        {
            int si = resp.IndexOf($"<{tag}>");
            if (si < 0) return new Resultado { Exitoso = false, Mensaje = "Respuesta invalida" };
            int ei = resp.IndexOf($"</{tag}>", si);
            string inner = resp.Substring(si, ei - si + $"</{tag}>".Length);
            return new Resultado
            {
                Exitoso = inner.Contains("<Exitoso>true</Exitoso>"),
                Mensaje = Extract(inner, "Mensaje"),
                Saldo = double.TryParse(Extract(inner, "Saldo"), out var s) ? s : 0
            };
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
                list.Add(new CuentaResumen { CodigoCuenta = Extract(item, "CodigoCuenta"), Moneda = Extract(item, "Moneda"), Saldo = double.TryParse(Extract(item, "Saldo"), out var s) ? s : 0, Estado = Extract(item, "Estado"), CodigoCliente = Extract(item, "CodigoCliente"), NombreCliente = Extract(item, "NombreCliente") });
                p = ep + 16;
            }
            return list;
        }

        private List<ClienteResumen> ParseClientes(string resp)
        {
            var list = new List<ClienteResumen>();
            int p = 0;
            while ((p = resp.IndexOf("<ClienteResumen>", p)) >= 0)
            {
                int ep = resp.IndexOf("</ClienteResumen>", p);
                if (ep < 0) break;
                string item = resp.Substring(p, ep - p + 17);
                list.Add(new ClienteResumen { Codigo = Extract(item, "Codigo"), Dni = Extract(item, "Dni"), Nombre = Extract(item, "Nombre") });
                p = ep + 17;
            }
            return list;
        }

        private List<MovimientoModel> ParseMovimientos(string resp)
        {
            var list = new List<MovimientoModel>();
            int p = 0;
            while ((p = resp.IndexOf("<MovimientoModel>", p)) >= 0)
            {
                int ep = resp.IndexOf("</MovimientoModel>", p);
                if (ep < 0) break;
                string item = resp.Substring(p, ep - p + 18);
                list.Add(new MovimientoModel { CodigoCuenta = Extract(item, "CodigoCuenta"), NumeroMovimiento = int.TryParse(Extract(item, "NumeroMovimiento"), out var n) ? n : 0, FechaMovimiento = Extract(item, "FechaMovimiento"), CodigoEmpleado = Extract(item, "CodigoEmpleado"), CodigoTipoMovimiento = Extract(item, "CodigoTipoMovimiento"), TipoDescripcion = Extract(item, "TipoDescripcion"), ImporteMovimiento = double.TryParse(Extract(item, "ImporteMovimiento"), out var imp) ? imp : 0, CuentaReferencia = Extract(item, "CuentaReferencia"), MonedaOrigen = Extract(item, "MonedaOrigen"), ImporteOrigen = double.TryParse(Extract(item, "ImporteOrigen"), out var io) ? io : null, TasaAplicada = double.TryParse(Extract(item, "TasaAplicada"), out var ta) ? ta : null });
                p = ep + 18;
            }
            return list;
        }
    }

    public class Resultado { public bool Exitoso { get; set; } public string Mensaje { get; set; } = ""; public double Saldo { get; set; } }
    public class CuentaResumen { public string CodigoCuenta { get; set; } = ""; public string Moneda { get; set; } = ""; public double Saldo { get; set; } public string Estado { get; set; } = ""; public string CodigoCliente { get; set; } = ""; public string NombreCliente { get; set; } = ""; }
    public class ClienteResumen { public string Codigo { get; set; } = ""; public string Dni { get; set; } = ""; public string Nombre { get; set; } = ""; }
    public class MovimientoModel { public string CodigoCuenta { get; set; } = ""; public int NumeroMovimiento { get; set; } public string FechaMovimiento { get; set; } = ""; public string CodigoEmpleado { get; set; } = ""; public string CodigoTipoMovimiento { get; set; } = ""; public string TipoDescripcion { get; set; } = ""; public double ImporteMovimiento { get; set; } public string CuentaReferencia { get; set; } = ""; public string MonedaOrigen { get; set; } = ""; public double? ImporteOrigen { get; set; } public double? TasaAplicada { get; set; } }
}
