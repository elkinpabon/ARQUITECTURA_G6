using System.Net.Http.Json;
using System.Text.Json;
using Ec.Edu.Monster.Modelo;

namespace Ec.Edu.Monster.Utils;

public class ClienteRest
{
    private static readonly JsonSerializerOptions OpcionesJson = new()
    {
        PropertyNameCaseInsensitive = true
    };

    private readonly HttpClient cliente = new();

    public string DireccionServicio { get; set; } = ConstantesRest.DireccionServicio;

    public Resultado IniciarSesion(string usuario, string contrasena)
        => IniciarSesionAsync(usuario, contrasena).GetAwaiter().GetResult();

    public Resultado Convertir(string categoria, string operacion, double valor)
        => ConvertirAsync(categoria, operacion, valor).GetAwaiter().GetResult();

    public async Task<Resultado> IniciarSesionAsync(string usuario, string contrasena)
    {
        try
        {
            var respuesta = await cliente.PostAsJsonAsync(
                ConstruirUrl("login"),
                new Credencial { Usuario = usuario, Contrasena = contrasena }).ConfigureAwait(false);

            return await LeerResultado(respuesta, "Sesion REST").ConfigureAwait(false);
        }
        catch (Exception ex)
        {
            return new Resultado { Exito = false, Mensaje = $"No se pudo conectar al servidor REST: {ex.Message}" };
        }
    }

    public async Task<Resultado> ConvertirAsync(string categoria, string operacion, double valor)
    {
        try
        {
            var url = ConstruirUrl($"conversion/{categoria}/{operacion}?valor={Uri.EscapeDataString(valor.ToString(System.Globalization.CultureInfo.InvariantCulture))}");
            var respuesta = await cliente.GetAsync(url).ConfigureAwait(false);
            return await LeerResultado(respuesta, "Conversion REST").ConfigureAwait(false);
        }
        catch (Exception ex)
        {
            return new Resultado { Exito = false, Mensaje = $"No se pudo conectar al servidor REST: {ex.Message}" };
        }
    }

    private async Task<Resultado> LeerResultado(HttpResponseMessage respuesta, string contexto)
    {
        if (!respuesta.IsSuccessStatusCode)
        {
            return new Resultado { Exito = false, Mensaje = $"{contexto}: HTTP {(int)respuesta.StatusCode}" };
        }

        var resultado = await respuesta.Content.ReadFromJsonAsync<Resultado>(OpcionesJson).ConfigureAwait(false);
        return resultado ?? new Resultado { Exito = false, Mensaje = $"{contexto}: respuesta invalida" };
    }

    private string ConstruirUrl(string ruta)
    {
        return $"{DireccionServicio.TrimEnd('/')}/{ruta}";
    }
}
