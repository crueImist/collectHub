package com.collecthub.collecthub.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "rol")
public class Rol implements Serializable {

    private static final long serialVersionUID = 1L;

    // Nombres de los roles tal como están en la tabla (sin el prefijo ROLE_)
    public static final String ADMIN_PLATAFORMA = "ADMIN_PLATAFORMA";
    public static final String ADMIN_EMPRESA = "ADMIN_EMPRESA";
    public static final String INVENTARIO = "INVENTARIO";
    public static final String VENTAS = "VENTAS";
    public static final String CONSULTA = "CONSULTA";
    public static final String CLIENTE = "CLIENTE";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_rol")
    private Integer idRol;

    // En la tabla la columna se llama "rol", pero en Java dejamos el nombre "nombre"
    @Column(name = "rol", unique = true, nullable = false, length = 30)
    @NotBlank
    @Size(max = 30)
    private String nombre;
}
