using SoapCore;
using SERVIDOR.Controlador;

var builder = WebApplication.CreateBuilder(args);

builder.Services.AddSingleton<WSLoginService>();
builder.Services.AddSingleton<WSCuentaService>();
builder.Services.AddSingleton<WSMovimientoService>();

var app = builder.Build();

app.UseRouting();

app.UseEndpoints(endpoints =>
{
    endpoints.UseSoapEndpoint<WSLoginService>("/WSLogin.asmx", new SoapEncoderOptions(), SoapSerializer.XmlSerializer);
    endpoints.UseSoapEndpoint<WSCuentaService>("/WSCuenta.asmx", new SoapEncoderOptions(), SoapSerializer.XmlSerializer);
    endpoints.UseSoapEndpoint<WSMovimientoService>("/WSMovimiento.asmx", new SoapEncoderOptions(), SoapSerializer.XmlSerializer);
});

app.MapGet("/", () => "EurekaBank SOAP Server - GR06");

app.Run("http://localhost:5000");
