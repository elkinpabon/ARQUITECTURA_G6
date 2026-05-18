package com.example.eurekabank_restful_java.rest;

import org.json.JSONObject;

/** Login vía REST/JSON. */
public class LoginService {

    public boolean iniciarSesion(String usuario, String clave) throws Exception {
        JSONObject body = new JSONObject();
        body.put("usuario", usuario == null ? "" : usuario);
        body.put("clave", clave == null ? "" : clave);
        String r = Http.post("/login", body.toString());
        return new JSONObject(r).optBoolean("exito", false);
    }

    /** Código de cliente del usuario, o "" si es admin/sin cliente. */
    public String clienteDeUsuario(String usuario) throws Exception {
        String s = Http.get("/login/cliente/" + Http.enc(usuario));
        return s == null ? "" : s.trim();
    }
}
