# Datos de prueba — EUREKABANK_SOAP_JAVA_GR06

## Base de datos

| Parámetro | Valor |
|---|---|
| Motor | MySQL 9.x (local) |
| Host / Puerto | `localhost:3306` |
| Usuario / Clave | `root` / `admin2002` |
| Base | `eurekabank` |

Carga: `mysql -u root -padmin2002 < "03. BDD/01_estructura.sql"` →
`02_datos.sql` → `03_gr06_seed.sql` → `04_gr06_rol.sql` (rol de acceso) →
`05_gr06_demo_usuarios.sql` (usuarios demo) → `06_gr06_cuenta_master.sql` (cuenta master) →
`07_gr06_tasacambio.sql` (tasas de cambio + detalle de conversión)

> **Moneda:** preferente **Dólares (02)**. Tasa `tasacambio`: 1 USD = 3.75 Soles.
> Depósito/retiro/transferencia: se elige la moneda del monto y el sistema la
> convierte a la moneda de la cuenta; en movimientos el botón 👁 muestra el detalle.

> **Cuenta MASTER:** `00900000` (cliente `00900` BANCO EUREKA MASTER, Dólares,
> saldo 1 000 000, ACTIVO). Sirve como cuenta puente para transferencias.

## Usuarios para iniciar sesión

| Usuario | Clave | Estado |
|---|---|---|
| **monster** | **monster9** | ACTIVO |
| internet | **admin2002** | ACTIVO |
| cromero, lcastro, aramos, cvalencia, rcruz, lpachas, htello, pcarrasco | **admin2002** | ACTIVO |
| creyes, ediaz, csarmiento | admin2002 | **ANULADO** (login debe fallar) |

> Regla: **solo `monster` usa `monster9`**; **todos los demás usuarios usan `admin2002`**.
>
> ⚠️ El **usuario** no distingue mayúsculas, pero la **clave SÍ**.
> La clave viaja en texto plano; el servidor aplica **SHA1** contra la tabla `usuario`.
> Los usuarios ANULADOS deben ser rechazados aunque la clave sea correcta.

## Usuarios de DEMOSTRACIÓN (integrantes + Juan Pérez)

Script `03. BDD/05_gr06_demo_usuarios.sql`. **Clave de todos: `demo123`**.
Rol cliente (solo ven/operan SU cuenta; **no** pueden depositar, sí transferir/retirar).

| Usuario | Clave | Nombre | Cliente | Cuenta | Moneda | Saldo |
|---|---|---|---|---|---|---|
| `jmarin`   | `demo123` | JOSUE MARIN LOPEZ    | 00021 | `00900021` | Dólares | 5000.00 |
| `msalcedo` | `demo123` | MIKAELA SALCEDO RUIZ | 00022 | `00900022` | Dólares | 5000.00 |
| `epabon`   | `demo123` | ELKIN PABON TORRES   | 00023 | `00900023` | Dólares | 5000.00 |
| `jperez`   | `demo123` | JUAN PEREZ GOMEZ     | 00024 | `00900024` | Dólares | 5000.00 |
| **monster** | **monster9** | — (ADMIN) | — | cualquiera | — |

Demo: `monster` busca cualquier cliente; `jmarin` transfiere a `00900024` y
`jperez` ve el ingreso en sus movimientos.

## Cuentas para operar (depósito / retiro / saldo / movimientos)

| Cuenta | Cliente | Saldo inicial | Estado | Clave cuenta |
|---|---|---|---|---|
| `00100001` | 00005 | 7404.00 | ACTIVO | 123456 |
| `00100002` | 00005 | 5002.97 | ACTIVO | 123456 |
| `00200001` | 00008 | 7000.00 | ACTIVO | 123456 |
| `00200002` | 00001 | 6800.00 | ACTIVO | 123456 |
| `00200003` | 00007 | 6000.00 | ACTIVO | 123456 |
| `00300001` | 00010 | 0.00 | **CANCELADO** | 123456 |

> `00300001` sirve para probar el rechazo "La cuenta no está ACTIVA".

## Casos de prueba sugeridos

| Caso | Entrada | Resultado esperado |
|---|---|---|
| Login OK | monster / monster9 | Entra al menú |
| Login clave mala | monster / `Monster9` | "Usuario o clave inválidos." |
| Login otro usuario | cromero / admin2002 | Entra al menú |
| Login ANULADO | creyes / admin2002 | "Usuario o clave inválidos." |
| Depósito | `00200002`, monto 100 | "Depósito realizado correctamente.", saldo +100 |
| Retiro válido | `00200002`, monto 50 | "Retiro realizado correctamente.", saldo −50 |
| Retiro sin saldo | `00200002`, monto 999999 | "Saldo insuficiente. Saldo actual: …" |
| Cuenta cancelada | `00300001`, depósito 10 | "La cuenta no está ACTIVA (estado: CANCELADO)." |
| Monto inválido | `00200002`, monto `abc` | "El monto no es un número válido." |
| Consultar saldo | `00200002` | Muestra 6800.00 |
| Movimientos | `00200002` | Lista con INGRESO/EGRESO |

## Tipos de movimiento (tabla `tipomovimiento`)

| Código | Descripción | Acción |
|---|---|---|
| 001 | Apertura de Cuenta | INGRESO |
| 003 | Depósito | INGRESO |
| 004 | Retiro | SALIDA |
| 005 | Interés | INGRESO |
| 008 | Transferencia | INGRESO |
| 002 / 006 / 007 / 009 / 010 | Cancelar / Manten. / ITF / Transf. / Cargo | SALIDA |

El cliente web clasifica como **INGRESO** los códigos `001,003,005,008`; el resto **EGRESO**.

## URLs

| Recurso | URL |
|---|---|
| Cliente Web | http://localhost:8080/eurekabank_soap_java_cliweb_gr06/ |
| WSDL Login | http://localhost:8080/eurekabank_soap_java_gr06/WSLogin?wsdl |
| WSDL Cuenta | http://localhost:8080/eurekabank_soap_java_gr06/WSCuenta?wsdl |
| WSDL Movimiento | http://localhost:8080/eurekabank_soap_java_gr06/WSMovimiento?wsdl |

> Tras pruebas que modifican `00200002`, restaurar con:
> `UPDATE cuenta SET dec_cuensaldo=6800.00, int_cuencontmov=3 WHERE chr_cuencodigo='00200002';`
> y borrar los movimientos del día de esa cuenta.
