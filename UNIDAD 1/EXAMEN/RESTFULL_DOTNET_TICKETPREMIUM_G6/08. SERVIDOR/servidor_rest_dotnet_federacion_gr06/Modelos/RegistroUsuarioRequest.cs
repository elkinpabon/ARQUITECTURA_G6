using System.ComponentModel.DataAnnotations;

namespace servidor_rest_dotnet_federacion_gr06.Modelos;

public class RegistroUsuarioRequest
{
    [Required]
    [MaxLength(120)]
    public string NombreCompleto { get; set; } = string.Empty;

    [Required]
    [MinLength(4)]
    [MaxLength(50)]
    public string Usuario { get; set; } = string.Empty;

    [Required]
    [EmailAddress]
    [MaxLength(120)]
    public string Correo { get; set; } = string.Empty;

    [Required]
    [MinLength(4)]
    [MaxLength(50)]
    public string Password { get; set; } = string.Empty;

    [Required]
    [Compare(nameof(Password), ErrorMessage = "Las contraseñas no coinciden.")]
    public string ConfirmarPassword { get; set; } = string.Empty;
}
