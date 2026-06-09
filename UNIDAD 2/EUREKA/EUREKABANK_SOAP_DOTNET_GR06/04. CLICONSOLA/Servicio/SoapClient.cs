using System.Net.Http;
using System.Text;
using System.Xml.Linq;
using System.Globalization;
using System.Linq;
using CLICONSOLA.Config;
using CLICONSOLA.Servicio;

namespace CLICONSOLA.Servicio
{
    public static class SoapClient
    {
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
            using var client = new HttpClient();
            client.Timeout = TimeSpan.FromSeconds(30);

            var envelope = Envelope(bodyXml);
            var content = new StringContent(envelope, Encoding.UTF8, "text/xml");
            client.DefaultRequestHeaders.TryAddWithoutValidation("SOAPAction", $"\"{Ns}{action}\"");

            var response = await client.PostAsync(url, content);
            response.EnsureSuccessStatusCode();
            var body = await response.Content.ReadAsStringAsync();
            if (body.Contains("<soap:Fault>") || body.Contains("<Fault>"))
            {
                throw new InvalidOperationException("El servidor SOAP devolvió un fault.");
            }
            return body;
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
            return bool.TryParse(element.Value, out var value) && value;
        }

        private static string GetStringResult(string responseXml)
        {
            var element = GetBody(responseXml);
            return element.Value?.Trim() ?? string.Empty;
        }

        private static Resultado GetResultado(string responseXml)
        {
            var element = GetBody(responseXml);
            return new Resultado
            {
                Exitoso = bool.TryParse(ValueByLocalName(element, "Exitoso"), out var exitoso) && exitoso,
                Mensaje = ValueByLocalName(element, "Mensaje"),
                Saldo = double.TryParse(ValueByLocalName(element, "Saldo"), NumberStyles.Any, CultureInfo.InvariantCulture, out var s) ? s : 0
            };
        }

        private static List<CuentaResumen> GetCuentas(string responseXml)
        {
            var element = GetBody(responseXml);
            var list = new List<CuentaResumen>();
            var items = element.Descendants().Where(x => x.Name.LocalName == "CuentaResumen").ToList();

            foreach (var item in items)
            {
                list.Add(new CuentaResumen
                {
                    CodigoCuenta = ValueByLocalName(item, "CodigoCuenta"),
                    Moneda = ValueByLocalName(item, "Moneda"),
                    Saldo = double.TryParse(ValueByLocalName(item, "Saldo"), NumberStyles.Any, CultureInfo.InvariantCulture, out var s) ? s : 0,
                    Estado = ValueByLocalName(item, "Estado"),
                    CodigoCliente = ValueByLocalName(item, "CodigoCliente"),
                    NombreCliente = ValueByLocalName(item, "NombreCliente")
                });
            }
            return list;
        }

        private static List<ClienteResumen> GetClientes(string responseXml)
        {
            var element = GetBody(responseXml);
            var list = new List<ClienteResumen>();
            var items = element.Descendants().Where(x => x.Name.LocalName == "ClienteResumen").ToList();

            foreach (var item in items)
            {
                list.Add(new ClienteResumen
                {
                    Codigo = ValueByLocalName(item, "Codigo"),
                    Dni = ValueByLocalName(item, "Dni"),
                    Nombre = ValueByLocalName(item, "Nombre")
                });
            }
            return list;
        }

        private static List<MovimientoModel> GetMovimientos(string responseXml)
        {
            var element = GetBody(responseXml);
            var list = new List<MovimientoModel>();
            var items = element.Descendants().Where(x => x.Name.LocalName == "MovimientoModel").ToList();

            foreach (var item in items)
            {
                list.Add(new MovimientoModel
                {
                    CodigoCuenta = ValueByLocalName(item, "CodigoCuenta"),
                    NumeroMovimiento = int.TryParse(ValueByLocalName(item, "NumeroMovimiento"), out var nm) ? nm : 0,
                    FechaMovimiento = ValueByLocalName(item, "FechaMovimiento"),
                    CodigoEmpleado = ValueByLocalName(item, "CodigoEmpleado"),
                    CodigoTipoMovimiento = ValueByLocalName(item, "CodigoTipoMovimiento"),
                    TipoDescripcion = ValueByLocalName(item, "TipoDescripcion"),
                    ImporteMovimiento = double.TryParse(ValueByLocalName(item, "ImporteMovimiento"), NumberStyles.Any, CultureInfo.InvariantCulture, out var im) ? im : 0,
                    CuentaReferencia = ValueByLocalName(item, "CuentaReferencia"),
                    MonedaOrigen = ValueByLocalName(item, "MonedaOrigen"),
                    ImporteOrigen = double.TryParse(ValueByLocalName(item, "ImporteOrigen"), NumberStyles.Any, CultureInfo.InvariantCulture, out var io) ? io : null,
                    TasaAplicada = double.TryParse(ValueByLocalName(item, "TasaAplicada"), NumberStyles.Any, CultureInfo.InvariantCulture, out var ta) ? ta : null
                });
            }
            return list;
        }

        private static string ValueByLocalName(XElement parent, string localName)
        {
            return parent.Descendants().FirstOrDefault(x => x.Name.LocalName == localName)?.Value ?? string.Empty;
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
