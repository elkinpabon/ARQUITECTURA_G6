using System.Security.Cryptography;
using System.Text;
using Microsoft.EntityFrameworkCore;
using servidor_rest_dotnet_federacion_gr06.Datos;
using servidor_rest_dotnet_federacion_gr06.Modelos;
using servidor_rest_dotnet_federacion_gr06.Servicios.Interfaces;

namespace servidor_rest_dotnet_federacion_gr06.Servicios;

public class ServicioAutenticacion(ContextoAplicacion contexto) : IServicioAutenticacion
{
    public async Task<AutenticacionResponse> RegistrarAsync(RegistroUsuarioRequest request, CancellationToken cancellationToken = default)
    {
        ValidarRegistro(request);

        var usuarioNormalizado = request.Usuario.Trim().ToLowerInvariant();
        var correoNormalizado = request.Correo.Trim().ToLowerInvariant();

        if (await contexto.Usuarios.AnyAsync(x => x.Usuario == usuarioNormalizado, cancellationToken))
        {
            throw new InvalidOperationException("El usuario ya existe.");
        }

        if (await contexto.Usuarios.AnyAsync(x => x.Correo == correoNormalizado, cancellationToken))
        {
            throw new InvalidOperationException("El correo ya esta registrado.");
        }

        var salt = RandomNumberGenerator.GetBytes(16);
        var usuario = new UsuarioSistema
        {
            NombreCompleto = request.NombreCompleto.Trim(),
            Usuario = usuarioNormalizado,
            Correo = correoNormalizado,
            PasswordSalt = Convert.ToBase64String(salt),
            PasswordHash = CalcularHash(salt, request.Password),
            FechaRegistro = DateTime.Now
        };

        contexto.Usuarios.Add(usuario);
        await contexto.SaveChangesAsync(cancellationToken);

        return new AutenticacionResponse
        {
            Exito = true,
            Mensaje = "Usuario registrado correctamente.",
            IdUsuario = usuario.IdUsuario,
            NombreCompleto = usuario.NombreCompleto,
            Usuario = usuario.Usuario
        };
    }

    public async Task<AutenticacionResponse> IniciarSesionAsync(LoginUsuarioRequest request, CancellationToken cancellationToken = default)
    {
        if (request is null)
        {
            throw new ArgumentNullException(nameof(request));
        }

        if (string.IsNullOrWhiteSpace(request.Usuario) || string.IsNullOrWhiteSpace(request.Password))
        {
            throw new InvalidOperationException("Usuario y contrasena son obligatorios.");
        }

        var usuarioNormalizado = request.Usuario.Trim().ToLowerInvariant();
        var usuario = await contexto.Usuarios.FirstOrDefaultAsync(x => x.Usuario == usuarioNormalizado, cancellationToken)
            ?? throw new InvalidOperationException("Usuario o contrasena incorrectos.");

        var salt = Convert.FromBase64String(usuario.PasswordSalt);
        var hashIngresado = CalcularHash(salt, request.Password);

        if (!CryptographicOperations.FixedTimeEquals(
                Convert.FromBase64String(usuario.PasswordHash),
                Convert.FromBase64String(hashIngresado)))
        {
            throw new InvalidOperationException("Usuario o contrasena incorrectos.");
        }

        return new AutenticacionResponse
        {
            Exito = true,
            Mensaje = "Inicio de sesion exitoso.",
            IdUsuario = usuario.IdUsuario,
            NombreCompleto = usuario.NombreCompleto,
            Usuario = usuario.Usuario
        };
    }

    private static void ValidarRegistro(RegistroUsuarioRequest request)
    {
        if (request is null)
        {
            throw new ArgumentNullException(nameof(request));
        }

        if (string.IsNullOrWhiteSpace(request.NombreCompleto) ||
            string.IsNullOrWhiteSpace(request.Usuario) ||
            string.IsNullOrWhiteSpace(request.Correo) ||
            string.IsNullOrWhiteSpace(request.Password))
        {
            throw new InvalidOperationException("Complete todos los campos.");
        }

        if (!string.Equals(request.Password, request.ConfirmarPassword, StringComparison.Ordinal))
        {
            throw new InvalidOperationException("Las contrasenas no coinciden.");
        }
    }

    private static string CalcularHash(byte[] salt, string password)
    {
        var passwordBytes = Encoding.UTF8.GetBytes(password);
        var buffer = new byte[salt.Length + passwordBytes.Length];
        Buffer.BlockCopy(salt, 0, buffer, 0, salt.Length);
        Buffer.BlockCopy(passwordBytes, 0, buffer, salt.Length, passwordBytes.Length);

        return Convert.ToBase64String(SHA256.HashData(buffer));
    }
}
