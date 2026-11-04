# Sistema de Atención de Turnos

Aplicación de consola en Java para administrar la atención de clientes en una oficina de servicios: registra clientes y servicios, genera turnos y los atiende en orden de llegada (FIFO). Usa una cola con lista enlazada propia, un árbol binario de búsqueda (BST) propio para los clientes y persistencia en SQLite mediante JDBC.

Curso: Programación II

## Integrantes

| Nombre | Carné | Módulo |
|---|---|---|
| Brandon Alexander Gomez Escobar | 3190-25-15022 | Clientes y BST |
| Tania Lisbeth Cortez Perez | 9989-25-6467 | Turnos y cola enlazada |
| Juan Pablo Suram Xol | 0902-22-1013 | Servicios, persistencia y reportes |

## Requisitos

- **Java 21** (JDK). Por ejemplo [Eclipse Temurin 21](https://adoptium.net/temurin/releases/?version=21).
- **Visual Studio Code** con la extensión **Extension Pack for Java** (Microsoft).
- No hace falta instalar Maven ni SQLite: el driver `sqlite-jdbc` está declarado en `pom.xml` y se descarga automáticamente.

## Cómo abrir y ejecutar en VS Code

1. Clonar el repositorio:
   ```
   git clone https://github.com/PbSuram/Sistema-Atencion-de-Turnos.git
   ```
2. En VS Code: **File → Open Folder** y elegir la carpeta del proyecto (la que contiene `pom.xml`).
3. Si pregunta, aceptar **"Yes, I trust the authors"** y esperar a que termine de cargar el proyecto Java.
4. Abrir `src/main/java/com/oficina/turnos/Main.java` y presionar **Run** sobre el método `main`.
5. El menú aparece en la terminal integrada.

> El programa usa la ruta relativa `data/turnos.db`, por eso debe ejecutarse con la carpeta del proyecto abierta en VS Code.

## Base de datos

La base `data/turnos.db` ya viene incluida con los datos iniciales (5 clientes, 3 servicios y **ningún turno**). El programa nunca la borra ni la reinicia al abrir.

### Crear la base desde el script

Si se necesita una base nueva, se crea con `sql/crear_y_cargar.sql`:

- **macOS / Linux** (sqlite3 ya viene instalado en macOS):
  ```
  sqlite3 data/turnos.db < sql/crear_y_cargar.sql
  ```
- **Windows**: descargar *sqlite-tools* desde https://sqlite.org/download.html y ejecutar el mismo comando en la carpeta del proyecto, o abrir el script en *DB Browser for SQLite* (pestaña **Ejecutar SQL**) y guardar como `data/turnos.db`.

El script elimina y vuelve a crear las tablas `clientes`, `servicios` y `turnos`, con sus claves primarias y foráneas, y carga los datos iniciales.

## Estructura del proyecto

```
├── pom.xml                         Java 21 + dependencia sqlite-jdbc
├── data/turnos.db                  Base de datos SQLite
├── sql/crear_y_cargar.sql          Script de creación y carga inicial
└── src/main/java/com/oficina/turnos
    ├── Main.java                   Arma los objetos, carga BST y cola, inicia el menú
    ├── modelo/                     Cliente, Servicio, Turno, EstadoGeneral, EstadoTurno
    ├── estructuras/                NodoBST, ArbolClientes, NodoCola, ColaTurnos
    ├── datos/                      ConexionSQLite, interfaces DAO y sus implementaciones SQLite
    ├── negocio/                    GestorClientes, GestorServicios, GestorTurnos (validaciones)
    ├── reportes/                   ResumenServicio
    ├── excepciones/                OperacionInvalidaException
    └── vista/                      MenuConsola
```

### Decisiones de diseño

- **Separación por capas (MVC / SRP):** la vista solo lee y muestra; los gestores validan las reglas; los DAO acceden a la base.
- **Interfaces:** `ClienteDAO`, `ServicioDAO` y `TurnoDAO` definen las operaciones; las clases `...DAOSQLite` las implementan.
- **Cola propia (`ColaTurnos`):** nodos enlazados con `frente` y `fin`; encolar, desencolar, ver frente, cancelar por número, recorrer y comprobar si está vacía. No usa `LinkedList` ni `ArrayDeque`.
- **BST propio (`ArbolClientes`):** clave = código del cliente; inserción, búsqueda y recorrido inorden recursivos.
- **Colecciones de Java:** `LinkedHashMap` para indexar servicios y para agrupar el resumen por servicio; `ArrayList` para devolver resultados de consultas.
- **Consistencia:** cada operación sigue el orden *validar → guardar en SQLite → actualizar cola/árbol*. Si la base falla, la memoria no cambia.
- **Al iniciar:** se construye el BST con todos los clientes y se reconstruye la cola con los turnos `PENDIENTE` ordenados por número.
- **Números de turno:** `AUTOINCREMENT` de SQLite, por lo que nunca se reutilizan.

## Distribución del trabajo

| Integrante | Clases |
|---|---|
| Brandon Gomez | `EstadoGeneral`, `Cliente`, `NodoBST`, `ArbolClientes`, `ClienteDAO`, `ClienteDAOSQLite`, `GestorClientes` |
| Tania Cortez | `EstadoTurno`, `Turno`, `NodoCola`, `ColaTurnos`, `TurnoDAO`, `TurnoDAOSQLite`, `GestorTurnos` |
| Juan Pablo Suram | `Servicio`, `ConexionSQLite`, `ServicioDAO`, `ServicioDAOSQLite`, `GestorServicios`, `ResumenServicio`, `OperacionInvalidaException`, `pom.xml`, script SQL |
| Integración | `MenuConsola`, `Main` |
| Todo el grupo | Diagrama UML, diagrama entidad-relación, README e informe |

## Secuencia de comprobación

| Paso | Operación | Resultado |
|---|---|---|
| 1 | Buscar cliente 1003 (BST) | María García |
| 2 | Turnos 1001/1, 1002/2, 1003/1, 1004/3, 1005/2 | Turnos 1 a 5 |
| 3 | Otro turno para 1001 | Rechazado: tiene turno pendiente |
| 4 | Cancelar turno 3 | Cola: 1, 2, 4, 5 |
| 5 | Atender dos turnos | 1 y luego 2 |
| 6 | Cerrar y volver a abrir | Cola reconstruida: 4, 5 |
| 7 | Atender los restantes | 4 y luego 5 |
| 8 | Atender otra vez | "No existen turnos pendientes" |
| 9 | Historial de 1003 | Turno 3 CANCELADO |
| 10 | Resumen por servicio | Ver tabla |

| Servicio | Pendientes | Atendidos | Cancelados | Total |
|---|---|---|---|---|
| Información | 0 | 1 | 1 | 2 |
| Entrega de documentos | 0 | 2 | 0 | 2 |
| Actualización de datos | 0 | 1 | 0 | 1 |
| **Total** | **0** | **4** | **1** | **5** |

Las capturas de cada paso están en el informe.

## Diagramas

- Diagrama UML de clases: `docs/diagrama_uml.png`
- Diagrama entidad-relación: `docs/diagrama_er.png`

## Repositorio

https://github.com/PbSuram/Sistema-Atencion-de-Turnos
