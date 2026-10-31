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

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_rol")
    private Integer idRol;

    // Ejemplo: ROLE_ADMIN_EMPRESA (los valores iniciales están en database/01_base.sql)
    @Column(unique = true, nullable = false, length = 50)
    @NotBlank
    @Size(max = 50)
    private String nombre;

    // Relación de uno a muchos con la clase Usuario
    @OneToMany(mappedBy = "rol")
    private List<Usuario> usuarios;
}
