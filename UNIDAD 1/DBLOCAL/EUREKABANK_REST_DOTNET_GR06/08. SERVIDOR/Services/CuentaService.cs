using Microsoft.Data.SqlClient;
using SERVIDOR.Data;
using SERVIDOR.Models;

namespace SERVIDOR.Services;

public class CuentaService
{
    private const string EMPLEADO_CAJA = "0001";
    private const string TIPO_DEPOSITO = "003";
    private const string TIPO_RETIRO = "004";
    private const string TIPO_TRANSF_IN = "008";
    private const string TIPO_TRANSF_OUT = "009";

    private readonly CuentaDAO _cuentaDAO;
    private readonly MovimientoDAO _movimientoDAO;
    private readonly ClienteDAO _clienteDAO;
    private readonly TasaCambioDAO _tasaDAO;

    public CuentaService(CuentaDAO cuentaDAO, MovimientoDAO movimientoDAO,
                         ClienteDAO clienteDAO, TasaCambioDAO tasaDAO)
    {
        _cuentaDAO = cuentaDAO;
        _movimientoDAO = movimientoDAO;
        _clienteDAO = clienteDAO;
        _tasaDAO = tasaDAO;
    }

    public List<ClienteResumen> ListarClientes()
    {
        try
        {
            return _clienteDAO.ListarTodos();
        }
        catch (Exception e)
        {
            Console.WriteLine($"Error listando clientes: {e.Message}");
            return new List<ClienteResumen>();
        }
    }

    public Resultado Depositar(string codigoCuenta, string monto, string moneda) =>
        Operar(codigoCuenta, monto, true, moneda);

    public Resultado Retirar(string codigoCuenta, string monto, string moneda) =>
        Operar(codigoCuenta, monto, false, moneda);

    private static double Redondear2(double v) =>
        Math.Round(v * 100.0) / 100.0;

    public List<CuentaResumen> ListarCuentasPorCliente(string criterio)
    {
        if (string.IsNullOrWhiteSpace(criterio))
            return new List<CuentaResumen>();

        try
        {
            return _cuentaDAO.ListarPorCliente(criterio.Trim());
        }
        catch (Exception e)
        {
            Console.WriteLine($"Error listando cuentas por cliente: {e.Message}");
            return new List<CuentaResumen>();
        }
    }

    public Resultado RegistrarCliente(string paterno, string materno, string nombre,
        string dni, string ciudad, string direccion,
        string telefono, string email)
    {
        if (string.IsNullOrWhiteSpace(paterno) || string.IsNullOrWhiteSpace(nombre)
            || string.IsNullOrWhiteSpace(dni))
            return Resultado.Error("Apellido paterno, nombre y DNI son obligatorios.");

        try
        {
            var cod = _clienteDAO.Insertar(paterno.Trim(),
                string.IsNullOrWhiteSpace(materno) ? "" : materno.Trim(), nombre.Trim(), dni.Trim(),
                string.IsNullOrWhiteSpace(ciudad) ? "" : ciudad.Trim(),
                string.IsNullOrWhiteSpace(direccion) ? "" : direccion.Trim(),
                string.IsNullOrWhiteSpace(telefono) ? "" : telefono.Trim(),
                string.IsNullOrWhiteSpace(email) ? "" : email.Trim());
            return Resultado.Ok($"Cliente registrado con c\u00f3digo {cod}.", 0);
        }
        catch (Exception e)
        {
            Console.WriteLine($"Error registrando cliente: {e.Message}");
            return Resultado.Error($"No se pudo registrar el cliente: {e.Message}");
        }
    }

    public Resultado RegistrarCuenta(string clienteCodigo, string moneda)
    {
        if (string.IsNullOrWhiteSpace(clienteCodigo))
            return Resultado.Error("C\u00f3digo de cliente requerido.");

        var mon = string.IsNullOrWhiteSpace(moneda) ? "02" : moneda.Trim();
        if (mon != "01" && mon != "02")
            return Resultado.Error("Moneda inv\u00e1lida (use 01 Soles o 02 D\u00f3lares).");

        try
        {
            if (!_clienteDAO.Existe(clienteCodigo.Trim()))
                return Resultado.Error($"El cliente {clienteCodigo} no existe.");

            var cod = _cuentaDAO.Insertar(clienteCodigo.Trim(), mon);
            return Resultado.Ok($"Cuenta {cod} creada para el cliente {clienteCodigo}.", 0);
        }
        catch (Exception e)
        {
            Console.WriteLine($"Error registrando cuenta: {e.Message}");
            return Resultado.Error($"No se pudo registrar la cuenta: {e.Message}");
        }
    }

    public Resultado EliminarCuenta(string codigoCuenta)
    {
        if (string.IsNullOrWhiteSpace(codigoCuenta))
            return Resultado.Error("C\u00f3digo de cuenta requerido.");

        var cod = codigoCuenta.Trim();
        if (cod == "00900000")
            return Resultado.Error("La cuenta MASTER del banco no se puede eliminar.");

        try
        {
            var filas = _cuentaDAO.Eliminar(cod);
            if (filas == 0)
                return Resultado.Error($"La cuenta {cod} no existe.");

            return Resultado.Ok($"Cuenta {cod} eliminada (junto con sus movimientos).", 0);
        }
        catch (Exception e)
        {
            Console.WriteLine($"Error eliminando cuenta: {e.Message}");
            return Resultado.Error($"No se pudo eliminar la cuenta: {e.Message}");
        }
    }

    public Resultado ConsultarSaldo(string codigoCuenta)
    {
        try
        {
            var c = _cuentaDAO.ObtenerPorCodigo(codigoCuenta);
            if (c == null)
                return Resultado.Error("La cuenta no existe.");

            return Resultado.Ok("Saldo consultado.", (double)c["dec_cuensaldo"]);
        }
        catch (Exception e)
        {
            Console.WriteLine($"Error consultando saldo: {e.Message}");
            return Resultado.Error("Error interno al consultar el saldo.");
        }
    }

    private Resultado Operar(string codigoCuenta, string montoTexto,
                             bool esDeposito, string monedaMonto)
    {
        if (string.IsNullOrWhiteSpace(codigoCuenta))
            return Resultado.Error("C\u00f3digo de cuenta requerido.");

        double montoIngresado;
        try
        {
            montoIngresado = double.Parse(montoTexto);
        }
        catch (FormatException)
        {
            return Resultado.Error("El monto no es un n\u00famero v\u00e1lido.");
        }

        if (montoIngresado <= 0)
            return Resultado.Error("El monto debe ser mayor que cero.");

        using var cn = ConexionBD.CrearConexion();
        cn.Open();
        using var tx = cn.BeginTransaction();
        try
        {
            var cuenta = _cuentaDAO.ObtenerParaActualizar(cn, codigoCuenta);
            if (cuenta == null)
            {
                tx.Rollback();
                return Resultado.Error("La cuenta no existe.");
            }
            if (!"ACTIVO".Equals(cuenta["vch_cuenestado"].ToString(), StringComparison.OrdinalIgnoreCase))
            {
                tx.Rollback();
                return Resultado.Error($"La cuenta no est\u00e1 ACTIVA (estado: {cuenta["vch_cuenestado"]}).");
            }

            var monedaCuenta = cuenta["chr_monecodigo"].ToString()!;
            var monIn = string.IsNullOrWhiteSpace(monedaMonto) ? monedaCuenta : monedaMonto;
            var tasa = _tasaDAO.Tasa(cn, monIn, monedaCuenta);
            var monto = Redondear2(montoIngresado * tasa);
            var huboConversion = monIn != monedaCuenta;

            double delta;
            string tipo;
            if (esDeposito)
            {
                delta = monto;
                tipo = TIPO_DEPOSITO;
            }
            else
            {
                if ((double)cuenta["dec_cuensaldo"] < monto)
                {
                    tx.Rollback();
                    return Resultado.Error($"Saldo insuficiente. Saldo actual: {cuenta["dec_cuensaldo"]}");
                }
                delta = -monto;
                tipo = TIPO_RETIRO;
            }

            var filas = _cuentaDAO.ActualizarSaldo(cn, codigoCuenta, delta);
            if (filas == 0)
            {
                tx.Rollback();
                return Resultado.Error("No se pudo actualizar el saldo.");
            }

            var mov = new MovimientoModel
            {
                CodigoCuenta = codigoCuenta,
                NumeroMovimiento = _movimientoDAO.SiguienteNumero(cn, codigoCuenta),
                FechaMovimiento = DateOnly.FromDateTime(DateTime.Now).ToString(),
                CodigoEmpleado = EMPLEADO_CAJA,
                CodigoTipoMovimiento = tipo,
                ImporteMovimiento = monto,
                CuentaReferencia = null
            };
            if (huboConversion)
            {
                mov.MonedaOrigen = monIn;
                mov.ImporteOrigen = Redondear2(montoIngresado);
                mov.TasaAplicada = tasa;
            }
            _movimientoDAO.Insertar(cn, mov);

            tx.Commit();
            var nuevoSaldo = (double)cuenta["dec_cuensaldo"] + delta;
            var extra = huboConversion
                ? $" ({Redondear2(montoIngresado)} {monIn} \u2192 {monto} {monedaCuenta}, tasa {tasa})"
                : "";
            return Resultado.Ok(
                $"{(esDeposito ? "Dep\u00f3sito" : "Retiro")} realizado correctamente.{extra}",
                nuevoSaldo);
        }
        catch (Exception e)
        {
            Console.WriteLine($"Error en operaci\u00f3n bancaria, se hace rollback: {e.Message}");
            try { tx.Rollback(); } catch { }
            return Resultado.Error("Error interno en la operaci\u00f3n. Se revirtieron los cambios.");
        }
    }

    public Resultado Transferir(string origen, string destino,
                                string montoTexto, string monedaMonto)
    {
        if (string.IsNullOrWhiteSpace(origen) || string.IsNullOrWhiteSpace(destino))
            return Resultado.Error("Cuenta de origen y destino requeridas.");

        origen = origen.Trim();
        destino = destino.Trim();
        if (origen == destino)
            return Resultado.Error("La cuenta destino debe ser distinta a la de origen.");

        double montoIngresado;
        try
        {
            montoIngresado = double.Parse(montoTexto);
        }
        catch (FormatException)
        {
            return Resultado.Error("El monto no es un n\u00famero v\u00e1lido.");
        }

        if (montoIngresado <= 0)
            return Resultado.Error("El monto debe ser mayor que cero.");

        using var cn = ConexionBD.CrearConexion();
        cn.Open();
        using var tx = cn.BeginTransaction();
        try
        {
            var first = origen.CompareTo(destino) < 0 ? origen : destino;
            var second = origen.CompareTo(destino) < 0 ? destino : origen;
            var cFirst = _cuentaDAO.ObtenerParaActualizar(cn, first);
            var cSecond = _cuentaDAO.ObtenerParaActualizar(cn, second);
            var cOrig = origen == first ? cFirst : cSecond;
            var cDest = destino == first ? cFirst : cSecond;

            if (cOrig == null)
            {
                tx.Rollback();
                return Resultado.Error("La cuenta de origen no existe.");
            }
            if (cDest == null)
            {
                tx.Rollback();
                return Resultado.Error("La cuenta de destino no existe.");
            }
            if (!"ACTIVO".Equals(cOrig["vch_cuenestado"].ToString(), StringComparison.OrdinalIgnoreCase))
            {
                tx.Rollback();
                return Resultado.Error("La cuenta de origen no est\u00e1 ACTIVA.");
            }
            if (!"ACTIVO".Equals(cDest["vch_cuenestado"].ToString(), StringComparison.OrdinalIgnoreCase))
            {
                tx.Rollback();
                return Resultado.Error("La cuenta de destino no est\u00e1 ACTIVA.");
            }

            var monOrig = cOrig["chr_monecodigo"].ToString()!;
            var monDest = cDest["chr_monecodigo"].ToString()!;
            var monIn = string.IsNullOrWhiteSpace(monedaMonto) ? monOrig : monedaMonto;
            var tasaOrig = _tasaDAO.Tasa(cn, monIn, monOrig);
            var tasaDest = _tasaDAO.Tasa(cn, monIn, monDest);
            var montoOrigen = Redondear2(montoIngresado * tasaOrig);
            var montoDestino = Redondear2(montoIngresado * tasaDest);
            var convOrig = monIn != monOrig;
            var convDest = monIn != monDest;

            if ((double)cOrig["dec_cuensaldo"] < montoOrigen)
            {
                tx.Rollback();
                return Resultado.Error($"Saldo insuficiente. Saldo actual: {cOrig["dec_cuensaldo"]}");
            }

            if (_cuentaDAO.ActualizarSaldo(cn, origen, -montoOrigen) == 0
                || _cuentaDAO.ActualizarSaldo(cn, destino, montoDestino) == 0)
            {
                tx.Rollback();
                return Resultado.Error("No se pudo actualizar el saldo.");
            }

            var hoy = DateOnly.FromDateTime(DateTime.Now).ToString();

            var salida = new MovimientoModel
            {
                CodigoCuenta = origen,
                NumeroMovimiento = _movimientoDAO.SiguienteNumero(cn, origen),
                FechaMovimiento = hoy,
                CodigoEmpleado = EMPLEADO_CAJA,
                CodigoTipoMovimiento = TIPO_TRANSF_OUT,
                ImporteMovimiento = montoOrigen,
                CuentaReferencia = destino
            };
            if (convOrig)
            {
                salida.MonedaOrigen = monIn;
                salida.ImporteOrigen = Redondear2(montoIngresado);
                salida.TasaAplicada = tasaOrig;
            }
            _movimientoDAO.Insertar(cn, salida);

            var ingreso = new MovimientoModel
            {
                CodigoCuenta = destino,
                NumeroMovimiento = _movimientoDAO.SiguienteNumero(cn, destino),
                FechaMovimiento = hoy,
                CodigoEmpleado = EMPLEADO_CAJA,
                CodigoTipoMovimiento = TIPO_TRANSF_IN,
                ImporteMovimiento = montoDestino,
                CuentaReferencia = origen
            };
            if (convDest)
            {
                ingreso.MonedaOrigen = monIn;
                ingreso.ImporteOrigen = Redondear2(montoIngresado);
                ingreso.TasaAplicada = tasaDest;
            }
            _movimientoDAO.Insertar(cn, ingreso);

            tx.Commit();
            var det = (convOrig || convDest)
                ? $" [{Redondear2(montoIngresado)} {monIn} \u2192 origen {montoOrigen} {monOrig}, destino {montoDestino} {monDest}]"
                : "";
            return Resultado.Ok(
                $"Transferencia de {montoIngresado:F2} {monIn} de {origen} a {destino} realizada correctamente.{det}",
                (double)cOrig["dec_cuensaldo"] - montoOrigen);
        }
        catch (Exception e)
        {
            Console.WriteLine($"Error en transferencia, se hace rollback: {e.Message}");
            try { tx.Rollback(); } catch { }
            return Resultado.Error("Error interno en la transferencia. Se revirtieron los cambios.");
        }
    }
}
