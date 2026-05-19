using cliente_escritorio_rest_dotnet_ticketpremium_gr06.Modelo;
using cliente_escritorio_rest_dotnet_ticketpremium_gr06.Controlador;

namespace cliente_escritorio_rest_dotnet_ticketpremium_gr06.Vista;

public partial class FrmPrincipal : Form
{
    private const string ApiBaseUrl = "https://localhost:44348/";

    private readonly ClienteTicketPremiumControlador _controlador = new(ApiBaseUrl);
    private readonly string _usuarioActual;

    public FrmPrincipal(string usuarioActual)
    {
        _usuarioActual = usuarioActual;
        InitializeComponent();
        Text = $"TicketPremium - Partidos Ecuatorianos | {usuarioActual}";
        Shown += async (_, _) => await CargarTodoAsync();
    }

    private async Task CargarTodoAsync()
    {
        await CargarPartidosAsync();
        await CargarReporteAsync();
        LblUsuarioActual.Text = $"Sesión: {_usuarioActual}";
    }

    private async Task CargarPartidosAsync()
    {
        var partidos = await _controlador.ObtenerPartidosAsync();
        BindingPartidos.DataSource = partidos.ToList();
        CboPartidos.DataSource = partidos.ToList();
        CboPartidos.DisplayMember = nameof(PartidoDto.Descripcion);
        CboPartidos.ValueMember = nameof(PartidoDto.Codigo);
        CboReporte.DataSource = partidos.ToList();
        CboReporte.DisplayMember = nameof(PartidoDto.Descripcion);
        CboReporte.ValueMember = nameof(PartidoDto.Codigo);
        GridPartidos.DataSource = BindingPartidos;
        await CargarLocalidadesAsync();
    }

    private PartidoDto? PartidoSeleccionado => CboPartidos.SelectedItem as PartidoDto;

    private async Task CargarLocalidadesAsync()
    {
        if (PartidoSeleccionado is null)
        {
            BindingLocalidades.DataSource = new List<LocalidadDto>();
            GridLocalidades.DataSource = BindingLocalidades;
            CboLocalidades.DataSource = new List<LocalidadDto>();
            return;
        }

        var localidades = await _controlador.ObtenerLocalidadesAsync(PartidoSeleccionado.Codigo);
        BindingLocalidades.DataSource = localidades.ToList();
        GridLocalidades.DataSource = BindingLocalidades;
        CboLocalidades.DataSource = localidades.ToList();
        CboLocalidades.DisplayMember = nameof(LocalidadDto.Descripcion);
        CboLocalidades.ValueMember = nameof(LocalidadDto.CodigoLocalidad);
    }

    private async Task CargarReporteAsync()
    {
        if (CboReporte.SelectedItem is not PartidoDto partido)
        {
            BindingReporte.DataSource = new List<ResumenVentaDto>();
            GridReporte.DataSource = BindingReporte;
            return;
        }

        var reporte = await _controlador.ObtenerReporteAsync(partido.Codigo);
        BindingReporte.DataSource = reporte.ToList();
        GridReporte.DataSource = BindingReporte;
    }

    private async void BtnRefrescarPartidos_Click(object sender, EventArgs e)
    {
        await CargarPartidosAsync();
    }

    private async void GridPartidos_SelectionChanged(object sender, EventArgs e)
    {
        if (GridPartidos.CurrentRow?.DataBoundItem is PartidoDto partido)
        {
            CboPartidos.SelectedItem = partido;
            await CargarLocalidadesAsync();
        }
    }

    private async void CboPartidos_SelectedIndexChanged(object sender, EventArgs e)
    {
        await CargarLocalidadesAsync();
    }

    private async void BtnComprar_Click(object sender, EventArgs e)
    {
        if (PartidoSeleccionado is null || CboLocalidades.SelectedItem is not LocalidadDto localidad)
        {
            LblEstado.Text = "Seleccione un partido y una localidad.";
            return;
        }

        try
        {
            var respuesta = await _controlador.ComprarAsync(new CompraRequest
            {
                CodigoPartido = PartidoSeleccionado.Codigo,
                CodigoLocalidad = localidad.CodigoLocalidad,
                Cantidad = (int)NudCantidad.Value
            });

            LblEstado.Text = $"Compra registrada. Factura #{respuesta.IdFactura}. Total Q{respuesta.Total:N2}.";
            await CargarPartidosAsync();
            await CargarReporteAsync();
        }
        catch (Exception ex)
        {
            LblEstado.Text = ex.Message;
        }
    }

    private async void BtnCargarReporte_Click(object sender, EventArgs e)
    {
        await CargarReporteAsync();
    }

    protected override void OnFormClosed(FormClosedEventArgs e)
    {
        _controlador.Dispose();
        base.OnFormClosed(e);
    }
}
