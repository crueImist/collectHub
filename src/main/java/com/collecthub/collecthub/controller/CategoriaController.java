package com.collecthub.collecthub.controller;

import com.collecthub.collecthub.domain.Categoria;
import com.collecthub.collecthub.dto.CategoriaForm;
import com.collecthub.collecthub.security.UsuarioSesion;
import com.collecthub.collecthub.service.CategoriaService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Locale;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


@Controller
@RequiredArgsConstructor
@RequestMapping("/categoria")
public class CategoriaController {

    private final CategoriaService categoriaService;
    private final MessageSource messageSource;

    @GetMapping("/listado")
    public String listado(@AuthenticationPrincipal UsuarioSesion sesion, Model model) {
        List<Categoria> categorias = categoriaService.getCategorias(sesion.getIdEmpresa());
        model.addAttribute("categorias", categorias);
        model.addAttribute("totalCategorias", categorias.size());
        return "categoria/listado";
    }

    @PostMapping("/guardar")
    public String guardar(@AuthenticationPrincipal UsuarioSesion sesion,
            @Valid CategoriaForm form, BindingResult bindingResult,
            @RequestParam(value = "imagenFile", required = false) MultipartFile imagenFile,
            RedirectAttributes redirectAttributes, Locale locale) {
        String destino = "redirect:/categoria/listado";

        if (bindingResult.hasErrors()) {
            return error(redirectAttributes, "categoria.error04", locale, destino);
        }
        if (!categoriaService.imagenValida(imagenFile)) {
            return error(redirectAttributes, "categoria.error06", locale, destino);
        }
        try {
            boolean imagenGuardada = categoriaService.guardar(sesion.getIdEmpresa(), form, imagenFile);
            String clave = imagenGuardada ? "categoria.guardada" : "categoria.guardada.sinImagen";
            redirectAttributes.addFlashAttribute("todoOk", messageSource.getMessage(clave, null, locale));
        } catch (IllegalArgumentException e) {
            return error(redirectAttributes, "categoria.error01", locale, destino);   // no existe
        } catch (IllegalStateException | DataIntegrityViolationException e) {
            return error(redirectAttributes, "categoria.error05", locale, destino);   // descripción repetida
        }
        return destino;
    }

    @PostMapping("/estado")
    public String cambiarEstado(@AuthenticationPrincipal UsuarioSesion sesion,
            @RequestParam Integer idCategoria, RedirectAttributes redirectAttributes, Locale locale) {
        String destino = "redirect:/categoria/listado";
        try {
            categoriaService.cambiarEstado(sesion.getIdEmpresa(), idCategoria);
            redirectAttributes.addFlashAttribute("todoOk",
                    messageSource.getMessage("categoria.estado.cambiado", null, locale));
        } catch (IllegalArgumentException e) {
            return error(redirectAttributes, "categoria.error01", locale, destino);
        }
        return destino;
    }

    private String error(RedirectAttributes redirectAttributes, String clave, Locale locale, String destino) {
        redirectAttributes.addFlashAttribute("error", messageSource.getMessage(clave, null, locale));
        return destino;
    }
}
