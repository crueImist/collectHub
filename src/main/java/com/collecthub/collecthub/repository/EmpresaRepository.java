package com.collecthub.collecthub.repository;

import com.collecthub.collecthub.domain.Empresa;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface EmpresaRepository extends JpaRepository<Empresa, Integer> {

    @Query("SELECT e FROM Empresa e WHERE e.activo = true ORDER BY e.nombre ASC")
    public List<Empresa> listarActivas();

    @Query("SELECT e FROM Empresa e WHERE e.identificador = :identificador")
    public Optional<Empresa> buscarPorIdentificador(String identificador);
}
