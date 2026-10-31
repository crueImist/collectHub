package com.collecthub.collecthub.repository;

import com.collecthub.collecthub.domain.Producto;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Integer> {

    // Vista administrativa: todos los productos de una empresa
    @Query("SELECT p FROM Producto p WHERE p.empresa.idEmpresa = :idEmpresa ORDER BY p.nombre ASC")
    public List<Producto> listarPorEmpresa(Integer idEmpresa);

    // Para validar que el código no se repita dentro de la empresa
    @Query("SELECT COUNT(p) FROM Producto p WHERE p.empresa.idEmpresa = :idEmpresa AND p.codigo = :codigo")
    public Long contarPorCodigo(Integer idEmpresa, String codigo);

    // Catálogo con búsqueda y filtros (HU-08 y HU-09). Siempre filtra por empresa (RN-01).
    @Query("SELECT p FROM Producto p "
            + "WHERE p.empresa.idEmpresa = :idEmpresa "
            + "AND p.activo = true "
            + "AND LOWER(p.nombre) LIKE LOWER(CONCAT('%', :nombre, '%')) "
            + "AND (:idCategoria = 0 OR p.categoria.idCategoria = :idCategoria) "
            + "AND p.precio >= :precioMin AND p.precio <= :precioMax "
            + "AND (:soloDisponibles = false OR p.existencias > 0) "
            + "ORDER BY p.nombre ASC")
    public List<Producto> buscar(Integer idEmpresa, String nombre, Integer idCategoria,
            BigDecimal precioMin, BigDecimal precioMax, boolean soloDisponibles);
}
