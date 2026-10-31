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

    // ---------- Empresa (identificada por la ruta /tienda/{identificador}) ----------

    @Transactional(readOnly = true)
    public List<Empresa> getEmpresasActivas() {
        return empresaRepository.listarActivas();
    }

    // Una empresa suspendida no es accesible (RN-12)
    @Transactional(readOnly = true)
    public Optional<Empresa> getEmpresaActiva(String identificador) {
        return empresaRepository.buscarPorIdentificador(identificador)
                .filter(Empresa::isActivo);
    }

    // ---------- Consultas ----------

    @Transactional(readOnly = true)
    public List<Categoria> getCategorias(Integer idEmpresa) {
        return categoriaRepository.listarActivasPorEmpresa(idEmpresa);
    }

    // Vista administrativa: todos los productos de la empresa, activos o no
    @Transactional(readOnly = true)
    public List<Producto> getProductos(Integer idEmpresa) {
        return productoRepository.listarPorEmpresa(idEmpresa);
    }

    // Catálogo con búsqueda por nombre y filtros por categoría, precio y disponibilidad (HU-09)
    @Transactional(readOnly = true)
    public List<Producto> buscar(Integer idEmpresa, String nombre, Integer idCategoria,
            BigDecimal precioMin, BigDecimal precioMax, boolean soloDisponibles) {
        String texto = (nombre == null) ? "" : nombre.trim();
        Integer categoria = (idCategoria == null) ? 0 : idCategoria;
        BigDecimal minimo = (precioMin == null) ? BigDecimal.ZERO : precioMin;
        BigDecimal maximo = (precioMax == null) ? new BigDecimal("9999999999.99") : precioMax;
        return productoRepository.buscar(idEmpresa, texto, categoria, minimo, maximo, soloDisponibles);
    }

    // ---------- Registro de producto con imágenes (HU-07) ----------

    /**
     * Registra un producto nuevo de la empresa y sube sus imágenes a Firebase.
     *
     * @return cantidad de imágenes que no se pudieron guardar (0 si todo salió bien)
     * @throws IllegalArgumentException si la categoría no existe o es de otra empresa
     * @throws IllegalStateException si el código ya existe en la empresa
     */
    @Transactional
    public int registrar(Producto producto, Empresa empresa, List<MultipartFile> imagenes) {
        Integer idCategoria = (producto.getCategoria() == null)
                ? null : producto.getCategoria().getIdCategoria();
        // La categoría debe existir, estar activa y ser de la misma empresa (RN-01)
        Categoria categoria = categoriaRepository.findById(idCategoria == null ? -1 : idCategoria)
                .filter(c -> c.isActivo()
                && c.getEmpresa().getIdEmpresa().equals(empresa.getIdEmpresa()))
                .orElseThrow(() -> new IllegalArgumentException("categoria"));

        producto.setCodigo(producto.getCodigo().trim());
        if (productoRepository.contarPorCodigo(empresa.getIdEmpresa(), producto.getCodigo()) > 0) {
            throw new IllegalStateException("codigo");
        }

        // Estos datos nunca se toman del formulario
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
                continue; // El input de archivos vacío envía una parte sin contenido
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
                    producto.setRutaImagen(ruta); // la primera es la imagen principal
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
