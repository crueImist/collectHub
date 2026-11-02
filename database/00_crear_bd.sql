-- ============================================================
-- CollectHub - Paso 1: crear la base de datos y el usuario local
-- Ejecutar UNA vez en su MySQL, con un usuario administrador (root).
-- La clave de collecthub_user es solo para desarrollo local.
-- ============================================================
CREATE DATABASE IF NOT EXISTS collecthub
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

CREATE USER IF NOT EXISTS 'collecthub_user'@'localhost' IDENTIFIED BY 'Collecthub_2026';
GRANT ALL PRIVILEGES ON collecthub.* TO 'collecthub_user'@'localhost';
FLUSH PRIVILEGES;
