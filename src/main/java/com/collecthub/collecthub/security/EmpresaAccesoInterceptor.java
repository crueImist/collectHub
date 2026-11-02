package com.collecthub.collecthub.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.servlet.HandlerInterceptor;

// RN-01: en /tienda/{identificador}/... el identificador debe ser el de la empresa de la sesión
public class EmpresaAccesoInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        Authentication autenticacion = SecurityContextHolder.getContext().getAuthentication();
        if (autenticacion != null && autenticacion.getPrincipal() instanceof UsuarioSesion sesion) {
            String ruta = request.getRequestURI().substring(request.getContextPath().length());
            String[] partes = ruta.split("/");              // ["", "tienda", "{identificador}", ...]
            String identificador = (partes.length > 2) ? partes[2] : "";
            if (!identificador.equals(sesion.getIdentificadorEmpresa())) {
                response.sendRedirect(request.getContextPath() + "/403");
                return false;
            }
        }
        return true;
    }
}