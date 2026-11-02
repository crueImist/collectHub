package com.collecthub.collecthub.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class PerfilEmpresaForm {

    @NotBlank
    @Size(max = 100)
    private String nombre;

    @Email
    @Size(max = 100)
    private String correoContacto;

    @Size(max = 30)
    private String telefonoContacto;

    @Size(max = 255)
    private String direccion;
}