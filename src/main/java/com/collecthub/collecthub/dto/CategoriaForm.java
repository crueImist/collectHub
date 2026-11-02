package com.collecthub.collecthub.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class CategoriaForm {

    
    private Integer idCategoria;

    @NotBlank
    @Size(max = 50)
    private String descripcion;

    private boolean activo;
}
