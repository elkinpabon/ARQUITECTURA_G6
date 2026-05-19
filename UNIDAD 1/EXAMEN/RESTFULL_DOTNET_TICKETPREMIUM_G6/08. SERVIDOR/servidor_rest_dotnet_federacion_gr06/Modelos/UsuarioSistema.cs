using System.ComponentModel.DataAnnotations;

namespace servidor_rest_dotnet_federacion_gr06.Modelos;

public class UsuarioSistema
{
    [Key]
    public int IdUsuario { get; set; }

    [Required]
    [MaxLength(120)]
    public string NombreCompleto { get; set; } = string.Empty;

    [Required]
    [MaxLength(50)]
    public string Usuario { get; set; } = string.Empty;

    [Required]
    [MaxLength(120)]
    public string Correo { get; set; } = string.Empty;

    [Required]
    [MaxLength(256)]
    public string PasswordHash { get; set; } = string.Empty;

    [Required]
    [MaxLength(64)]
    public string PasswordSalt { get; set; } = string.Empty;

    public DateTime FechaRegistro { get; set; }
}
