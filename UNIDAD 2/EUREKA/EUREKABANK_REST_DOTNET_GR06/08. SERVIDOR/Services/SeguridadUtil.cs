using System.Security.Cryptography;
using System.Text;

namespace SERVIDOR.Services;

public static class SeguridadUtil
{
    public static string Sha1(string texto)
    {
        using var md = SHA1.Create();
        var hash = md.ComputeHash(Encoding.UTF8.GetBytes(texto));
        var sb = new StringBuilder(hash.Length * 2);
        foreach (var b in hash)
            sb.Append(b.ToString("x2"));
        return sb.ToString();
    }
}
