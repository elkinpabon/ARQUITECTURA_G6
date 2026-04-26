using Ec.Edu.Monster.Controladores;
using Ec.Edu.Monster.Modelo;
using Microsoft.Maui.Graphics;

namespace Ec.Edu.Monster.Conuni;

public partial class ActividadConversor : ContentPage
{
    private readonly AplicacionControlador controlador = new();
    private readonly string categoria;

    public ActividadConversor(string categoria)
    {
        InitializeComponent();
        this.categoria = categoria;
        ConfigurarCategoria();
        CargarOpciones();
    }

    private void ConfigurarCategoria()
    {
        (TituloCategoria.Text, DescripcionCategoria.Text, IconoCategoria.Text) = categoria switch
        {
            "longitud" => ("Longitud", "Metros, pies, kilometros, millas, pulgadas y mas.", "↔"),
            "masa" => ("Masa", "Kilogramos, libras, gramos, onzas, toneladas y mas.", "⚖"),
            _ => ("Temperatura", "Celsius, Fahrenheit y Kelvin.", "🌡")
        };
    }

    private void CargarOpciones()
    {
        var opciones = categoria switch
        {
            "longitud" => new[]
            {
                new OpcionConversion("metrosAPies", "Metros a Pies"),
                new OpcionConversion("kilometrosAMillas", "Kilometros a Millas"),
                new OpcionConversion("centimetrosAPulgadas", "Centimetros a Pulgadas"),
                new OpcionConversion("yardasAMetros", "Yardas a Metros"),
                new OpcionConversion("milimetrosAPulgadas", "Milimetros a Pulgadas")
            },
            "masa" => new[]
            {
                new OpcionConversion("kilogramosALibras", "Kilogramos a Libras"),
                new OpcionConversion("gramosAOnzas", "Gramos a Onzas"),
                new OpcionConversion("toneladasAKilogramos", "Toneladas a Kilogramos"),
                new OpcionConversion("librasAOnzas", "Libras a Onzas"),
                new OpcionConversion("miligramosAGramos", "Miligramos a Gramos")
            },
            _ => new[]
            {
                new OpcionConversion("celsiusAFahrenheit", "Celsius a Fahrenheit"),
                new OpcionConversion("fahrenheitACelsius", "Fahrenheit a Celsius"),
                new OpcionConversion("celsiusAKelvin", "Celsius a Kelvin"),
                new OpcionConversion("kelvinACelsius", "Kelvin a Celsius"),
                new OpcionConversion("fahrenheitAKelvin", "Fahrenheit a Kelvin")
            }
        };

        SelectorConversion.ItemsSource = opciones;
        SelectorConversion.ItemDisplayBinding = new Binding(nameof(OpcionConversion.Etiqueta));
        SelectorConversion.SelectedIndex = 0;
    }

    private async void Convertir_Clicked(object sender, EventArgs e)
    {
        if (SelectorConversion.SelectedItem is not OpcionConversion opcion)
        {
            await DisplayAlert("CONUNI", "Selecciona una conversion", "OK");
            return;
        }

        if (!double.TryParse(EntradaValor.Text?.Replace(',', '.'), System.Globalization.NumberStyles.Any, System.Globalization.CultureInfo.InvariantCulture, out var valor))
        {
            await DisplayAlert("CONUNI", "Ingresa un numero valido", "OK");
            return;
        }

        Resultado resultado = categoria switch
        {
            "longitud" => controlador.ConvertirLongitud(opcion.Clave, valor),
            "masa" => controlador.ConvertirMasa(opcion.Clave, valor),
            _ => controlador.ConvertirTemperatura(opcion.Clave, valor)
        };

        CajaResultado.BackgroundColor = resultado.Exito ? Color.FromArgb("#E2FDE2") : Color.FromArgb("#FDE2E2");
        EtiquetaResultado.TextColor = resultado.Exito ? Color.FromArgb("#006B00") : Color.FromArgb("#A10000");
        EtiquetaResultado.Text = resultado.Exito ? $"Resultado: {resultado.Valor:0.00}" : resultado.Mensaje;
        CajaResultado.IsVisible = true;
    }

    private async void Volver_Clicked(object sender, EventArgs e)
    {
        await Navigation.PopAsync();
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
}
