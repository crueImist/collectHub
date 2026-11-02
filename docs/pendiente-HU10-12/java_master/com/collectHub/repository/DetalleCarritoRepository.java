package com.collectHub.repository;

import com.collectHub.domain.DetalleCarrito;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DetalleCarritoRepository
        extends JpaRepository<DetalleCarrito, Integer> {

    List<DetalleCarrito> findByCarritoIdCarrito(Integer idCarrito);

    Optional<DetalleCarrito> findByCarritoIdCarritoAndIdProducto(
            Integer idCarrito, Integer idProducto);
}