package com.collecthub.collecthub.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import lombok.*;


@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "producto",
        uniqueConstraints = @UniqueConstraint(columnNames = {"id_empresa", "codigo"}))
public class Producto implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_producto")
    private Integer idProducto;

    @Column(nullable = false, length = 30)
    @NotBlank
    @Size(max = 30)
    private String codigo;

    @Column(nullable = false, length = 100)
    @NotBlank
    @Size(max = 100)
    private String nombre;

    @Column(name = "serie_franquicia", length = 100)
    @Size(max = 100)
    private String serieFranquicia;

    @Column(length = 100)
    @Size(max = 100)
    private String fabricante;

    @Column(length = 20)
    @Size(max = 20)
    private String escala;

    @Column(columnDefinition = "TEXT")
    @Size(max = 5000)
    private String descripcion;

    @Column(nullable = false, precision = 12, scale = 2)
    @NotNull
    @DecimalMin(value = "0.01")
    private BigDecimal precio;

    @Column(nullable = false)
    @NotNull
    @Min(0)
    private Integer existencias;

    @Column(name = "stock_minimo", nullable = false)
    @NotNull
    @Min(0)
    private Integer stockMinimo = 0;

    
    @Column(name = "ruta_imagen", length = 1024)
    private String rutaImagen;

    @Column(nullable = false)
    private boolean activo = true;

    
    @ManyToOne
    @JoinColumn(name = "id_empresa", nullable = false)
    private Empresa empresa;

    @ManyToOne
    @JoinColumn(name = "id_categoria", nullable = false)
    @NotNull
    private Categoria categoria;

    
    @OneToMany(mappedBy = "producto")
    private List<ProductoImagen> imagenes;
}
