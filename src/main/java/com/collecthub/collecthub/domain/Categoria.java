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
@Table(name = "categoria",
        uniqueConstraints = @UniqueConstraint(columnNames = {"id_empresa", "descripcion"}))
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
    @Size(max = 1024)
    private String rutaImagen;

    @Column(nullable = false)
    private boolean activo = true;

    // Se asigna en el servidor a partir de la sesión, nunca desde el formulario
    @ManyToOne
    @JoinColumn(name = "id_empresa", nullable = false)
    private Empresa empresa;
}
