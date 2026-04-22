package ec.edu.monster.servicio;

import ec.edu.monster.util.SoapConstants;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

public class ServicioTemperatura {

    public double celsiusAFahrenheit(double celsius) throws Exception {
        return invocarUnario(SoapConstants.METODO_CELSIUS_A_FAHRENHEIT, "celsius", celsius);
    }

    public double fahrenheitACelsius(double fahrenheit) throws Exception {
        return invocarUnario(SoapConstants.METODO_FAHRENHEIT_A_CELSIUS, "fahrenheit", fahrenheit);
    }

    public double celsiusAKelvin(double celsius) throws Exception {
        return invocarUnario(SoapConstants.METODO_CELSIUS_A_KELVIN, "celsius", celsius);
    }

    public double kelvinACelsius(double kelvin) throws Exception {
        return invocarUnario(SoapConstants.METODO_KELVIN_A_CELSIUS, "kelvin", kelvin);
    }

    public double fahrenheitAKelvin(double fahrenheit) throws Exception {
        return invocarUnario(SoapConstants.METODO_FAHRENHEIT_A_KELVIN, "fahrenheit", fahrenheit);
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
