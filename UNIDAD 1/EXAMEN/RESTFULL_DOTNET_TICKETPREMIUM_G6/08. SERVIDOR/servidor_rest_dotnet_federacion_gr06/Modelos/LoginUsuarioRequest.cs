using System.ComponentModel.DataAnnotations;

namespace servidor_rest_dotnet_federacion_gr06.Modelos;

public class LoginUsuarioRequest
{
    [Required]
    [MaxLength(50)]
    public string Usuario { get; set; } = string.Empty;

    [Required]
    [MaxLength(50)]
    public string Password { get; set; } = string.Empty;
}
