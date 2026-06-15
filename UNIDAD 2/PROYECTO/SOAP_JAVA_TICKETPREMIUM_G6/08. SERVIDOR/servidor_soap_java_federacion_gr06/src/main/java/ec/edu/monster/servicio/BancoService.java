package ec.edu.monster.servicio;

import ec.edu.monster.modelo.Cuenta;
import ec.edu.monster.modelo.Movimiento;
import ec.edu.monster.persistencia.BancoDAO;
import java.util.List;

/** Consulta de la cuenta y los movimientos del usuario. */
public class BancoService {

    private final BancoDAO bancoDAO = new BancoDAO();

    public Cuenta cuentaDe(int idUsuario)              { return bancoDAO.cuentaDe(idUsuario); }
    public List<Movimiento> movimientosDe(int idUsuario){ return bancoDAO.movimientosDe(idUsuario); }
}
