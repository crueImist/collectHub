package com.collecthub.collecthub.service;

import com.collecthub.collecthub.domain.Empresa;
import com.collecthub.collecthub.domain.Rol;
import com.collecthub.collecthub.domain.Usuario;
import com.collecthub.collecthub.dto.RegistroClienteForm;
import com.collecthub.collecthub.repository.RolRepository;
import com.collecthub.collecthub.repository.UsuarioRepository;
import java.util.Locale;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final PasswordEncoder passwordEncoder;

    // Crea un usuario en la empresa con los roles indicados.
    // Lanza IllegalStateException si el correo ya existe en esa empresa.
    @Transactional
    public Usuario crear(Empresa empresa, String nombre, String apellidos, String correo,
            String telefono, String contrasena, String... roles) {
        String correoNormalizado = correo.trim().toLowerCase(Locale.ROOT);
        if (usuarioRepository.contarPorEmpresaYCorreo(empresa.getIdEmpresa(), correoNormalizado) > 0) {
            throw new IllegalStateException("correo");
        }

        Usuario usuario = new Usuario();
        usuario.setEmpresa(empresa);
        usuario.setUsername(generarUsername(empresa, correoNormalizado));
        usuario.setPassword(passwordEncoder.encode(contrasena));   // hash BCrypt
        usuario.setNombre(nombre.trim());
        usuario.setApellidos(apellidos.trim());
        usuario.setCorreo(correoNormalizado);
        usuario.setTelefono((telefono == null || telefono.isBlank()) ? null : telefono.trim());
        usuario.setActivo(true);
        for (String nombreRol : roles) {
            Rol rol = rolRepository.buscarPorNombre(nombreRol)
                    .orElseThrow(() -> new IllegalStateException(
                    "Falta el rol " + nombreRol + " en la tabla rol (ejecute collecthub_bd.sql)"));
            usuario.getRoles().add(rol);
        }
        return usuarioRepository.save(usuario);
    }

    // HU-03: el cliente se registra con nombre, apellidos, correo y contraseña
    @Transactional
    public Usuario registrarCliente(Empresa empresa, RegistroClienteForm form) {
        return crear(empresa, form.getNombre(), form.getApellidos(), form.getCorreo(),
                null, form.getContrasena(), Rol.CLIENTE);
    }

    // El username es único en toda la plataforma: identificador + parte local del correo (+ número si ya existe)
    private String generarUsername(Empresa empresa, String correo) {
        String local = correo.substring(0, correo.indexOf('@')).replaceAll("[^a-z0-9._-]", "");
        if (local.isEmpty()) {
            local = "usuario";
        }
        String base = recortar(empresa.getIdentificador() + "." + local, 44);
        String candidato = base;
        int numero = 2;
        while (usuarioRepository.contarPorUsername(candidato) > 0) {
            candidato = base + numero++;
        }
        return candidato;
    }

    private String recortar(String texto, int maximo) {
        return (texto.length() <= maximo) ? texto : texto.substring(0, maximo);
    }
}