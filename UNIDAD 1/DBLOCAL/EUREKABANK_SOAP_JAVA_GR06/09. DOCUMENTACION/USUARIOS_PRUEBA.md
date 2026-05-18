# EUREKABANK GR06 — Usuarios de prueba

Cliente web: `http://localhost:8080/eurekabank_soap_java_cliweb_gr06/login`
Login: la clave viaja en texto plano y el servidor compara `SHA1(clave)` contra `usuario.vch_emplclave`.

## Usuarios

| # | Usuario | Clave | Rol | Cliente asociado |
|---|---------|-------|-----|------------------|
| 1 | `monster`   | `monster9`  | **ADMIN** | — (puede ver/operar cualquier cliente) |
| 2 | `cromero`   | `admin2002` | Cliente   | `00001` — ERIC GUSTAVO CORONEL CASTILLO |
| 3 | `cvalencia` | `admin2002` | Cliente   | `00005` — ALAN ALBERTO ARANDA LUNA |

> El rol sale de `usuario.chr_cliecodigo` (NULL = sin cliente). ADMIN es **solo** el usuario `monster`.
> Para mapear más usuarios a clientes: editar los `UPDATE` de `03. BDD/04_gr06_rol.sql`.

## Usuarios de DEMOSTRACIÓN (integrantes + Juan Pérez)

Creados por `03. BDD/05_gr06_demo_usuarios.sql` (idempotente; reinicia saldos a 5000).
**Clave de todos: `demo123`**.

Cada usuario tiene **2 cuentas** (Dólares y Soles) para poder escoger en el selector.
El admin puede crear más con "Registrar cuenta".

| Usuario | Clave | Nombre | Cliente | Cuenta USD | Cuenta Soles |
|---|---|---|---|---|---|
| `jmarin`   | `demo123` | JOSUE MARIN LOPEZ     | 00021 | `00900021` (5000) | `00900031` (3000) |
| `msalcedo` | `demo123` | MIKAELA SALCEDO RUIZ  | 00022 | `00900022` (5000) | `00900032` (3000) |
| `epabon`   | `demo123` | ELKIN PABON TORRES    | 00023 | `00900023` (5000) | `00900033` (3000) |
| `jperez`   | `demo123` | JUAN PEREZ GOMEZ      | 00024 | `00900024` (5000) | `00900034` (3000) |

Guion de demo sugerido:
1. `monster/monster9` → combo con los 24 clientes (incluye a los 4).
2. `jmarin/demo123` → ve solo su cuenta `00900021`; **no** aparece Depositar.
3. `jmarin` transfiere 100 a `00900024` (Juan Pérez) → éxito.
4. `jperez/demo123` → en sus movimientos aparece la **Transferencia INGRESO** de `00900021`.

## Qué puede hacer cada rol

| Funcionalidad | ADMIN (`monster`) | Cliente (`cromero`, `cvalencia`) |
|---|---|---|
| Elegir cliente desde combo con **todos** los registrados | ✅ | ❌ (no ve el combo) |
| Ver sus cuentas y **saldo total** | ✅ (del cliente elegido) | ✅ (solo las suyas, automático al entrar) |
| **Consultar saldo** de una cuenta | ✅ | ✅ (solo sus cuentas) |
| **Depositar** | ✅ | ❌ **solo admin** («Solo el administrador puede depositar») |
| **Retirar** (valida saldo suficiente) | ✅ | ✅ (solo sus cuentas) |
| **Transferir** a otra cuenta del banco (origen propio → cualquier cuenta destino) | ✅ | ✅ (origen = cuenta propia) |
| **Ver / imprimir movimientos** (estado de cuenta, ingresos/egresos separados, totales, botón imprimir) | ✅ | ✅ (solo sus cuentas) |
| Elegir moneda del monto (Dólares preferente) y conversión automática | ✅ | ✅ |
| Ver detalle de conversión (botón 👁 en movimientos) | ✅ | ✅ |
| **Registrar / eliminar cuentas y registrar clientes** | ✅ (sección admin en el menú; MASTER `00900000` protegida) | ❌ bloqueado |
| Operar una cuenta ajena | ✅ (la que busque) | ❌ bloqueado: «No tienes acceso a la cuenta …» |

> **Transferencia:** atómica. Debita el origen, acredita el destino y registra **dos movimientos**
> que se ven en el estado de cuenta de ambas: `009 Transferencia SALIDA` (origen, egreso) y
> `008 Transferencia INGRESO` (destino, ingreso), con `Cta. Ref.` cruzada. Valida saldo suficiente,
> que ambas cuentas existan y estén ACTIVAS, y que origen ≠ destino.

## Operaciones SOAP disponibles (contrato actual)

| Servicio | Operación | Descripción |
|---|---|---|
| `WSLogin` | `iniciarSesion(usuario, clave)` | Autentica (boolean) |
| `WSLogin` | `clienteDeUsuario(usuario)` | Código de cliente del usuario o `""` (admin) |
| `WSCuenta` | `consultarSaldo(cuenta)` | Saldo de la cuenta |
| `WSCuenta` | `depositar(cuenta, monto)` | Depósito (transaccional) |
| `WSCuenta` | `retirar(cuenta, monto)` | Retiro (transaccional, valida saldo) |
| `WSCuenta` | `transferir(origen, destino, monto)` | Transferencia atómica (débito + crédito + 2 movimientos) |
| `WSCuenta` | `listarCuentasPorCliente(criterio)` | Cuentas de un cliente (código o DNI) |
| `WSCuenta` | `listarClientes()` | Todos los clientes registrados |
| `WSCuenta` | `registrarCliente(...)` | Alta de cliente (admin), código autogenerado |
| `WSCuenta` | `registrarCuenta(cliente, moneda)` | Alta de cuenta (admin), saldo 0 ACTIVO |
| `WSCuenta` | `eliminarCuenta(cuenta)` | Borra cuenta + movimientos (admin); MASTER protegida |
| `WSCuenta` | `depositar/retirar/transferir(...,moneda)` | Convierten a la moneda de la cuenta |
| `WSMovimiento` | `listarMovimientos(cuenta)` | Movimientos (incluye detalle de conversión) |

## Cuentas de ejemplo (datos semilla)

| Cuenta | Cliente | Moneda | Estado |
|---|---|---|---|
| `00200002` | 00001 ERIC GUSTAVO | 01 | ACTIVO |
| `00100001` | 00005 ALAN ALBERTO | 01 | ACTIVO |
| `00100002` | 00005 ALAN ALBERTO | 02 | ACTIVO |
| `00200001` | 00008 ROSA LIZET   | 01 | ACTIVO |
| `00300001` | 00010 GABRIEL      | 01 | CANCELADO |

## Notas de despliegue

- Servidor: `eurekabank_soap_java_gr06` · Cliente: `eurekabank_soap_java_cliweb_gr06` (Payara 6).
- El cliente regenera stubs con `wsimport` contra el WSDL en vivo → **redeployar SERVIDOR antes que CLIWEB**.
- Tras cambios de CSS, subir la versión `styles.css?v=N` y recargar con `Cmd+Shift+R`.
