package com.example.eurekabank_soap_java.service;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.util.Log;

import com.example.eurekabank_soap_java.models.MovimientoModel;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class MovementService {
    private static final String TAG = "MovimientoService";
    private static final String NAMESPACE = "http://ws.monster.edu.ec/";
    private static final String METHOD_NAME = "listarMovimientos";
    private static final String SOAP_ACTION = "http://ws.monster.edu.ec/WSMovimiento/listarMovimientosRequest";
    // Emulador Android -> host local: 10.0.2.2. Dispositivo fisico: usar la IP LAN del PC.
    private static final String URL = "http://10.0.2.2:8080/eurekabank_soap_java_gr06/WSMovimiento";
    private static final int TIMEOUT = 15000;

    public interface MovimientoCallback {
        void onSuccess(List<MovimientoModel> movimientos);
        void onError(String errorMessage);
    }

    @SuppressLint("StaticFieldLeak")
    public void getMovimientos(final String numeroCuenta, final MovimientoCallback callback) {
        new AsyncTask<Void, Void, AsyncTaskResult<List<MovimientoModel>>>() {
            @Override
            protected AsyncTaskResult<List<MovimientoModel>> doInBackground(Void... voids) {
                try {
                    Log.d(TAG, "Creating SOAP request with account number: " + numeroCuenta);

                    // Create SOAP request
                    SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);

                    // Add account number property
                    PropertyInfo cuentaProperty = new PropertyInfo();
                    cuentaProperty.setName("cuenta");
                    cuentaProperty.setValue(numeroCuenta);
                    cuentaProperty.setType(String.class);
                    request.addProperty(cuentaProperty);

                    Log.d(TAG, "Request object created: " + request.toString());

                    // Configure SOAP envelope
                    SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                    envelope.setOutputSoapObject(request);
                    envelope.dotNet = false;

                    Log.d(TAG, "Envelope configured");

                    // Configure transport with timeout
                    HttpTransportSE transport = new HttpTransportSE(URL, TIMEOUT);
                    transport.debug = true;

                    try {
                        Log.d(TAG, "Making SOAP request to " + URL);
                        Log.d(TAG, "SOAP Action: " + SOAP_ACTION);

                        transport.call(SOAP_ACTION, envelope);

                        Log.d(TAG, "Request dump: " + transport.requestDump);
                        Log.d(TAG, "Response dump: " + transport.responseDump);

                        // Handle response
                        Object response = envelope.getResponse();
                        List<MovimientoModel> movimientos = new ArrayList<>();

                        // Handle different possible response types
                        if (response instanceof Vector) {
                            Vector<?> vector = (Vector<?>) response;
                            for (Object obj : vector) {
                                if (obj instanceof SoapObject) {
                                    SoapObject movimientoSoap = (SoapObject) obj;
                                    MovimientoModel movimiento = new MovimientoModel();

                                    // Safely extract properties
                                    movimiento.setCodigoCuenta(getSafeStringProperty(movimientoSoap, "codigoCuenta"));
                                    movimiento.setCodigoEmpleado(getSafeStringProperty(movimientoSoap, "codigoEmpleado"));
                                    movimiento.setCodigoTipoMovimiento(getSafeStringProperty(movimientoSoap, "codigoTipoMovimiento"));
                                    movimiento.setDescTipoMovimiento(getSafeStringProperty(movimientoSoap, "tipoDescripcion"));
                                    movimiento.setFechaMovimiento(getSafeStringProperty(movimientoSoap, "fechaMovimiento"));

                                    // Extract numeric values with default
                                    movimiento.setImporteMovimiento(getSafeDoubleProperty(movimientoSoap, "importeMovimiento"));
                                    movimiento.setNumeroMovimiento(getSafeIntProperty(movimientoSoap, "numeroMovimiento"));

                                    movimientos.add(movimiento);
                                }
                            }
                        } else if (response instanceof SoapObject) {
                            // If single object is returned
                            SoapObject movimientoSoap = (SoapObject) response;
                            MovimientoModel movimiento = new MovimientoModel();

                            movimiento.setCodigoCuenta(getSafeStringProperty(movimientoSoap, "codigoCuenta"));
                            movimiento.setCodigoEmpleado(getSafeStringProperty(movimientoSoap, "codigoEmpleado"));
                            movimiento.setCodigoTipoMovimiento(getSafeStringProperty(movimientoSoap, "codigoTipoMovimiento"));
                            movimiento.setFechaMovimiento(getSafeStringProperty(movimientoSoap, "fechaMovimiento"));
                            movimiento.setDescTipoMovimiento(getSafeStringProperty(movimientoSoap, "tipoDescripcion"));

                            movimiento.setImporteMovimiento(getSafeDoubleProperty(movimientoSoap, "importeMovimiento"));
                            movimiento.setNumeroMovimiento(getSafeIntProperty(movimientoSoap, "numeroMovimiento"));

                            movimientos.add(movimiento);
                        }

                        return new AsyncTaskResult<>(movimientos);

                    } catch (Exception e) {
                        Log.e(TAG, "SOAP call failed", e);
                        return new AsyncTaskResult<>(e);
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Request preparation failed", e);
                    return new AsyncTaskResult<>(e);
                }
            }

            @Override
            protected void onPostExecute(AsyncTaskResult<List<MovimientoModel>> result) {
                if (result.getError() != null) {
                    String errorMessage = "Error: " + result.getError().getMessage();
                    Log.e(TAG, errorMessage);
                    callback.onError(errorMessage);
                } else {
                    Log.d(TAG, "Success! Found " + result.getResult().size() + " transactions");
                    callback.onSuccess(result.getResult());
                }
            }
        }.execute();
    }

    // Helper methods for safe property extraction
    private String getSafeStringProperty(SoapObject soapObject, String propertyName) {
        try {
            Object prop = soapObject.getProperty(propertyName);
            Log.d(TAG, "Property " + propertyName + ": " + (prop != null ? prop.toString() : "null"));

            if (prop == null || prop.toString().equals("anyType{}")) {
                return "";
            }
            return prop.toString().trim();
        } catch (Exception e) {
            Log.e(TAG, "Error extracting property " + propertyName, e);
            return "";
        }
    }

    private double getSafeDoubleProperty(SoapObject soapObject, String propertyName) {
        try {
            Object prop = soapObject.getProperty(propertyName);
            return prop != null ? Double.parseDouble(prop.toString()) : 0.0;
        } catch (Exception e) {
            return 0.0;
        }
    }

    private int getSafeIntProperty(SoapObject soapObject, String propertyName) {
        try {
            Object prop = soapObject.getProperty(propertyName);
            return prop != null ? Integer.parseInt(prop.toString()) : 0;
        } catch (Exception e) {
            return 0;
        }
    }

    // Utility class for handling AsyncTask results (same as in AuthService)
    private static class AsyncTaskResult<T> {
        private T result;
        private Exception error;

        AsyncTaskResult(T result) {
            this.result = result;
        }

        AsyncTaskResult(Exception error) {
            this.error = error;
        }

        T getResult() {
            return result;
        }

        Exception getError() {
            return error;
        }
    }
}
