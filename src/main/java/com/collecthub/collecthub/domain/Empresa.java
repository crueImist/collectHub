package com.collecthub.collecthub.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import java.util.List;
import lombok.*;


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

    
    @Column(unique = true, nullable = false, length = 50)
    @NotBlank
    @Size(max = 50)
    @Pattern(regexp = "^[a-z0-9-]+$", message = "Solo minúsculas, números y guiones.")
    private String identificador;

    
    @Column(nullable = false)
    private boolean activo = true;
    
    
    @Column(name = "correo_contacto", length = 100)
    @Size(max = 100)
    private String correoContacto;

    @Column(name = "telefono_contacto", length = 30)
    @Size(max = 30)
    private String telefonoContacto;

    @Column(length = 255)
    @Size(max = 255)
    private String direccion;

    @Column(name = "logo_ruta", length = 1024)
    private String logoRuta;
    
    
    @OneToMany(mappedBy = "empresa")
    private List<Usuario> usuarios;
}
