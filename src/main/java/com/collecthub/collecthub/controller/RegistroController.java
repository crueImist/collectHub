package com.collecthub.collecthub.controller;

import com.collecthub.collecthub.domain.Empresa;
import com.collecthub.collecthub.dto.RegistroClienteForm;
import com.collecthub.collecthub.service.EmpresaService;
import com.collecthub.collecthub.service.UsuarioService;
import jakarta.validation.Valid;
import java.util.Locale;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


@Controller
@RequiredArgsConstructor
@RequestMapping("/tienda/{identificador}/registro")
public class RegistroController {

    private final EmpresaService empresaService;
    private final UsuarioService usuarioService;
    private final MessageSource messageSource;

    @GetMapping
    public String formulario(@PathVariable String identificador, Model model) {
        Optional<Empresa> empresa = empresaService.buscarActiva(identificador);
        if (empresa.isEmpty()) {
            return "redirect:/login";           
        }
        model.addAttribute("form", new RegistroClienteForm());
        return vista(model, empresa.get());
    }

    @PostMapping
    public String registrar(@PathVariable String identificador,
            @Valid @ModelAttribute("form") RegistroClienteForm form, BindingResult bindingResult,
            Model model, RedirectAttributes redirectAttributes, Locale locale) {
        Optional<Empresa> empresa = empresaService.buscarActiva(identificador);
        if (empresa.isEmpty()) {
            return "redirect:/login";
        }
        if (bindingResult.hasErrors()) {
            return vista(model, empresa.get());
        }
        try {
            usuarioService.registrarCliente(empresa.get(), form);
        } catch (IllegalStateException | DataIntegrityViolationException e) {
            // El correo ya existe en esta tienda (RN-03)
            bindingResult.rejectValue("correo", "correo.duplicado",
                    messageSource.getMessage("registro.error.correo", null, locale));
            return vista(model, empresa.get());
        }
        redirectAttributes.addFlashAttribute("todoOk",
                messageSource.getMessage("registro.ok", null, locale));
        return "redirect:/tienda/" + empresa.get().getIdentificador() + "/login";
    }

    private String vista(Model model, Empresa empresa) {
        model.addAttribute("empresa", empresa);
        model.addAttribute("identificador", empresa.getIdentificador());
        return "registro/cliente";
    }
}
