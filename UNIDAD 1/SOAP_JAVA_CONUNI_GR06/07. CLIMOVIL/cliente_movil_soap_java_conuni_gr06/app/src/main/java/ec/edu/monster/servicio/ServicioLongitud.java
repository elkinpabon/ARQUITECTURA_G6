package ec.edu.monster.servicio;

import ec.edu.monster.util.SoapConstants;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

public class ServicioLongitud {

    public double metrosAPies(double metros) throws Exception {
        return invocarUnario(SoapConstants.METODO_METROS_A_PIES, "metros", metros);
    }

    public double kilometrosAMillas(double kilometros) throws Exception {
        return invocarUnario(SoapConstants.METODO_KILOMETROS_A_MILLAS, "kilometros", kilometros);
    }

    public double centimetrosAPulgadas(double centimetros) throws Exception {
        return invocarUnario(SoapConstants.METODO_CENTIMETROS_A_PULGADAS, "centimetros", centimetros);
    }

    public double yardasAMetros(double yardas) throws Exception {
        return invocarUnario(SoapConstants.METODO_YARDAS_A_METROS, "yardas", yardas);
    }

    public double milimetrosAPulgadas(double milimetros) throws Exception {
        return invocarUnario(SoapConstants.METODO_MILIMETROS_A_PULGADAS, "milimetros", milimetros);
    }

    private double invocarUnario(String metodo, String nombreParametro, double valor) throws Exception {
        SoapObject request = new SoapObject(SoapConstants.NAMESPACE, metodo);
        request.addProperty(nombreParametro, String.valueOf(valor));

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.setOutputSoapObject(request);

        HttpTransportSE transport = new HttpTransportSE(SoapConstants.URL);
        transport.call(SoapConstants.SOAP_ACTION, envelope);

        SoapPrimitive response = (SoapPrimitive) envelope.getResponse();
        return Double.parseDouble(response.toString());
    }
}
