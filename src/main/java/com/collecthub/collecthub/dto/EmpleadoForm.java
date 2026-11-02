package com.collecthub.collecthub.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class EmpleadoForm {

    
    private Integer idUsuario;

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

    @Size(max = 25)
    private String telefono;

    
    @Size(max = 72)
    private String contrasena;

    
    private List<String> roles = new ArrayList<>();
}
