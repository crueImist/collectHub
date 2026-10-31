package com.collecthub.collecthub.controller;

import com.collecthub.collecthub.domain.Empresa;
import com.collecthub.collecthub.domain.Producto;
import com.collecthub.collecthub.service.ProductoService;
import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Catálogo público de cada tienda (HU-08) con búsqueda y filtros (HU-09).
 * La empresa se identifica por la ruta: /tienda/{identificador}/catalogo
 */
@Controller
@RequiredArgsConstructor
@RequestMapping("/tienda")
public class CatalogoController {

    private final ProductoService productoService;
    private final MessageSource messageSource;

    // Lista de tiendas activas, para entrar al catálogo de una de ellas
    @GetMapping
    public String listadoTiendas(Model model) {
        model.addAttribute("empresas", productoService.getEmpresasActivas());
        return "catalogo/tiendas";
    }

    @GetMapping("/{identificador}/catalogo")
    public String catalogo(@PathVariable String identificador,
            @RequestParam(required = false) String nombre,
            @RequestParam(required = false) Integer idCategoria,
            @RequestParam(required = false) BigDecimal precioMin,
            @RequestParam(required = false) BigDecimal precioMax,
            @RequestParam(defaultValue = "false") boolean soloDisponibles,
            Model model, RedirectAttributes redirectAttributes, Locale locale) {

        Optional<Empresa> empresa = productoService.getEmpresaActiva(identificador);
        if (empresa.isEmpty()) {
            redirectAttributes.addFlashAttribute("error",
                    messageSource.getMessage("tienda.error01", null, locale));
            return "redirect:/tienda";
        }

        List<Producto> productos;
        if (precioMin != null && precioMax != null && precioMin.compareTo(precioMax) > 0) {
            // Rango inválido: se avisa y no se muestran resultados
            model.addAttribute("error", messageSource.getMessage("consulta.error", null, locale));
            productos = List.of();
        } else {
            productos = productoService.buscar(empresa.get().getIdEmpresa(),
                    nombre, idCategoria, precioMin, precioMax, soloDisponibles);
        }

        model.addAttribute("empresa", empresa.get());
        model.addAttribute("identificador", identificador);
        model.addAttribute("categorias", productoService.getCategorias(empresa.get().getIdEmpresa()));
        model.addAttribute("productos", productos);
        model.addAttribute("totalProductos", productos.size());
        // Se devuelven los filtros para que el formulario conserve lo que escribió la persona
        model.addAttribute("nombre", nombre);
        model.addAttribute("idCategoria", idCategoria);
        model.addAttribute("precioMin", precioMin);
        model.addAttribute("precioMax", precioMax);
        model.addAttribute("soloDisponibles", soloDisponibles);
        return "catalogo/listado";
    }
}
