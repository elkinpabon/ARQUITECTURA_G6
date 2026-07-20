package com.example.eurekabank_soap_java.soap;

import com.example.eurekabank_soap_java.config.ServidorConfig;

/** WSLogin vía ksoap2. */
public class LoginService {

    public boolean iniciarSesion(String usuario, String clave) throws Exception {
        Object body = SoapBase.llamar(ServidorConfig.EP_LOGIN,
                SoapBase.req("iniciarSesion", "usuario", usuario, "clave", clave));
        return "true".equalsIgnoreCase(SoapBase.returnText(body));
    }

    /** Código de cliente del usuario, o "" si es admin/sin cliente. */
    public String clienteDeUsuario(String usuario) throws Exception {
        Object body = SoapBase.llamar(ServidorConfig.EP_LOGIN,
                SoapBase.req("clienteDeUsuario", "usuario", usuario));
        return SoapBase.returnText(body);
    }
}
