# Flujo de trabajo en Git

Un solo repositorio, con los cuatro integrantes como colaboradores. `main` siempre debe estar estable y arrancar.

## Reglas

- **Nadie hace commit directo a `main`.** Todo entra por pull request.
- Cada pull request lo revisa **al menos otro integrante** antes de integrarse (no se aprueba el propio).
- Una rama por historia de usuario (o por parte grande de una historia).
- Commits pequeños y frecuentes, con mensaje claro. Cada integrante hace sus propios commits con su propio usuario de GitHub.

## Nombres de rama

```
feature/hu-01-registro-empresa
feature/hu-07-productos
feature/hu-10-carrito
fix/descripcion-corta
```

## Mensajes de commit

```
HU-07: agrega entidad Producto y repositorio
HU-07: formulario de producto con subida de imagen
fix: corrige filtro por empresa en categorías
```

## Paso a paso

```bash
git checkout main
git pull                                  # siempre partir de main actualizado
git checkout -b feature/hu-07-productos

# ... trabajar, probar, y luego:
git add .
git commit -m "HU-07: agrega entidad Producto y repositorio"
git push -u origin feature/hu-07-productos
```

Luego, en GitHub: **New pull request** → base `main`, compare con su rama. Se llena la plantilla, se pide
revisión a otro integrante, y cuando lo aprueba se hace **Merge**. Después cada quien actualiza su copia con `git pull` en `main`.

Si hay conflictos al integrar, primero traiga los cambios de `main` a su rama (`git merge main`),
resuélvalos localmente y vuelva a subir.

## Configuración que hace el administrador del repo (una vez)

En GitHub, **Settings → Branches → Add rule** sobre `main`:

- Require a pull request before merging
- Require approvals (1)

Y en **Settings → Collaborators**, agregar a los otros tres integrantes.

## Sugerencia para no pisarse

- Cada módulo tiene sus propios paquetes de clases, su carpeta en `templates/` y su script en `database/`.
- Los archivos compartidos (`fragmentos.html`, `messages*.properties`, `pom.xml`, `ProjectConfig.java`) se tocan lo mínimo
  y en pull requests pequeños, para que los conflictos sean fáciles de resolver.
