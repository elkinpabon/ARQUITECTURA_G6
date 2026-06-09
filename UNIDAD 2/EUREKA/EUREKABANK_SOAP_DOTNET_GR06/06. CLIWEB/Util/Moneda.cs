namespace CLIWEB.Util
{
    public static class Moneda
    {
        public static string Nombre(string codigo)
        {
            return codigo switch
            {
                "01" => "Soles",
                "02" => "Dolares",
                _ => codigo
            };
        }
    }
}
