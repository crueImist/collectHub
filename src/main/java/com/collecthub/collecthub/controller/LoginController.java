package com.collecthub.collecthub.controller;

import com.collecthub.collecthub.domain.Empresa;
import com.collecthub.collecthub.repository.EmpresaRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
public class LoginController {

    private final EmpresaRepository empresaRepository;

    
    @GetMapping("/login")
    public String login(Model model) {
        model.addAttribute("tiendaFija", false);
        return "login/login";
    }

    
    @GetMapping("/tienda/{identificador}/login")
    public String loginTienda(@PathVariable String identificador, Model model) {
        Optional<Empresa> empresa = empresaRepository.findByIdentificador(identificador)
                .filter(Empresa::isActivo);
        if (empresa.isEmpty()) {
            return "redirect:/login"; 
        }
        model.addAttribute("empresa", empresa.get());
        model.addAttribute("identificador", identificador);
        model.addAttribute("tiendaFija", true);
        return "login/login";
    }

    
    @RequestMapping("/403")
    public String accesoDenegado() {
        return "errores/403";
    }
}