var builder = WebApplication.CreateBuilder(args);

builder.Services.AddControllers();
builder.Services.AddOpenApi();

builder.Services.AddCors(options =>
{
    options.AddPolicy("AllowAll", policy =>
    {
        policy.AllowAnyOrigin()
              .AllowAnyMethod()
              .AllowAnyHeader();
    });
});

builder.Services.AddSingleton<SERVIDOR.Data.UsuarioDAO>();
builder.Services.AddSingleton<SERVIDOR.Data.ClienteDAO>();
builder.Services.AddSingleton<SERVIDOR.Data.CuentaDAO>();
builder.Services.AddSingleton<SERVIDOR.Data.MovimientoDAO>();
builder.Services.AddSingleton<SERVIDOR.Data.TasaCambioDAO>();
builder.Services.AddSingleton<SERVIDOR.Services.LoginService>();
builder.Services.AddSingleton<SERVIDOR.Services.CuentaService>();
builder.Services.AddSingleton<SERVIDOR.Services.MovimientoService>();

var app = builder.Build();

if (app.Environment.IsDevelopment())
{
    app.MapOpenApi();
}

app.UseCors("AllowAll");
app.UseAuthorization();
app.MapControllers();

app.Run("http://localhost:5010");
