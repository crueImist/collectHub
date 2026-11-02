package com.collecthub.collecthub.controller;

import com.collecthub.collecthub.domain.Empresa;
import com.collecthub.collecthub.dto.PerfilEmpresaForm;
import com.collecthub.collecthub.security.UsuarioSesion;
import com.collecthub.collecthub.service.EmpresaService;
import jakarta.validation.Valid;
import java.util.Locale;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

// HU-02: el administrador ve y modifica ÚNICAMENTE su empresa. La empresa sale de la
// sesión, nunca de un parámetro del navegador (RN-01).
@Controller
@RequiredArgsConstructor
@RequestMapping("/empresa")
public class EmpresaController {

    private final EmpresaService empresaService;
    private final MessageSource messageSource;

    @GetMapping("/perfil")
    public String perfil(@AuthenticationPrincipal UsuarioSesion sesion, Model model) {
        Empresa empresa = empresaService.getEmpresa(sesion.getIdEmpresa());
        PerfilEmpresaForm form = new PerfilEmpresaForm();
        form.setNombre(empresa.getNombre());
        form.setCorreoContacto(empresa.getCorreoContacto());
        form.setTelefonoContacto(empresa.getTelefonoContacto());
        form.setDireccion(empresa.getDireccion());
        model.addAttribute("form", form);
        return vista(model, empresa);
    }

    @PostMapping("/perfil")
    public String guardar(@AuthenticationPrincipal UsuarioSesion sesion,
            @Valid @ModelAttribute("form") PerfilEmpresaForm form, BindingResult bindingResult,
            @RequestParam(value = "logo", required = false) MultipartFile logo,
            Model model, RedirectAttributes redirectAttributes, Locale locale) {
        if (!empresaService.logoValido(logo)) {
            model.addAttribute("error", messageSource.getMessage("empresa.logo.error", null, locale));
            return vista(model, empresaService.getEmpresa(sesion.getIdEmpresa()));
        }
        if (bindingResult.hasErrors()) {
            return vista(model, empresaService.getEmpresa(sesion.getIdEmpresa()));
        }

        boolean logoGuardado = empresaService.actualizarPerfil(sesion.getIdEmpresa(), form, logo);
        String clave = logoGuardado ? "empresa.perfil.actualizada" : "empresa.perfil.actualizada.sinLogo";
        redirectAttributes.addFlashAttribute("todoOk", messageSource.getMessage(clave, null, locale));
        return "redirect:/empresa/perfil";
    }

    private String vista(Model model, Empresa empresa) {
        model.addAttribute("empresa", empresa);
        model.addAttribute("identificador", empresa.getIdentificador());
        return "empresa/perfil";
    }
}