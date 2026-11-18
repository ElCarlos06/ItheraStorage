-- =============================================================================
-- SIRMA - ItheraStorage: Esquema de Base de Datos para AWS RDS (MySQL 8.0+)
-- Arquitectura Limpia desde Cero (Compatible con Spring Boot + AWS S3)
-- =============================================================================

CREATE DATABASE IF NOT EXISTS sirma
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

USE sirma;

SET FOREIGN_KEY_CHECKS = 0;

-- Limpieza preventiva en orden inverso de dependencias
DROP TABLE IF EXISTS IMAGEN_MANTENIMIENTO;
DROP TABLE IF EXISTS IMAGEN_REPORTE;
DROP TABLE IF EXISTS IMAGEN_PERFIL;
DROP TABLE IF EXISTS IMAGEN_ACTIVO;
DROP TABLE IF EXISTS BITACORA;
DROP TABLE IF EXISTS SOLICITUD_BAJA;
DROP TABLE IF EXISTS MANTENIMIENTO;
DROP TABLE IF EXISTS REPORTE;
DROP TABLE IF EXISTS RESGUARDO_CHECKLIST;
DROP TABLE IF EXISTS RESGUARDO;
DROP TABLE IF EXISTS ACTIVO;
DROP TABLE IF EXISTS TIPO_ACTIVO;
DROP TABLE IF EXISTS USUARIO;
DROP TABLE IF EXISTS ESPACIO;
DROP TABLE IF EXISTS EDIFICIO;
DROP TABLE IF EXISTS AREA;
DROP TABLE IF EXISTS CAMPUS;
DROP TABLE IF EXISTS TIPO_FALLAS;
DROP TABLE IF EXISTS PRIORIDADES;
DROP TABLE IF EXISTS ROL;

-- -----------------------------------------------------------------------------
-- 1. Catálogos de Seguridad y Estructura Organizacional
-- -----------------------------------------------------------------------------

CREATE TABLE ROL (
    id_rol BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(50) NOT NULL UNIQUE,
    descripcion VARCHAR(255)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE CAMPUS (
    id_campus BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    descripcion TEXT,
    es_activo BOOLEAN NOT NULL DEFAULT TRUE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE EDIFICIO (
    id_edificio BIGINT AUTO_INCREMENT PRIMARY KEY,
    id_campus BIGINT NOT NULL,
    nombre VARCHAR(100) NOT NULL,
    es_activo BOOLEAN NOT NULL DEFAULT TRUE,
    CONSTRAINT fk_edificio_campus FOREIGN KEY (id_campus) REFERENCES CAMPUS(id_campus)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE AREA (
    id_area BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL UNIQUE,
    descripcion VARCHAR(255)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE ESPACIO (
    id_espacio BIGINT AUTO_INCREMENT PRIMARY KEY,
    id_edificio BIGINT NOT NULL,
    nombre_espacio VARCHAR(100) NOT NULL,
    tipo_espacio VARCHAR(50),
    descripcion VARCHAR(255),
    es_activo BOOLEAN NOT NULL DEFAULT TRUE,
    CONSTRAINT fk_espacio_edificio FOREIGN KEY (id_edificio) REFERENCES EDIFICIO(id_edificio)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE USUARIO (
    id_usuario BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre_completo VARCHAR(150) NOT NULL,
    correo VARCHAR(100) NOT NULL UNIQUE,
    curp VARCHAR(18) NOT NULL UNIQUE,
    fecha_nacimiento DATE NOT NULL,
    numero_empleado VARCHAR(20) NOT NULL UNIQUE,
    id_rol BIGINT NOT NULL,
    id_area BIGINT NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    primer_login BOOLEAN NOT NULL DEFAULT TRUE,
    es_activo BOOLEAN NOT NULL DEFAULT TRUE,
    creado_en TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_usuario_rol FOREIGN KEY (id_rol) REFERENCES ROL(id_rol),
    CONSTRAINT fk_usuario_area FOREIGN KEY (id_area) REFERENCES AREA(id_area)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- -----------------------------------------------------------------------------
-- 2. Gestión de Activos y Resguardos
-- -----------------------------------------------------------------------------

CREATE TABLE TIPO_ACTIVO (
    id_tipo_activo BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL UNIQUE,
    tipo_bien VARCHAR(50) NOT NULL, -- 'Mueble' | 'Inmueble'
    descripcion VARCHAR(255),
    marca VARCHAR(100),
    modelo VARCHAR(100),
    es_activo BOOLEAN NOT NULL DEFAULT TRUE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE ACTIVO (
    id_activo BIGINT AUTO_INCREMENT PRIMARY KEY,
    etiqueta VARCHAR(50) NOT NULL UNIQUE,
    numero_serie VARCHAR(100) NOT NULL UNIQUE,
    id_tipo_activo BIGINT NOT NULL,
    id_espacio BIGINT NOT NULL,
    estado_custodia VARCHAR(50) NOT NULL DEFAULT 'Disponible',
    estado_operativo VARCHAR(50) NOT NULL DEFAULT 'OK',
    descripcion TEXT,
    costo DECIMAL(10,2),
    qr_codigo VARCHAR(255) UNIQUE,
    fecha_alta DATE NOT NULL,
    es_activo BOOLEAN NOT NULL DEFAULT TRUE,
    CONSTRAINT fk_activo_tipo FOREIGN KEY (id_tipo_activo) REFERENCES TIPO_ACTIVO(id_tipo_activo),
    CONSTRAINT fk_activo_espacio FOREIGN KEY (id_espacio) REFERENCES ESPACIO(id_espacio)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE RESGUARDO (
    id_resguardo BIGINT AUTO_INCREMENT PRIMARY KEY,
    id_activo BIGINT NOT NULL,
    id_usuario_empleado BIGINT NOT NULL,
    id_usuario_admin BIGINT NOT NULL,
    fecha_asignacion DATETIME NOT NULL,
    fecha_confirmacion DATETIME,
    fecha_devolucion DATETIME,
    observaciones_asig TEXT,
    observaciones_conf TEXT,
    observaciones_dev TEXT,
    estado_resguardo VARCHAR(50) NOT NULL DEFAULT 'Pendiente',
    -- Columna calculada que impide duplicar resguardos activos en el mismo activo
    centinela_activo INT GENERATED ALWAYS AS (CASE WHEN estado_resguardo IN ('Pendiente', 'Confirmado') THEN 1 ELSE NULL END) STORED,
    CONSTRAINT fk_resguardo_activo FOREIGN KEY (id_activo) REFERENCES ACTIVO(id_activo),
    CONSTRAINT fk_resguardo_empleado FOREIGN KEY (id_usuario_empleado) REFERENCES USUARIO(id_usuario),
    CONSTRAINT fk_resguardo_admin FOREIGN KEY (id_usuario_admin) REFERENCES USUARIO(id_usuario),
    UNIQUE KEY uq_resguardo_activo_centinela (id_activo, centinela_activo)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE RESGUARDO_CHECKLIST (
    id_checklist BIGINT AUTO_INCREMENT PRIMARY KEY,
    id_resguardo BIGINT NOT NULL,
    item VARCHAR(100) NOT NULL,
    resultado VARCHAR(50) NOT NULL, -- 'OK' | 'Falla' | 'No Aplica'
    CONSTRAINT fk_checklist_resguardo FOREIGN KEY (id_resguardo) REFERENCES RESGUARDO(id_resguardo) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- -----------------------------------------------------------------------------
-- 3. Mantenimiento, Reportes e Incidencias
-- -----------------------------------------------------------------------------

CREATE TABLE PRIORIDADES (
    id_prioridad BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(50) NOT NULL UNIQUE,
    tiempo_resolucion_horas INT,
    es_activo BOOLEAN NOT NULL DEFAULT TRUE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE TIPO_FALLAS (
    id_tipo_falla BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL UNIQUE,
    descripcion VARCHAR(255),
    es_activo BOOLEAN NOT NULL DEFAULT TRUE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE REPORTE (
    id_reporte BIGINT AUTO_INCREMENT PRIMARY KEY,
    id_activo BIGINT NOT NULL,
    id_usuario_reporta BIGINT NOT NULL,
    id_tipo_falla BIGINT NOT NULL,
    id_prioridad BIGINT NOT NULL,
    descripcion_falla TEXT NOT NULL,
    estado_reporte VARCHAR(50) NOT NULL DEFAULT 'Pendiente',
    fecha_reporte TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    -- Columna calculada para evitar reportes concurrentes no resueltos sobre el mismo activo
    centinela_activo INT GENERATED ALWAYS AS (CASE WHEN estado_reporte IN ('Pendiente', 'En Proceso') THEN 1 ELSE NULL END) STORED,
    CONSTRAINT fk_reporte_activo FOREIGN KEY (id_activo) REFERENCES ACTIVO(id_activo),
    CONSTRAINT fk_reporte_usuario FOREIGN KEY (id_usuario_reporta) REFERENCES USUARIO(id_usuario),
    CONSTRAINT fk_reporte_falla FOREIGN KEY (id_tipo_falla) REFERENCES TIPO_FALLAS(id_tipo_falla),
    CONSTRAINT fk_reporte_prioridad FOREIGN KEY (id_prioridad) REFERENCES PRIORIDADES(id_prioridad),
    UNIQUE KEY uq_reporte_activo_centinela (id_activo, centinela_activo)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE MANTENIMIENTO (
    id_mantenimiento BIGINT AUTO_INCREMENT PRIMARY KEY,
    id_reporte BIGINT NOT NULL UNIQUE,
    id_activo BIGINT NOT NULL,
    id_usuario_tecnico BIGINT,
    id_usuario_admin BIGINT NOT NULL,
    id_prioridad BIGINT NOT NULL,
    tipo_asignado VARCHAR(50) NOT NULL, -- 'Correctivo' | 'Preventivo'
    tipo_ejecutado VARCHAR(50),
    diagnostico TEXT,
    acciones_realizadas TEXT,
    piezas_utilizadas TEXT,
    conclusion VARCHAR(50), -- 'Reparado' | 'Irreparable'
    observaciones TEXT,
    costo DECIMAL(10,2),
    fecha_inicio DATETIME,
    fecha_fin DATETIME,
    estado_mantenimiento VARCHAR(50) NOT NULL DEFAULT 'Asignado', -- 'Asignado' | 'En Proceso' | 'Finalizado'
    CONSTRAINT fk_mantenimiento_reporte FOREIGN KEY (id_reporte) REFERENCES REPORTE(id_reporte),
    CONSTRAINT fk_mantenimiento_activo FOREIGN KEY (id_activo) REFERENCES ACTIVO(id_activo),
    CONSTRAINT fk_mantenimiento_tecnico FOREIGN KEY (id_usuario_tecnico) REFERENCES USUARIO(id_usuario),
    CONSTRAINT fk_mantenimiento_admin FOREIGN KEY (id_usuario_admin) REFERENCES USUARIO(id_usuario),
    CONSTRAINT fk_mantenimiento_prioridad FOREIGN KEY (id_prioridad) REFERENCES PRIORIDADES(id_prioridad)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE SOLICITUD_BAJA (
    id_solicitud_baja BIGINT AUTO_INCREMENT PRIMARY KEY,
    id_activo BIGINT NOT NULL,
    id_mantenimiento BIGINT NOT NULL UNIQUE,
    id_usuario_admin BIGINT,
    estado VARCHAR(50) NOT NULL DEFAULT 'Pendiente', -- 'Pendiente' | 'Aprobada' | 'Rechazada'
    justificacion TEXT,
    observaciones_admin TEXT,
    fecha_solicitud TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_resolucion DATETIME,
    -- Impide solicitudes de baja duplicadas en estado Pendiente para el mismo activo
    centinela_pendiente INT GENERATED ALWAYS AS (CASE WHEN estado = 'Pendiente' THEN 1 ELSE NULL END) STORED,
    CONSTRAINT fk_solicitud_activo FOREIGN KEY (id_activo) REFERENCES ACTIVO(id_activo),
    CONSTRAINT fk_solicitud_mantenimiento FOREIGN KEY (id_mantenimiento) REFERENCES MANTENIMIENTO(id_mantenimiento),
    CONSTRAINT fk_solicitud_admin FOREIGN KEY (id_usuario_admin) REFERENCES USUARIO(id_usuario),
    UNIQUE KEY uq_solicitud_baja_centinela (id_activo, centinela_pendiente)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE BITACORA (
    id_bitacora BIGINT AUTO_INCREMENT PRIMARY KEY,
    id_activo BIGINT NOT NULL,
    id_usuario BIGINT NOT NULL,
    tipo_evento VARCHAR(50) NOT NULL,
    descripcion TEXT NOT NULL,
    estado_custodia_anterior VARCHAR(50),
    fecha_evento TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_bitacora_activo FOREIGN KEY (id_activo) REFERENCES ACTIVO(id_activo),
    CONSTRAINT fk_bitacora_usuario FOREIGN KEY (id_usuario) REFERENCES USUARIO(id_usuario)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- -----------------------------------------------------------------------------
-- 4. Almacenamiento de Imágenes y Evidencias (AWS S3)
-- -----------------------------------------------------------------------------

CREATE TABLE IMAGEN_ACTIVO (
    id_imagen_activo BIGINT AUTO_INCREMENT PRIMARY KEY,
    id_activo BIGINT NOT NULL,
    url_imagen VARCHAR(500) NOT NULL,
    storage_key VARCHAR(255) NOT NULL,
    nombre_archivo VARCHAR(255),
    fecha_subida TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_imagen_activo FOREIGN KEY (id_activo) REFERENCES ACTIVO(id_activo) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IMAGEN_PERFIL (
    id_imagen BIGINT AUTO_INCREMENT PRIMARY KEY,
    id_usuario BIGINT NOT NULL,
    url_imagen VARCHAR(500) NOT NULL,
    storage_key VARCHAR(255) NOT NULL,
    nombre_archivo VARCHAR(255),
    fecha_subida TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_imagen_usuario FOREIGN KEY (id_usuario) REFERENCES USUARIO(id_usuario) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IMAGEN_REPORTE (
    id_imagen_reporte BIGINT AUTO_INCREMENT PRIMARY KEY,
    id_reporte BIGINT NOT NULL,
    url_imagen VARCHAR(500) NOT NULL,
    storage_key VARCHAR(255) NOT NULL,
    nombre_archivo VARCHAR(255),
    fecha_subida TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_imagen_reporte FOREIGN KEY (id_reporte) REFERENCES REPORTE(id_reporte) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IMAGEN_MANTENIMIENTO (
    id_imagen_mant BIGINT AUTO_INCREMENT PRIMARY KEY,
    id_mantenimiento BIGINT NOT NULL,
    url_imagen VARCHAR(500) NOT NULL,
    storage_key VARCHAR(255) NOT NULL,
    nombre_archivo VARCHAR(255),
    fecha_subida TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_imagen_mantenimiento FOREIGN KEY (id_mantenimiento) REFERENCES MANTENIMIENTO(id_mantenimiento) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

SET FOREIGN_KEY_CHECKS = 1;

-- =============================================================================
-- 5. DATOS SEMILLA ESENCIALES (Para que la app funcione inmediatamente sin errores)
-- =============================================================================

-- A. Roles del Sistema
INSERT INTO ROL (id_rol, nombre, descripcion) VALUES
(1, 'Administrador', 'Control total y supervisión general del sistema SIRMA'),
(2, 'Tecnico', 'Atención y resolución de reportes y mantenimientos'),
(3, 'Empleado', 'Usuario institucional con activos bajo resguardo')
ON DUPLICATE KEY UPDATE nombre=VALUES(nombre);

-- B. Niveles de Prioridad
INSERT INTO PRIORIDADES (id_prioridad, nombre, tiempo_resolucion_horas, es_activo) VALUES
(1, 'Baja', 72, TRUE),
(2, 'Media', 48, TRUE),
(3, 'Alta', 24, TRUE),
(4, 'Urgente', 4, TRUE)
ON DUPLICATE KEY UPDATE nombre=VALUES(nombre);

-- C. Catálogo de Tipos de Fallas
INSERT INTO TIPO_FALLAS (id_tipo_falla, nombre, descripcion, es_activo) VALUES
(1, 'Falla de Hardware', 'Problemas físicos con componentes de equipo', TRUE),
(2, 'Falla de Software', 'Problemas de sistema operativo o aplicaciones instaladas', TRUE),
(3, 'Red y Conectividad', 'Pérdida de enlace, puerto dañado o problemas de red', TRUE),
(4, 'Eléctrica', 'Problemas de alimentación, fusible, cable de poder o corto', TRUE),
(5, 'Física / Mecánica', 'Daño estructural, golpes, bisagras o roturas', TRUE),
(6, 'Otro', 'Incidencias diversas', TRUE)
ON DUPLICATE KEY UPDATE nombre=VALUES(nombre);

-- D. Estructura de Ubicaciones Inicial
INSERT INTO CAMPUS (id_campus, nombre, descripcion, es_activo) VALUES
(1, 'Campus Principal', 'Sede central institucional', TRUE)
ON DUPLICATE KEY UPDATE nombre=VALUES(nombre);

INSERT INTO EDIFICIO (id_edificio, id_campus, nombre, es_activo) VALUES
(1, 1, 'Edificio A - Administrativo', TRUE),
(2, 1, 'Edificio B - Docencia y Laboratorios', TRUE)
ON DUPLICATE KEY UPDATE nombre=VALUES(nombre);

INSERT INTO AREA (id_area, nombre, descripcion) VALUES
(1, 'Tecnologías de la Información', 'Departamento de sistemas, soporte y telecomunicaciones'),
(2, 'Recursos Humanos', 'Gestión de personal y talento institucional'),
(3, 'Dirección General', 'Alta dirección y coordinación'),
(4, 'Mantenimiento e Infraestructura', 'Servicios generales y mantenimiento de inmuebles')
ON DUPLICATE KEY UPDATE nombre=VALUES(nombre);

INSERT INTO ESPACIO (id_espacio, id_edificio, nombre_espacio, tipo_espacio, descripcion, es_activo) VALUES
(1, 1, 'Almacén General', 'Bodega', 'Área de recepción y custodia inicial de activos', TRUE),
(2, 1, 'Oficina de TI', 'Oficina', 'Área técnica y soporte informático', TRUE),
(3, 2, 'Laboratorio 1', 'Laboratorio', 'Laboratorio de cómputo y prácticas', TRUE)
ON DUPLICATE KEY UPDATE nombre=VALUES(nombre);

-- E. Tipos de Activos Iniciales (Para permitir altas inmediatas de bienes muebles)
INSERT INTO TIPO_ACTIVO (id_tipo_activo, nombre, tipo_bien, descripcion, marca, modelo, es_activo) VALUES
(1, 'Laptop', 'Mueble', 'Computadora portátil institucional', 'Dell', 'Latitude 5420', TRUE),
(2, 'Computadora de Escritorio', 'Mueble', 'Equipo de cómputo fijo', 'HP', 'ProDesk 400', TRUE),
(3, 'Proyector', 'Mueble', 'Proyector audiovisual', 'Epson', 'PowerLite E20', TRUE),
(4, 'Silla Ergonómica', 'Mueble', 'Mobiliario de oficina', 'Genérica', 'Ejecutiva', TRUE),
(5, 'Impresora Multifuncional', 'Mueble', 'Impresora láser de red', 'HP', 'LaserJet Pro', TRUE)
ON DUPLICATE KEY UPDATE nombre=VALUES(nombre);

-- F. Usuario Administrador Inicial para Iniciar Sesión Inmediatamente
-- Credenciales:
--   Correo:      admin@sirma.com
--   Contraseña:  admin123
--   Password Hash: BCrypt de 'admin123'
INSERT INTO USUARIO (id_usuario, nombre_completo, correo, curp, fecha_nacimiento, numero_empleado, id_rol, id_area, password_hash, primer_login, es_activo)
VALUES (
    1,
    'Administrador del Sistema',
    'admin@sirma.com',
    'ADMIN000000XXXX01',
    '1990-01-01',
    'ADM0001',
    1, -- Rol Administrador
    1, -- Área TI
    '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG', -- admin123
    FALSE, -- Sin bloqueo de primer login
    TRUE
)
ON DUPLICATE KEY UPDATE correo=VALUES(correo);

-- G. Usuario Técnico de Prueba
--   Correo:      tecnico@sirma.com
--   Contraseña:  admin123
INSERT INTO USUARIO (id_usuario, nombre_completo, correo, curp, fecha_nacimiento, numero_empleado, id_rol, id_area, password_hash, primer_login, es_activo)
VALUES (
    2,
    'Técnico de Soporte',
    'tecnico@sirma.com',
    'TECNI000000XXXX02',
    '1992-05-15',
    'TEC0001',
    2, -- Rol Tecnico
    1, -- Área TI
    '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG', -- admin123
    FALSE,
    TRUE
)
ON DUPLICATE KEY UPDATE correo=VALUES(correo);

-- H. Usuario Empleado de Prueba (Para pruebas de resguardos)
--   Correo:      empleado@sirma.com
--   Contraseña:  admin123
INSERT INTO USUARIO (id_usuario, nombre_completo, correo, curp, fecha_nacimiento, numero_empleado, id_rol, id_area, password_hash, primer_login, es_activo)
VALUES (
    3,
    'Empleado Institucional',
    'empleado@sirma.com',
    'EMPLE000000XXXX03',
    '1995-10-20',
    'EMP0001',
    3, -- Rol Empleado
    2, -- Área RH
    '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG', -- admin123
    FALSE,
    TRUE
)
ON DUPLICATE KEY UPDATE correo=VALUES(correo);
