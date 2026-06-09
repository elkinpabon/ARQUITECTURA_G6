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

        // Empleado que opera la caja (igual que la version Java: EMPLEADO_CAJA = "0001").
        private const string EMPLEADO_CAJA = "0001";

        /** Redondeo a 2 decimales (medio hacia arriba), como la version Java. */
        private static double Redondear2(double v) => Math.Round(v, 2, MidpointRounding.AwayFromZero);

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
                var cuentaData = cuentaDAO.ObtenerParaActualizar(cn, cuenta, tx);
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
                double tasa = tasaCambioDAO.Tasa(cn, moneda, monedaCuenta, tx);
                double montoConvertido = Redondear2(monto * tasa);
                bool huboConversion = moneda != monedaCuenta;

                cuentaDAO.ActualizarSaldo(cn, cuenta, montoConvertido, tx);

                var movimiento = new MovimientoModel
                {
                    CodigoCuenta = cuenta,
                    NumeroMovimiento = movimientoDAO.SiguienteNumero(cn, cuenta, tx),
                    FechaMovimiento = DateTime.Now.ToString("yyyy-MM-dd"),
                    CodigoEmpleado = EMPLEADO_CAJA,
                    CodigoTipoMovimiento = "003",
                    ImporteMovimiento = montoConvertido,
                    MonedaOrigen = moneda != monedaCuenta ? moneda : string.Empty,
                    ImporteOrigen = moneda != monedaCuenta ? monto : null,
                    TasaAplicada = moneda != monedaCuenta ? tasa : null
                };
                movimientoDAO.Insertar(cn, movimiento, tx);

                tx.Commit();

                var cuentaActualizada = cuentaDAO.ObtenerPorCodigo(cuenta);
                double saldo = cuentaActualizada != null ? Convert.ToDouble(cuentaActualizada["dec_cuensaldo"]) : 0.0;

                string detalleDep = huboConversion
                    ? $" ({Redondear2(monto)} {moneda} -> {montoConvertido} {monedaCuenta}, tasa {tasa})"
                    : string.Empty;
                return Resultado.Ok("Deposito realizado correctamente." + detalleDep, saldo);
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
                var cuentaData = cuentaDAO.ObtenerParaActualizar(cn, cuenta, tx);
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
                double tasa = tasaCambioDAO.Tasa(cn, moneda, monedaCuenta, tx);
                double montoConvertido = Redondear2(monto * tasa);
                bool huboConversion = moneda != monedaCuenta;

                if (saldo < montoConvertido)
                {
                    tx.Rollback();
                    return Resultado.Error("Saldo insuficiente");
                }

                cuentaDAO.ActualizarSaldo(cn, cuenta, -montoConvertido, tx);

                var movimiento = new MovimientoModel
                {
                    CodigoCuenta = cuenta,
                    NumeroMovimiento = movimientoDAO.SiguienteNumero(cn, cuenta, tx),
                    FechaMovimiento = DateTime.Now.ToString("yyyy-MM-dd"),
                    CodigoEmpleado = EMPLEADO_CAJA,
                    CodigoTipoMovimiento = "004",
                    ImporteMovimiento = montoConvertido,
                    MonedaOrigen = moneda != monedaCuenta ? moneda : string.Empty,
                    ImporteOrigen = moneda != monedaCuenta ? monto : null,
                    TasaAplicada = moneda != monedaCuenta ? tasa : null
                };
                movimientoDAO.Insertar(cn, movimiento, tx);

                tx.Commit();

                var cuentaActualizada = cuentaDAO.ObtenerPorCodigo(cuenta);
                double saldoFinal = cuentaActualizada != null ? Convert.ToDouble(cuentaActualizada["dec_cuensaldo"]) : 0.0;

                string detalleRet = huboConversion
                    ? $" ({Redondear2(monto)} {moneda} -> {montoConvertido} {monedaCuenta}, tasa {tasa})"
                    : string.Empty;
                return Resultado.Ok("Retiro realizado correctamente." + detalleRet, saldoFinal);
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
                // Bloqueo en orden deterministico para evitar deadlocks (igual que la version Java).
                string primera = string.CompareOrdinal(origen, destino) < 0 ? origen : destino;
                string segunda = string.CompareOrdinal(origen, destino) < 0 ? destino : origen;
                var dataPrimera = cuentaDAO.ObtenerParaActualizar(cn, primera, tx);
                var dataSegunda = cuentaDAO.ObtenerParaActualizar(cn, segunda, tx);
                var origenData = origen == primera ? dataPrimera : dataSegunda;
                var destinoData = destino == primera ? dataPrimera : dataSegunda;

                if (origenData == null)
                {
                    tx.Rollback();
                    return Resultado.Error("Cuenta origen no existe");
                }
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

                double tasaOrigen = tasaCambioDAO.Tasa(cn, moneda, monedaOrigenCuenta, tx);
                double montoEnOrigen = Redondear2(monto * tasaOrigen);

                if (saldoOrigen < montoEnOrigen)
                {
                    tx.Rollback();
                    return Resultado.Error("Saldo insuficiente en cuenta origen");
                }

                double tasaDestino = tasaCambioDAO.Tasa(cn, moneda, monedaDestinoCuenta, tx);
                double montoEnDestino = Redondear2(monto * tasaDestino);

                cuentaDAO.ActualizarSaldo(cn, origen, -montoEnOrigen, tx);
                cuentaDAO.ActualizarSaldo(cn, destino, montoEnDestino, tx);

                int numOrigen = movimientoDAO.SiguienteNumero(cn, origen, tx);
                int numDestino = movimientoDAO.SiguienteNumero(cn, destino, tx);
                string fecha = DateTime.Now.ToString("yyyy-MM-dd");

                bool conversionOrigen = moneda != monedaOrigenCuenta;
                bool conversionDestino = moneda != monedaDestinoCuenta;

                var movSalida = new MovimientoModel
                {
                    CodigoCuenta = origen,
                    NumeroMovimiento = numOrigen,
                    FechaMovimiento = fecha,
                    CodigoEmpleado = EMPLEADO_CAJA,
                    CodigoTipoMovimiento = "009",
                    ImporteMovimiento = montoEnOrigen,
                    CuentaReferencia = destino,
                    MonedaOrigen = conversionOrigen ? moneda : string.Empty,
                    ImporteOrigen = conversionOrigen ? monto : null,
                    TasaAplicada = conversionOrigen ? tasaOrigen : null
                };
                movimientoDAO.Insertar(cn, movSalida, tx);

                var movIngreso = new MovimientoModel
                {
                    CodigoCuenta = destino,
                    NumeroMovimiento = numDestino,
                    FechaMovimiento = fecha,
                    CodigoEmpleado = EMPLEADO_CAJA,
                    CodigoTipoMovimiento = "008",
                    ImporteMovimiento = montoEnDestino,
                    CuentaReferencia = origen,
                    MonedaOrigen = conversionDestino ? moneda : string.Empty,
                    ImporteOrigen = conversionDestino ? monto : null,
                    TasaAplicada = conversionDestino ? tasaDestino : null
                };
                movimientoDAO.Insertar(cn, movIngreso, tx);

                tx.Commit();

                var cuentaActualizada = cuentaDAO.ObtenerPorCodigo(origen);
                double saldoFinal = cuentaActualizada != null ? Convert.ToDouble(cuentaActualizada["dec_cuensaldo"]) : 0.0;

                string detalleTr = (conversionOrigen || conversionDestino)
                    ? $" [{Redondear2(monto)} {moneda} -> origen {montoEnOrigen} {monedaOrigenCuenta}, destino {montoEnDestino} {monedaDestinoCuenta}]"
                    : string.Empty;
                return Resultado.Ok(
                    $"Transferencia de {monto:F2} {moneda} de {origen} a {destino} realizada correctamente." + detalleTr,
                    saldoFinal);
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
            if (string.IsNullOrWhiteSpace(paterno) || string.IsNullOrWhiteSpace(nombre) ||
                string.IsNullOrWhiteSpace(dni))
                return Resultado.Error("Apellido paterno, nombre y DNI son obligatorios.");

            try
            {
                string codigo = clienteDAO.Insertar(
                    paterno.Trim(), (materno ?? string.Empty).Trim(), nombre.Trim(), dni.Trim(),
                    (ciudad ?? string.Empty).Trim(), (direccion ?? string.Empty).Trim(),
                    (telefono ?? string.Empty).Trim(), (email ?? string.Empty).Trim());
                return Resultado.Ok($"Cliente registrado exitosamente. Codigo: {codigo}");
            }
            catch (Exception ex)
            {
                return Resultado.Error("Error registrando cliente: " + ex.Message);
            }
        }

        public Resultado RegistrarCuenta(string cliente, string moneda)
        {
            if (string.IsNullOrWhiteSpace(cliente))
                return Resultado.Error("Codigo de cliente requerido.");

            string mon = string.IsNullOrWhiteSpace(moneda) ? "02" : moneda.Trim();
            if (mon != "01" && mon != "02")
                return Resultado.Error("Moneda invalida (use 01 Soles o 02 Dolares).");

            if (!clienteDAO.Existe(cliente))
                return Resultado.Error("Cliente no existe");

            try
            {
                string codigo = cuentaDAO.Insertar(cliente, mon);
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
