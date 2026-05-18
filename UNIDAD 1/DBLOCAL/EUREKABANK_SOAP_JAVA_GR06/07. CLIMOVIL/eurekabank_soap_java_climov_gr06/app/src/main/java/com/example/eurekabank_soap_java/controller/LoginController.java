package com.example.eurekabank_soap_java.controller;

import com.example.eurekabank_soap_java.service.AuthService;

public class LoginController {
    private AuthService authService;

    public LoginController(AuthService authService) {
        this.authService = authService;
    }

    public interface LoginCallback {
        void onLoginSuccess(boolean isAuthenticated);
        void onLoginError(String errorMessage);
    }

    public void attemptLogin(String username, String password, final LoginCallback callback) {
        authService.auth(username, password, new AuthService.SoapCallback() {
            @Override
            public void onSuccess(boolean isAuth) {
                callback.onLoginSuccess(isAuth);
            }

            @Override
            public void onError(String errorMessage) {
                callback.onLoginError(errorMessage);
            }
        });
    }
}