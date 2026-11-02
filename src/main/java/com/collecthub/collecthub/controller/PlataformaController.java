package com.collecthub.collecthub.controller;

import com.collecthub.collecthub.dto.RegistroEmpresaForm;
import com.collecthub.collecthub.service.EmpresaService;
import jakarta.validation.Valid;
import java.util.Locale;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
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

// HU-01: solo ROLE_ADMIN_PLATAFORMA llega aquí (regla /plataforma/** en SecurityConfig)
@Controller
@RequiredArgsConstructor
@RequestMapping("/plataforma/empresa")
public class PlataformaController {

    private final EmpresaService empresaService;
    private final MessageSource messageSource;

    @GetMapping("/nueva")
    public String formulario(Model model) {
        model.addAttribute("form", new RegistroEmpresaForm());
        return "plataforma/empresa-nueva";
    }

    @PostMapping("/nueva")
    public String registrar(@Valid @ModelAttribute("form") RegistroEmpresaForm form,
            BindingResult bindingResult,
            @RequestParam(value = "logo", required = false) MultipartFile logo,
            Model model, RedirectAttributes redirectAttributes, Locale locale) {
        if (!empresaService.logoValido(logo)) {
            model.addAttribute("error", messageSource.getMessage("empresa.logo.error", null, locale));
            return "plataforma/empresa-nueva";
        }
        if (bindingResult.hasErrors()) {
            return "plataforma/empresa-nueva";
        }

        EmpresaService.ResultadoRegistro resultado = empresaService.registrar(form, logo);
        String clave = resultado.logoGuardado()
                ? "plataforma.empresa.creada" : "plataforma.empresa.creada.sinLogo";
        redirectAttributes.addFlashAttribute("todoOk", messageSource.getMessage(clave,
                new Object[]{resultado.empresa().getIdentificador()}, locale));
        return "redirect:/plataforma/empresa/nueva";
    }
}