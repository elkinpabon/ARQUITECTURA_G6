using Microsoft.EntityFrameworkCore;
using servidor_rest_dotnet_federacion_gr06.Datos;
using servidor_rest_dotnet_federacion_gr06.Modelos;
using servidor_rest_dotnet_federacion_gr06.Servicios.Interfaces;

namespace servidor_rest_dotnet_federacion_gr06.Servicios;

public class ServicioVenta(ContextoAplicacion contexto, IServicioPartido servicioPartido, IServicioLocalidad servicioLocalidad) : IServicioVenta
{
    public async Task<CompraResponse> RegistrarCompraAsync(CompraRequest request, CancellationToken cancellationToken = default)
    {
        if (request is null)
        {
            throw new ArgumentNullException(nameof(request));
        }

        if (request.Cantidad <= 0)
        {
            throw new InvalidOperationException("La cantidad debe ser mayor a cero.");
        }

        var partido = await servicioPartido.ObtenerPorCodigoAsync(request.CodigoPartido, cancellationToken)
            ?? throw new InvalidOperationException("El partido no existe.");

        if (partido.Fecha < DateTime.Now)
        {
            throw new InvalidOperationException("El partido ya no esta disponible para compra.");
        }

        var localidad = await servicioLocalidad.ObtenerPorPartidoYCodigoAsync(request.CodigoPartido, request.CodigoLocalidad, cancellationToken)
            ?? throw new InvalidOperationException("La localidad no existe para ese partido.");

        if (localidad.Disponibilidad < request.Cantidad)
        {
            throw new InvalidOperationException("No hay disponibilidad suficiente.");
        }

        await using var transaction = await contexto.Database.BeginTransactionAsync(cancellationToken);

        var subtotal = localidad.Precio * request.Cantidad;
        var iva = subtotal * 0.15m;
        var total = subtotal + iva;

        var factura = new Factura
        {
            Fecha = DateTime.Now,
            Subtotal = subtotal,
            Iva = iva,
            Total = total
        };

        contexto.Facturas.Add(factura);
        await contexto.SaveChangesAsync(cancellationToken);

        var detalle = new DetalleFactura
        {
            IdFactura = factura.IdFactura,
            CodigoPartido = request.CodigoPartido,
            Localidad = request.CodigoLocalidad,
            Cantidad = request.Cantidad,
            PrecioUnitario = localidad.Precio,
            Total = subtotal
        };

        contexto.DetallesFactura.Add(detalle);
        localidad.Disponibilidad -= request.Cantidad;

        await contexto.SaveChangesAsync(cancellationToken);
        await transaction.CommitAsync(cancellationToken);

        return new CompraResponse
        {
            IdFactura = factura.IdFactura,
            CodigoPartido = request.CodigoPartido,
            CodigoLocalidad = request.CodigoLocalidad,
            Cantidad = request.Cantidad,
            Subtotal = subtotal,
            Iva = iva,
            Total = total,
            DisponibilidadRestante = localidad.Disponibilidad,
            Fecha = factura.Fecha
        };
    }
}
