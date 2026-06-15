/* Tema claro/oscuro: aplica antes del primer pintado y persiste en localStorage. */
(function () {
    var saved = null;
    try { saved = localStorage.getItem('tp-theme'); } catch (e) { }
    if (!saved) {
        saved = (window.matchMedia && matchMedia('(prefers-color-scheme: dark)').matches) ? 'dark' : 'light';
    }
    document.documentElement.setAttribute('data-theme', saved);

    window.toggleTheme = function () {
        var next = document.documentElement.getAttribute('data-theme') === 'dark' ? 'light' : 'dark';
        document.documentElement.setAttribute('data-theme', next);
        try { localStorage.setItem('tp-theme', next); } catch (e) { }
    };
})();
