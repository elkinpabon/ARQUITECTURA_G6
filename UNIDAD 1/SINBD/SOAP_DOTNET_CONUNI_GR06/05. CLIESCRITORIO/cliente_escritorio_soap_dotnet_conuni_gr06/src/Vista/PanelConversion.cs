using Ec.Edu.Monster.Controlador;
using System.Drawing;
using System.Globalization;
using System.IO;
using System.Windows.Forms;

namespace Ec.Edu.Monster.Vista;

public partial class PanelConversion : UserControl
{
    public event Action? VolverSolicitado;

    private readonly ControladorEscritorio controlador;
    private readonly string categoria;

    public PanelConversion(ControladorEscritorio controlador, string categoria)
    {
        this.controlador = controlador;
        this.categoria = categoria;
        InitializeComponent();
        ConstruirInterfaz();
    }

    private void ConstruirInterfaz()
    {
        BackColor = Color.White;
        panelEncabezado.BackColor = Paleta.AZUL;
        btnVolver.BackColor = Paleta.AMARILLO;
        btnVolver.ForeColor = Paleta.AZUL;
        btnVolver.FlatStyle = FlatStyle.Flat;
        btnVolver.FlatAppearance.BorderSize = 0;
        lblTituloCategoria.Font = Paleta.TITULO;
        lblTituloCategoria.ForeColor = Color.White;
        lblEncabezado.Font = new Font("SansSerif", 20, FontStyle.Bold);
        lblEncabezado.ForeColor = Paleta.AZUL;
        lblDescripcion.Font = Paleta.SUBTITULO;
        lblDescripcion.ForeColor = Paleta.TEXTO_SUAVE;
        btnConvertir.BackColor = Paleta.AZUL;
        btnConvertir.ForeColor = Color.White;
        btnConvertir.FlatStyle = FlatStyle.Flat;
        btnConvertir.FlatAppearance.BorderSize = 0;
        lblResultado.BackColor = Color.White;

        btnVolver.Click += (_, _) => VolverSolicitado?.Invoke();
        lblTituloCategoria.Text = TituloCategoria();
        lblEncabezado.Text = $"Conversiones de {TituloCategoria()}";
        lblDescripcion.Text = DescripcionCategoria();
        campoValor.KeyDown += (_, e) =>
        {
            if (e.KeyCode == Keys.Enter)
            {
                Convertir();
                e.SuppressKeyPress = true;
            }
        };
        btnConvertir.Click += (_, _) => Convertir();
        CargarOpciones();
    }

    private void CargarOpciones()
    {
        comboOperacion.Items.Clear();
        var opciones = categoria switch
        {
            "longitud" => new[]
            {
                ("metrosAPies", "Metros a Pies"),
                ("kilometrosAMillas", "Kilometros a Millas"),
                ("centimetrosAPulgadas", "Centimetros a Pulgadas"),
                ("yardasAMetros", "Yardas a Metros"),
                ("milimetrosAPulgadas", "Milimetros a Pulgadas")
            },
            "masa" => new[]
            {
                ("kilogramosALibras", "Kilogramos a Libras"),
                ("gramosAOnzas", "Gramos a Onzas"),
                ("toneladasAKilogramos", "Toneladas a Kilogramos"),
                ("librasAOnzas", "Libras a Onzas"),
                ("miligramosAGramos", "Miligramos a Gramos")
            },
            _ => new[]
            {
                ("celsiusAFahrenheit", "Celsius a Fahrenheit"),
                ("fahrenheitACelsius", "Fahrenheit a Celsius"),
                ("celsiusAKelvin", "Celsius a Kelvin"),
                ("kelvinACelsius", "Kelvin a Celsius"),
                ("fahrenheitAKelvin", "Fahrenheit a Kelvin")
            }
        };

        foreach (var opcion in opciones)
        {
            comboOperacion.Items.Add(new OpcionConversion(opcion.Item1, opcion.Item2));
        }

        comboOperacion.DisplayMember = nameof(OpcionConversion.Etiqueta);
        comboOperacion.ValueMember = nameof(OpcionConversion.Clave);
        comboOperacion.SelectedIndex = 0;
    }

    private void Convertir()
    {
        if (!double.TryParse(campoValor.Text.Replace(',', '.'), NumberStyles.Any, CultureInfo.InvariantCulture, out var valor))
        {
            lblResultado.Text = "Ingresa un numero valido";
            lblResultado.ForeColor = Color.FromArgb(0xA1, 0x00, 0x00);
            return;
        }

        if (comboOperacion.SelectedItem is not OpcionConversion opcion)
        {
            return;
        }

        var resultado = categoria switch
        {
            "longitud" => controlador.ConvertirLongitud(opcion.Clave, valor),
            "masa" => controlador.ConvertirMasa(opcion.Clave, valor),
            _ => controlador.ConvertirTemperatura(opcion.Clave, valor)
        };

        if (resultado.Exito)
        {
            lblResultado.Text = $"Resultado: {resultado.Valor:0.000}";
            lblResultado.ForeColor = Color.FromArgb(0x00, 0x6B, 0x00);
            lblResultado.BackColor = Color.FromArgb(0xE2, 0xFD, 0xE2);
        }
        else
        {
            lblResultado.Text = resultado.Mensaje;
            lblResultado.ForeColor = Color.FromArgb(0xA1, 0x00, 0x00);
            lblResultado.BackColor = Color.FromArgb(0xFD, 0xE2, 0xE2);
        }
    }

    private string TituloCategoria() => categoria switch
    {
        "longitud" => "Longitud",
        "masa" => "Masa",
        "temperatura" => "Temperatura",
        _ => categoria
    };

    private string DescripcionCategoria() => categoria switch
    {
        "longitud" => "Convierte entre metros, pies, kilometros, millas, pulgadas y mas.",
        "masa" => "Convierte entre kilogramos, libras, gramos, onzas, toneladas y mas.",
        _ => "Convierte entre Celsius, Fahrenheit y Kelvin."
    };

    private static string Ruta(string archivo)
    {
        var rutaPrincipal = Path.Combine(AppContext.BaseDirectory, "src", "img", archivo);
        if (File.Exists(rutaPrincipal))
        {
            return rutaPrincipal;
        }

        return Path.Combine(AppContext.BaseDirectory, "img", archivo);
    }

    private sealed class OpcionConversion
    {
        public OpcionConversion(string clave, string etiqueta)
        {
            Clave = clave;
            Etiqueta = etiqueta;
        }

        public string Clave { get; }

        public string Etiqueta { get; }

        public override string ToString() => Etiqueta;
    }

    private void lblEncabezado_Click(object sender, EventArgs e)
    {

    }

    private void lblDescripcion_Click(object sender, EventArgs e)
    {

    }
}
