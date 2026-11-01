-- =====================================================================
-- crear_y_cargar.sql
-- Crea la base de datos data/turnos.db con las tablas clientes, servicios
-- y turnos, y la carga con los datos iniciales comunes del proyecto.
-- Este script debe poder ejecutarse sobre una base de datos nueva y
-- dejarla lista, sin turnos registrados.
--
-- Uso (CLI de sqlite3):
--   sqlite3 data/turnos.db < sql/crear_y_cargar.sql
-- =====================================================================

PRAGMA foreign_keys = ON;

-- ---------------------------------------------------------------------
-- Limpieza previa (permite re-ejecutar el script sobre una base nueva)
-- ---------------------------------------------------------------------
DROP TABLE IF EXISTS turnos;
DROP TABLE IF EXISTS servicios;
DROP TABLE IF EXISTS clientes;

-- ---------------------------------------------------------------------
-- Tabla: clientes
-- ---------------------------------------------------------------------
CREATE TABLE clientes (
    codigo      INTEGER PRIMARY KEY,                 -- único e inmodificable
    nombre      TEXT    NOT NULL,
    telefono    TEXT    NOT NULL CHECK (length(telefono) = 8),
    estado      TEXT    NOT NULL DEFAULT 'ACTIVO'
                        CHECK (estado IN ('ACTIVO', 'INACTIVO'))
);

-- ---------------------------------------------------------------------
-- Tabla: servicios
-- ---------------------------------------------------------------------
CREATE TABLE servicios (
    codigo      INTEGER PRIMARY KEY,                 -- único e inmodificable
    nombre      TEXT    NOT NULL,
    estado      TEXT    NOT NULL DEFAULT 'ACTIVO'
                        CHECK (estado IN ('ACTIVO', 'INACTIVO'))
);

-- ---------------------------------------------------------------------
-- Tabla: turnos
-- AUTOINCREMENT garantiza un número único y sin reutilización, incluso
-- si un turno llegara a eliminarse (requisito del proyecto: no reutilizar
-- números ya asignados).
-- ---------------------------------------------------------------------
CREATE TABLE turnos (
    numero              INTEGER PRIMARY KEY AUTOINCREMENT,
    codigo_cliente      INTEGER NOT NULL,
    codigo_servicio     INTEGER NOT NULL,
    fecha_generacion    TEXT    NOT NULL,             -- ISO 8601: 'YYYY-MM-DD HH:MM:SS'
    estado              TEXT    NOT NULL DEFAULT 'PENDIENTE'
                                CHECK (estado IN ('PENDIENTE', 'ATENDIDO', 'CANCELADO')),
    fecha_atencion      TEXT,                         -- NULL hasta que se atienda
    fecha_cancelacion   TEXT,                         -- NULL hasta que se cancele

    FOREIGN KEY (codigo_cliente)  REFERENCES clientes(codigo),
    FOREIGN KEY (codigo_servicio) REFERENCES servicios(codigo)
);

-- Índices de apoyo para las consultas y reportes del punto F.
CREATE INDEX idx_turnos_estado          ON turnos(estado);
CREATE INDEX idx_turnos_cliente         ON turnos(codigo_cliente);
CREATE INDEX idx_turnos_servicio        ON turnos(codigo_servicio);
CREATE INDEX idx_turnos_numero_estado   ON turnos(estado, numero);

-- ---------------------------------------------------------------------
-- Datos iniciales comunes (sin turnos registrados)
-- ---------------------------------------------------------------------
INSERT INTO clientes (codigo, nombre, telefono, estado) VALUES
    (1001, 'Ana López',       '55501001', 'ACTIVO'),
    (1002, 'Carlos Pérez',    '55501002', 'ACTIVO'),
    (1003, 'María García',    '55501003', 'ACTIVO'),
    (1004, 'José Hernández',  '55501004', 'ACTIVO'),
    (1005, 'Luisa Morales',   '55501005', 'ACTIVO');

INSERT INTO servicios (codigo, nombre, estado) VALUES
    (1, 'Información',             'ACTIVO'),
    (2, 'Entrega de documentos',   'ACTIVO'),
    (3, 'Actualización de datos',  'ACTIVO');
