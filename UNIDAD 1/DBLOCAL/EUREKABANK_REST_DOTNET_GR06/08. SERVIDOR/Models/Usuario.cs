namespace SERVIDOR.Models;

public class Usuario
{
    public string CodigoEmpleado { get; set; } = string.Empty;
    public string UsuarioNombre { get; set; } = string.Empty;
    public string Clave { get; set; } = string.Empty;
    public string Estado { get; set; } = string.Empty;
    public string? ClienteCodigo { get; set; }
}
