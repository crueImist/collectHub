package com.collecthub.collecthub.controller;

import com.collecthub.collecthub.domain.Empresa;
import com.collecthub.collecthub.domain.Producto;
import com.collecthub.collecthub.service.ProductoService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Administración de productos de una tienda (HU-07).
 * TODO (cuando exista Spring Security): restringir las rutas de producto de cada tienda a
 * ROLE_ADMIN_EMPRESA y ROLE_INVENTARIO, y validar que el usuario pertenece a
 * la empresa del identificador.
 */
@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/tienda/{identificador}/producto")
public class ProductoController {

    private final ProductoService productoService;
    private final MessageSource messageSource;

    @GetMapping("/listado")
    public String listado(@PathVariable String identificador, Model model,
            RedirectAttributes redirectAttributes, Locale locale) {
        Optional<Empresa> empresa = productoService.getEmpresaActiva(identificador);
        if (empresa.isEmpty()) {
            redirectAttributes.addFlashAttribute("error",
                    messageSource.getMessage("tienda.error01", null, locale));
            return "redirect:/tienda";
        }
        List<Producto> productos = productoService.getProductos(empresa.get().getIdEmpresa());
        model.addAttribute("empresa", empresa.get());
        model.addAttribute("identificador", identificador);
        model.addAttribute("productos", productos);
        model.addAttribute("totalProductos", productos.size());
        model.addAttribute("categorias", productoService.getCategorias(empresa.get().getIdEmpresa()));
        return "producto/listado";
    }

    @PostMapping("/guardar")
    public String guardar(@PathVariable String identificador, @Valid Producto producto,
            BindingResult bindingResult,
            @RequestParam(value = "imagenes", required = false) List<MultipartFile> imagenes,
            RedirectAttributes redirectAttributes, Locale locale) {

        Optional<Empresa> empresa = productoService.getEmpresaActiva(identificador);
        if (empresa.isEmpty()) {
            redirectAttributes.addFlashAttribute("error",
                    messageSource.getMessage("tienda.error01", null, locale));
            return "redirect:/tienda";
        }
        String destino = "redirect:/tienda/" + identificador + "/producto/listado";

        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("error",
                    messageSource.getMessage("producto.error04", null, locale));
            return destino;
        }
        try {
            int fallidas = productoService.registrar(producto, empresa.get(), imagenes);
            if (fallidas > 0) {
                redirectAttributes.addFlashAttribute("error",
                        messageSource.getMessage("producto.error06", new Object[]{fallidas}, locale));
            } else {
                redirectAttributes.addFlashAttribute("todoOk",
                        messageSource.getMessage("producto.guardado", null, locale));
            }
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error",
                    messageSource.getMessage("producto.error07", null, locale));
        } catch (IllegalStateException | DataIntegrityViolationException e) {
            redirectAttributes.addFlashAttribute("error",
                    messageSource.getMessage("producto.error08", null, locale));
        }
        return destino;
    }
}
