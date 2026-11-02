package com.collecthub.collecthub.repository;

import com.collecthub.collecthub.domain.Categoria;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * Toda consulta filtra por empresa (RN-01).
 */
@Repository
public interface CategoriaRepository extends JpaRepository<Categoria, Integer> {

    
    @Query("SELECT c FROM Categoria c WHERE c.empresa.idEmpresa = :idEmpresa ORDER BY c.descripcion ASC")
    public List<Categoria> listarPorEmpresa(Integer idEmpresa);

    
    @Query("SELECT c FROM Categoria c WHERE c.empresa.idEmpresa = :idEmpresa AND c.activo = true ORDER BY c.descripcion ASC")
    public List<Categoria> listarActivasPorEmpresa(Integer idEmpresa);

    
    @Query("SELECT c FROM Categoria c WHERE c.idCategoria = :idCategoria AND c.empresa.idEmpresa = :idEmpresa")
    public Optional<Categoria> buscarPorIdYEmpresa(Integer idCategoria, Integer idEmpresa);

    
    @Query("SELECT COUNT(c) FROM Categoria c WHERE c.empresa.idEmpresa = :idEmpresa "
            + "AND LOWER(c.descripcion) = LOWER(:descripcion) AND c.idCategoria <> :idCategoria")
    public long contarPorDescripcion(Integer idEmpresa, String descripcion, Integer idCategoria);
}
