using Ec.Edu.Monster.Controlador;
using Ec.Edu.Monster.Servicio;

var builder = WebApplication.CreateBuilder(args);
builder.WebHost.UseUrls("http://0.0.0.0:5000");

builder.Services.AddControllers();
builder.Services.AddSingleton<ServicioAutenticacion>();
builder.Services.AddSingleton<ServicioLongitud>();
builder.Services.AddSingleton<ServicioMasa>();
builder.Services.AddSingleton<ServicioTemperatura>();

var app = builder.Build();

app.MapControllers();
app.MapGet("/", () => Results.Text("CONUNI REST API activa"));

app.Run();
