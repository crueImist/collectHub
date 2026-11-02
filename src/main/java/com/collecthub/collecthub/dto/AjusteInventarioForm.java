package com.collecthub.collecthub.dto;

import com.collecthub.collecthub.domain.TipoMovimiento;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class AjusteInventarioForm {

    @NotNull
    private Integer idProducto;

    
    @NotNull
    private TipoMovimiento tipo;

    @NotNull
    @Min(1)
    @Max(1000000)
    private Integer cantidad;

    @NotBlank
    @Size(max = 255)
    private String motivo;
}
