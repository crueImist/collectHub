package com.collecthub.collecthub.service;

import com.collecthub.collecthub.domain.Empresa;
import com.collecthub.collecthub.domain.Rol;
import com.collecthub.collecthub.domain.Usuario;
import com.collecthub.collecthub.dto.EmpleadoForm;
import com.collecthub.collecthub.dto.RegistroClienteForm;
import com.collecthub.collecthub.dto.RegistroEmpresaForm;
import com.collecthub.collecthub.repository.EmpresaRepository;
import com.collecthub.collecthub.repository.RolRepository;
import com.collecthub.collecthub.repository.UsuarioRepository;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final EmpresaRepository empresaRepository;
    private final PasswordEncoder passwordEncoder;

    
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
        usuario.setPassword(passwordEncoder.encode(contrasena));   
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

    
    public Usuario registrarCliente(Empresa empresa, RegistroClienteForm form) {
        return crear(empresa, form.getNombre(), form.getApellidos(), form.getCorreo(),
                null, form.getContrasena(), Rol.CLIENTE);
    }

    
    @Transactional(readOnly = true)
    public List<Usuario> listarPersonal(Integer idEmpresa) {
        return usuarioRepository.listarPersonal(idEmpresa, Rol.ROLES_PERSONAL);
    }

    
    @Transactional
    public Usuario guardarEmpleado(Integer idEmpresa, EmpleadoForm form) {
        Set<String> roles = rolesDeEmpleado(form.getRoles());
        if (roles.isEmpty()) {
            throw new IllegalArgumentException("rol");
        }
        String clave = form.getContrasena();
        boolean hayClave = clave != null && !clave.isEmpty();
        if (hayClave && clave.length() < RegistroEmpresaForm.CLAVE_MINIMA) {
            throw new IllegalArgumentException("clave");
        }

        if (form.getIdUsuario() == null) {
            if (!hayClave) {
                throw new IllegalArgumentException("clave");
            }
            Empresa empresa = empresaRepository.findById(idEmpresa)
                    .orElseThrow(() -> new IllegalArgumentException("usuario"));
            return crear(empresa, form.getNombre(), form.getApellidos(), form.getCorreo(),
                    form.getTelefono(), clave, roles.toArray(String[]::new));
        }

        
        Usuario usuario = usuarioRepository.buscarPorIdYEmpresa(form.getIdUsuario(), idEmpresa)
                .filter(this::esEmpleadoEditable)
                .orElseThrow(() -> new IllegalArgumentException("usuario"));

        String correo = form.getCorreo().trim().toLowerCase(Locale.ROOT);
        if (!correo.equals(usuario.getCorreo())
                && usuarioRepository.contarPorEmpresaYCorreo(idEmpresa, correo) > 0) {
            throw new IllegalStateException("correo");
        }
        usuario.setNombre(form.getNombre().trim());
        usuario.setApellidos(form.getApellidos().trim());
        usuario.setCorreo(correo);
        usuario.setTelefono((form.getTelefono() == null || form.getTelefono().isBlank())
                ? null : form.getTelefono().trim());
        if (hayClave) {
            usuario.setPassword(passwordEncoder.encode(clave));
        }
        usuario.getRoles().clear();
        for (String nombreRol : roles) {
            usuario.getRoles().add(rolRepository.buscarPorNombre(nombreRol)
                    .orElseThrow(() -> new IllegalStateException(
                    "Falta el rol " + nombreRol + " en la tabla rol (ejecute collecthub_bd.sql)")));
        }
        return usuarioRepository.save(usuario);
    }

   
    @Transactional
    public void cambiarEstado(Integer idEmpresa, Integer idUsuario) {
        Usuario usuario = usuarioRepository.buscarPorIdYEmpresa(idUsuario, idEmpresa)
                .filter(this::esEmpleadoEditable)
                .orElseThrow(() -> new IllegalArgumentException("usuario"));
        usuario.setActivo(!usuario.isActivo());
        usuarioRepository.save(usuario);
    }

    
    private boolean esEmpleadoEditable(Usuario usuario) {
        boolean esAdmin = usuario.getRoles().stream()
                .anyMatch(r -> Rol.ADMIN_EMPRESA.equals(r.getNombre()));
        boolean esEmpleado = usuario.getRoles().stream()
                .anyMatch(r -> Rol.ROLES_EMPLEADO.contains(r.getNombre()));
        return esEmpleado && !esAdmin;
    }

    
    private Set<String> rolesDeEmpleado(List<String> solicitados) {
        Set<String> roles = new LinkedHashSet<>();
        if (solicitados != null) {
            for (String rol : solicitados) {
                if (rol != null && Rol.ROLES_EMPLEADO.contains(rol.trim())) {
                    roles.add(rol.trim());
                }
            }
        }
        return roles;
    }

    
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