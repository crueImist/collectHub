-- ============================================================
-- CollectHub - Paso 2: tablas base (empresa, rol, usuario)
-- Cuidado: borra y vuelve a crear estas tablas (es para su BD local de desarrollo).
--
-- Convención del equipo: un archivo .sql por módulo, para no pisarse en Git.
--   01_base.sql        -> empresa, rol, usuario        (esqueleto)
--   02_catalogo.sql    -> categoria, producto, ...     (quien tenga HU-06, HU-07)
--   03_compras.sql     -> carrito, pedido, pago, ...   (quien tenga HU-10, HU-11, HU-12)
--   04_inventario.sql  -> movimiento_inventario        (quien tenga HU-13)
-- Toda tabla de negocio lleva id_empresa (RN-01, RN-02).
-- ============================================================
USE collecthub;

SET FOREIGN_KEY_CHECKS = 0;
DROP TABLE IF EXISTS usuario;
DROP TABLE IF EXISTS rol;
DROP TABLE IF EXISTS empresa;
SET FOREIGN_KEY_CHECKS = 1;

CREATE TABLE empresa (
    id_empresa     INT AUTO_INCREMENT PRIMARY KEY,
    nombre         VARCHAR(100) NOT NULL,
    identificador  VARCHAR(50)  NOT NULL UNIQUE,
    activo         TINYINT(1)   NOT NULL DEFAULT 1
) ENGINE = InnoDB;

CREATE TABLE rol (
    id_rol  INT AUTO_INCREMENT PRIMARY KEY,
    nombre  VARCHAR(50) NOT NULL UNIQUE
) ENGINE = InnoDB;

CREATE TABLE usuario (
    id_usuario  INT AUTO_INCREMENT PRIMARY KEY,
    id_empresa  INT NULL,                       -- NULL solo para el administrador de la plataforma
    id_rol      INT NOT NULL,
    nombre      VARCHAR(100) NOT NULL,
    correo      VARCHAR(100) NOT NULL,
    contrasena  VARCHAR(255) NOT NULL,
    activo      TINYINT(1)   NOT NULL DEFAULT 1,
    CONSTRAINT uq_usuario_empresa_correo UNIQUE (id_empresa, correo),
    CONSTRAINT fk_usuario_empresa FOREIGN KEY (id_empresa) REFERENCES empresa (id_empresa),
    CONSTRAINT fk_usuario_rol     FOREIGN KEY (id_rol)     REFERENCES rol (id_rol)
) ENGINE = InnoDB;

-- Roles iniciales (ajustables según lo que defina HU-05: roles y permisos de empleados)
INSERT INTO rol (nombre) VALUES
    ('ROLE_ADMIN_PLATAFORMA'),
    ('ROLE_ADMIN_EMPRESA'),
    ('ROLE_INVENTARIO'),
    ('ROLE_VENTAS'),
    ('ROLE_CONSULTA'),
    ('ROLE_CLIENTE');

-- Dos empresas de prueba, para verificar el aislamiento de datos entre empresas
INSERT INTO empresa (nombre, identificador) VALUES
    ('Figuras Demo Uno', 'demo-uno'),
    ('Figuras Demo Dos', 'demo-dos');
