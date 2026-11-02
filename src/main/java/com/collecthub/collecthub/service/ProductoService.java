package com.collecthub.collecthub.service;

import com.collecthub.collecthub.domain.Categoria;
import com.collecthub.collecthub.domain.Empresa;
import com.collecthub.collecthub.domain.Producto;
import com.collecthub.collecthub.domain.ProductoImagen;
import com.collecthub.collecthub.repository.CategoriaRepository;
import com.collecthub.collecthub.repository.EmpresaRepository;
import com.collecthub.collecthub.repository.ProductoImagenRepository;
import com.collecthub.collecthub.repository.ProductoRepository;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductoService {

    public static final int MAX_IMAGENES = 5;

    private final ProductoRepository productoRepository;
    private final ProductoImagenRepository productoImagenRepository;
    private final CategoriaRepository categoriaRepository;
    private final EmpresaRepository empresaRepository;
    private final FirebaseStorageService firebaseStorageService;

    

    @Transactional(readOnly = true)
    public List<Empresa> getEmpresasActivas() {
        return empresaRepository.listarActivas();
    }

    
    @Transactional(readOnly = true)
    public Optional<Empresa> getEmpresaActiva(String identificador) {
        return empresaRepository.buscarPorIdentificador(identificador)
                .filter(Empresa::isActivo);
    }

    

    @Transactional(readOnly = true)
    public List<Categoria> getCategorias(Integer idEmpresa) {
        return categoriaRepository.listarActivasPorEmpresa(idEmpresa);
    }

    
    @Transactional(readOnly = true)
    public List<Producto> getProductos(Integer idEmpresa) {
        return productoRepository.listarPorEmpresa(idEmpresa);
    }

    
    @Transactional(readOnly = true)
    public List<Producto> buscar(Integer idEmpresa, String nombre, Integer idCategoria,
            BigDecimal precioMin, BigDecimal precioMax, boolean soloDisponibles) {
        String texto = (nombre == null) ? "" : nombre.trim();
        Integer categoria = (idCategoria == null) ? 0 : idCategoria;
        BigDecimal minimo = (precioMin == null) ? BigDecimal.ZERO : precioMin;
        BigDecimal maximo = (precioMax == null) ? new BigDecimal("9999999999.99") : precioMax;
        return productoRepository.buscar(idEmpresa, texto, categoria, minimo, maximo, soloDisponibles);
    }

    
    @Transactional
    public int registrar(Producto producto, Empresa empresa, List<MultipartFile> imagenes) {
        Integer idCategoria = (producto.getCategoria() == null)
                ? null : producto.getCategoria().getIdCategoria();
        
        Categoria categoria = categoriaRepository.findById(idCategoria == null ? -1 : idCategoria)
                .filter(c -> c.isActivo()
                && c.getEmpresa().getIdEmpresa().equals(empresa.getIdEmpresa()))
                .orElseThrow(() -> new IllegalArgumentException("categoria"));

        producto.setCodigo(producto.getCodigo().trim());
        if (productoRepository.contarPorCodigo(empresa.getIdEmpresa(), producto.getCodigo()) > 0) {
            throw new IllegalStateException("codigo");
        }

        
        producto.setIdProducto(null);
        producto.setRutaImagen(null);
        producto.setEmpresa(empresa);
        producto.setCategoria(categoria);
        producto.setActivo(true);
        producto = productoRepository.save(producto);

        return guardarImagenes(producto, empresa, imagenes);
    }

    private int guardarImagenes(Producto producto, Empresa empresa, List<MultipartFile> imagenes) {
        int fallidas = 0;
        if (imagenes == null) {
            return fallidas;
        }
        int orden = 0;
        String carpeta = "empresa-" + empresa.getIdEmpresa() + "/producto-" + producto.getIdProducto();
        for (MultipartFile imagen : imagenes) {
            if (imagen == null || imagen.isEmpty()) {
                continue; 
            }
            orden++;
            String tipo = imagen.getContentType();
            if (orden > MAX_IMAGENES || tipo == null || !tipo.startsWith("image/")) {
                fallidas++;
                continue;
            }
            try {
                String ruta = firebaseStorageService.uploadImage(imagen, carpeta, orden);
                ProductoImagen productoImagen = new ProductoImagen();
                productoImagen.setProducto(producto);
                productoImagen.setRutaImagen(ruta);
                productoImagen.setOrden(orden);
                productoImagenRepository.save(productoImagen);
                if (producto.getRutaImagen() == null) {
                    producto.setRutaImagen(ruta); 
                    productoRepository.save(producto);
                }
            } catch (IOException e) {
                log.warn("No se pudo subir la imagen {} del producto {}: {}",
                        orden, producto.getIdProducto(), e.getMessage());
                fallidas++;
            }
        }
        return fallidas;
    }
}
