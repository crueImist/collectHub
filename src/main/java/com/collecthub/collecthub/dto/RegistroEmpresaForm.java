package com.collecthub.collecthub.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

// HU-01: registrar una empresa con su administrador inicial
@Getter
@Setter
public class RegistroEmpresaForm {

    // Mínimo de la contraseña (el prototipo dice 8). Se cambia solo aquí.
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

    // Es el correo de contacto de la empresa y el correo con el que el administrador inicia sesión
    @NotBlank
    @Email
    @Size(max = 75)
    private String correo;

    @Size(max = 25)
    private String telefono;

    @NotBlank
    @Size(min = CLAVE_MINIMA, max = 72)   // BCrypt solo usa los primeros 72 caracteres
    private String contrasena;
}