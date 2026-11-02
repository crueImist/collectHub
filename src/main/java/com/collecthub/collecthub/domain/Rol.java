package com.collecthub.collecthub.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import java.util.List;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "rol")
public class Rol implements Serializable {

    private static final long serialVersionUID = 1L;

   
    public static final String ADMIN_PLATAFORMA = "ADMIN_PLATAFORMA";
    public static final String ADMIN_EMPRESA = "ADMIN_EMPRESA";
    public static final String INVENTARIO = "INVENTARIO";
    public static final String VENTAS = "VENTAS";
    public static final String CONSULTA = "CONSULTA";
    public static final String CLIENTE = "CLIENTE";

    
    public static final List<String> ROLES_EMPLEADO = List.of(INVENTARIO, VENTAS, CONSULTA);

    
    public static final List<String> ROLES_PERSONAL = List.of(ADMIN_EMPRESA, INVENTARIO, VENTAS, CONSULTA);

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_rol")
    private Integer idRol;

    
    @Column(name = "rol", unique = true, nullable = false, length = 30)
    @NotBlank
    @Size(max = 30)
    private String nombre;
}
