package ec.edu.monster.modelo;

import java.io.Serializable;
import java.math.BigDecimal;

/** Cuenta del usuario (parte bancaria del esquema). SALDO = deuda acumulada por creditos. */
public class Cuenta implements Serializable {
    private int idCuenta;
    private int idUsuario;
    private String numero;
    private BigDecimal saldo;

    public Cuenta() { }

    public Cuenta(int idCuenta, int idUsuario, String numero, BigDecimal saldo) {
        this.idCuenta = idCuenta;
        this.idUsuario = idUsuario;
        this.numero = numero;
        this.saldo = saldo;
    }

    public int getIdCuenta()           { return idCuenta; }
    public void setIdCuenta(int v)     { this.idCuenta = v; }
    public int getIdUsuario()          { return idUsuario; }
    public void setIdUsuario(int v)    { this.idUsuario = v; }
    public String getNumero()          { return numero; }
    public void setNumero(String v)    { this.numero = v; }
    public BigDecimal getSaldo()       { return saldo; }
    public void setSaldo(BigDecimal v) { this.saldo = v; }
}
