using System.Net;
using System.Text;
using System.Xml;
using CLIESCRITORIO.Config;

namespace CLIESCRITORIO.Servicio
{
    public class SoapClient
    {
        private readonly string _loginUrl;
        private readonly string _cuentaUrl;
        private readonly string _movimientoUrl;

        public SoapClient()
        {
            _loginUrl = ServidorConfig.WsLoginUrl;
            _cuentaUrl = ServidorConfig.WsCuentaUrl;
            _movimientoUrl = ServidorConfig.WsMovimientoUrl;
        }

        public SoapClient(string baseUrl)
        {
            _loginUrl = $"{baseUrl.TrimEnd('/')}/WSLogin.asmx";
            _cuentaUrl = $"{baseUrl.TrimEnd('/')}/WSCuenta.asmx";
            _movimientoUrl = $"{baseUrl.TrimEnd('/')}/WSMovimiento.asmx";
        }

        public bool IniciarSesion(string usuario, string clave)
        {
            string soapEnvelope = $@"<?xml version=""1.0"" encoding=""utf-8""?>
<soap:Envelope xmlns:xsi=""http://www.w3.org/2001/XMLSchema-instance"" xmlns:xsd=""http://www.w3.org/2001/XMLSchema"" xmlns:soap=""http://schemas.xmlsoap.org/soap/envelope/"">
  <soap:Body>
    <IniciarSesion xmlns=""http://ws.monster.edu.ec/"">
      <usuario>{XmlEscape(usuario)}</usuario>
      <clave>{XmlEscape(clave)}</clave>
    </IniciarSesion>
  </soap:Body>
</soap:Envelope>";

            string response = SendSoapRequest(_loginUrl, "IniciarSesion", soapEnvelope);
            return response.Contains("<IniciarSesionResult>true</IniciarSesionResult>");
        }

        public string ClienteDeUsuario(string usuario)
        {
            string soapEnvelope = $@"<?xml version=""1.0"" encoding=""utf-8""?>
<soap:Envelope xmlns:xsi=""http://www.w3.org/2001/XMLSchema-instance"" xmlns:xsd=""http://www.w3.org/2001/XMLSchema"" xmlns:soap=""http://schemas.xmlsoap.org/soap/envelope/"">
  <soap:Body>
    <ClienteDeUsuario xmlns=""http://ws.monster.edu.ec/"">
      <usuario>{XmlEscape(usuario)}</usuario>
    </ClienteDeUsuario>
  </soap:Body>
</soap:Envelope>";

            string response = SendSoapRequest(_loginUrl, "ClienteDeUsuario", soapEnvelope);
            return ExtractStringValue(response, "ClienteDeUsuarioResult");
        }

        public Resultado Depositar(string cuenta, string monto, string moneda)
        {
            string soapEnvelope = $@"<?xml version=""1.0"" encoding=""utf-8""?>
<soap:Envelope xmlns:xsi=""http://www.w3.org/2001/XMLSchema-instance"" xmlns:xsd=""http://www.w3.org/2001/XMLSchema"" xmlns:soap=""http://schemas.xmlsoap.org/soap/envelope/"">
  <soap:Body>
    <Depositar xmlns=""http://ws.monster.edu.ec/"">
      <cuenta>{XmlEscape(cuenta)}</cuenta>
      <monto>{XmlEscape(monto)}</monto>
      <moneda>{XmlEscape(moneda)}</moneda>
    </Depositar>
  </soap:Body>
</soap:Envelope>";

            string response = SendSoapRequest(_cuentaUrl, "Depositar", soapEnvelope);
            return ParseResultado(response, "DepositarResult");
        }

        public Resultado Retirar(string cuenta, string monto, string moneda)
        {
            string soapEnvelope = $@"<?xml version=""1.0"" encoding=""utf-8""?>
<soap:Envelope xmlns:xsi=""http://www.w3.org/2001/XMLSchema-instance"" xmlns:xsd=""http://www.w3.org/2001/XMLSchema"" xmlns:soap=""http://schemas.xmlsoap.org/soap/envelope/"">
  <soap:Body>
    <Retirar xmlns=""http://ws.monster.edu.ec/"">
      <cuenta>{XmlEscape(cuenta)}</cuenta>
      <monto>{XmlEscape(monto)}</monto>
      <moneda>{XmlEscape(moneda)}</moneda>
    </Retirar>
  </soap:Body>
</soap:Envelope>";

            string response = SendSoapRequest(_cuentaUrl, "Retirar", soapEnvelope);
            return ParseResultado(response, "RetirarResult");
        }

        public Resultado ConsultarSaldo(string cuenta)
        {
            string soapEnvelope = $@"<?xml version=""1.0"" encoding=""utf-8""?>
<soap:Envelope xmlns:xsi=""http://www.w3.org/2001/XMLSchema-instance"" xmlns:xsd=""http://www.w3.org/2001/XMLSchema"" xmlns:soap=""http://schemas.xmlsoap.org/soap/envelope/"">
  <soap:Body>
    <ConsultarSaldo xmlns=""http://ws.monster.edu.ec/"">
      <cuenta>{XmlEscape(cuenta)}</cuenta>
    </ConsultarSaldo>
  </soap:Body>
</soap:Envelope>";

            string response = SendSoapRequest(_cuentaUrl, "ConsultarSaldo", soapEnvelope);
            return ParseResultado(response, "ConsultarSaldoResult");
        }

        public Resultado Transferir(string origen, string destino, string monto, string moneda)
        {
            string soapEnvelope = $@"<?xml version=""1.0"" encoding=""utf-8""?>
<soap:Envelope xmlns:xsi=""http://www.w3.org/2001/XMLSchema-instance"" xmlns:xsd=""http://www.w3.org/2001/XMLSchema"" xmlns:soap=""http://schemas.xmlsoap.org/soap/envelope/"">
  <soap:Body>
    <Transferir xmlns=""http://ws.monster.edu.ec/"">
      <origen>{XmlEscape(origen)}</origen>
      <destino>{XmlEscape(destino)}</destino>
      <monto>{XmlEscape(monto)}</monto>
      <moneda>{XmlEscape(moneda)}</moneda>
    </Transferir>
  </soap:Body>
</soap:Envelope>";

            string response = SendSoapRequest(_cuentaUrl, "Transferir", soapEnvelope);
            return ParseResultado(response, "TransferirResult");
        }

        public List<CuentaResumen> ListarCuentasPorCliente(string cliente)
        {
            string soapEnvelope = $@"<?xml version=""1.0"" encoding=""utf-8""?>
<soap:Envelope xmlns:xsi=""http://www.w3.org/2001/XMLSchema-instance"" xmlns:xsd=""http://www.w3.org/2001/XMLSchema"" xmlns:soap=""http://schemas.xmlsoap.org/soap/envelope/"">
  <soap:Body>
    <ListarCuentasPorCliente xmlns=""http://ws.monster.edu.ec/"">
      <cliente>{XmlEscape(cliente)}</cliente>
    </ListarCuentasPorCliente>
  </soap:Body>
</soap:Envelope>";

            string response = SendSoapRequest(_cuentaUrl, "ListarCuentasPorCliente", soapEnvelope);
            return ParseCuentaResumenList(response);
        }

        public List<ClienteResumen> ListarClientes()
        {
            string soapEnvelope = @"<?xml version=""1.0"" encoding=""utf-8""?>
<soap:Envelope xmlns:xsi=""http://www.w3.org/2001/XMLSchema-instance"" xmlns:xsd=""http://www.w3.org/2001/XMLSchema"" xmlns:soap=""http://schemas.xmlsoap.org/soap/envelope/"">
  <soap:Body>
    <ListarClientes xmlns=""http://ws.monster.edu.ec/"" />
  </soap:Body>
</soap:Envelope>";

            string response = SendSoapRequest(_cuentaUrl, "ListarClientes", soapEnvelope);
            return ParseClienteResumenList(response);
        }

        public Resultado RegistrarCliente(string paterno, string materno, string nombre, string dni, string ciudad, string direccion, string telefono, string email)
        {
            string soapEnvelope = $@"<?xml version=""1.0"" encoding=""utf-8""?>
<soap:Envelope xmlns:xsi=""http://www.w3.org/2001/XMLSchema-instance"" xmlns:xsd=""http://www.w3.org/2001/XMLSchema"" xmlns:soap=""http://schemas.xmlsoap.org/soap/envelope/"">
  <soap:Body>
    <RegistrarCliente xmlns=""http://ws.monster.edu.ec/"">
      <paterno>{XmlEscape(paterno)}</paterno>
      <materno>{XmlEscape(materno)}</materno>
      <nombre>{XmlEscape(nombre)}</nombre>
      <dni>{XmlEscape(dni)}</dni>
      <ciudad>{XmlEscape(ciudad)}</ciudad>
      <direccion>{XmlEscape(direccion)}</direccion>
      <telefono>{XmlEscape(telefono)}</telefono>
      <email>{XmlEscape(email)}</email>
    </RegistrarCliente>
  </soap:Body>
</soap:Envelope>";

            string response = SendSoapRequest(_cuentaUrl, "RegistrarCliente", soapEnvelope);
            return ParseResultado(response, "RegistrarClienteResult");
        }

        public Resultado RegistrarCuenta(string cliente, string moneda)
        {
            string soapEnvelope = $@"<?xml version=""1.0"" encoding=""utf-8""?>
<soap:Envelope xmlns:xsi=""http://www.w3.org/2001/XMLSchema-instance"" xmlns:xsd=""http://www.w3.org/2001/XMLSchema"" xmlns:soap=""http://schemas.xmlsoap.org/soap/envelope/"">
  <soap:Body>
    <RegistrarCuenta xmlns=""http://ws.monster.edu.ec/"">
      <cliente>{XmlEscape(cliente)}</cliente>
      <moneda>{XmlEscape(moneda)}</moneda>
    </RegistrarCuenta>
  </soap:Body>
</soap:Envelope>";

            string response = SendSoapRequest(_cuentaUrl, "RegistrarCuenta", soapEnvelope);
            return ParseResultado(response, "RegistrarCuentaResult");
        }

        public Resultado EliminarCuenta(string cuenta)
        {
            string soapEnvelope = $@"<?xml version=""1.0"" encoding=""utf-8""?>
<soap:Envelope xmlns:xsi=""http://www.w3.org/2001/XMLSchema-instance"" xmlns:xsd=""http://www.w3.org/2001/XMLSchema"" xmlns:soap=""http://schemas.xmlsoap.org/soap/envelope/"">
  <soap:Body>
    <EliminarCuenta xmlns=""http://ws.monster.edu.ec/"">
      <cuenta>{XmlEscape(cuenta)}</cuenta>
    </EliminarCuenta>
  </soap:Body>
</soap:Envelope>";

            string response = SendSoapRequest(_cuentaUrl, "EliminarCuenta", soapEnvelope);
            return ParseResultado(response, "EliminarCuentaResult");
        }

        public List<MovimientoModel> ListarMovimientos(string cuenta)
        {
            string soapEnvelope = $@"<?xml version=""1.0"" encoding=""utf-8""?>
<soap:Envelope xmlns:xsi=""http://www.w3.org/2001/XMLSchema-instance"" xmlns:xsd=""http://www.w3.org/2001/XMLSchema"" xmlns:soap=""http://schemas.xmlsoap.org/soap/envelope/"">
  <soap:Body>
    <ListarMovimientos xmlns=""http://ws.monster.edu.ec/"">
      <cuenta>{XmlEscape(cuenta)}</cuenta>
    </ListarMovimientos>
  </soap:Body>
</soap:Envelope>";

            string response = SendSoapRequest(_movimientoUrl, "ListarMovimientos", soapEnvelope);
            return ParseMovimientoList(response);
        }

        private string SendSoapRequest(string url, string action, string soapEnvelope)
        {
            using var client = new HttpClient();
            client.Timeout = TimeSpan.FromSeconds(30);

            var content = new StringContent(soapEnvelope, Encoding.UTF8, "text/xml");
            client.DefaultRequestHeaders.TryAddWithoutValidation("SOAPAction", $"\"http://ws.monster.edu.ec/{action}\"");

            var response = client.PostAsync(url, content).Result;
            return response.Content.ReadAsStringAsync().Result;
        }

        private string XmlEscape(string s)
        {
            if (string.IsNullOrEmpty(s)) return string.Empty;
            return s.Replace("&", "&amp;").Replace("<", "&lt;").Replace(">", "&gt;").Replace("\"", "&quot;").Replace("'", "&apos;");
        }

        private string ExtractStringValue(string response, string tagName)
        {
            string startTag = $"<{tagName}>";
            string endTag = $"</{tagName}>";
            int start = response.IndexOf(startTag);
            if (start < 0) return string.Empty;
            start += startTag.Length;
            int end = response.IndexOf(endTag, start);
            if (end < 0) return string.Empty;
            return response.Substring(start, end - start);
        }

        private Resultado ParseResultado(string response, string tagName)
        {
            var result = new Resultado();

            int startIdx = response.IndexOf($"<{tagName}>");
            if (startIdx < 0)
            {
                result.Exitoso = false;
                result.Mensaje = "Respuesta invalida del servidor";
                return result;
            }

            string content = ExtractStringValue(response, tagName);

            result.Exitoso = content.Contains("<Exitoso>true</Exitoso>");
            result.Mensaje = ExtractStringValue(content, "Mensaje");
            string saldoStr = ExtractStringValue(content, "Saldo");
            result.Saldo = double.TryParse(saldoStr, out double s) ? s : 0.0;

            return result;
        }

        private List<CuentaResumen> ParseCuentaResumenList(string response)
        {
            var lista = new List<CuentaResumen>();
            int pos = 0;
            while ((pos = response.IndexOf("<CuentaResumen>", pos)) >= 0)
            {
                int endPos = response.IndexOf("</CuentaResumen>", pos);
                if (endPos < 0) break;
                string item = response.Substring(pos, endPos - pos + 16);

                lista.Add(new CuentaResumen
                {
                    CodigoCuenta = ExtractStringValue(item, "CodigoCuenta"),
                    Moneda = ExtractStringValue(item, "Moneda"),
                    Saldo = double.TryParse(ExtractStringValue(item, "Saldo"), out var s) ? s : 0.0,
                    Estado = ExtractStringValue(item, "Estado"),
                    CodigoCliente = ExtractStringValue(item, "CodigoCliente"),
                    NombreCliente = ExtractStringValue(item, "NombreCliente")
                });
                pos = endPos + 16;
            }
            return lista;
        }

        private List<ClienteResumen> ParseClienteResumenList(string response)
        {
            var lista = new List<ClienteResumen>();
            int pos = 0;
            while ((pos = response.IndexOf("<ClienteResumen>", pos)) >= 0)
            {
                int endPos = response.IndexOf("</ClienteResumen>", pos);
                if (endPos < 0) break;
                string item = response.Substring(pos, endPos - pos + 17);

                lista.Add(new ClienteResumen
                {
                    Codigo = ExtractStringValue(item, "Codigo"),
                    Dni = ExtractStringValue(item, "Dni"),
                    Nombre = ExtractStringValue(item, "Nombre")
                });
                pos = endPos + 17;
            }
            return lista;
        }

        private List<MovimientoModel> ParseMovimientoList(string response)
        {
            var lista = new List<MovimientoModel>();
            int pos = 0;
            while ((pos = response.IndexOf("<MovimientoModel>", pos)) >= 0)
            {
                int endPos = response.IndexOf("</MovimientoModel>", pos);
                if (endPos < 0) break;
                string item = response.Substring(pos, endPos - pos + 18);

                string importeOrigenStr = ExtractStringValue(item, "ImporteOrigen");
                string tasaAplicadaStr = ExtractStringValue(item, "TasaAplicada");

                lista.Add(new MovimientoModel
                {
                    CodigoCuenta = ExtractStringValue(item, "CodigoCuenta"),
                    NumeroMovimiento = int.TryParse(ExtractStringValue(item, "NumeroMovimiento"), out var n) ? n : 0,
                    FechaMovimiento = ExtractStringValue(item, "FechaMovimiento"),
                    CodigoEmpleado = ExtractStringValue(item, "CodigoEmpleado"),
                    CodigoTipoMovimiento = ExtractStringValue(item, "CodigoTipoMovimiento"),
                    TipoDescripcion = ExtractStringValue(item, "TipoDescripcion"),
                    ImporteMovimiento = double.TryParse(ExtractStringValue(item, "ImporteMovimiento"), out var imp) ? imp : 0.0,
                    CuentaReferencia = ExtractStringValue(item, "CuentaReferencia"),
                    MonedaOrigen = ExtractStringValue(item, "MonedaOrigen"),
                    ImporteOrigen = string.IsNullOrEmpty(importeOrigenStr) ? null : double.TryParse(importeOrigenStr, out var io) ? io : null,
                    TasaAplicada = string.IsNullOrEmpty(tasaAplicadaStr) ? null : double.TryParse(tasaAplicadaStr, out var ta) ? ta : null
                });
                pos = endPos + 18;
            }
            return lista;
        }
    }

    public class Resultado
    {
        public bool Exitoso { get; set; }
        public string Mensaje { get; set; } = string.Empty;
        public double Saldo { get; set; }
    }

    public class CuentaResumen
    {
        public string CodigoCuenta { get; set; } = string.Empty;
        public string Moneda { get; set; } = string.Empty;
        public double Saldo { get; set; }
        public string Estado { get; set; } = string.Empty;
        public string CodigoCliente { get; set; } = string.Empty;
        public string NombreCliente { get; set; } = string.Empty;
    }

    public class ClienteResumen
    {
        public string Codigo { get; set; } = string.Empty;
        public string Dni { get; set; } = string.Empty;
        public string Nombre { get; set; } = string.Empty;
    }

    public class MovimientoModel
    {
        public string CodigoCuenta { get; set; } = string.Empty;
        public int NumeroMovimiento { get; set; }
        public string FechaMovimiento { get; set; } = string.Empty;
        public string CodigoEmpleado { get; set; } = string.Empty;
        public string CodigoTipoMovimiento { get; set; } = string.Empty;
        public string TipoDescripcion { get; set; } = string.Empty;
        public double ImporteMovimiento { get; set; }
        public string CuentaReferencia { get; set; } = string.Empty;
        public string MonedaOrigen { get; set; } = string.Empty;
        public double? ImporteOrigen { get; set; }
        public double? TasaAplicada { get; set; }
    }
}
