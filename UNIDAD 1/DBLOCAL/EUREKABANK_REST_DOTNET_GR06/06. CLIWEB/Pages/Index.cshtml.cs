using Microsoft.AspNetCore.Mvc;
using Microsoft.AspNetCore.Mvc.RazorPages;

namespace CLIWEB.Pages;

public class IndexModel : PageModel
{
    public string User => HttpContext.Session.GetString("Usuario") ?? "";
    public bool IsAdmin => HttpContext.Session.GetString("EsAdmin") == "true";
    public IActionResult OnGet() => string.IsNullOrEmpty(User) ? RedirectToPage("/Login") : Page();
}
