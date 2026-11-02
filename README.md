### Aporte de [tu nombre]: HU-05, HU-06 y HU-13, e integración de versiones

**Integración:** se unieron las ramas de HU-01 a HU-04 (login por tienda y roles) y de HU-07 a HU-09
(productos y catálogo) en un solo proyecto, con un único script de base de datos
(`database/collecthub_bd.sql`). Se corrigieron el nombre de una plantilla, el token CSRF del formulario
de productos y el menú de navegación, que ahora cambia según el rol.

**HU-05, empleados con roles y permisos** (`/usuario/listado`): el administrador de la empresa crea y
modifica empleados, les asigna roles (Inventario, Ventas, Consulta) y los activa o desactiva. Solo ve y
modifica personal de su propia empresa, y no puede crear otro administrador.

**HU-06, categorías** (`/categoria/listado`): crear, editar y desactivar categorías, con imagen en Firebase.
La descripción es única por empresa y las categorías no se eliminan.

**HU-13, ajuste de stock** (`/inventario/listado`): se suma o resta stock con un motivo obligatorio. No se
permite stock negativo, cada ajuste queda en un historial por producto y se guarda en una sola transacción.
El rol Consulta solo puede ver el inventario.

**Seguridad:** Spring Security con roles; las rutas se restringen en `SecurityConfig` y la empresa siempre
se toma de la sesión, nunca del navegador.
