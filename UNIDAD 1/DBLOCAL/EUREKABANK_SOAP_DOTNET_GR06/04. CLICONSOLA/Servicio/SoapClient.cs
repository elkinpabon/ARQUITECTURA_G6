using System.Net.Http;
using System.Text;
using System.Xml.Linq;
using CLICONSOLA.Config;
using CLICONSOLA.Servicio;

namespace CLICONSOLA.Servicio
{
    public static class SoapClient
    {
        private static readonly HttpClient _http = new HttpClient();
        private const string Ns = "http://ws.monster.edu.ec/";

        private static string Envelope(string body)
        {
            return $@"<?xml version=""1.0"" encoding=""utf-8""?>
<soap:Envelope xmlns:xsi=""http://www.w3.org/2001/XMLSchema-instance"" xmlns:xsd=""http://www.w3.org/2001/XMLSchema"" xmlns:soap=""http://schemas.xmlsoap.org/soap/envelope/"">
  <soap:Body>
    {body}
  </soap:Body>
</soap:Envelope>";
        }

        private static async Task<string> SendAsync(string url, string action, string bodyXml)
        {
            var envelope = Envelope(bodyXml);
            var content = new StringContent(envelope, Encoding.UTF8, "text/xml");
            var request = new HttpRequestMessage(HttpMethod.Post, url)
            {
                Content = content
            };
            request.Headers.TryAddWithoutValidation("SOAPAction", $"\"{Ns}{action}\"");

            var response = await _http.SendAsync(request);
            response.EnsureSuccessStatusCode();
            return await response.Content.ReadAsStringAsync();
        }

        private static XElement GetBody(string responseXml)
        {
            var doc = XDocument.Parse(responseXml);
            XNamespace soap = "http://schemas.xmlsoap.org/soap/envelope/";
            var body = doc.Root?.Element(soap + "Body");
            return body?.Elements().FirstOrDefault() ?? throw new Exception("Respuesta SOAP vacia");
        }

        private static bool GetBoolResult(string responseXml)
        {
            var element = GetBody(responseXml);
            return bool.Parse(element.Value);
        }

        private static string GetStringResult(string responseXml)
        {
            var element = GetBody(responseXml);
            return element.Value ?? string.Empty;
        }

        private static Resultado GetResultado(string responseXml)
        {
            var element = GetBody(responseXml);
            return new Resultado
            {
                Exitoso = bool.Parse(element.Element("Exitoso")?.Value ?? "false"),
                Mensaje = element.Element("Mensaje")?.Value ?? string.Empty,
                Saldo = double.TryParse(element.Element("Saldo")?.Value, out var s) ? s : 0
            };
        }

        private static List<CuentaResumen> GetCuentas(string responseXml)
        {
            var element = GetBody(responseXml);
            var list = new List<CuentaResumen>();
            foreach (var item in element.Elements())
            {
                list.Add(new CuentaResumen
                {
                    CodigoCuenta = item.Element("CodigoCuenta")?.Value ?? string.Empty,
                    Moneda = item.Element("Moneda")?.Value ?? string.Empty,
                    Saldo = double.TryParse(item.Element("Saldo")?.Value, out var s) ? s : 0,
                    Estado = item.Element("Estado")?.Value ?? string.Empty,
                    CodigoCliente = item.Element("CodigoCliente")?.Value ?? string.Empty,
                    NombreCliente = item.Element("NombreCliente")?.Value ?? string.Empty
                });
            }
            return list;
        }

        private static List<ClienteResumen> GetClientes(string responseXml)
        {
            var element = GetBody(responseXml);
            var list = new List<ClienteResumen>();
            foreach (var item in element.Elements())
            {
                list.Add(new ClienteResumen
                {
                    Codigo = item.Element("Codigo")?.Value ?? string.Empty,
                    Dni = item.Element("Dni")?.Value ?? string.Empty,
                    Nombre = item.Element("Nombre")?.Value ?? string.Empty
                });
            }
            return list;
        }

        private static List<MovimientoModel> GetMovimientos(string responseXml)
        {
            var element = GetBody(responseXml);
            var list = new List<MovimientoModel>();
            foreach (var item in element.Elements())
            {
                list.Add(new MovimientoModel
                {
                    CodigoCuenta = item.Element("CodigoCuenta")?.Value ?? string.Empty,
                    NumeroMovimiento = int.TryParse(item.Element("NumeroMovimiento")?.Value, out var nm) ? nm : 0,
                    FechaMovimiento = item.Element("FechaMovimiento")?.Value ?? string.Empty,
                    CodigoEmpleado = item.Element("CodigoEmpleado")?.Value ?? string.Empty,
                    CodigoTipoMovimiento = item.Element("CodigoTipoMovimiento")?.Value ?? string.Empty,
                    TipoDescripcion = item.Element("TipoDescripcion")?.Value ?? string.Empty,
                    ImporteMovimiento = double.TryParse(item.Element("ImporteMovimiento")?.Value, out var im) ? im : 0,
                    CuentaReferencia = item.Element("CuentaReferencia")?.Value ?? string.Empty,
                    MonedaOrigen = item.Element("MonedaOrigen")?.Value ?? string.Empty,
                    ImporteOrigen = double.TryParse(item.Element("ImporteOrigen")?.Value, out var io) ? io : null,
                    TasaAplicada = double.TryParse(item.Element("TasaAplicada")?.Value, out var ta) ? ta : null
                });
            }
            return list;
        }

        public static bool IniciarSesion(string usuario, string clave)
        {
            var body = $"<IniciarSesion xmlns=\"{Ns}\"><usuario>{usuario}</usuario><clave>{clave}</clave></IniciarSesion>";
            var response = SendAsync(ServidorConfig.WsLoginUrl, "IniciarSesion", body).Result;
            return GetBoolResult(response);
        }

        public static string ClienteDeUsuario(string usuario)
        {
            var body = $"<ClienteDeUsuario xmlns=\"{Ns}\"><usuario>{usuario}</usuario></ClienteDeUsuario>";
            var response = SendAsync(ServidorConfig.WsLoginUrl, "ClienteDeUsuario", body).Result;
            return GetStringResult(response);
        }

        public static Resultado Depositar(string cuenta, string monto, string moneda)
        {
            var body = $"<Depositar xmlns=\"{Ns}\"><cuenta>{cuenta}</cuenta><monto>{monto}</monto><moneda>{moneda}</moneda></Depositar>";
            var response = SendAsync(ServidorConfig.WsCuentaUrl, "Depositar", body).Result;
            return GetResultado(response);
        }

        public static Resultado Retirar(string cuenta, string monto, string moneda)
        {
            var body = $"<Retirar xmlns=\"{Ns}\"><cuenta>{cuenta}</cuenta><monto>{monto}</monto><moneda>{moneda}</moneda></Retirar>";
            var response = SendAsync(ServidorConfig.WsCuentaUrl, "Retirar", body).Result;
            return GetResultado(response);
        }

        public static Resultado ConsultarSaldo(string cuenta)
        {
            var body = $"<ConsultarSaldo xmlns=\"{Ns}\"><cuenta>{cuenta}</cuenta></ConsultarSaldo>";
            var response = SendAsync(ServidorConfig.WsCuentaUrl, "ConsultarSaldo", body).Result;
            return GetResultado(response);
        }

        public static Resultado Transferir(string origen, string destino, string monto, string moneda)
        {
            var body = $"<Transferir xmlns=\"{Ns}\"><origen>{origen}</origen><destino>{destino}</destino><monto>{monto}</monto><moneda>{moneda}</moneda></Transferir>";
            var response = SendAsync(ServidorConfig.WsCuentaUrl, "Transferir", body).Result;
            return GetResultado(response);
        }

        public static List<CuentaResumen> ListarCuentasPorCliente(string cliente)
        {
            var body = $"<ListarCuentasPorCliente xmlns=\"{Ns}\"><cliente>{cliente}</cliente></ListarCuentasPorCliente>";
            var response = SendAsync(ServidorConfig.WsCuentaUrl, "ListarCuentasPorCliente", body).Result;
            return GetCuentas(response);
        }

        public static List<ClienteResumen> ListarClientes()
        {
            var body = $"<ListarClientes xmlns=\"{Ns}\" />";
            var response = SendAsync(ServidorConfig.WsCuentaUrl, "ListarClientes", body).Result;
            return GetClientes(response);
        }

        public static Resultado RegistrarCliente(string paterno, string materno, string nombre, string dni, string ciudad, string direccion, string telefono, string email)
        {
            var body = $"<RegistrarCliente xmlns=\"{Ns}\"><paterno>{paterno}</paterno><materno>{materno}</materno><nombre>{nombre}</nombre><dni>{dni}</dni><ciudad>{ciudad}</ciudad><direccion>{direccion}</direccion><telefono>{telefono}</telefono><email>{email}</email></RegistrarCliente>";
            var response = SendAsync(ServidorConfig.WsCuentaUrl, "RegistrarCliente", body).Result;
            return GetResultado(response);
        }

        public static Resultado RegistrarCuenta(string cliente, string moneda)
        {
            var body = $"<RegistrarCuenta xmlns=\"{Ns}\"><cliente>{cliente}</cliente><moneda>{moneda}</moneda></RegistrarCuenta>";
            var response = SendAsync(ServidorConfig.WsCuentaUrl, "RegistrarCuenta", body).Result;
            return GetResultado(response);
        }

        public static Resultado EliminarCuenta(string cuenta)
        {
            var body = $"<EliminarCuenta xmlns=\"{Ns}\"><cuenta>{cuenta}</cuenta></EliminarCuenta>";
            var response = SendAsync(ServidorConfig.WsCuentaUrl, "EliminarCuenta", body).Result;
            return GetResultado(response);
        }

        public static List<MovimientoModel> ListarMovimientos(string cuenta)
        {
            var body = $"<ListarMovimientos xmlns=\"{Ns}\"><cuenta>{cuenta}</cuenta></ListarMovimientos>";
            var response = SendAsync(ServidorConfig.WsMovimientoUrl, "ListarMovimientos", body).Result;
            return GetMovimientos(response);
        }
    }
}
