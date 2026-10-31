package com.collecthub.collecthub.repository;

import com.collecthub.collecthub.domain.Empresa;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmpresaRepository extends JpaRepository<Empresa, Integer> {

    public List<Empresa> findByActivoTrue();

    public Optional<Empresa> findByIdentificador(String identificador);
}
