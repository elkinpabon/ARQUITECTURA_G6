package ec.edu.monster.util;

import ec.edu.monster.modelo.Cuota;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Genera la tabla de amortizacion por el SISTEMA FRANCES (cuota fija).
 *
 *   cuota = P * i / (1 - (1+i)^-n)     (si i > 0)
 *   cuota = P / n                       (si i == 0)
 *
 * Donde P = monto financiado, i = tasa mensual, n = numero de cuotas.
 * La ultima cuota ajusta el redondeo para que el saldo final cierre en 0.
 */
public final class CalculadoraAmortizacion {

    private CalculadoraAmortizacion() { }

    public static List<Cuota> generar(BigDecimal montoFinanciado, BigDecimal tasaMensual,
                                       int numCuotas, LocalDate fechaBase) {
        List<Cuota> tabla = new ArrayList<>();
        if (montoFinanciado == null || numCuotas <= 0 || montoFinanciado.signum() <= 0) {
            return tabla;
        }
        BigDecimal i = (tasaMensual == null) ? BigDecimal.ZERO : tasaMensual;
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        BigDecimal cuotaFija;
        if (i.signum() == 0) {
            cuotaFija = montoFinanciado.divide(BigDecimal.valueOf(numCuotas), 2, RoundingMode.HALF_UP);
        } else {
            double iD = i.doubleValue();
            double factor = iD / (1 - Math.pow(1 + iD, -numCuotas));
            cuotaFija = montoFinanciado.multiply(BigDecimal.valueOf(factor))
                                       .setScale(2, RoundingMode.HALF_UP);
        }

        BigDecimal saldo = montoFinanciado.setScale(2, RoundingMode.HALF_UP);
        for (int n = 1; n <= numCuotas; n++) {
            BigDecimal saldoInicial = saldo;
            BigDecimal interes = saldoInicial.multiply(i).setScale(2, RoundingMode.HALF_UP);
            BigDecimal abono;
            BigDecimal cuota;
            if (n == numCuotas) {
                abono = saldoInicial;                 // ultima cuota cierra el saldo
                cuota = abono.add(interes);
            } else {
                cuota = cuotaFija;
                abono = cuota.subtract(interes);
            }
            BigDecimal saldoFinal = saldoInicial.subtract(abono).setScale(2, RoundingMode.HALF_UP);
            String venc = fechaBase.plusMonths(n).format(fmt);
            tabla.add(new Cuota(n, venc, saldoInicial, cuota, interes, abono, saldoFinal));
            saldo = saldoFinal;
        }
        return tabla;
    }
}
