var builder = WebApplication.CreateBuilder(args);

builder.Services.AddControllersWithViews().AddRazorOptions(options =>
{
    options.ViewLocationFormats.Clear();
    options.ViewLocationFormats.Add("/Vista/{1}/{0}.cshtml");
    options.ViewLocationFormats.Add("/Vista/Compartido/{0}.cshtml");
});
builder.Services.AddDistributedMemoryCache();
builder.Services.AddSession(options =>
{
    options.IdleTimeout = TimeSpan.FromMinutes(30);
    options.Cookie.HttpOnly = true;
    options.Cookie.IsEssential = true;
});
builder.Services.AddHttpClient<cliente_web_rest_dotnet_ticketpremium_gr06.Servicios.ITicketPremiumApiService, cliente_web_rest_dotnet_ticketpremium_gr06.Servicios.TicketPremiumApiService>(client =>
{
    client.BaseAddress = new Uri(builder.Configuration["ApiBaseUrl"] ?? "https://localhost:44348/");
});

var app = builder.Build();

if (!app.Environment.IsDevelopment())
{
    app.UseExceptionHandler("/Inicio/Error");
    app.UseHsts();
}

app.UseHttpsRedirection();
app.UseRouting();

app.UseSession();
app.UseAuthorization();
app.UseStaticFiles();

app.MapStaticAssets();

app.MapControllerRoute(
    name: "default",
    pattern: "{controller=Cuenta}/{action=Login}/{id?}")
    .WithStaticAssets();


app.Run();
