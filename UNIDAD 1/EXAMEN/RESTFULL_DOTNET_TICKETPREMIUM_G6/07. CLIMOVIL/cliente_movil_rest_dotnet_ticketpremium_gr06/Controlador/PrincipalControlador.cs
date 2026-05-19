using System.Collections.ObjectModel;
using System.ComponentModel;
using System.Runtime.CompilerServices;
using System.Windows.Input;
using cliente_movil_rest_dotnet_ticketpremium_gr06.Modelo;
using cliente_movil_rest_dotnet_ticketpremium_gr06.Servicios;

namespace cliente_movil_rest_dotnet_ticketpremium_gr06.Controlador;

public class PrincipalControlador : INotifyPropertyChanged
{
    private readonly ITicketPremiumApiService _servicioApi;
    private PartidoDto? _partidoSeleccionado;
    private LocalidadDto? _localidadSeleccionada;
    private int _cantidad = 1;
    private string _mensaje = "Cargando partidos...";

    public ObservableCollection<PartidoDto> Partidos { get; } = new();

    public ObservableCollection<LocalidadDto> Localidades { get; } = new();

    public ObservableCollection<ResumenVentaDto> Reportes { get; } = new();

    public ICommand ComprarCommand { get; }

    public ICommand CargarReporteCommand { get; }

    public PartidoDto? PartidoSeleccionado
    {
        get => _partidoSeleccionado;
        set
        {
            if (SetProperty(ref _partidoSeleccionado, value))
            {
                _ = CargarLocalidadesAsync();
            }
        }
    }

    public LocalidadDto? LocalidadSeleccionada
    {
        get => _localidadSeleccionada;
        set => SetProperty(ref _localidadSeleccionada, value);
    }

    public int Cantidad
    {
        get => _cantidad;
        set => SetProperty(ref _cantidad, value);
    }

    public string Mensaje
    {
        get => _mensaje;
        set => SetProperty(ref _mensaje, value);
    }

    public event PropertyChangedEventHandler? PropertyChanged;

    public PrincipalControlador(ITicketPremiumApiService servicioApi)
    {
        _servicioApi = servicioApi;
        ComprarCommand = new Command(async () => await ComprarAsync());
        CargarReporteCommand = new Command(async () => await CargarReporteAsync());
        _ = CargarPartidosAsync();
    }

    private async Task CargarPartidosAsync()
    {
        try
        {
            var partidos = await _servicioApi.ObtenerPartidosAsync();
            MainThread.BeginInvokeOnMainThread(() =>
            {
                Partidos.Clear();
                foreach (var partido in partidos)
                {
                    Partidos.Add(partido);
                }

                PartidoSeleccionado = Partidos.FirstOrDefault();
                Mensaje = Partidos.Count == 0 ? "No hay partidos disponibles." : "Partidos cargados correctamente.";
            });
        }
        catch (Exception ex)
        {
            MainThread.BeginInvokeOnMainThread(() => Mensaje = ex.Message);
        }
    }

    private async Task CargarLocalidadesAsync()
    {
        if (PartidoSeleccionado is null)
        {
            return;
        }

        try
        {
            var localidades = await _servicioApi.ObtenerLocalidadesAsync(PartidoSeleccionado.Codigo);
            MainThread.BeginInvokeOnMainThread(() =>
            {
                Localidades.Clear();
                foreach (var localidad in localidades)
                {
                    Localidades.Add(localidad);
                }

                LocalidadSeleccionada = Localidades.FirstOrDefault();
                Mensaje = $"Localidades cargadas para {PartidoSeleccionado.Descripcion}.";
            });
        }
        catch (Exception ex)
        {
            MainThread.BeginInvokeOnMainThread(() => Mensaje = ex.Message);
        }
    }

    private async Task ComprarAsync()
    {
        if (PartidoSeleccionado is null || LocalidadSeleccionada is null)
        {
            Mensaje = "Seleccione partido y localidad.";
            return;
        }

        try
        {
            var respuesta = await _servicioApi.ComprarAsync(new CompraRequest
            {
                CodigoPartido = PartidoSeleccionado.Codigo,
                CodigoLocalidad = LocalidadSeleccionada.CodigoLocalidad,
                Cantidad = Cantidad
            });

            Mensaje = $"Compra registrada. Factura #{respuesta.IdFactura}, total Q{respuesta.Total:N2}.";
            await CargarLocalidadesAsync();
            await CargarReporteAsync();
        }
        catch (Exception ex)
        {
            Mensaje = ex.Message;
        }
    }

    private async Task CargarReporteAsync()
    {
        if (PartidoSeleccionado is null)
        {
            return;
        }

        try
        {
            var reporte = await _servicioApi.ObtenerReporteAsync(PartidoSeleccionado.Codigo);
            MainThread.BeginInvokeOnMainThread(() =>
            {
                Reportes.Clear();
                foreach (var item in reporte)
                {
                    Reportes.Add(item);
                }
            });
        }
        catch (Exception ex)
        {
            MainThread.BeginInvokeOnMainThread(() => Mensaje = ex.Message);
        }
    }

    private bool SetProperty<T>(ref T backingStore, T value, [CallerMemberName] string propertyName = "")
    {
        if (EqualityComparer<T>.Default.Equals(backingStore, value))
        {
            return false;
        }

        backingStore = value;
        PropertyChanged?.Invoke(this, new PropertyChangedEventArgs(propertyName));
        return true;
    }
}
