package com.collecthub.collecthub.repository;

import com.collecthub.collecthub.domain.Producto;
import jakarta.persistence.LockModeType;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Integer> {

    
    @Query("SELECT p FROM Producto p WHERE p.empresa.idEmpresa = :idEmpresa ORDER BY p.nombre ASC")
    public List<Producto> listarPorEmpresa(Integer idEmpresa);

    
    @Query("SELECT COUNT(p) FROM Producto p WHERE p.empresa.idEmpresa = :idEmpresa AND p.codigo = :codigo")
    public Long contarPorCodigo(Integer idEmpresa, String codigo);

    
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

    
    @Query("SELECT p FROM Producto p WHERE p.idProducto = :idProducto AND p.empresa.idEmpresa = :idEmpresa")
    public Optional<Producto> buscarPorIdYEmpresa(Integer idProducto, Integer idEmpresa);

    
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM Producto p WHERE p.idProducto = :idProducto AND p.empresa.idEmpresa = :idEmpresa")
    public Optional<Producto> buscarParaActualizar(Integer idProducto, Integer idEmpresa);
}
