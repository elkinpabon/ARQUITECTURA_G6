package com.example.eurekabank_soap_java.service;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.util.Log;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class AuthService {
    private static final String TAG = "AuthServicio";
    private static final String NAMESPACE = "http://ws.monster.edu.ec/";
    private static final String METHOD_NAME = "iniciarSesion";
    private static final String SOAP_ACTION = "http://ws.monster.edu.ec/WSLogin/iniciarSesionRequest";
    // Emulador Android -> host local: 10.0.2.2. Dispositivo fisico: usar la IP LAN del PC.
    private static final String URL = "http://10.0.2.2:8080/eurekabank_soap_java_gr06/WSLogin";
    private static final int TIMEOUT = 15000;

    public interface SoapCallback {
        void onSuccess(boolean isAuth);
        void onError(String errorMessage);
    }

    private static String hashPassword(String input) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hashBytes = digest.digest(input.getBytes());
        StringBuilder hexString = new StringBuilder();
        for (byte b : hashBytes) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }

    @SuppressLint("StaticFieldLeak")
    public void auth(final String username, final String password, final SoapCallback callback) {
        new AsyncTask<Void, Void, AsyncTaskResult<Boolean>>() {
            @Override
            protected AsyncTaskResult<Boolean> doInBackground(Void... voids) {
                try {
                    Log.d(TAG, "Creating SOAP request with parameters: username=" + username +
                            ", password=" + password);

                    // Create SOAP request
                    SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);

                    // Add properties using PropertyInfo for better type handling
                    PropertyInfo usuarioProperty = new PropertyInfo();
                    usuarioProperty.setName("usuario");
                    usuarioProperty.setValue(username);
                    usuarioProperty.setType(String.class);
                    request.addProperty(usuarioProperty);

                    // La clave viaja en texto plano: el SERVIDOR aplica SHA1.
                    PropertyInfo claveProperty = new PropertyInfo();
                    claveProperty.setName("clave");
                    claveProperty.setValue(password);
                    claveProperty.setType(String.class);
                    request.addProperty(claveProperty);

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
                        if (envelope.getResponse() instanceof SoapPrimitive) {
                            SoapPrimitive response = (SoapPrimitive) envelope.getResponse();
                            return new AsyncTaskResult<>(Boolean.parseBoolean(response.toString()));
                        } else if (envelope.getResponse() != null) {
                            return new AsyncTaskResult<>(Boolean.parseBoolean(envelope.getResponse().toString()));
                        } else {
                            return new AsyncTaskResult<>(new Exception("Empty response received"));
                        }
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
            protected void onPostExecute(AsyncTaskResult<Boolean> result) {
                if (result.getError() != null) {
                    String errorMessage = "Error: " + result.getError().getMessage();
                    Log.e(TAG, errorMessage);
                    callback.onError(errorMessage);
                } else {
                    Log.d(TAG, "Success! Result: " + result.getResult());
                    callback.onSuccess(result.getResult());
                }
            }
        }.execute();
    }

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