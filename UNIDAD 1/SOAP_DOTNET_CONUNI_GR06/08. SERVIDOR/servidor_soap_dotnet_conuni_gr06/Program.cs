using CoreWCF;
using CoreWCF.Configuration;
using CoreWCF.Description;
using Ec.Edu.Monster.Controlador;
using Ec.Edu.Monster.Contrato;
using Ec.Edu.Monster.Utilidades;

var builder = WebApplication.CreateBuilder(args);
builder.WebHost.UseUrls(ConstantesServidor.UrlBase);

builder.Services.AddServiceModelServices();
builder.Services.AddServiceModelMetadata();
builder.Services.AddSingleton<CONUNI>();

var app = builder.Build();

app.UseServiceModel(serviceBuilder =>
{
    serviceBuilder.AddService<CONUNI>();
    serviceBuilder.AddServiceEndpoint<CONUNI, IConuniServicio>(new BasicHttpBinding(), "/CONUNI.svc");
});

var metadata = app.Services.GetRequiredService<ServiceMetadataBehavior>();
metadata.HttpGetEnabled = true;

app.Run();
