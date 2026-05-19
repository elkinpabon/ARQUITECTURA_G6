# Examen Práctico Primera Unidad — TicketPremium

## Contexto General

La empresa intermediadora **TicketPremium** es la encargada de comercializar boletos para espectáculos deportivos, principalmente partidos de fútbol.

La Federación de Fútbol contrata a TicketPremium para manejar la venta de boletos de todos los partidos del campeonato nacional.

El sistema debe trabajar mediante una arquitectura distribuida basada en:

- RESTful APIs
- .NET
- JSON
- HTTP

La solución debe evidenciar claramente la existencia de:

1. Un sistema servidor central llamado **Federación de Fútbol**
2. Cuatro clientes independientes llamados **TicketPremium**

---

# Restricción Tecnológica

La actividad debe desarrollarse exclusivamente utilizando:

- .NET
- Arquitectura RESTful

No se utilizará SOAP debido a la restricción tecnológica definida para la actividad.

---

# Restricción de Arquitectura MVC (OBLIGATORIO)

El uso de **MVC (Model-View-Controller)** es **OBLIGATORIO** en todos los proyectos.

## Estructura de Carpetas Requerida

Cada proyecto debe implementar la siguiente estructura de carpetas:

```
ProyectoNombre/
│
├── Modelos/
│   ├── Partido.cs
│   ├── Localidad.cs
│   ├── Factura.cs
│   ├── DetalleFactura.cs
│   └── [otros modelos necesarios]
│
├── Vistas/
│   ├── Partidos/
│   ├── Localidades/
│   ├── Ventas/
│   └── [otras vistas necesarias]
│
├── Controladores/
│   ├── PartidosControlador.cs
│   ├── LocalidadesControlador.cs
│   ├── VentasControlador.cs
│   └── [otros controladores necesarios]
│
├── Servicios/
│   ├── IServicioPartido.cs
│   ├── ServicioPartido.cs
│   ├── IServicioLocalidad.cs
│   ├── ServicioLocalidad.cs
│   ├── IServicioVenta.cs
│   ├── ServicioVenta.cs
│   └── [otros servicios necesarios]
│
└── Datos/
    ├── ContextoAplicacion.cs
    ├── [migraciones y configuraciones]
```

## Reglas Obligatorias

1. **Modelos**: Contienen las entidades del dominio
2. **Vistas**: Contienen la presentación (solo para clientes Web y Escritorio)
3. **Controladores**: Contienen la lógica de enrutamiento y coordinación
4. **Servicios**: Contienen la lógica de negocio (implementación de interfaces)
5. **Datos**: Contienen la configuración de base de datos y contexto

## Implementación de Servicios

- **Obligatorio**: Usar interfaces (IServicioPartido, IServicioLocalidad, etc.)
- **Obligatorio**: Inyección de dependencias
- **Obligatorio**: Separación clara entre Controladores y Servicios
- **No permitido**: Lógica de negocio directa en Controladores

---

---

# Arquitectura General

## Sistema Central

### Federación de Fútbol API

Proyecto encargado de:

- Administrar partidos
- Administrar localidades
- Gestionar disponibilidad
- Exponer endpoints RESTful

Tecnología recomendada:

- ASP.NET Core Web API
- SQL Server
- Entity Framework Core

---

# Clientes TicketPremium

Los clientes consumirán la API REST de Federación de Fútbol.

Se deben implementar los siguientes clientes:

1. Consola
2. Escritorio
3. Web
4. Móvil

Todos deben consumir los mismos endpoints REST.

---

# Estructura OBLIGATORIA DE  proyectos

```text
ticketpremium_gr06
│
├── servidor_rest_dotnet_federacion_gr06
│
├── cliente_consola_rest_dotnet_ticketpremium_gr06
│
├── cliente_escritorio_rest_dotnet_ticketpremium_gr06
│
├── cliente_web_rest_dotnet_ticketpremium_gr06
└── cliente_movil_rest_dotnet_ticketpremium_gr06

---

# Sistema 1 — Federación de Fútbol API

## Objetivo

Centralizar la información de partidos, localidades y disponibilidad de boletos.

---

# Base de Datos Federación

## Tabla PARTIDO_FUTBOL

Debe contener:

| Campo | Descripción |
|---|---|
| CODIGO | Código identificador del partido |
| EQUIPO_LOCAL | Nombre del equipo local |
| EQUIPO_VISITA | Nombre del equipo visitante |
| FECHA | Fecha y hora del partido |
| LUGAR | Lugar del encuentro |

---

## Script sugerido

```sql
CREATE TABLE PARTIDO_FUTBOL (
    CODIGO INT PRIMARY KEY,
    EQUIPO_LOCAL VARCHAR(100),
    EQUIPO_VISITA VARCHAR(100),
    FECHA DATETIME,
    LUGAR VARCHAR(150)
);
```

---

## Requisito de rúbrica

- Implementar la tabla
- Insertar mínimo 5 registros de prueba

Valor:
- 0.5 puntos

---

# Tabla LOCALIDAD_PARTIDO

Relacionada con PARTIDO_FUTBOL.

Debe contener:

| Campo | Descripción |
|---|---|
| ID | Identificador |
| CODIGO_PARTIDO | Relación con partido |
| CODIGO_LOCALIDAD | Tipo de localidad |
| DISPONIBILIDAD | Cantidad disponible |
| PRECIO | Precio unitario |

---

## Script sugerido

```sql
CREATE TABLE LOCALIDAD_PARTIDO (
    ID INT PRIMARY KEY IDENTITY,
    CODIGO_PARTIDO INT NOT NULL,
    CODIGO_LOCALIDAD VARCHAR(50),
    DISPONIBILIDAD INT,
    PRECIO DECIMAL(10,2),

    FOREIGN KEY (CODIGO_PARTIDO)
    REFERENCES PARTIDO_FUTBOL(CODIGO)
);
```

---

## Requisito de rúbrica

- Implementar tabla
- Insertar mínimo 20 registros de prueba

Valor:
- 0.5 puntos

---

# Endpoints RESTful Requeridos

---

## 1. Obtener partidos disponibles

### Endpoint

```http
GET /api/partidos/disponibles
```

---

## Condición

Solo deben mostrarse partidos cuya:

```text
FECHA >= fecha actual
```

---

## Valor rúbrica

- 1.0 punto

---

# 2. Obtener localidades disponibles

### Endpoint

```http
GET /api/partidos/{codigoPartido}/localidades
```

---

## Parámetro

- Código del partido

---

## Condición

Solo mostrar localidades con:

```text
DISPONIBILIDAD > 0
```

---

## Datos retornados

- Localidad
- Precio
- Disponibilidad

---

## Valor rúbrica

- 1.0 punto

---

# 3. Registrar compra / disminuir disponibilidad

### Endpoint

```http
POST /api/ventas
```

---

## Responsabilidades del endpoint

Debe:

- Validar disponibilidad
- Registrar venta
- Calcular subtotal
- Calcular IVA
- Calcular total
- Disminuir disponibilidad

---

## Valor rúbrica

- 0.5 puntos

---

# Sistema 2 — Clientes TicketPremium

---

# Funcionalidad 1 — Mostrar partidos disponibles

El cliente debe consumir:

```http
GET /api/partidos/disponibles
```

---

## Valor rúbrica

- 1.0 punto

---

# Funcionalidad 2 — Mostrar localidades

El cliente debe consumir:

```http
GET /api/partidos/{codigoPartido}/localidades
```

---

## Valor rúbrica

- 1.0 punto

---

# Funcionalidad 3 — Compra de boletos

El cliente debe permitir:

- Seleccionar partido
- Seleccionar localidad
- Ingresar cantidad
- Calcular subtotal
- Calcular IVA
- Mostrar total final
- Registrar compra

---

## IVA

```text
IVA = SUBTOTAL * 0.15
```

---

## Total

```text
TOTAL = SUBTOTAL + IVA
```

---

# Base de Datos TicketPremium

## Tabla FACTURA

```sql
CREATE TABLE FACTURA (
    ID_FACTURA INT PRIMARY KEY IDENTITY,
    FECHA DATETIME NOT NULL,
    SUBTOTAL DECIMAL(10,2),
    IVA DECIMAL(10,2),
    TOTAL DECIMAL(10,2)
);
```

---

## Tabla DETALLE_FACTURA

```sql
CREATE TABLE DETALLE_FACTURA (
    ID_DETALLE INT PRIMARY KEY IDENTITY,
    ID_FACTURA INT NOT NULL,
    CODIGO_PARTIDO INT,
    LOCALIDAD VARCHAR(50),
    CANTIDAD INT,
    PRECIO_UNITARIO DECIMAL(10,2),
    TOTAL DECIMAL(10,2),

    FOREIGN KEY (ID_FACTURA)
    REFERENCES FACTURA(ID_FACTURA)
);
```

---

# Clientes Obligatorios

Se deben implementar:

| Cliente | Tecnología sugerida |
|---|---|
| Consola | .NET Console |
| Escritorio | WinForms o WPF |
| Web | ASP.NET MVC / Razor |
| Móvil | .NET MAUI |

---

## Valor rúbrica

- 3.0 puntos

---

# Reporte — Resumen de Ventas de un Partido

Debe mostrar:

| Campo |
|---|
| Partido |
| Fecha |
| Localidad |
| Vendidos |
| Total recaudado |

---

## Ejemplo

| Localidad | Vendidos | Total |
|---|---|---|
| GENERAL | 1456 | 8500 |
| TRIBUNA | 300 | 6000 |
| PALCO | 50 | 1500 |

---

## Implementación permitida

No es necesario:

- PDF
- Exportación
- Crystal Reports
- SSRS

Puede implementarse como:

- Tabla HTML
- Vista Web
- Grid simple

---

## Valor rúbrica

- 1.0 punto

---

# Flujo Completo del Sistema

## Paso 1

Cliente consulta partidos disponibles.

```http
GET /api/partidos/disponibles
```

---

## Paso 2

Usuario selecciona partido.

---

## Paso 3

Cliente consulta localidades.

```http
GET /api/partidos/{id}/localidades
```

---

## Paso 4

Usuario selecciona localidad y cantidad.

---

## Paso 5

Cliente envía compra.

```http
POST /api/ventas
```

---

## Paso 6

API:

- valida disponibilidad
- registra factura
- calcula impuestos
- actualiza disponibilidad

---

# Restricciones Importantes

## Sí se debe hacer

- Dos sistemas separados
- Comunicación REST
- Uso de .NET
- Bases de datos
- Registro de facturas
- Reporte
- Clientes múltiples

---

## No se debe hacer

- CRUD administrativos innecesarios
- Funcionalidades extra
- Exportaciones
- Impresión
- Módulos no solicitados

---

# Rúbrica Oficial

| Ítem | Puntaje |
|---|---|
| Tabla PARTIDO_FUTBOL | 0.5 |
| Tabla LOCALIDAD_PARTIDO | 0.5 |
| Endpoint partidos disponibles | 1.0 |
| Endpoint localidades | 1.0 |
| Cliente muestra partidos | 1.0 |
| Cliente muestra localidades | 1.0 |
| Tablas factura | 0.5 |
| Endpoint decrementa disponibilidad | 0.5 |
| Clientes Consola/Web/Desktop/Móvil | 3.0 |
| Reporte ventas | 1.0 |
| Estructura MVC correcta (Models, Views, Controllers) | 0.5 |
| Servicios implementados con interfaces e inyección de dependencias | 0.5 |
| TOTAL | 10.5 |

---

# Tecnologías Recomendadas

| Componente | Tecnología |
|---|---|
| API | ASP.NET Core Web API |
| ORM | Entity Framework Core |
| Base de Datos | SQL Server |
| Consola | .NET Console |
| Escritorio | WinForms o WPF |
| Web | ASP.NET MVC |
| Móvil | .NET MAUI |

---

# Estructuras de Carpetas Recomendadas por Proyecto

## 1. Servidor (Federación de Fútbol API)

```
servidor_rest_dotnet_federacion_gr06/
│
├── Modelos/
│   ├── PartidoFutbol.cs
│   └── LocalidadPartido.cs
│
├── Controladores/
│   ├── PartidosControlador.cs
│   └── VentasControlador.cs
│
├── Servicios/
│   ├── Interfaces/
│   │   ├── IServicioPartido.cs
│   │   ├── IServicioLocalidad.cs
│   │   └── IServicioVenta.cs
│   ├── ServicioPartido.cs
│   ├── ServicioLocalidad.cs
│   └── ServicioVenta.cs
│
├── Datos/
│   ├── ContextoAplicacion.cs
│   └── Migraciones/
│
├── appsettings.json
└── Program.cs
```

---

## 2. Cliente Consola

```
cliente_consola_rest_dotnet_ticketpremium_gr06/
│
├── Modelos/
│   ├── Partido.cs
│   ├── Localidad.cs
│   └── Factura.cs
│
├── Servicios/
│   ├── Interfaces/
│   │   ├── IServicioApi.cs
│   │   └── IServicioPartido.cs
│   ├── ServicioApi.cs
│   └── ServicioPartido.cs
│
├── Vistas/
│   ├── VistaPrincipal.cs
│   ├── VistaPartidos.cs
│   ├── VistaVentas.cs
│   └── VistaFactura.cs
│
├── Controladores/
│   └── ControladorConsola.cs
│
└── Program.cs
```

---

## 3. Cliente Escritorio (WinForms/WPF)

```
cliente_escritorio_rest_dotnet_ticketpremium_gr06/
│
├── Modelos/
│   ├── Partido.cs
│   ├── Localidad.cs
│   └── Factura.cs
│
├── Servicios/
│   ├── Interfaces/
│   │   ├── IServicioApi.cs
│   │   ├── IServicioPartido.cs
│   │   └── IServicioVenta.cs
│   ├── ServicioApi.cs
│   ├── ServicioPartido.cs
│   └── ServicioVenta.cs
│
├── Vistas/
│   ├── FrmPrincipal.cs
│   ├── FrmPartidos.cs
│   ├── FrmVentas.cs
│   └── FrmFactura.cs
│
├── Controladores/
│   ├── ControladorPartido.cs
│   └── ControladorVenta.cs
│
└── Program.cs
```

---

## 4. Cliente Web (ASP.NET MVC)

```
cliente_web_rest_dotnet_ticketpremium_gr06/
│
├── Modelos/
│   ├── Partido.cs
│   ├── Localidad.cs
│   ├── Factura.cs
│   └── DetalleFactura.cs
│
├── Controladores/
│   ├── ControladorPartidos.cs
│   ├── ControladorVentas.cs
│   └── ControladorInicio.cs
│
├── Vistas/
│   ├── Inicio/
│   │   └── Indice.cshtml
│   ├── Partidos/
│   │   ├── Indice.cshtml
│   │   └── Localidades.cshtml
│   ├── Ventas/
│   │   ├── Comprar.cshtml
│   │   └── Confirmacion.cshtml
│   ├── Compartido/
│   │   ├── _Diseño.cshtml
│   │   └── _BarraNavegacion.cshtml
│
├── Servicios/
│   ├── Interfaces/
│   │   ├── IServicioApi.cs
│   │   ├── IServicioPartido.cs
│   │   └── IServicioVenta.cs
│   ├── ServicioApi.cs
│   ├── ServicioPartido.cs
│   └── ServicioVenta.cs
│
├── www-root/
│   ├── css/
│   ├── js/
│   └── imagenes/
│
├── appsettings.json
└── Program.cs
```

---

## 5. Cliente Móvil (.NET MAUI)

```
cliente_movil_rest_dotnet_ticketpremium_gr06/
│
├── Modelos/
│   ├── Partido.cs
│   ├── Localidad.cs
│   └── Factura.cs
│
├── Servicios/
│   ├── Interfaces/
│   │   ├── IServicioApi.cs
│   │   ├── IServicioPartido.cs
│   │   └── IServicioVenta.cs
│   ├── ServicioApi.cs
│   ├── ServicioPartido.cs
│   └── ServicioVenta.cs
│
├── Vistas/
│   ├── PaginaPrincipal.xaml
│   ├── PaginaPartidos.xaml
│   ├── PaginaLocalidades.xaml
│   ├── PaginaVentas.xaml
│   └── PaginaFactura.xaml
│
├── ModelosVista/
│   ├── ModeloVistaPartido.cs
│   ├── ModeloVistaVenta.cs
│   └── ModeloVistaFactura.cs
│
├── Controladores/
│   └── ControladorNavegacion.cs
│
├── AppShell.xaml
└── ProgramaMaui.cs
```

---

# Objetivo Final

Desarrollar un ecosistema distribuido de venta de boletos utilizando RESTful APIs en .NET, separando claramente el sistema central de la Federación y los distintos clientes TicketPremium.