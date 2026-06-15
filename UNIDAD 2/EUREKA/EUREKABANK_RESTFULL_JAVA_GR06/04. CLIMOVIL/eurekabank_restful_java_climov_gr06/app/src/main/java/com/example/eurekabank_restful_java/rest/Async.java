package com.example.eurekabank_restful_java.rest;

import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;

/** Ejecuta llamadas SOAP fuera del hilo de UI (Android lo exige). */
public final class Async {

    public interface Op<T> { T run() throws Exception; }
    public interface Done<T> { void ok(T value); }
    public interface Fail { void error(Exception e); }

    private static final ExecutorService POOL = Executors.newCachedThreadPool();
    private static final Handler UI = new Handler(Looper.getMainLooper());

    private Async() { }

    public static <T> void run(final Op<T> op, final Done<T> done, final Fail fail) {
        POOL.execute(new Runnable() {
            @Override public void run() {
                try {
                    final T v = op.run();
                    UI.post(new Runnable() { @Override public void run() { done.ok(v); } });
                } catch (final Exception e) {
                    UI.post(new Runnable() { @Override public void run() { fail.error(e); } });
                }
            }
        });
    }
}
