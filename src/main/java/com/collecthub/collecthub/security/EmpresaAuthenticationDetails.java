package com.collecthub.collecthub.security;

import jakarta.servlet.http.HttpServletRequest;
import lombok.Getter;
import org.springframework.security.web.authentication.WebAuthenticationDetails;

@Getter
public class EmpresaAuthenticationDetails extends WebAuthenticationDetails {

    private static final long serialVersionUID = 1L;

    private final String identificadorEmpresa;

    public EmpresaAuthenticationDetails(HttpServletRequest request) {
        super(request);
        String valor = request.getParameter("tienda");
        this.identificadorEmpresa = (valor == null) ? "" : valor.trim().toLowerCase();
    }
}