using Ec.Edu.Monster.Controlador;
using Ec.Edu.Monster.Servicio;

var builder = WebApplication.CreateBuilder(args);

builder.Services.AddControllersWithViews();
builder.Services.AddDistributedMemoryCache();
builder.Services.AddSingleton<ServicioAutenticacion>();
builder.Services.AddSingleton<ServicioLongitud>();
builder.Services.AddSingleton<ServicioMasa>();
builder.Services.AddSingleton<ServicioTemperatura>();
builder.Services.AddSession(options =>
{
    options.IdleTimeout = TimeSpan.FromMinutes(20);
    options.Cookie.HttpOnly = true;
    options.Cookie.IsEssential = true;
});

var app = builder.Build();

app.UseStaticFiles();
app.UseRouting();
app.UseSession();

app.MapControllerRoute(
    name: "default",
    pattern: "{controller=ControladorAutenticacion}/{action=IniciarSesion}/{id?}");

app.Run();
