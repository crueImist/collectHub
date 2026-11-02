package com.collecthub.collecthub.service;

import com.collecthub.collecthub.domain.Empresa;
import com.collecthub.collecthub.domain.Rol;
import com.collecthub.collecthub.dto.PerfilEmpresaForm;
import com.collecthub.collecthub.dto.RegistroEmpresaForm;
import com.collecthub.collecthub.repository.EmpresaRepository;
import java.io.IOException;
import java.text.Normalizer;
import java.util.Locale;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmpresaService {

    public static final long LOGO_MAX_BYTES = 5L * 1024 * 1024;

    private final EmpresaRepository empresaRepository;
    private final UsuarioService usuarioService;
    private final FirebaseStorageService firebaseStorageService;

    
    public record ResultadoRegistro(Empresa empresa, boolean logoGuardado) {

    }

    @Transactional(readOnly = true)
    public Optional<Empresa> buscarActiva(String identificador) {
        return empresaRepository.findByIdentificador(identificador).filter(Empresa::isActivo);
    }

    @Transactional(readOnly = true)
    public Empresa getEmpresa(Integer idEmpresa) {
        return empresaRepository.findById(idEmpresa)
                .orElseThrow(() -> new IllegalStateException("La empresa " + idEmpresa + " no existe"));
    }

    
    public boolean logoValido(MultipartFile logo) {
        if (logo == null || logo.isEmpty()) {
            return true;
        }
        String tipo = logo.getContentType();
        return logo.getSize() <= LOGO_MAX_BYTES
                && ("image/png".equals(tipo) || "image/jpeg".equals(tipo));
    }

    
    @Transactional
    public ResultadoRegistro registrar(RegistroEmpresaForm form, MultipartFile logo) {
        Empresa empresa = new Empresa();
        empresa.setNombre(form.getNombreComercial().trim());
        empresa.setIdentificador(generarIdentificador(form.getNombreComercial()));
        empresa.setCorreoContacto(form.getCorreo().trim().toLowerCase(Locale.ROOT));
        empresa.setTelefonoContacto(vacioANulo(form.getTelefono()));
        empresa.setActivo(true);
        empresa = empresaRepository.save(empresa);

        
        usuarioService.crear(empresa, form.getNombreAdministrador(), form.getApellidosAdministrador(),
                form.getCorreo(), form.getTelefono(), form.getContrasena(),
                Rol.ADMIN_EMPRESA, Rol.INVENTARIO, Rol.VENTAS, Rol.CONSULTA);

        boolean logoGuardado = true;
        if (logo != null && !logo.isEmpty()) {
            logoGuardado = guardarLogo(empresa, logo);
        }
        return new ResultadoRegistro(empresa, logoGuardado);
    }

    
    @Transactional
    public boolean actualizarPerfil(Integer idEmpresa, PerfilEmpresaForm form, MultipartFile logo) {
        Empresa empresa = getEmpresa(idEmpresa);
        empresa.setNombre(form.getNombre().trim());
        empresa.setCorreoContacto(vacioANulo(form.getCorreoContacto()));
        empresa.setTelefonoContacto(vacioANulo(form.getTelefonoContacto()));
        empresa.setDireccion(vacioANulo(form.getDireccion()));
        empresaRepository.save(empresa);

        if (logo != null && !logo.isEmpty()) {
            return guardarLogo(empresa, logo);
        }
        return true;
    }

    
    private boolean guardarLogo(Empresa empresa, MultipartFile logo) {
        try {
            String ruta = firebaseStorageService.uploadImage(logo,
                    "empresa-" + empresa.getIdEmpresa() + "/logo", 1);
            empresa.setLogoRuta(ruta);
            empresaRepository.save(empresa);
            return true;
        } catch (IOException e) {
            log.warn("No se pudo guardar el logo de la empresa {}: {}", empresa.getIdEmpresa(), e.getMessage());
            return false;
        }
    }

    
    private String generarIdentificador(String nombreComercial) {
        String base = Normalizer.normalize(nombreComercial, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9]+", "-")
                .replaceAll("^-+|-+$", "");
        if (base.isEmpty()) {
            base = "tienda";
        }
        if (base.length() > 40) {
            base = base.substring(0, 40).replaceAll("-+$", "");
        }
        String candidato = base;
        int numero = 2;
        while (empresaRepository.contarPorIdentificador(candidato) > 0) {
            candidato = base + "-" + numero++;
        }
        return candidato;
    }

    private String vacioANulo(String texto) {
        return (texto == null || texto.isBlank()) ? null : texto.trim();
    }
}