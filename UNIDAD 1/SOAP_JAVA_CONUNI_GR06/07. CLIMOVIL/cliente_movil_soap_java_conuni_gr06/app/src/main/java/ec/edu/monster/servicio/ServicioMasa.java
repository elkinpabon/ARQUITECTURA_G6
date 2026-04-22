package ec.edu.monster.servicio;

import ec.edu.monster.util.SoapConstants;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

public class ServicioMasa {

    public double kilogramosALibras(double kilogramos) throws Exception {
        return invocarUnario(SoapConstants.METODO_KILOGRAMOS_A_LIBRAS, "kilogramos", kilogramos);
    }

    public double gramosAOnzas(double gramos) throws Exception {
        return invocarUnario(SoapConstants.METODO_GRAMOS_A_ONZAS, "gramos", gramos);
    }

    public double toneladasAKilogramos(double toneladas) throws Exception {
        return invocarUnario(SoapConstants.METODO_TONELADAS_A_KILOGRAMOS, "toneladas", toneladas);
    }

    public double librasAOnzas(double libras) throws Exception {
        return invocarUnario(SoapConstants.METODO_LIBRAS_A_ONZAS, "libras", libras);
    }

    public double miligramosAGramos(double miligramos) throws Exception {
        return invocarUnario(SoapConstants.METODO_MILIGRAMOS_A_GRAMOS, "miligramos", miligramos);
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
