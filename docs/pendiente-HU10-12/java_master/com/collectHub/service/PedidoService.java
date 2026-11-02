package com.collectHub.service;

import com.collectHub.domain.*;
import com.collectHub.repository.*;
import java.math.BigDecimal;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final DetallePedidoRepository detallePedidoRepository;
    private final CarritoRepository carritoRepository;
    private final DetalleCarritoRepository detalleCarritoRepository;
    private final UsuarioRepository usuarioRepository;
    private final EmpresaRepository empresaRepository;
    private final ObjectProvider<CatalogoComprasService> catalogoProvider;

    @Transactional
    public Pedido realizarPedido(
            Integer idUsuario,
            Integer idEmpresa,
            DireccionEntrega direccion,
            MetodoPago metodoPago) {

        CatalogoComprasService catalogo = obtenerCatalogo();

        Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new IllegalArgumentException(
                        "El usuario no existe"));

        Empresa empresa = empresaRepository.findById(idEmpresa)
                .orElseThrow(() -> new IllegalArgumentException(
                        "La empresa no existe"));

        if (!usuario.isActivo() || !empresa.isActivo()
                || usuario.getEmpresa() == null
                || !empresa.getIdEmpresa().equals(
                        usuario.getEmpresa().getIdEmpresa())) {
            throw new IllegalArgumentException(
                    "Usuario o empresa no autorizados");
        }

        if (direccion == null || metodoPago == null) {
            throw new IllegalArgumentException(
                    "Debe indicar dirección y método de pago");
        }

        Carrito carrito = carritoRepository
                .findByUsuarioIdUsuarioAndEmpresaIdEmpresaAndActivoTrue(
                        idUsuario, idEmpresa)
                .orElseThrow(() -> new IllegalArgumentException(
                        "El carrito no existe"));

        List<DetalleCarrito> productos = detalleCarritoRepository
                .findByCarritoIdCarrito(carrito.getIdCarrito());

        if (productos.isEmpty()) {
            throw new IllegalArgumentException(
                    "El carrito está vacío");
        }

        Pedido pedido = new Pedido();
        pedido.setUsuario(usuario);
        pedido.setEmpresa(empresa);
        pedido.setDireccionEntrega(direccion);
        pedido.setMetodoPago(metodoPago);
        pedido.setEstado(EstadoPedido.PENDIENTE);

        BigDecimal total = BigDecimal.ZERO;

        // Validacion de  todos los productos antes de guardar el pedido.
        for (DetalleCarrito item : productos) {
            catalogo.validarDisponibilidad(
                    idEmpresa, item.getIdProducto(),
                    item.getCantidad());

            BigDecimal precio = catalogo.obtenerPrecio(
                    idEmpresa, item.getIdProducto());

            if (precio == null || precio.signum() < 0) {
                throw new IllegalArgumentException(
                        "El precio del producto no es válido");
            }

            total = total.add(precio.multiply(
                    BigDecimal.valueOf(item.getCantidad())));
        }

        pedido.setTotal(total);
        pedido = pedidoRepository.save(pedido);

        for (DetalleCarrito item : productos) {

            BigDecimal precio = catalogo.obtenerPrecio(
                    idEmpresa, item.getIdProducto());

            DetallePedido detalle = new DetallePedido();
            detalle.setPedido(pedido);
            detalle.setIdProducto(item.getIdProducto());
            detalle.setCantidad(item.getCantidad());
            detalle.setPrecioUnitario(precio);
            detalle.setSubtotal(precio.multiply(
                    BigDecimal.valueOf(item.getCantidad())));

            detallePedidoRepository.save(detalle);

            catalogo.descontarExistencias(
                    idEmpresa, item.getIdProducto(),
                    item.getCantidad());
        }

        detalleCarritoRepository.deleteAll(productos);

        log.info("Pedido registrado: {}", pedido.getIdPedido());

        return pedido;
    }

    @Transactional(readOnly = true)
    public List<Pedido> getPedidos(Integer idUsuario,
                                   Integer idEmpresa) {

        return pedidoRepository
                .findByUsuarioIdUsuarioAndEmpresaIdEmpresaOrderByFechaPedidoDesc(
                        idUsuario, idEmpresa);
    }

    @Transactional(readOnly = true)
    public Pedido getPedido(Integer idPedido,
                            Integer idUsuario,
                            Integer idEmpresa) {

        return pedidoRepository
                .findByIdPedidoAndUsuarioIdUsuarioAndEmpresaIdEmpresa(
                        idPedido, idUsuario, idEmpresa)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Pedido no encontrado"));
    }

    @Transactional(readOnly = true)
    public List<DetallePedido> getDetalles(Integer idPedido,
                                          Integer idUsuario,
                                          Integer idEmpresa) {

        Pedido pedido = getPedido(
                idPedido, idUsuario, idEmpresa);

        return detallePedidoRepository.findByPedidoIdPedido(
                pedido.getIdPedido());
    }

    @Transactional(readOnly = true)
    public List<Pedido> getPedidosEmpresa(Integer idEmpresa) {

        return pedidoRepository
                .findByEmpresaIdEmpresaOrderByFechaPedidoDesc(
                        idEmpresa);
    }

    @Transactional
    public void cambiarEstado(Integer idPedido,
                              Integer idEmpresa,
                              EstadoPedido nuevoEstado) {

        Pedido pedido = pedidoRepository
                .findByIdPedidoAndEmpresaIdEmpresa(
                        idPedido, idEmpresa)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Pedido no encontrado"));

        if (nuevoEstado == null) {
            throw new IllegalArgumentException(
                    "Debe seleccionar un estado");
        }

        EstadoPedido actual = pedido.getEstado();

        boolean permitido = switch (actual) {
            case PENDIENTE ->
                nuevoEstado == EstadoPedido.CONFIRMADO
                || nuevoEstado == EstadoPedido.CANCELADO;
            case CONFIRMADO ->
                nuevoEstado == EstadoPedido.EN_PREPARACION
                || nuevoEstado == EstadoPedido.CANCELADO;
            case EN_PREPARACION ->
                nuevoEstado == EstadoPedido.ENVIADO;
            case ENVIADO ->
                nuevoEstado == EstadoPedido.ENTREGADO;
            case ENTREGADO, CANCELADO -> false;
        };

        if (!permitido) {
            throw new IllegalArgumentException(
                    "Cambio de estado no permitido");
        }

        pedido.setEstado(nuevoEstado);
        pedidoRepository.save(pedido);
    }

    private CatalogoComprasService obtenerCatalogo() {

        CatalogoComprasService catalogo =
                catalogoProvider.getIfAvailable();

        if (catalogo == null) {
            throw new IllegalStateException(
                    "Falta integrar el módulo de Catálogo");
        }

        return catalogo;
    }
}