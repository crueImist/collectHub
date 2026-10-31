package com.collecthub.collecthub.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import java.util.List;
import lombok.*;

/**
 * Inquilino de la plataforma (RN-01, RN-02). Toda entidad de negocio que se
 * cree después (categoría, producto, pedido...) debe tener una referencia a
 * Empresa (columna id_empresa).
 */
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "empresa")
public class Empresa implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_empresa")
    private Integer idEmpresa;

    @Column(nullable = false, length = 100)
    @NotBlank
    @Size(max = 100)
    private String nombre;

    // Identificador que va en la ruta de la tienda: /tienda/{identificador}
    @Column(unique = true, nullable = false, length = 50)
    @NotBlank
    @Size(max = 50)
    @Pattern(regexp = "^[a-z0-9-]+$", message = "Solo minúsculas, números y guiones.")
    private String identificador;

    // Una empresa suspendida (RN-12) queda con activo = false
    @Column(nullable = false)
    private boolean activo = true;

    // Relación de uno a muchos con la clase Usuario
    @OneToMany(mappedBy = "empresa")
    private List<Usuario> usuarios;
}
