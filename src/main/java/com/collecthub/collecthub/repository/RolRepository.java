package com.collecthub.collecthub.repository;

import com.collecthub.collecthub.domain.Rol;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface RolRepository extends JpaRepository<Rol, Integer> {

    @Query("SELECT r FROM Rol r WHERE r.nombre = :nombre")
    public Optional<Rol> buscarPorNombre(String nombre);
}
