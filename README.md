Prueba los roles iniciando sesión con distintos usuarios. La clave de todos es `Collect123*`.

**Usuarios de prueba**

| Quién | Dónde inicia sesión | Qué debería ver |
|---|---|---|
| plataforma@collecthub.test | `/login` (sin tienda) | Solo "Registrar empresa" |
| ana@otakucorner.test (administradora) | `/tienda/otaku-corner/login` | Mi empresa, Usuarios, Categorías, Productos, Inventario, Pedidos |
| luis@otakucorner.test (Inventario) | `/tienda/otaku-corner/login` | Categorías, Productos, Inventario. No ve Usuarios ni Mi empresa |
| carla@correo.test (cliente) | `/tienda/otaku-corner/login` | Solo catálogo y carrito, sin Administración |
