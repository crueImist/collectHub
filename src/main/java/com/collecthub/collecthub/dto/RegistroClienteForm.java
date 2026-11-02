package com.collecthub.collecthub.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class RegistroClienteForm {

    @NotBlank
    @Size(max = 30)
    private String nombre;

    @NotBlank
    @Size(max = 40)
    private String apellidos;

    @NotBlank
    @Email
    @Size(max = 75)
    private String correo;

    @NotBlank
    @Size(min = RegistroEmpresaForm.CLAVE_MINIMA, max = 72)
    private String contrasena;
}