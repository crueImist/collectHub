/*
  CollectHub - Script de creación de la base de datos (MySQL 8 / MySQL Workbench)
  Basado en el script de TechShop visto en clase: mismo estilo (usuarios, permisos,
  timestamps, ENUM, tablas rol / usuario_rol / ruta / constante) y adaptado a
  una plataforma MULTIEMPRESA: las tablas de negocio llevan id_empresa (RN-01, RN-02).

  Cómo usarlo en Workbench: File > Open SQL Script, y ejecutar todo con el rayo.

  CUIDADO: borra y vuelve a crear la base de datos collecthub y sus usuarios.
  Es solo para el entorno local de desarrollo.

  Clave de TODOS los usuarios de prueba de la aplicación:  Collect123*
*/

-- Sección de administración (ejecutar una vez en un entorno de desarrollo)
drop database if exists collecthub;
drop user if exists collecthub_user;
drop user if exists collecthub_reportes;
drop user if exists 'collecthub_user'@'localhost';   -- por si quedó de una versión anterior del script

-- Creación del esquema
CREATE database collecthub
  DEFAULT CHARACTER SET utf8mb4
  DEFAULT COLLATE utf8mb4_unicode_ci;

-- Creación de usuarios de MySQL (solo para desarrollo local; en producción las claves van fuera del script)
create user 'collecthub_user'@'%' identified by 'Collecthub_2026';
create user 'collecthub_reportes'@'%' identified by 'Collecthub_Reportes_2026';

-- Asignación de permisos
grant select, insert, update, delete on collecthub.* to 'collecthub_user'@'%';
grant select on collecthub.* to 'collecthub_reportes'@'%';
flush privileges;

use collecthub;

-- --- Sección de Creación de Tablas ---

-- Tabla de empresas (inquilinos de la plataforma). Perfil de tienda: HU-02 y HU-12. Configuración comercial: HU-21.
create table empresa (
  id_empresa INT NOT NULL AUTO_INCREMENT,
  nombre VARCHAR(100) NOT NULL,
  identificador VARCHAR(50) NOT NULL,               -- va en la ruta: /tienda/{identificador}
  activo boolean NOT NULL DEFAULT TRUE,             -- false = empresa suspendida (RN-12)
  correo_contacto VARCHAR(100),
  telefono_contacto VARCHAR(30),
  direccion VARCHAR(255),
  logo_ruta varchar(1024),
  moneda CHAR(3) NOT NULL DEFAULT 'CRC',
  color_primario CHAR(7),                           -- ejemplo: #198754
  color_secundario CHAR(7),
  impuesto_porcentaje decimal(5,2) NOT NULL DEFAULT 13.00 CHECK (impuesto_porcentaje >= 0),
  costo_envio decimal(12,2) NOT NULL DEFAULT 0.00 CHECK (costo_envio >= 0),
  fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  fecha_modificacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id_empresa),
  unique (identificador),
  index ndx_identificador (identificador))
  ENGINE = InnoDB;

-- Tabla de roles
create table rol (
  id_rol INT NOT NULL AUTO_INCREMENT,
  rol varchar(30) unique,
  fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  fecha_modificacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  primary key (id_rol))
  ENGINE = InnoDB;

-- Tabla de usuarios. id_empresa es NULL solo para el administrador de la plataforma.
-- username es único en toda la plataforma (lo usa el inicio de sesión); el correo es único dentro de cada empresa (RN-03).
CREATE TABLE usuario (
  id_usuario INT NOT NULL AUTO_INCREMENT,
  id_empresa INT NULL,
  username varchar(50) NOT NULL UNIQUE,
  password varchar(512) NOT NULL,                   -- guardada con hash BCrypt, nunca en texto plano
  nombre VARCHAR(30) NOT NULL,
  apellidos VARCHAR(40) NOT NULL,
  correo VARCHAR(75) NOT NULL,
  telefono VARCHAR(25) NULL,
  ruta_imagen varchar(1024),
  activo boolean NOT NULL DEFAULT TRUE,
  fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  fecha_modificacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id_usuario`),
  UNIQUE (id_empresa, correo),
  CHECK (correo REGEXP '^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$'),
  index ndx_username (username),
  index ndx_usuario_empresa (id_empresa),
  foreign key fk_usuario_empresa (id_empresa) references empresa(id_empresa))
  ENGINE = InnoDB;

-- Tabla de relación entre usuarios y roles
create table usuario_rol (
  id_usuario int not null,
  id_rol INT NOT NULL,
  fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  fecha_modificacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id_usuario,id_rol),
  foreign key fk_usuarioRol_usuario (id_usuario) references usuario(id_usuario),
  foreign key fk_usuarioRol_rol (id_rol) references rol(id_rol))
  ENGINE = InnoDB;

-- Tabla de rutas (qué rol puede entrar a cada ruta)
CREATE TABLE ruta (
    id_ruta INT AUTO_INCREMENT NOT NULL,
    ruta VARCHAR(255) NOT NULL,
    id_rol INT NULL,
    requiere_rol boolean NOT NULL DEFAULT TRUE,
  fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  fecha_modificacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    check (id_rol IS NOT NULL OR requiere_rol = FALSE),
    PRIMARY KEY (id_ruta),
    FOREIGN KEY (id_rol) REFERENCES rol(id_rol))
    ENGINE = InnoDB;

-- Tabla de constantes de la aplicación
CREATE TABLE constante (
    id_constante INT AUTO_INCREMENT NOT NULL,
    atributo VARCHAR(50) NOT NULL,
    valor VARCHAR(150) NOT NULL,
  fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  fecha_modificacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id_constante),
    UNIQUE (atributo))
    ENGINE = InnoDB;

-- Tabla para recuperar la contraseña con un enlace enviado al correo (HU-14)
create table recuperacion_clave (
  id_recuperacion INT NOT NULL AUTO_INCREMENT,
  id_usuario INT NOT NULL,
  token VARCHAR(100) NOT NULL,
  fecha_expira DATETIME NOT NULL,
  usado boolean NOT NULL DEFAULT FALSE,
  fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  fecha_modificacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id_recuperacion),
  unique (token),
  foreign key fk_recuperacion_usuario (id_usuario) references usuario(id_usuario) ON DELETE CASCADE)
  ENGINE = InnoDB;

-- Tabla de categorías (HU-06). La descripción es única dentro de cada empresa.
create table categoria (
  id_categoria INT NOT NULL AUTO_INCREMENT,
  id_empresa INT NOT NULL,
  descripcion VARCHAR(50) NOT NULL,
  ruta_imagen varchar(1024),
  activo boolean NOT NULL DEFAULT TRUE,
  fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  fecha_modificacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id_categoria),
  unique (id_empresa, descripcion),
  index ndx_categoria_empresa (id_empresa),
  foreign key fk_categoria_empresa (id_empresa) references empresa(id_empresa))
  ENGINE = InnoDB;

-- Tabla de productos / figuras coleccionables (HU-07). El código es único dentro de cada empresa.
create table producto (
  id_producto INT NOT NULL AUTO_INCREMENT,
  id_empresa INT NOT NULL,
  id_categoria INT NOT NULL,
  codigo VARCHAR(30) NOT NULL,
  nombre VARCHAR(100) NOT NULL,
  serie_franquicia VARCHAR(100),
  fabricante VARCHAR(100),
  escala VARCHAR(20),                               -- ejemplo: 1/7, 1/8
  descripcion text,
  precio decimal(12,2) NOT NULL CHECK (precio > 0),
  existencias int unsigned NOT NULL DEFAULT 0 CHECK (existencias >= 0),
  stock_minimo int unsigned NOT NULL DEFAULT 0 CHECK (stock_minimo >= 0),   -- alerta de stock bajo (HU-23)
  ruta_imagen varchar(1024),                        -- imagen principal
  activo boolean NOT NULL DEFAULT TRUE,             -- RN-11: con pedidos asociados no se elimina, se inactiva
  fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  fecha_modificacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id_producto),
  unique (id_empresa, codigo),
  index ndx_producto_busqueda (id_empresa, activo, nombre),
  index ndx_producto_categoria (id_empresa, id_categoria),
  foreign key fk_producto_empresa (id_empresa) references empresa(id_empresa),
  foreign key fk_producto_categoria (id_categoria) references categoria(id_categoria))
  ENGINE = InnoDB;

-- Galería de imágenes de cada producto (HU-07 y HU-15)
create table producto_imagen (
  id_imagen INT NOT NULL AUTO_INCREMENT,
  id_producto INT NOT NULL,
  ruta_imagen varchar(1024) NOT NULL,
  orden int NOT NULL DEFAULT 1,
  fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  fecha_modificacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id_imagen),
  index ndx_imagen_producto (id_producto),
  foreign key fk_imagen_producto (id_producto) references producto(id_producto) ON DELETE CASCADE)
  ENGINE = InnoDB;

-- Tabla de carritos (HU-10 y HU-16)
create table carrito (
  id_carrito INT NOT NULL AUTO_INCREMENT,
  id_empresa INT NOT NULL,
  id_usuario INT NOT NULL,                          -- el cliente
  activo boolean NOT NULL DEFAULT TRUE,             -- true = carrito abierto
  fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  fecha_modificacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id_carrito),
  index ndx_carrito_usuario (id_usuario),
  foreign key fk_carrito_empresa (id_empresa) references empresa(id_empresa),
  foreign key fk_carrito_usuario (id_usuario) references usuario(id_usuario))
  ENGINE = InnoDB;

create table carrito_detalle (
  id_carrito_detalle INT NOT NULL AUTO_INCREMENT,
  id_carrito INT NOT NULL,
  id_producto INT NOT NULL,
  cantidad int unsigned NOT NULL CHECK (cantidad > 0),
  fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  fecha_modificacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id_carrito_detalle),
  UNIQUE (id_carrito, id_producto),
  index ndx_cardet_producto (id_producto),
  foreign key fk_cardet_carrito (id_carrito) references carrito(id_carrito) ON DELETE CASCADE,
  foreign key fk_cardet_producto (id_producto) references producto(id_producto))
  ENGINE = InnoDB;

-- Tabla de pedidos (equivale a la factura de TechShop). Estados según RN-06.
create table pedido (
  id_pedido INT NOT NULL AUTO_INCREMENT,
  id_empresa INT NOT NULL,
  id_usuario INT NOT NULL,                          -- el cliente que compró
  fecha TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  estado ENUM('PENDIENTE','PAGADO','EN_PREPARACION','ENVIADO','ENTREGADO','CANCELADO') NOT NULL DEFAULT 'PENDIENTE',
  direccion_entrega VARCHAR(255) NOT NULL,
  metodo_pago VARCHAR(30) NOT NULL,                 -- ejemplo: TARJETA, SINPE, TRANSFERENCIA
  subtotal decimal(12,2) NOT NULL DEFAULT 0 CHECK (subtotal >= 0),
  impuesto decimal(12,2) NOT NULL DEFAULT 0 CHECK (impuesto >= 0),
  costo_envio decimal(12,2) NOT NULL DEFAULT 0 CHECK (costo_envio >= 0),
  total decimal(12,2) NOT NULL DEFAULT 0 CHECK (total >= 0),
  fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  fecha_modificacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id_pedido),
  index ndx_pedido_empresa_estado (id_empresa, estado),
  index ndx_pedido_usuario (id_usuario),
  foreign key fk_pedido_empresa (id_empresa) references empresa(id_empresa),
  foreign key fk_pedido_usuario (id_usuario) references usuario(id_usuario))
  ENGINE = InnoDB;

-- Líneas del pedido (equivale a la tabla venta de TechShop). precio_historico: RN-10.
create table pedido_detalle (
  id_pedido_detalle INT NOT NULL AUTO_INCREMENT,
  id_pedido INT NOT NULL,
  id_producto INT NOT NULL,
  precio_historico decimal(12,2) NOT NULL CHECK (precio_historico >= 0),
  cantidad int unsigned NOT NULL CHECK (cantidad > 0),
  fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  fecha_modificacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id_pedido_detalle),
  UNIQUE (id_pedido, id_producto),
  index ndx_peddet_producto (id_producto),
  foreign key fk_peddet_pedido (id_pedido) references pedido(id_pedido) ON DELETE CASCADE,
  foreign key fk_peddet_producto (id_producto) references producto(id_producto))
  ENGINE = InnoDB;

-- Pago simulado (RN-08)
create table pago (
  id_pago INT NOT NULL AUTO_INCREMENT,
  id_empresa INT NOT NULL,
  id_pedido INT NOT NULL,
  monto decimal(12,2) NOT NULL CHECK (monto > 0),
  referencia VARCHAR(50) NOT NULL,
  estado ENUM('PENDIENTE','APROBADO','RECHAZADO') NOT NULL DEFAULT 'APROBADO',
  fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  fecha_modificacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id_pago),
  index ndx_pago_pedido (id_pedido),
  foreign key fk_pago_empresa (id_empresa) references empresa(id_empresa),
  foreign key fk_pago_pedido (id_pedido) references pedido(id_pedido))
  ENGINE = InnoDB;

-- Movimientos de inventario: entradas, salidas y ajustes con su motivo (HU-13)
create table movimiento_inventario (
  id_movimiento INT NOT NULL AUTO_INCREMENT,
  id_empresa INT NOT NULL,
  id_producto INT NOT NULL,
  id_usuario INT NULL,                              -- quién hizo el ajuste
  id_pedido INT NULL,                               -- si fue por una venta o una cancelación
  tipo ENUM('ENTRADA','SALIDA','AJUSTE') NOT NULL,
  cantidad int unsigned NOT NULL CHECK (cantidad > 0),
  motivo VARCHAR(255) NOT NULL,
  fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  fecha_modificacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id_movimiento),
  index ndx_mov_producto (id_producto),
  foreign key fk_mov_empresa (id_empresa) references empresa(id_empresa),
  foreign key fk_mov_producto (id_producto) references producto(id_producto),
  foreign key fk_mov_usuario (id_usuario) references usuario(id_usuario),
  foreign key fk_mov_pedido (id_pedido) references pedido(id_pedido))
  ENGINE = InnoDB;

-- Lista de deseos (HU-24)
create table lista_deseo (
  id_usuario INT NOT NULL,
  id_producto INT NOT NULL,
  fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  fecha_modificacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id_usuario, id_producto),
  foreign key fk_deseo_usuario (id_usuario) references usuario(id_usuario) ON DELETE CASCADE,
  foreign key fk_deseo_producto (id_producto) references producto(id_producto) ON DELETE CASCADE)
  ENGINE = InnoDB;

-- Bitácora de acciones sensibles: cambios de precio, ajustes, roles, estados de pedido (HU-25)
create table bitacora (
  id_bitacora INT NOT NULL AUTO_INCREMENT,
  id_empresa INT NULL,
  id_usuario INT NULL,
  accion VARCHAR(50) NOT NULL,
  detalle VARCHAR(500),
  fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  fecha_modificacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id_bitacora),
  index ndx_bitacora_empresa (id_empresa),
  foreign key fk_bitacora_empresa (id_empresa) references empresa(id_empresa),
  foreign key fk_bitacora_usuario (id_usuario) references usuario(id_usuario))
  ENGINE = InnoDB;

-- --- Sección de Inserción de Datos ---

-- Inserción de roles (el código de la aplicación les antepone ROLE_, como en clase)
insert into rol (rol) values
 ('ADMIN_PLATAFORMA'), ('ADMIN_EMPRESA'), ('INVENTARIO'), ('VENTAS'), ('CONSULTA'), ('CLIENTE');

-- Inserción de empresas (dos tiendas, para comprobar que los datos no se mezclan entre empresas)
INSERT INTO empresa (nombre, identificador, correo_contacto, telefono_contacto, color_primario, color_secundario) VALUES
 ('Otaku Corner', 'otaku-corner', 'contacto@otakucorner.test', '2222-1111', '#198754', '#0dcaf0'),
 ('Galaxia Figuras', 'galaxia-figuras', 'contacto@galaxiafiguras.test', '2222-2222', '#0d6efd', '#ffc107');

-- Inserción de usuarios. Clave de todos: Collect123*   (hash BCrypt, igual que en clase)
INSERT INTO usuario (id_empresa, username, password, nombre, apellidos, correo, telefono, activo)
 SELECT NULL, 'plataforma', '$2a$10$xTvZ5ZtFH3jfko8JF4OozOStBxhesB3uiVpfFjrl/gWDZQlmpEI8m', 'Admin', 'Plataforma', 'plataforma@collecthub.test', '4000-0000', true;
INSERT INTO usuario (id_empresa, username, password, nombre, apellidos, correo, telefono, activo)
 SELECT e.id_empresa, u.username, '$2a$10$xTvZ5ZtFH3jfko8JF4OozOStBxhesB3uiVpfFjrl/gWDZQlmpEI8m', u.nombre, u.apellidos, u.correo, u.telefono, true
 FROM empresa e
 JOIN (
   SELECT 'otaku-corner' AS identificador, 'admin.otaku' AS username, 'Ana' AS nombre, 'Mora Solano' AS apellidos, 'ana@otakucorner.test' AS correo, '4556-8978' AS telefono
   UNION ALL SELECT 'otaku-corner', 'inventario.otaku', 'Luis', 'Vargas Rojas', 'luis@otakucorner.test', '5456-8789'
   UNION ALL SELECT 'otaku-corner', 'cliente.otaku', 'Carla', 'Jiménez Soto', 'carla@correo.test', '7898-8936'
   UNION ALL SELECT 'galaxia-figuras', 'admin.galaxia', 'Pedro', 'Mena Loria', 'pedro@galaxiafiguras.test', '6012-3344'
   UNION ALL SELECT 'galaxia-figuras', 'cliente.galaxia', 'Sofía', 'Quesada Brenes', 'sofia@correo.test', '8800-1122'
 ) u ON u.identificador = e.identificador COLLATE utf8mb4_unicode_ci;

-- Asignación de roles a usuarios (el administrador de cada empresa tiene también los roles de empleado, como ADMIN en clase)
insert into usuario_rol (id_usuario, id_rol)
 SELECT u.id_usuario, r.id_rol FROM usuario u, rol r
 WHERE (u.username = 'plataforma'     AND r.rol = 'ADMIN_PLATAFORMA')
    OR (u.username = 'admin.otaku'      AND r.rol IN ('ADMIN_EMPRESA','INVENTARIO','VENTAS','CONSULTA'))
    OR (u.username = 'inventario.otaku' AND r.rol IN ('INVENTARIO','CONSULTA'))
    OR (u.username = 'cliente.otaku'    AND r.rol = 'CLIENTE')
    OR (u.username = 'admin.galaxia'      AND r.rol IN ('ADMIN_EMPRESA','INVENTARIO','VENTAS','CONSULTA'))
    OR (u.username = 'cliente.galaxia'    AND r.rol = 'CLIENTE');

-- Inserción de rutas con roles específicos (cada módulo ajusta las suyas)
INSERT INTO ruta (ruta, id_rol)
 SELECT x.ruta, r.id_rol
 FROM (
   SELECT '/plataforma/**' AS ruta, 'ADMIN_PLATAFORMA' AS rol
   UNION ALL SELECT '/ruta/**',               'ADMIN_PLATAFORMA'
   UNION ALL SELECT '/constante/**',          'ADMIN_PLATAFORMA'
   UNION ALL SELECT '/empresa/**',            'ADMIN_EMPRESA'
   UNION ALL SELECT '/usuario/**',            'ADMIN_EMPRESA'
   UNION ALL SELECT '/role/**',               'ADMIN_EMPRESA'
   UNION ALL SELECT '/reportes/**',           'ADMIN_EMPRESA'
   UNION ALL SELECT '/categoria/**',          'INVENTARIO'
   UNION ALL SELECT '/tienda/*/producto/**',  'INVENTARIO'
   UNION ALL SELECT '/inventario/**',         'INVENTARIO'
   UNION ALL SELECT '/pedido/**',             'VENTAS'
   UNION ALL SELECT '/carrito/**',            'CLIENTE'
   UNION ALL SELECT '/mis-pedidos/**',        'CLIENTE'
 ) x
 JOIN rol r ON r.rol = x.rol COLLATE utf8mb4_unicode_ci;

-- Inserción de rutas que no requieren rol
INSERT INTO ruta (ruta,requiere_rol) VALUES
 ('/',false),
 ('/index',false),
 ('/tienda',false),
 ('/tienda/*/catalogo',false),
 ('/login',false),
 ('/registro/**',false),
 ('/errores/**',false),
 ('/403',false),
 ('/fav/**',false),
 ('/js/**',false),
 ('/css/**',false),
 ('/webjars/**',false);

-- Inserción de constantes de la aplicación
INSERT INTO constante (atributo,valor) VALUES
 ('dominio','localhost'),
 ('servidor.http','http://localhost:8080'),
 ('moneda.por.defecto','CRC'),
 ('iva.porcentaje','13');

-- Inserción de categorías (en cada empresa)
INSERT INTO categoria (id_empresa, descripcion, activo) SELECT id_empresa, 'Anime', true FROM empresa;
INSERT INTO categoria (id_empresa, descripcion, activo) SELECT id_empresa, 'Videojuegos', true FROM empresa;
INSERT INTO categoria (id_empresa, descripcion, activo) SELECT id_empresa, 'Películas', true FROM empresa;

-- Inserción de productos (en cada empresa). Incluye agotados y distintos precios para probar los filtros.
INSERT INTO producto (id_empresa, id_categoria, codigo, nombre, serie_franquicia, fabricante, escala, descripcion, precio, existencias, stock_minimo, activo)
SELECT c.id_empresa, c.id_categoria, s.codigo, s.nombre, s.serie, s.fabricante, s.escala, s.descripcion, s.precio, s.existencias, s.stock_min, true
FROM (
    SELECT 'Anime' AS cat, 'ANI-001' AS codigo, 'Figura Goku Ultra Instinto' AS nombre, 'Dragon Ball Super' AS serie, 'Bandai' AS fabricante, '1/8' AS escala, 'Figura de PVC con base.' AS descripcion, 45000.00 AS precio, 10 AS existencias, 3 AS stock_min
    UNION ALL SELECT 'Anime', 'ANI-002', 'Figura Naruto Uzumaki', 'Naruto Shippuden', 'Banpresto', '1/10', 'Pose de combate, edición estándar.', 28000.00, 0, 2
    UNION ALL SELECT 'Anime', 'ANI-003', 'Figura Luffy Gear 5', 'One Piece', 'Bandai', '1/8', 'Edición especial con efectos.', 52000.00, 6, 2
    UNION ALL SELECT 'Videojuegos', 'VID-001', 'Figura Link Tears of the Kingdom', 'The Legend of Zelda', 'Good Smile', '1/7', 'Incluye espada y escudo.', 62000.00, 5, 2
    UNION ALL SELECT 'Videojuegos', 'VID-002', 'Figura Master Chief', 'Halo', 'McFarlane', '1/12', 'Articulada, con rifle.', 38000.00, 4, 1
    UNION ALL SELECT 'Videojuegos', 'VID-003', 'Figura Sonic', 'Sonic the Hedgehog', 'Jakks', '1/8', 'Edición clásica.', 21000.00, 15, 3
    UNION ALL SELECT 'Películas', 'PEL-001', 'Figura Darth Vader', 'Star Wars', 'Hot Toys', '1/6', 'Articulada con capa de tela.', 120000.00, 2, 1
    UNION ALL SELECT 'Películas', 'PEL-002', 'Figura Iron Man Mark 85', 'Marvel', 'Hot Toys', '1/6', 'Con luces LED.', 185000.00, 1, 1
    UNION ALL SELECT 'Películas', 'PEL-003', 'Figura Batman', 'DC', 'NECA', '1/10', 'Edición de colección.', 75000.00, 0, 1
) s
JOIN categoria c ON c.descripcion = s.cat COLLATE utf8mb4_unicode_ci;

-- Inserción de pedidos de ejemplo de la empresa otaku-corner (para probar historial y reportes)
INSERT INTO pedido (id_empresa, id_usuario, fecha, estado, direccion_entrega, metodo_pago, subtotal, impuesto, costo_envio, total)
 SELECT u.id_empresa, u.id_usuario, '2026-10-05 10:30:00', 'ENTREGADO', 'San José, Costa Rica', 'TARJETA', 90000.00, 11700.00, 2500.00, 104200.00
 FROM usuario u WHERE u.username = 'cliente.otaku';
INSERT INTO pedido (id_empresa, id_usuario, fecha, estado, direccion_entrega, metodo_pago, subtotal, impuesto, costo_envio, total)
 SELECT u.id_empresa, u.id_usuario, '2026-10-20 15:10:00', 'PAGADO', 'San José, Costa Rica', 'SINPE', 83000.00, 10790.00, 2500.00, 96290.00
 FROM usuario u WHERE u.username = 'cliente.otaku';

-- Inserción de líneas de pedido (el precio histórico se copia del producto)
INSERT INTO pedido_detalle (id_pedido, id_producto, precio_historico, cantidad)
 SELECT 1, p.id_producto, p.precio, 2 FROM producto p JOIN empresa e ON e.id_empresa = p.id_empresa
 WHERE e.identificador = 'otaku-corner' AND p.codigo = 'ANI-001';
INSERT INTO pedido_detalle (id_pedido, id_producto, precio_historico, cantidad)
 SELECT 2, p.id_producto, p.precio, 1 FROM producto p JOIN empresa e ON e.id_empresa = p.id_empresa
 WHERE e.identificador = 'otaku-corner' AND p.codigo IN ('VID-001', 'VID-003');

-- Inserción de pagos simulados
INSERT INTO pago (id_empresa, id_pedido, monto, referencia, estado)
 SELECT id_empresa, id_pedido, total, CONCAT('SIM-', LPAD(id_pedido, 6, '0')), 'APROBADO' FROM pedido;

-- Verificación rápida
SELECT e.identificador, COUNT(DISTINCT p.id_producto) AS productos, COUNT(DISTINCT u.id_usuario) AS usuarios
FROM empresa e
LEFT JOIN producto p ON p.id_empresa = e.id_empresa
LEFT JOIN usuario u ON u.id_empresa = e.id_empresa
GROUP BY e.identificador;
