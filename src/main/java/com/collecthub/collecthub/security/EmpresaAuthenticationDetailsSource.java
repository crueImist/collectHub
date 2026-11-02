package com.collecthub.collecthub.security;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.authentication.AuthenticationDetailsSource;

public class EmpresaAuthenticationDetailsSource
        implements AuthenticationDetailsSource<HttpServletRequest, EmpresaAuthenticationDetails> {

    @Override
    public EmpresaAuthenticationDetails buildDetails(HttpServletRequest request) {
        return new EmpresaAuthenticationDetails(request);
    }
}