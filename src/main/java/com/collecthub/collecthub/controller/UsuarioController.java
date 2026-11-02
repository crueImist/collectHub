package com.collecthub.collecthub.controller;

import com.collecthub.collecthub.domain.Rol;
import com.collecthub.collecthub.domain.Usuario;
import com.collecthub.collecthub.dto.EmpleadoForm;
import com.collecthub.collecthub.security.UsuarioSesion;
import com.collecthub.collecthub.service.UsuarioService;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


@Controller
@RequiredArgsConstructor
@RequestMapping("/usuario")
public class UsuarioController {

    private final UsuarioService usuarioService;
    private final MessageSource messageSource;

    @GetMapping("/listado")
    public String listado(@AuthenticationPrincipal UsuarioSesion sesion, Model model) {
        List<Usuario> usuarios = usuarioService.listarPersonal(sesion.getIdEmpresa());
        model.addAttribute("usuarios", usuarios);
        model.addAttribute("totalUsuarios", usuarios.size());
        model.addAttribute("rolesDisponibles", Rol.ROLES_EMPLEADO);
        return "usuario/listado";
    }

    @PostMapping("/guardar")
    public String guardar(@AuthenticationPrincipal UsuarioSesion sesion,
            @Valid EmpleadoForm form, BindingResult bindingResult,
            RedirectAttributes redirectAttributes, Locale locale) {
        String destino = "redirect:/usuario/listado";

        if (bindingResult.hasErrors()) {
            return error(redirectAttributes, "usuario.error04", locale, destino);
        }
        try {
            usuarioService.guardarEmpleado(sesion.getIdEmpresa(), form);
            redirectAttributes.addFlashAttribute("todoOk",
                    messageSource.getMessage("usuario.guardado", null, locale));
        } catch (IllegalArgumentException e) {
            String clave = switch (String.valueOf(e.getMessage())) {
                case "rol" -> "usuario.error07";       // sin roles válidos
                case "clave" -> "usuario.error06";     // contraseña faltante o muy corta
                default -> "usuario.error01";          // el usuario no existe
            };
            return error(redirectAttributes, clave, locale, destino);
        } catch (IllegalStateException | DataIntegrityViolationException e) {
            return error(redirectAttributes, "usuario.error05", locale, destino);   // correo repetido (RN-03)
        }
        return destino;
    }

    @PostMapping("/estado")
    public String cambiarEstado(@AuthenticationPrincipal UsuarioSesion sesion,
            @RequestParam Integer idUsuario, RedirectAttributes redirectAttributes, Locale locale) {
        String destino = "redirect:/usuario/listado";
        try {
            usuarioService.cambiarEstado(sesion.getIdEmpresa(), idUsuario);
            redirectAttributes.addFlashAttribute("todoOk",
                    messageSource.getMessage("usuario.estado.cambiado", null, locale));
        } catch (IllegalArgumentException e) {
            return error(redirectAttributes, "usuario.error01", locale, destino);
        }
        return destino;
    }

    private String error(RedirectAttributes redirectAttributes, String clave, Locale locale, String destino) {
        redirectAttributes.addFlashAttribute("error", messageSource.getMessage(clave, null, locale));
        return destino;
    }
}
