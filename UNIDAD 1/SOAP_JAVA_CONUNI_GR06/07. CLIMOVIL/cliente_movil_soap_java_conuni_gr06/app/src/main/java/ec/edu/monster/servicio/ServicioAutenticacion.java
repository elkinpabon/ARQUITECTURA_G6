package ec.edu.monster.servicio;

import ec.edu.monster.util.SoapConstants;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

public class ServicioAutenticacion {

    public boolean iniciarSesion(String usuario, String contrasena) throws Exception {
        SoapObject request = new SoapObject(SoapConstants.NAMESPACE, SoapConstants.METODO_INICIAR_SESION);
        request.addProperty("usuario", usuario);
        request.addProperty("contrasena", contrasena);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.setOutputSoapObject(request);

        HttpTransportSE transport = new HttpTransportSE(SoapConstants.URL);
        transport.call(SoapConstants.SOAP_ACTION, envelope);

        SoapPrimitive response = (SoapPrimitive) envelope.getResponse();
        return Boolean.parseBoolean(response.toString());
    }
}
