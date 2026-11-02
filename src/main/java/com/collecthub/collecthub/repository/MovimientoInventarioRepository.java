package com.collecthub.collecthub.repository;

import com.collecthub.collecthub.domain.MovimientoInventario;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface MovimientoInventarioRepository extends JpaRepository<MovimientoInventario, Integer> {

    
    @Query("SELECT m FROM MovimientoInventario m "
            + "WHERE m.empresa.idEmpresa = :idEmpresa AND m.producto.idProducto = :idProducto "
            + "ORDER BY m.idMovimiento DESC")
    public List<MovimientoInventario> listarPorProducto(Integer idEmpresa, Integer idProducto);
}
