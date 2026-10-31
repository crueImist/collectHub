package com.collecthub.collecthub.repository;

import com.collecthub.collecthub.domain.Categoria;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * VERSIÓN MÍNIMA (ver comentario en Categoria.java). La reemplaza la de HU-06.
 */
@Repository
public interface CategoriaRepository extends JpaRepository<Categoria, Integer> {

    @Query("SELECT c FROM Categoria c WHERE c.empresa.idEmpresa = :idEmpresa AND c.activo = true ORDER BY c.descripcion ASC")
    public List<Categoria> listarActivasPorEmpresa(Integer idEmpresa);
}
