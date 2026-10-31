package com.collecthub.collecthub.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import lombok.*;

/**
 * VERSIÓN MÍNIMA, solo para que el módulo de catálogo compile y pueda mostrar
 * categorías. El CRUD de categorías es de HU-06 (integrante 2): cuando él suba
 * su Categoria.java y CategoriaRepository.java, se usan los suyos y se borran estos.
 * Lo que necesita el catálogo: empresa, descripcion, activo, y en el repositorio
 * findByEmpresa_IdEmpresaAndActivoTrue.
 */
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "categoria")
public class Categoria implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_categoria")
    private Integer idCategoria;

    @Column(nullable = false, length = 50)
    @NotBlank
    @Size(max = 50)
    private String descripcion;

    @Column(name = "ruta_imagen", length = 1024)
    private String rutaImagen;

    @Column(nullable = false)
    private boolean activo = true;

    @ManyToOne
    @JoinColumn(name = "id_empresa", nullable = false)
    private Empresa empresa;
}
