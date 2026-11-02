package com.collectHub.service;

import com.collectHub.domain.Carrito;
import com.collectHub.domain.DetalleCarrito;
import com.collectHub.domain.Empresa;
import com.collectHub.domain.Usuario;
import com.collectHub.repository.CarritoRepository;
import com.collectHub.repository.DetalleCarritoRepository;
import com.collectHub.repository.EmpresaRepository;
import com.collectHub.repository.UsuarioRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CarritoService {
    private final CarritoRepository carritoRepository;
    private final DetalleCarritoRepository detalleCarritoRepository;
    private final UsuarioRepository usuarioRepository;
    private final EmpresaRepository empresaRepository;

    @Transactional
    public Carrito obtenerCarrito(Integer idUsuario,
                                  Integer idEmpresa) {

        validarUsuarioEmpresa(idUsuario, idEmpresa);

        return carritoRepository
                .findByUsuarioIdUsuarioAndEmpresaIdEmpresaAndActivoTrue(
                        idUsuario, idEmpresa)
                .orElseGet(() -> crearCarrito(idUsuario, idEmpresa));
    }

    private Carrito crearCarrito(Integer idUsuario,
                                 Integer idEmpresa) {

        Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "El usuario no existe"));

        Empresa empresa = empresaRepository.findById(idEmpresa)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "La empresa no existe"));

        Carrito carrito = new Carrito();
        carrito.setUsuario(usuario);
        carrito.setEmpresa(empresa);
        carrito.setActivo(true);

        return carritoRepository.save(carrito);
    }

    @Transactional
    public void agregarProducto(Integer idUsuario,
                                Integer idEmpresa,
                                Integer idProducto,
                                Integer cantidad) {

        validarProductoCantidad(idProducto, cantidad);

        Carrito carrito = obtenerCarrito(idUsuario, idEmpresa);

        DetalleCarrito detalle = detalleCarritoRepository
                .findByCarritoIdCarritoAndIdProducto(
                        carrito.getIdCarrito(), idProducto)
                .orElse(null);

        if (detalle != null) {
            detalle.setCantidad(
                    Math.addExact(detalle.getCantidad(), cantidad));
        } else {
            detalle = new DetalleCarrito();
            detalle.setCarrito(carrito);
            detalle.setIdProducto(idProducto);
            detalle.setCantidad(cantidad);
        }

        detalleCarritoRepository.save(detalle);

        log.info("Producto agregado al carrito: {}", idProducto);
    }

    @Transactional(readOnly = true)
    public List<DetalleCarrito> getDetalles(Integer idUsuario,
                                           Integer idEmpresa) {

        validarUsuarioEmpresa(idUsuario, idEmpresa);

        return carritoRepository
                .findByUsuarioIdUsuarioAndEmpresaIdEmpresaAndActivoTrue(
                        idUsuario, idEmpresa)
                .map(carrito -> detalleCarritoRepository
                        .findByCarritoIdCarrito(carrito.getIdCarrito()))
                .orElseGet(List::of);
    }

    @Transactional
    public void actualizarCantidad(Integer idUsuario,
                                   Integer idEmpresa,
                                   Integer idDetalleCarrito,
                                   Integer cantidad) {

        if (cantidad == null || cantidad <= 0) {
            throw new IllegalArgumentException(
                    "La cantidad debe ser mayor que cero");
        }

        DetalleCarrito detalle = obtenerDetallePropio(
                idUsuario, idEmpresa, idDetalleCarrito);

        detalle.setCantidad(cantidad);
        detalleCarritoRepository.save(detalle);
    }

    @Transactional
    public void eliminarProducto(Integer idUsuario,
                                 Integer idEmpresa,
                                 Integer idDetalleCarrito) {

        DetalleCarrito detalle = obtenerDetallePropio(
                idUsuario, idEmpresa, idDetalleCarrito);

        detalleCarritoRepository.delete(detalle);
    }

    @Transactional
    public void vaciarCarrito(Integer idUsuario,
                              Integer idEmpresa) {

        validarUsuarioEmpresa(idUsuario, idEmpresa);

        carritoRepository
                .findByUsuarioIdUsuarioAndEmpresaIdEmpresaAndActivoTrue(
                        idUsuario, idEmpresa)
                .ifPresent(carrito -> {
                    List<DetalleCarrito> detalles =
                            detalleCarritoRepository
                                    .findByCarritoIdCarrito(
                                            carrito.getIdCarrito());

                    detalleCarritoRepository.deleteAll(detalles);
                });
    }

    private DetalleCarrito obtenerDetallePropio(
            Integer idUsuario,
            Integer idEmpresa,
            Integer idDetalleCarrito) {

        validarUsuarioEmpresa(idUsuario, idEmpresa);

        DetalleCarrito detalle = detalleCarritoRepository
                .findById(idDetalleCarrito)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "El producto no está en el carrito"));

        Carrito carrito = detalle.getCarrito();

        if (!carrito.isActivo()
                || !carrito.getUsuario().getIdUsuario().equals(idUsuario)
                || !carrito.getEmpresa().getIdEmpresa().equals(idEmpresa)) {

            throw new IllegalArgumentException(
                    "No tiene acceso a este carrito");
        }

        return detalle;
    }

    private void validarUsuarioEmpresa(Integer idUsuario,
                                       Integer idEmpresa) {

        Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "El usuario no existe"));

        Empresa empresa = empresaRepository.findById(idEmpresa)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "La empresa no existe"));

        if (!usuario.isActivo() || !empresa.isActivo()) {
            throw new IllegalArgumentException(
                    "El usuario o la empresa están inactivos");
        }

        if (usuario.getEmpresa() == null
                || !empresa.getIdEmpresa().equals(
                        usuario.getEmpresa().getIdEmpresa())) {

            throw new IllegalArgumentException(
                    "El usuario no pertenece a esta empresa");
        }
    }

    private void validarProductoCantidad(Integer idProducto,
                                         Integer cantidad) {

        if (idProducto == null || idProducto <= 0) {
            throw new IllegalArgumentException(
                    "El producto no es válido");
        }

        if (cantidad == null || cantidad <= 0) {
            throw new IllegalArgumentException(
                    "La cantidad debe ser mayor que cero");
        }
    }
}
