# CollectHub

Plataforma web multiempresa para la gestión y venta de figuras coleccionables.
Proyecto de Desarrollo de Aplicaciones Web y Patrones, Universidad Fidélitas.

**Integrantes:** Gabriel Mijangos Quesada, Carlos Andrés Hernández Sánchez, Josue Fabian Lanuza Portuguez y Arturo Acuña Jara.

## Tecnologías

Java 21, Spring Boot, Thymeleaf, Bootstrap 5 (WebJars), Hibernate/JPA, MySQL, Firebase Storage (imágenes).
Estructura en capas como en clase: `controller`, `service`, `repository`, `domain` y vistas en `templates`.

## Requisitos

- JDK 21
- Maven (o el que trae NetBeans)
- MySQL 8

## Cómo ejecutarlo (cada integrante, una sola vez)

1. Clone el repositorio y abra la carpeta como proyecto Maven (NetBeans, IntelliJ, VS Code...).
2. En MySQL ejecute, en este orden, los scripts de la carpeta `database/`:
   1. `00_crear_bd.sql` (crea la base `collecthub` y el usuario local `collecthub_user`)
   2. `01_base.sql` (tablas base y datos iniciales)
   3. Los demás scripts (`02_...`, `03_...`) a medida que se agreguen módulos.
3. **Credenciales de Firebase (imágenes):** el archivo JSON de credenciales **no está en GitHub**.
   Pídalo por mensaje privado a quien administra el proyecto de Firebase y guárdelo como
   `src/main/resources/firebase/firebase-credentials.json`. Git lo ignora automáticamente.
   Sin ese archivo la aplicación arranca igual, pero no se pueden subir imágenes.
4. Ejecute `CollectHubApplication` (o `mvn spring-boot:run`) y abra <http://localhost:8080>.

Si su MySQL usa otro usuario o clave, copie `application-local.properties.example` como
`application-local.properties` y cambie solo lo necesario (ese archivo tampoco se sube a GitHub).

## Estructura

```
src/main/java/com/collecthub/collecthub
├── CollectHubApplication.java
├── ProjectConfig.java        (idiomas, mensajes, resolver de plantillas)
├── StorageConfig.java        (conexión a Firebase Storage)
├── controller/
├── domain/                   (Empresa, Rol, Usuario ya están creadas)
├── repository/
└── service/                  (FirebaseStorageService ya está creado)
src/main/resources
├── templates/general/fragmentos.html   (head, header/menú, footer, toast, modal de eliminar)
├── templates/<modulo>/                 (una carpeta por módulo: categoria, producto, ...)
├── static/js/modulos/                  (un .js por módulo, si hace falta)
├── messages*.properties                (español por defecto, es y en)
└── firebase/                           (aquí va el JSON local, ignorado por Git)
database/                               (un script .sql por módulo)
```

## Reglas del equipo

1. **Multiempresa:** toda entidad de negocio lleva `@ManyToOne Empresa empresa` (columna `id_empresa`) y
   toda consulta de repositorio/servicio filtra por la empresa del usuario (RN-01 y RN-02).
2. **Estilo de clase:** mismas capas, Lombok, Bootstrap y fragmentos Thymeleaf que en el curso.
   No traer otra estructura ni otras librerías sin acordarlo, porque la rúbrica exige fidelidad a lo visto.
3. **Textos:** nada de texto fijo en las vistas; se agrega la clave en `messages.properties`,
   `messages_es.properties` y `messages_en.properties` (en orden alfabético, para evitar conflictos).
4. **Base de datos:** un script `.sql` por módulo en `database/`. No editar el script de otro módulo sin avisar.
5. **Menú:** el menú de `fragmentos.html` ya apunta a las rutas previstas de cada módulo
   (`/catalogo`, `/categoria/listado`, `/producto/listado`, `/carrito`, `/pedido/listado`, ...).
   Use esas rutas para no tener que editar el menú. Si necesita otra, avise antes de cambiarlo.
6. **Cero secretos en Git:** nada de contraseñas, llaves ni JSON de credenciales en commits.

El flujo de ramas y pull requests está en [CONTRIBUTING.md](CONTRIBUTING.md).

## Alcance del Avance 2 y reparto propuesto

Se implementan las 13 historias de prioridad Alta (HU-01 a HU-13), 52% del backlog.

| # | Historias | Módulo | Responsable |
|---|-----------|--------|-------------|
| 1 | HU-01, HU-02, HU-03, HU-04 | Empresa y acceso (registro de empresa, mi empresa, registro de cliente, login/logout) | _por asignar_ |
| 2 | HU-05, HU-06, HU-13 | Administración (empleados y roles, categorías, ajuste de stock) | _por asignar_ |
| 3 | HU-07, HU-08, HU-09 | Catálogo (productos con imágenes, consulta, búsqueda y filtros) | _por asignar_ |
| 4 | HU-10, HU-11, HU-12 | Compras (carrito, pago, pedidos y estados) | _por asignar_ |

## Estado del avance

- [x] Esqueleto del proyecto, layout Bootstrap/Thymeleaf, idiomas, entidades base
- [ ] Módulo 1 · HU-01, HU-02, HU-03, HU-04
- [ ] Módulo 2 · HU-05, HU-06, HU-13
- [ ] Módulo 3 · HU-07, HU-08, HU-09
- [ ] Módulo 4 · HU-10, HU-11, HU-12

## Pendiente de decidir

- **Spring Security** (autenticación y roles): el proyecto de clase `tienda_semana_8` todavía no lo traía,
  por eso no está en el `pom.xml`. Se agrega cuando se vea en clase, y lo integra quien tenga HU-04.
  Hasta entonces la contraseña no debe guardarse en texto plano en ninguna prueba real.
- **Proyecto de Firebase:** el bucket configurado es el del proyecto de clase. Si el equipo crea uno propio
  para CollectHub, solo cambian `firebase.bucket.name` y el JSON de credenciales.
