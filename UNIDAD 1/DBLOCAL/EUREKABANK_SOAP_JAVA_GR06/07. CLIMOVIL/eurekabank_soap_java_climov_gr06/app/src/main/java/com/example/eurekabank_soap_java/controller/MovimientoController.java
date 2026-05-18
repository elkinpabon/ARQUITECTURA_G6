package com.example.eurekabank_soap_java.controller;

import com.example.eurekabank_soap_java.service.MovementService;
import com.example.eurekabank_soap_java.models.MovimientoModel;

import java.util.List;

public class MovimientoController {
    private MovementService movimientoService;

    public MovimientoController(MovementService movimientoService) {
        this.movimientoService = movimientoService;
    }

    public interface MovimientoCallback {
        void onMovimientosSuccess(List<MovimientoModel> movimientos);
        void onMovimientosError(String errorMessage);
    }

    public void obtenerMovimientos(String numeroCuenta, final MovimientoCallback callback) {
        movimientoService.getMovimientos(numeroCuenta, new MovementService.MovimientoCallback() {
            @Override
            public void onSuccess(List<MovimientoModel> movimientos) {
                callback.onMovimientosSuccess(movimientos);
            }

            @Override
            public void onError(String errorMessage) {
                callback.onMovimientosError(errorMessage);
            }
        });
    }
}