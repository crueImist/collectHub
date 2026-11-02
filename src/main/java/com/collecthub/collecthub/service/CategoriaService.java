package com.collecthub.collecthub.service;

import com.collecthub.collecthub.domain.Categoria;
import com.collecthub.collecthub.domain.Empresa;
import com.collecthub.collecthub.dto.CategoriaForm;
import com.collecthub.collecthub.repository.CategoriaRepository;
import com.collecthub.collecthub.repository.EmpresaRepository;
import java.io.IOException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@RequiredArgsConstructor
public class CategoriaService {

    public static final long IMAGEN_MAX_BYTES = 5L * 1024 * 1024;

    private final CategoriaRepository categoriaRepository;
    private final EmpresaRepository empresaRepository;
    private final FirebaseStorageService firebaseStorageService;

    
    @Transactional(readOnly = true)
    public List<Categoria> getCategorias(Integer idEmpresa) {
        return categoriaRepository.listarPorEmpresa(idEmpresa);
    }

    
    public boolean imagenValida(MultipartFile imagen) {
        if (imagen == null || imagen.isEmpty()) {
            return true;
        }
        String tipo = imagen.getContentType();
        return imagen.getSize() <= IMAGEN_MAX_BYTES
                && ("image/png".equals(tipo) || "image/jpeg".equals(tipo));
    }

    
    @Transactional
    public boolean guardar(Integer idEmpresa, CategoriaForm form, MultipartFile imagen) {
        String descripcion = form.getDescripcion().trim();
        int idActual = (form.getIdCategoria() == null) ? 0 : form.getIdCategoria();
        if (categoriaRepository.contarPorDescripcion(idEmpresa, descripcion, idActual) > 0) {
            throw new IllegalStateException("descripcion");
        }

        Categoria categoria;
        if (form.getIdCategoria() == null) {
            Empresa empresa = empresaRepository.findById(idEmpresa)
                    .orElseThrow(() -> new IllegalArgumentException("categoria"));
            categoria = new Categoria();
            categoria.setEmpresa(empresa);
        } else {
            
            categoria = categoriaRepository.buscarPorIdYEmpresa(form.getIdCategoria(), idEmpresa)
                    .orElseThrow(() -> new IllegalArgumentException("categoria"));
        }
        categoria.setDescripcion(descripcion);
        categoria.setActivo(form.isActivo());
        categoria = categoriaRepository.save(categoria);

        if (imagen != null && !imagen.isEmpty()) {
            try {
                String ruta = firebaseStorageService.uploadImage(imagen,
                        "empresa-" + idEmpresa + "/categoria", categoria.getIdCategoria());
                categoria.setRutaImagen(ruta);
                categoriaRepository.save(categoria);
            } catch (IOException e) {
                log.warn("No se pudo guardar la imagen de la categoría {}: {}",
                        categoria.getIdCategoria(), e.getMessage());
                return false;
            }
        }
        return true;
    }

   
    @Transactional
    public void cambiarEstado(Integer idEmpresa, Integer idCategoria) {
        Categoria categoria = categoriaRepository.buscarPorIdYEmpresa(idCategoria, idEmpresa)
                .orElseThrow(() -> new IllegalArgumentException("categoria"));
        categoria.setActivo(!categoria.isActivo());
        categoriaRepository.save(categoria);
    }
}
