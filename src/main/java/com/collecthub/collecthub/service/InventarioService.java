package com.collecthub.collecthub.service;

import com.collecthub.collecthub.domain.MovimientoInventario;
import com.collecthub.collecthub.domain.Producto;
import com.collecthub.collecthub.domain.TipoMovimiento;
import com.collecthub.collecthub.repository.MovimientoInventarioRepository;
import com.collecthub.collecthub.repository.ProductoRepository;
import com.collecthub.collecthub.repository.UsuarioRepository;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class InventarioService {

    private final ProductoRepository productoRepository;
    private final MovimientoInventarioRepository movimientoRepository;
    private final UsuarioRepository usuarioRepository;

   
    @Transactional(readOnly = true)
    public List<Producto> getProductos(Integer idEmpresa) {
        return productoRepository.listarPorEmpresa(idEmpresa);
    }

    @Transactional(readOnly = true)
    public Optional<Producto> getProducto(Integer idEmpresa, Integer idProducto) {
        return productoRepository.buscarPorIdYEmpresa(idProducto, idEmpresa);
    }

    @Transactional(readOnly = true)
    public List<MovimientoInventario> getHistorial(Integer idEmpresa, Integer idProducto) {
        return movimientoRepository.listarPorProducto(idEmpresa, idProducto);
    }

    
    @Transactional
    public int ajustar(Integer idEmpresa, Integer idUsuario, Integer idProducto,
            TipoMovimiento tipo, Integer cantidad, String motivo) {
        if ((tipo != TipoMovimiento.ENTRADA && tipo != TipoMovimiento.SALIDA)
                || cantidad == null || cantidad <= 0
                || motivo == null || motivo.isBlank()) {
            throw new IllegalArgumentException("datos");
        }

        
        Producto producto = productoRepository.buscarParaActualizar(idProducto, idEmpresa)
                .orElseThrow(() -> new IllegalArgumentException("producto"));

        int nuevasExistencias = (tipo == TipoMovimiento.ENTRADA)
                ? Math.addExact(producto.getExistencias(), cantidad)
                : producto.getExistencias() - cantidad;
        if (nuevasExistencias < 0) {
            throw new IllegalStateException("stock");
        }
        producto.setExistencias(nuevasExistencias);
        productoRepository.save(producto);

        MovimientoInventario movimiento = new MovimientoInventario();
        movimiento.setEmpresa(producto.getEmpresa());
        movimiento.setProducto(producto);
        movimiento.setUsuario(idUsuario == null ? null : usuarioRepository.getReferenceById(idUsuario));
        movimiento.setTipo(tipo);
        movimiento.setCantidad(cantidad);
        movimiento.setMotivo(motivo.trim());
        movimientoRepository.save(movimiento);

        return nuevasExistencias;
    }
}
