package com.collectHub.repository;

import com.collectHub.domain.Carrito;
import com.collectHub.domain.Carrito;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CarritoRepository
        extends JpaRepository<Carrito, Integer> {

    Optional<Carrito> findByUsuarioIdUsuarioAndEmpresaIdEmpresaAndActivoTrue(
            Integer idUsuario, Integer idEmpresa);
}