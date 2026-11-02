package com.collecthub.collecthub.repository;

import com.collecthub.collecthub.domain.Usuario;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {

    
    @Query("SELECT u FROM Usuario u WHERE u.empresa.identificador = :identificador AND u.correo = :correo")
    public Optional<Usuario> buscarPorIdentificadorYCorreo(String identificador, String correo);

    
    @Query("SELECT u FROM Usuario u WHERE u.empresa IS NULL AND u.correo = :correo")
    public Optional<Usuario> buscarAdminPlataforma(String correo);

    
    @Query("SELECT COUNT(u) FROM Usuario u WHERE u.empresa.idEmpresa = :idEmpresa AND u.correo = :correo")
    public long contarPorEmpresaYCorreo(Integer idEmpresa, String correo);

    
    @Query("SELECT COUNT(u) FROM Usuario u WHERE u.username = :username")
    public long contarPorUsername(String username);

    
    @Query("SELECT DISTINCT u FROM Usuario u JOIN u.roles r WHERE u.empresa.idEmpresa = :idEmpresa "
            + "AND r.nombre IN (:roles) ORDER BY u.nombre ASC, u.apellidos ASC")
    public List<Usuario> listarPersonal(Integer idEmpresa, Collection<String> roles);

    
    @Query("SELECT u FROM Usuario u WHERE u.idUsuario = :idUsuario AND u.empresa.idEmpresa = :idEmpresa")
    public Optional<Usuario> buscarPorIdYEmpresa(Integer idUsuario, Integer idEmpresa);
}
