using Microsoft.Data.SqlClient;
using SERVIDOR.Modelo;
using SERVIDOR.Persistencia;

namespace SERVIDOR.Servicio
{
    public class CuentaService
    {
        private readonly CuentaDAO cuentaDAO = new CuentaDAO();
        private readonly MovimientoDAO movimientoDAO = new MovimientoDAO();
        private readonly ClienteDAO clienteDAO = new ClienteDAO();
        private readonly TasaCambioDAO tasaCambioDAO = new TasaCambioDAO();

        public Resultado Depositar(string cuenta, string montoStr, string moneda)
        {
            if (string.IsNullOrWhiteSpace(cuenta) || string.IsNullOrWhiteSpace(montoStr) || string.IsNullOrWhiteSpace(moneda))
                return Resultado.Error("Datos incompletos");

            if (!double.TryParse(montoStr, out double monto) || monto <= 0)
                return Resultado.Error("Monto invalido");

            using var cn = ConexionBD.Conectar();
            using var tx = cn.BeginTransaction();

            try
            {
                var cuentaData = cuentaDAO.ObtenerParaActualizar(cn, cuenta);
                if (cuentaData == null)
                {
                    tx.Rollback();
                    return Resultado.Error("Cuenta no existe");
                }

                string estado = cuentaData["vch_cuenestado"].ToString()!;
                if (!"ACTIVO".Equals(estado, StringComparison.OrdinalIgnoreCase))
                {
                    tx.Rollback();
                    return Resultado.Error("Cuenta no esta activa");
                }

                string monedaCuenta = cuentaData["chr_monecodigo"].ToString()!;
                double tasa = tasaCambioDAO.Tasa(cn, moneda, monedaCuenta);
                double montoConvertido = monto * tasa;

                cuentaDAO.ActualizarSaldo(cn, cuenta, montoConvertido);

                var movimiento = new MovimientoModel
                {
                    CodigoCuenta = cuenta,
                    NumeroMovimiento = movimientoDAO.SiguienteNumero(cn, cuenta),
                    FechaMovimiento = DateTime.Now.ToString("yyyy-MM-dd"),
                    CodigoEmpleado = "9999",
                    CodigoTipoMovimiento = "003",
                    ImporteMovimiento = montoConvertido,
                    MonedaOrigen = moneda != monedaCuenta ? moneda : string.Empty,
                    ImporteOrigen = moneda != monedaCuenta ? monto : null,
                    TasaAplicada = moneda != monedaCuenta ? tasa : null
                };
                movimientoDAO.Insertar(cn, movimiento);

                tx.Commit();

                var cuentaActualizada = cuentaDAO.ObtenerPorCodigo(cuenta);
                double saldo = cuentaActualizada != null ? Convert.ToDouble(cuentaActualizada["dec_cuensaldo"]) : 0.0;

                return Resultado.Ok("Deposito exitoso", saldo);
            }
            catch (Exception ex)
            {
                tx.Rollback();
                return Resultado.Error("Error en deposito: " + ex.Message);
            }
        }

        public Resultado Retirar(string cuenta, string montoStr, string moneda)
        {
            if (string.IsNullOrWhiteSpace(cuenta) || string.IsNullOrWhiteSpace(montoStr) || string.IsNullOrWhiteSpace(moneda))
                return Resultado.Error("Datos incompletos");

            if (!double.TryParse(montoStr, out double monto) || monto <= 0)
                return Resultado.Error("Monto invalido");

            using var cn = ConexionBD.Conectar();
            using var tx = cn.BeginTransaction();

            try
            {
                var cuentaData = cuentaDAO.ObtenerParaActualizar(cn, cuenta);
                if (cuentaData == null)
                {
                    tx.Rollback();
                    return Resultado.Error("Cuenta no existe");
                }

                string estado = cuentaData["vch_cuenestado"].ToString()!;
                if (!"ACTIVO".Equals(estado, StringComparison.OrdinalIgnoreCase))
                {
                    tx.Rollback();
                    return Resultado.Error("Cuenta no esta activa");
                }

                double saldo = Convert.ToDouble(cuentaData["dec_cuensaldo"]);
                string monedaCuenta = cuentaData["chr_monecodigo"].ToString()!;
                double tasa = tasaCambioDAO.Tasa(cn, moneda, monedaCuenta);
                double montoConvertido = monto * tasa;

                if (saldo < montoConvertido)
                {
                    tx.Rollback();
                    return Resultado.Error("Saldo insuficiente");
                }

                cuentaDAO.ActualizarSaldo(cn, cuenta, -montoConvertido);

                var movimiento = new MovimientoModel
                {
                    CodigoCuenta = cuenta,
                    NumeroMovimiento = movimientoDAO.SiguienteNumero(cn, cuenta),
                    FechaMovimiento = DateTime.Now.ToString("yyyy-MM-dd"),
                    CodigoEmpleado = "9999",
                    CodigoTipoMovimiento = "004",
                    ImporteMovimiento = montoConvertido,
                    MonedaOrigen = moneda != monedaCuenta ? moneda : string.Empty,
                    ImporteOrigen = moneda != monedaCuenta ? monto : null,
                    TasaAplicada = moneda != monedaCuenta ? tasa : null
                };
                movimientoDAO.Insertar(cn, movimiento);

                tx.Commit();

                var cuentaActualizada = cuentaDAO.ObtenerPorCodigo(cuenta);
                double saldoFinal = cuentaActualizada != null ? Convert.ToDouble(cuentaActualizada["dec_cuensaldo"]) : 0.0;

                return Resultado.Ok("Retiro exitoso", saldoFinal);
            }
            catch (Exception ex)
            {
                tx.Rollback();
                return Resultado.Error("Error en retiro: " + ex.Message);
            }
        }

        public Resultado ConsultarSaldo(string cuenta)
        {
            if (string.IsNullOrWhiteSpace(cuenta))
                return Resultado.Error("Cuenta no especificada");

            try
            {
                var cuentaData = cuentaDAO.ObtenerPorCodigo(cuenta);
                if (cuentaData == null)
                    return Resultado.Error("Cuenta no existe");

                double saldo = Convert.ToDouble(cuentaData["dec_cuensaldo"]);
                return Resultado.Ok("Consulta exitosa", saldo);
            }
            catch (Exception ex)
            {
                return Resultado.Error("Error en consulta: " + ex.Message);
            }
        }

        public Resultado Transferir(string origen, string destino, string montoStr, string moneda)
        {
            if (string.IsNullOrWhiteSpace(origen) || string.IsNullOrWhiteSpace(destino) ||
                string.IsNullOrWhiteSpace(montoStr) || string.IsNullOrWhiteSpace(moneda))
                return Resultado.Error("Datos incompletos");

            if (!double.TryParse(montoStr, out double monto) || monto <= 0)
                return Resultado.Error("Monto invalido");

            if (origen == destino)
                return Resultado.Error("Cuenta origen y destino no pueden ser la misma");

            using var cn = ConexionBD.Conectar();
            using var tx = cn.BeginTransaction();

            try
            {
                var origenData = cuentaDAO.ObtenerParaActualizar(cn, origen);
                if (origenData == null)
                {
                    tx.Rollback();
                    return Resultado.Error("Cuenta origen no existe");
                }

                var destinoData = cuentaDAO.ObtenerParaActualizar(cn, destino);
                if (destinoData == null)
                {
                    tx.Rollback();
                    return Resultado.Error("Cuenta destino no existe");
                }

                string estadoOrigen = origenData["vch_cuenestado"].ToString()!;
                string estadoDestino = destinoData["vch_cuenestado"].ToString()!;
                if (!"ACTIVO".Equals(estadoOrigen, StringComparison.OrdinalIgnoreCase) ||
                    !"ACTIVO".Equals(estadoDestino, StringComparison.OrdinalIgnoreCase))
                {
                    tx.Rollback();
                    return Resultado.Error("Una de las cuentas no esta activa");
                }

                double saldoOrigen = Convert.ToDouble(origenData["dec_cuensaldo"]);
                string monedaOrigenCuenta = origenData["chr_monecodigo"].ToString()!;
                string monedaDestinoCuenta = destinoData["chr_monecodigo"].ToString()!;

                double tasaOrigen = tasaCambioDAO.Tasa(cn, moneda, monedaOrigenCuenta);
                double montoEnOrigen = monto * tasaOrigen;

                if (saldoOrigen < montoEnOrigen)
                {
                    tx.Rollback();
                    return Resultado.Error("Saldo insuficiente en cuenta origen");
                }

                double tasaDestino = tasaCambioDAO.Tasa(cn, moneda, monedaDestinoCuenta);
                double montoEnDestino = monto * tasaDestino;

                cuentaDAO.ActualizarSaldo(cn, origen, -montoEnOrigen);
                cuentaDAO.ActualizarSaldo(cn, destino, montoEnDestino);

                int numOrigen = movimientoDAO.SiguienteNumero(cn, origen);
                int numDestino = movimientoDAO.SiguienteNumero(cn, destino);
                string fecha = DateTime.Now.ToString("yyyy-MM-dd");

                bool conversionOrigen = moneda != monedaOrigenCuenta;
                bool conversionDestino = moneda != monedaDestinoCuenta;

                var movSalida = new MovimientoModel
                {
                    CodigoCuenta = origen,
                    NumeroMovimiento = numOrigen,
                    FechaMovimiento = fecha,
                    CodigoEmpleado = "9999",
                    CodigoTipoMovimiento = "009",
                    ImporteMovimiento = montoEnOrigen,
                    CuentaReferencia = destino,
                    MonedaOrigen = conversionOrigen ? moneda : string.Empty,
                    ImporteOrigen = conversionOrigen ? monto : null,
                    TasaAplicada = conversionOrigen ? tasaOrigen : null
                };
                movimientoDAO.Insertar(cn, movSalida);

                var movIngreso = new MovimientoModel
                {
                    CodigoCuenta = destino,
                    NumeroMovimiento = numDestino,
                    FechaMovimiento = fecha,
                    CodigoEmpleado = "9999",
                    CodigoTipoMovimiento = "008",
                    ImporteMovimiento = montoEnDestino,
                    CuentaReferencia = origen,
                    MonedaOrigen = conversionDestino ? moneda : string.Empty,
                    ImporteOrigen = conversionDestino ? monto : null,
                    TasaAplicada = conversionDestino ? tasaDestino : null
                };
                movimientoDAO.Insertar(cn, movIngreso);

                tx.Commit();

                var cuentaActualizada = cuentaDAO.ObtenerPorCodigo(origen);
                double saldoFinal = cuentaActualizada != null ? Convert.ToDouble(cuentaActualizada["dec_cuensaldo"]) : 0.0;

                return Resultado.Ok("Transferencia exitosa", saldoFinal);
            }
            catch (Exception ex)
            {
                tx.Rollback();
                return Resultado.Error("Error en transferencia: " + ex.Message);
            }
        }

        public List<CuentaResumen> ListarCuentasPorCliente(string cliente)
        {
            if (string.IsNullOrWhiteSpace(cliente))
                return new List<CuentaResumen>();

            try
            {
                return cuentaDAO.ListarPorCliente(cliente.Trim());
            }
            catch
            {
                return new List<CuentaResumen>();
            }
        }

        public List<ClienteResumen> ListarClientes()
        {
            try
            {
                return clienteDAO.ListarTodos();
            }
            catch
            {
                return new List<ClienteResumen>();
            }
        }

        public Resultado RegistrarCliente(string paterno, string materno, string nombre, string dni,
            string ciudad, string direccion, string telefono, string email)
        {
            if (string.IsNullOrWhiteSpace(paterno) || string.IsNullOrWhiteSpace(materno) ||
                string.IsNullOrWhiteSpace(nombre) || string.IsNullOrWhiteSpace(dni))
                return Resultado.Error("Datos incompletos");

            try
            {
                string codigo = clienteDAO.Insertar(paterno, materno, nombre, dni, ciudad, direccion, telefono, email);
                return Resultado.Ok($"Cliente registrado exitosamente. Codigo: {codigo}");
            }
            catch (Exception ex)
            {
                return Resultado.Error("Error registrando cliente: " + ex.Message);
            }
        }

        public Resultado RegistrarCuenta(string cliente, string moneda)
        {
            if (string.IsNullOrWhiteSpace(cliente) || string.IsNullOrWhiteSpace(moneda))
                return Resultado.Error("Datos incompletos");

            if (!clienteDAO.Existe(cliente))
                return Resultado.Error("Cliente no existe");

            try
            {
                string codigo = cuentaDAO.Insertar(cliente, moneda);
                return Resultado.Ok($"Cuenta registrada exitosamente. Codigo: {codigo}");
            }
            catch (Exception ex)
            {
                return Resultado.Error("Error registrando cuenta: " + ex.Message);
            }
        }

        public Resultado EliminarCuenta(string cuenta)
        {
            if (string.IsNullOrWhiteSpace(cuenta))
                return Resultado.Error("Cuenta no especificada");

            if ("00900000".Equals(cuenta))
                return Resultado.Error("No se puede eliminar la cuenta master");

            if (!cuentaDAO.Existe(cuenta))
                return Resultado.Error("Cuenta no existe");

            try
            {
                cuentaDAO.Eliminar(cuenta);
                return Resultado.Ok("Cuenta eliminada exitosamente");
            }
            catch (Exception ex)
            {
                return Resultado.Error("Error eliminando cuenta: " + ex.Message);
            }
        }
    }
}
