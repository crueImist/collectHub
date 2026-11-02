package com.collecthub.collecthub.controller;

import com.collecthub.collecthub.domain.Producto;
import com.collecthub.collecthub.dto.AjusteInventarioForm;
import com.collecthub.collecthub.security.UsuarioSesion;
import com.collecthub.collecthub.service.InventarioService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


@Controller
@RequiredArgsConstructor
@RequestMapping("/inventario")
public class InventarioController {

    private final InventarioService inventarioService;
    private final MessageSource messageSource;

    @GetMapping("/listado")
    public String listado(@AuthenticationPrincipal UsuarioSesion sesion, Model model) {
        List<Producto> productos = inventarioService.getProductos(sesion.getIdEmpresa());
        model.addAttribute("productos", productos);
        model.addAttribute("totalProductos", productos.size());
        return "inventario/listado";
    }

    @GetMapping("/historial/{idProducto}")
    public String historial(@AuthenticationPrincipal UsuarioSesion sesion,
            @PathVariable Integer idProducto, Model model,
            RedirectAttributes redirectAttributes, Locale locale) {
        Optional<Producto> producto = inventarioService.getProducto(sesion.getIdEmpresa(), idProducto);
        if (producto.isEmpty()) {
            redirectAttributes.addFlashAttribute("error",
                    messageSource.getMessage("inventario.error01", null, locale));
            return "redirect:/inventario/listado";
        }
        model.addAttribute("producto", producto.get());
        model.addAttribute("movimientos",
                inventarioService.getHistorial(sesion.getIdEmpresa(), idProducto));
        return "inventario/historial";
    }

    @PostMapping("/ajustar")
    public String ajustar(@AuthenticationPrincipal UsuarioSesion sesion,
            @Valid AjusteInventarioForm form, BindingResult bindingResult,
            RedirectAttributes redirectAttributes, Locale locale) {
        String destino = "redirect:/inventario/listado";

        if (bindingResult.hasErrors()) {
            return error(redirectAttributes, "inventario.error03", locale, destino);
        }
        try {
            int existencias = inventarioService.ajustar(sesion.getIdEmpresa(), sesion.getIdUsuario(),
                    form.getIdProducto(), form.getTipo(), form.getCantidad(), form.getMotivo());
            redirectAttributes.addFlashAttribute("todoOk",
                    messageSource.getMessage("inventario.guardado", new Object[]{existencias}, locale));
        } catch (IllegalArgumentException e) {
            String clave = "producto".equals(e.getMessage()) ? "inventario.error01" : "inventario.error03";
            return error(redirectAttributes, clave, locale, destino);
        } catch (IllegalStateException e) {
            return error(redirectAttributes, "inventario.error02", locale, destino);   // stock insuficiente
        } catch (ArithmeticException e) {
            return error(redirectAttributes, "inventario.error03", locale, destino);   // desborda el máximo
        }
        return destino;
    }

    private String error(RedirectAttributes redirectAttributes, String clave, Locale locale, String destino) {
        redirectAttributes.addFlashAttribute("error", messageSource.getMessage(clave, null, locale));
        return destino;
    }
}
