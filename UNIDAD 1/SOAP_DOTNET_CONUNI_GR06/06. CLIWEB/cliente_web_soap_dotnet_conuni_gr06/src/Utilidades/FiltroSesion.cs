using Microsoft.AspNetCore.Mvc;
using Microsoft.AspNetCore.Mvc.Filters;
using Microsoft.AspNetCore.Http;

namespace Ec.Edu.Monster.Utilidades;

public class FiltroSesion : ActionFilterAttribute
{
    public override void OnActionExecuting(ActionExecutingContext contexto)
    {
        base.OnActionExecuting(contexto);

        if (string.IsNullOrWhiteSpace(contexto.HttpContext.Session.GetString("Usuario")))
        {
            contexto.Result = new RedirectResult("/ControladorAutenticacion/IniciarSesion");
        }
    }
}
