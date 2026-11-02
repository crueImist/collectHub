package com.collecthub.collecthub.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class RegistroEmpresaForm {

    
    public static final int CLAVE_MINIMA = 8;

    @NotBlank
    @Size(max = 100)
    private String nombreComercial;

    @NotBlank
    @Size(max = 30)
    private String nombreAdministrador;

    @NotBlank
    @Size(max = 40)
    private String apellidosAdministrador;

   
    @NotBlank
    @Email
    @Size(max = 75)
    private String correo;

    @Size(max = 25)
    private String telefono;

    @NotBlank
    @Size(min = CLAVE_MINIMA, max = 72)   
    private String contrasena;
}