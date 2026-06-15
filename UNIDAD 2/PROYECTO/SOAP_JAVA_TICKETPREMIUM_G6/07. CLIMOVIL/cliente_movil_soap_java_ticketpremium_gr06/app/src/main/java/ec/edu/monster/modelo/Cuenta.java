package ec.edu.monster.modelo;

import java.io.Serializable;
import java.math.BigDecimal;

public class Cuenta implements Serializable {
    private int idCuenta;
    private int idUsuario;
    private String numero;
    private BigDecimal saldo;

    public int getIdCuenta() { return idCuenta; }
    public void setIdCuenta(int idCuenta) { this.idCuenta = idCuenta; }
    public int getIdUsuario() { return idUsuario; }
    public void setIdUsuario(int idUsuario) { this.idUsuario = idUsuario; }
    public String getNumero() { return numero; }
    public void setNumero(String numero) { this.numero = numero; }
    public BigDecimal getSaldo() { return saldo; }
    public void setSaldo(BigDecimal saldo) { this.saldo = saldo; }
}
