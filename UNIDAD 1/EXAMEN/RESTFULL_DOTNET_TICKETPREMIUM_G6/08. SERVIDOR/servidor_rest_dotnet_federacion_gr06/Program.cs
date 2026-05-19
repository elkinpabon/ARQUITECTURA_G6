using Microsoft.EntityFrameworkCore;
using servidor_rest_dotnet_federacion_gr06.Datos;
using servidor_rest_dotnet_federacion_gr06.Servicios;
using servidor_rest_dotnet_federacion_gr06.Servicios.Interfaces;

var builder = WebApplication.CreateBuilder(args);

builder.Services.AddControllers();
builder.Services.AddDbContext<ContextoAplicacion>(options =>
{
    var connectionString = builder.Configuration.GetConnectionString("Federacion")
        ?? throw new InvalidOperationException("Falta la cadena de conexion Federacion.");

    options.UseSqlServer(connectionString);
});

builder.Services.AddScoped<IServicioPartido, ServicioPartido>();
builder.Services.AddScoped<IServicioLocalidad, ServicioLocalidad>();
builder.Services.AddScoped<IServicioVenta, ServicioVenta>();
builder.Services.AddScoped<IServicioReporte, ServicioReporte>();
builder.Services.AddScoped<IServicioAutenticacion, ServicioAutenticacion>();

builder.Services.AddCors(options =>
{
    options.AddPolicy("ClientesTicketPremium", policy =>
        policy.AllowAnyOrigin()
              .AllowAnyHeader()
              .AllowAnyMethod());
});

builder.Services.AddEndpointsApiExplorer();
builder.Services.AddSwaggerGen();

var app = builder.Build();

using (var scope = app.Services.CreateScope())
{
    var db = scope.ServiceProvider.GetRequiredService<ContextoAplicacion>();
    db.Database.EnsureCreated();

    var preciosLocalidades = new Dictionary<string, decimal>(StringComparer.OrdinalIgnoreCase)
    {
        ["GENERAL"] = 150m,
        ["TRIBUNA"] = 250m,
        ["PALCO"] = 450m,
        ["VIP"] = 750m
    };

    var partidosActualizados = new Dictionary<int, (string EquipoLocal, string EquipoVisita, DateTime Fecha, string Lugar)>
    {
        [1] = ("Barcelona SC", "Emelec", new DateTime(2026, 5, 18, 18, 30, 0), "Estadio Monumental"),
        [2] = ("Liga de Quito", "Aucas", new DateTime(2026, 5, 20, 20, 0, 0), "Estadio Rodrigo Paz"),
        [3] = ("El Nacional", "Deportivo Quito", new DateTime(2026, 5, 22, 19, 0, 0), "Estadio Olimpico Atahualpa"),
        [4] = ("Delfin", "Barcelona SC", new DateTime(2026, 5, 25, 21, 0, 0), "Estadio Jocay"),
        [5] = ("Macara", "Tecnico Universitario", new DateTime(2026, 5, 28, 17, 30, 0), "Estadio Bellavista")
    };

    foreach (var localidad in db.LocalidadesPartido)
    {
        if (preciosLocalidades.TryGetValue(localidad.CodigoLocalidad, out var precio) && localidad.Precio != precio)
        {
            localidad.Precio = precio;
        }
    }

    foreach (var partido in db.PartidosFutbol)
    {
        if (partidosActualizados.TryGetValue(partido.Codigo, out var actualizado))
        {
            partido.EquipoLocal = actualizado.EquipoLocal;
            partido.EquipoVisita = actualizado.EquipoVisita;
            partido.Fecha = actualizado.Fecha;
            partido.Lugar = actualizado.Lugar;
        }
    }

    db.SaveChanges();

    db.Database.ExecuteSqlRaw(@"
IF OBJECT_ID('USUARIO', 'U') IS NULL
BEGIN
    CREATE TABLE USUARIO (
        IdUsuario INT IDENTITY(1,1) PRIMARY KEY,
        NombreCompleto NVARCHAR(120) NOT NULL,
        Usuario NVARCHAR(50) NOT NULL UNIQUE,
        Correo NVARCHAR(120) NOT NULL UNIQUE,
        PasswordHash NVARCHAR(256) NOT NULL,
        PasswordSalt NVARCHAR(64) NOT NULL,
        FechaRegistro DATETIME NOT NULL
    );
END");
}

if (app.Environment.IsDevelopment())
{
    app.UseSwagger();
    app.UseSwaggerUI();
}

app.UseCors("ClientesTicketPremium");
app.UseAuthorization();

app.MapControllers();

app.Run();
