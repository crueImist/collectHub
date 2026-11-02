package com.collecthub.collecthub.security;

import com.collecthub.collecthub.domain.Usuario;
import com.collecthub.collecthub.repository.UsuarioRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EmpresaAuthenticationProvider implements AuthenticationProvider {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String correo = authentication.getName() == null ? "" : authentication.getName().trim();
        Object credenciales = authentication.getCredentials();
        String clave = (credenciales == null) ? "" : credenciales.toString();

        
        String identificador = "";
        if (authentication.getDetails() instanceof EmpresaAuthenticationDetails detalles) {
            identificador = detalles.getIdentificadorEmpresa();
        }

        Optional<Usuario> encontrado = identificador.isEmpty()
                ? usuarioRepository.buscarAdminPlataforma(correo)
                : usuarioRepository.buscarPorIdentificadorYCorreo(identificador, correo);

        
        Usuario usuario = encontrado
                .filter(u -> passwordEncoder.matches(clave, u.getPassword()))
                .orElseThrow(() -> new BadCredentialsException("Credenciales erróneas"));

        if (!usuario.isActivo()) {
            throw new DisabledException("Usuario inactivo");
        }

        if (usuario.getEmpresa() != null && !usuario.getEmpresa().isActivo()) {
            throw new DisabledException("Empresa suspendida");
        }

        UsuarioSesion sesion = new UsuarioSesion(usuario);
        return UsernamePasswordAuthenticationToken.authenticated(sesion, null, sesion.getAuthorities());
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}