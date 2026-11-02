package com.collecthub.collecthub.security;

import com.collecthub.collecthub.domain.Rol;
import com.collecthub.collecthub.domain.Usuario;
import java.util.Collection;
import java.util.List;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Getter
public class UsuarioSesion implements UserDetails {

    private static final long serialVersionUID = 1L;

    private final Integer idUsuario;
    private final Integer idEmpresa;               
    private final String identificadorEmpresa;
    private final String nombreCompleto;
    private final String correo;
    private final List<SimpleGrantedAuthority> authorities;

    public UsuarioSesion(Usuario usuario) {
        this.idUsuario = usuario.getIdUsuario();
        this.correo = usuario.getCorreo();
        this.nombreCompleto = (usuario.getNombre() + " " + usuario.getApellidos()).trim();
        if (usuario.getEmpresa() != null) {
            this.idEmpresa = usuario.getEmpresa().getIdEmpresa();
            this.identificadorEmpresa = usuario.getEmpresa().getIdentificador();
        } else {
            this.idEmpresa = null;
            this.identificadorEmpresa = null;
        }
        // En la tabla el rol es ADMIN_EMPRESA; Spring Security espera ROLE_ADMIN_EMPRESA
        this.authorities = usuario.getRoles().stream()
                .map(Rol::getNombre)
                .map(nombre -> new SimpleGrantedAuthority("ROLE_" + nombre))
                .toList();
    }

    public boolean tieneRol(String rol) {
        return authorities.contains(new SimpleGrantedAuthority("ROLE_" + rol));
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return null; 
    }

    @Override
    public String getUsername() {
        return correo;
    }
}