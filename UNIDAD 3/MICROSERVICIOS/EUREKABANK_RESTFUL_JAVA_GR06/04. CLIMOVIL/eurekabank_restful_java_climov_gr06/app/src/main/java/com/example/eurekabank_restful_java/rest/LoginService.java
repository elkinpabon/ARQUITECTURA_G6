package com.example.eurekabank_restful_java.rest;

import com.example.eurekabank_restful_java.config.ServidorConfig;

import org.json.JSONObject;

/** Login vía REST/JSON. */
public class LoginService {

    public boolean iniciarSesion(String usuario, String clave) throws Exception {
        JSONObject body = new JSONObject();
        body.put("usuario", usuario == null ? "" : usuario);
        body.put("clave", clave == null ? "" : clave);
        String r = Http.post(ServidorConfig.BASE_AUTENTICACION, "/login", body.toString());
        return new JSONObject(r).optBoolean("exito", false);
    }

    /** Código de cliente del usuario, o "" si es admin/sin cliente. */
    public String clienteDeUsuario(String usuario) throws Exception {
        String s = Http.get(ServidorConfig.BASE_AUTENTICACION,
                "/login/cliente/" + Http.enc(usuario));
        return s == null ? "" : s.trim();
    }
}
