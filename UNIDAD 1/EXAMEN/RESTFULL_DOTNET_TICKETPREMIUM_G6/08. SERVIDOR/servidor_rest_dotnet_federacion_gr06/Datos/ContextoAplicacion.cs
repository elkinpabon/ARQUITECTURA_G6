using Microsoft.EntityFrameworkCore;
using servidor_rest_dotnet_federacion_gr06.Modelos;

namespace servidor_rest_dotnet_federacion_gr06.Datos;

public class ContextoAplicacion(DbContextOptions<ContextoAplicacion> options) : DbContext(options)
{
    public DbSet<PartidoFutbol> PartidosFutbol => Set<PartidoFutbol>();

    public DbSet<LocalidadPartido> LocalidadesPartido => Set<LocalidadPartido>();

    public DbSet<Factura> Facturas => Set<Factura>();

    public DbSet<DetalleFactura> DetallesFactura => Set<DetalleFactura>();

    public DbSet<UsuarioSistema> Usuarios => Set<UsuarioSistema>();

    protected override void OnModelCreating(ModelBuilder modelBuilder)
    {
        modelBuilder.Entity<PartidoFutbol>(entity =>
        {
            entity.ToTable("PARTIDO_FUTBOL");
            entity.HasKey(e => e.Codigo);
            entity.Property(e => e.EquipoLocal).HasMaxLength(100).IsRequired();
            entity.Property(e => e.EquipoVisita).HasMaxLength(100).IsRequired();
            entity.Property(e => e.Lugar).HasMaxLength(150).IsRequired();

            entity.HasData(
                new PartidoFutbol { Codigo = 1, EquipoLocal = "Barcelona SC", EquipoVisita = "Emelec", Fecha = new DateTime(2026, 5, 18, 18, 30, 0), Lugar = "Estadio Monumental" },
                new PartidoFutbol { Codigo = 2, EquipoLocal = "Liga de Quito", EquipoVisita = "Aucas", Fecha = new DateTime(2026, 5, 20, 20, 0, 0), Lugar = "Estadio Rodrigo Paz" },
                new PartidoFutbol { Codigo = 3, EquipoLocal = "El Nacional", EquipoVisita = "Deportivo Quito", Fecha = new DateTime(2026, 5, 22, 19, 0, 0), Lugar = "Estadio Olimpico Atahualpa" },
                new PartidoFutbol { Codigo = 4, EquipoLocal = "Delfin", EquipoVisita = "Barcelona SC", Fecha = new DateTime(2026, 5, 25, 21, 0, 0), Lugar = "Estadio Jocay" },
                new PartidoFutbol { Codigo = 5, EquipoLocal = "Macara", EquipoVisita = "Tecnico Universitario", Fecha = new DateTime(2026, 5, 28, 17, 30, 0), Lugar = "Estadio Bellavista" }
            );
        });

        modelBuilder.Entity<LocalidadPartido>(entity =>
        {
            entity.ToTable("LOCALIDAD_PARTIDO");
            entity.HasKey(e => e.Id);
            entity.Property(e => e.CodigoLocalidad).HasMaxLength(50).IsRequired();
            entity.Property(e => e.Precio).HasPrecision(10, 2);
            entity.HasOne(e => e.Partido)
                .WithMany(e => e.Localidades)
                .HasForeignKey(e => e.CodigoPartido)
                .OnDelete(DeleteBehavior.Cascade);

            entity.HasData(
                new LocalidadPartido { Id = 1, CodigoPartido = 1, CodigoLocalidad = "GENERAL", Disponibilidad = 500, Precio = 150m },
                new LocalidadPartido { Id = 2, CodigoPartido = 1, CodigoLocalidad = "TRIBUNA", Disponibilidad = 250, Precio = 250m },
                new LocalidadPartido { Id = 3, CodigoPartido = 1, CodigoLocalidad = "PALCO", Disponibilidad = 80, Precio = 450m },
                new LocalidadPartido { Id = 4, CodigoPartido = 1, CodigoLocalidad = "VIP", Disponibilidad = 40, Precio = 750m },
                new LocalidadPartido { Id = 5, CodigoPartido = 2, CodigoLocalidad = "GENERAL", Disponibilidad = 480, Precio = 150m },
                new LocalidadPartido { Id = 6, CodigoPartido = 2, CodigoLocalidad = "TRIBUNA", Disponibilidad = 220, Precio = 250m },
                new LocalidadPartido { Id = 7, CodigoPartido = 2, CodigoLocalidad = "PALCO", Disponibilidad = 75, Precio = 450m },
                new LocalidadPartido { Id = 8, CodigoPartido = 2, CodigoLocalidad = "VIP", Disponibilidad = 35, Precio = 750m },
                new LocalidadPartido { Id = 9, CodigoPartido = 3, CodigoLocalidad = "GENERAL", Disponibilidad = 520, Precio = 150m },
                new LocalidadPartido { Id = 10, CodigoPartido = 3, CodigoLocalidad = "TRIBUNA", Disponibilidad = 230, Precio = 250m },
                new LocalidadPartido { Id = 11, CodigoPartido = 3, CodigoLocalidad = "PALCO", Disponibilidad = 70, Precio = 450m },
                new LocalidadPartido { Id = 12, CodigoPartido = 3, CodigoLocalidad = "VIP", Disponibilidad = 30, Precio = 750m },
                new LocalidadPartido { Id = 13, CodigoPartido = 4, CodigoLocalidad = "GENERAL", Disponibilidad = 510, Precio = 150m },
                new LocalidadPartido { Id = 14, CodigoPartido = 4, CodigoLocalidad = "TRIBUNA", Disponibilidad = 210, Precio = 250m },
                new LocalidadPartido { Id = 15, CodigoPartido = 4, CodigoLocalidad = "PALCO", Disponibilidad = 60, Precio = 450m },
                new LocalidadPartido { Id = 16, CodigoPartido = 4, CodigoLocalidad = "VIP", Disponibilidad = 25, Precio = 750m },
                new LocalidadPartido { Id = 17, CodigoPartido = 5, CodigoLocalidad = "GENERAL", Disponibilidad = 530, Precio = 150m },
                new LocalidadPartido { Id = 18, CodigoPartido = 5, CodigoLocalidad = "TRIBUNA", Disponibilidad = 240, Precio = 250m },
                new LocalidadPartido { Id = 19, CodigoPartido = 5, CodigoLocalidad = "PALCO", Disponibilidad = 65, Precio = 450m },
                new LocalidadPartido { Id = 20, CodigoPartido = 5, CodigoLocalidad = "VIP", Disponibilidad = 20, Precio = 750m }
            );
        });

        modelBuilder.Entity<Factura>(entity =>
        {
            entity.ToTable("FACTURA");
            entity.HasKey(e => e.IdFactura);
            entity.Property(e => e.Subtotal).HasPrecision(10, 2);
            entity.Property(e => e.Iva).HasPrecision(10, 2);
            entity.Property(e => e.Total).HasPrecision(10, 2);
        });

        modelBuilder.Entity<DetalleFactura>(entity =>
        {
            entity.ToTable("DETALLE_FACTURA");
            entity.HasKey(e => e.IdDetalle);
            entity.Property(e => e.Localidad).HasMaxLength(50).IsRequired();
            entity.Property(e => e.PrecioUnitario).HasPrecision(10, 2);
            entity.Property(e => e.Total).HasPrecision(10, 2);
            entity.HasOne(e => e.Factura)
                .WithMany(e => e.Detalles)
                .HasForeignKey(e => e.IdFactura)
                .OnDelete(DeleteBehavior.Cascade);
        });

        modelBuilder.Entity<UsuarioSistema>(entity =>
        {
            entity.ToTable("USUARIO");
            entity.HasKey(e => e.IdUsuario);
            entity.Property(e => e.NombreCompleto).HasMaxLength(120).IsRequired();
            entity.Property(e => e.Usuario).HasMaxLength(50).IsRequired();
            entity.Property(e => e.Correo).HasMaxLength(120).IsRequired();
            entity.Property(e => e.PasswordHash).HasMaxLength(256).IsRequired();
            entity.Property(e => e.PasswordSalt).HasMaxLength(64).IsRequired();
            entity.HasIndex(e => e.Usuario).IsUnique();
            entity.HasIndex(e => e.Correo).IsUnique();
        });
    }
}
