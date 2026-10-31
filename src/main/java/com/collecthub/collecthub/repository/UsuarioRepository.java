package com.collecthub.collecthub.repository;

import com.collecthub.collecthub.domain.Usuario;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {

    // Aislamiento por empresa (RN-01): las consultas de negocio siempre filtran por empresa
    public List<Usuario> findByEmpresa_IdEmpresa(Integer idEmpresa);

    public Optional<Usuario> findByEmpresa_IdEmpresaAndCorreo(Integer idEmpresa, String correo);
}
