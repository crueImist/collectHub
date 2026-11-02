package com.collecthub.collecthub.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "usuario",
        uniqueConstraints = @UniqueConstraint(columnNames = {"id_empresa", "correo"}))
public class Usuario implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario")
    private Integer idUsuario;

    
    @Column(nullable = false, unique = true, length = 50)
    @NotBlank
    @Size(max = 50)
    private String username;

    
    @Column(nullable = false, length = 512)
    private String password;

    @Column(nullable = false, length = 30)
    @NotBlank
    @Size(max = 30)
    private String nombre;

    @Column(nullable = false, length = 40)
    @NotBlank
    @Size(max = 40)
    private String apellidos;

    @Column(nullable = false, length = 75)
    @NotBlank
    @Email
    @Size(max = 75)
    private String correo;

    @Column(length = 25)
    @Size(max = 25)
    private String telefono;

    @Column(name = "ruta_imagen", length = 1024)
    private String rutaImagen;

    @Column(nullable = false)
    private boolean activo = true;

    
    @ManyToOne
    @JoinColumn(name = "id_empresa")
    private Empresa empresa;

    
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "usuario_rol",
            joinColumns = @JoinColumn(name = "id_usuario"),
            inverseJoinColumns = @JoinColumn(name = "id_rol"))
    private Set<Rol> roles = new HashSet<>();
}
