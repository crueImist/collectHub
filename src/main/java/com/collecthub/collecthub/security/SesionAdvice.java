package com.collecthub.collecthub.security;

import com.collecthub.collecthub.domain.Rol;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class SesionAdvice {

    @ModelAttribute("sesion")
    public UsuarioSesion sesion(@AuthenticationPrincipal UsuarioSesion sesion) {
        return sesion;
    }

    @ModelAttribute("esAdminPlataforma")
    public boolean esAdminPlataforma(@AuthenticationPrincipal UsuarioSesion sesion) {
        return sesion != null && sesion.tieneRol(Rol.ADMIN_PLATAFORMA);
    }

    @ModelAttribute("esAdminEmpresa")
    public boolean esAdminEmpresa(@AuthenticationPrincipal UsuarioSesion sesion) {
        return sesion != null && sesion.tieneRol(Rol.ADMIN_EMPRESA);
    }

    @ModelAttribute("esInventario")
    public boolean esInventario(@AuthenticationPrincipal UsuarioSesion sesion) {
        return sesion != null && sesion.tieneRol(Rol.INVENTARIO);
    }

    @ModelAttribute("esVentas")
    public boolean esVentas(@AuthenticationPrincipal UsuarioSesion sesion) {
        return sesion != null && sesion.tieneRol(Rol.VENTAS);
    }

    @ModelAttribute("esConsulta")
    public boolean esConsulta(@AuthenticationPrincipal UsuarioSesion sesion) {
        return sesion != null && sesion.tieneRol(Rol.CONSULTA);
    }

    @ModelAttribute("esCliente")
    public boolean esCliente(@AuthenticationPrincipal UsuarioSesion sesion) {
        return sesion != null && sesion.tieneRol(Rol.CLIENTE);
    }
}