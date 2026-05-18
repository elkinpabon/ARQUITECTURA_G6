using System.Security.Cryptography;
using System.Text;

namespace SERVIDOR.Servicio
{
    public static class SeguridadUtil
    {
        public static string Sha1(string texto)
        {
            using var sha1 = SHA1.Create();
            byte[] hash = sha1.ComputeHash(Encoding.UTF8.GetBytes(texto));
            StringBuilder sb = new StringBuilder(hash.Length * 2);
            foreach (byte b in hash)
            {
                sb.Append(b.ToString("x2"));
            }
            return sb.ToString();
        }
    }
}
