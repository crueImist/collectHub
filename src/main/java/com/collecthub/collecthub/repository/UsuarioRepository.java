package com.collecthub.collecthub.repository;

import com.collecthub.collecthub.domain.Usuario;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {

    // Inicio de sesión de empleados y clientes: el correo se busca dentro de la empresa
    @Query("SELECT u FROM Usuario u WHERE u.empresa.identificador = :identificador AND u.correo = :correo")
    public Optional<Usuario> buscarPorIdentificadorYCorreo(String identificador, String correo);

    // Inicio de sesión del administrador de la plataforma 
    @Query("SELECT u FROM Usuario u WHERE u.empresa IS NULL AND u.correo = :correo")
    public Optional<Usuario> buscarAdminPlataforma(String correo);

    // Para validar que el correo no se repita dentro de la empresa
    @Query("SELECT COUNT(u) FROM Usuario u WHERE u.empresa.idEmpresa = :idEmpresa AND u.correo = :correo")
    public long contarPorEmpresaYCorreo(Integer idEmpresa, String correo);

    // Para generar un username que no exista todavía
    @Query("SELECT COUNT(u) FROM Usuario u WHERE u.username = :username")
    public long contarPorUsername(String username);
}
