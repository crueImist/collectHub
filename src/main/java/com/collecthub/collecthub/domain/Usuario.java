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

    // Único en toda la plataforma. Lo generaremos nosotros a partir del correo.
    @Column(nullable = false, unique = true, length = 50)
    @NotBlank
    @Size(max = 50)
    private String username;

    // Aquí va el hash BCrypt, nunca la contraseña en texto plano
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

    // null solo para el administrador de la plataforma
    @ManyToOne
    @JoinColumn(name = "id_empresa")
    private Empresa empresa;

    // Muchos a muchos con Rol a través de la tabla usuario_rol.
    // EAGER: los roles se cargan junto con el usuario, porque se necesitan al iniciar sesión.
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "usuario_rol",
            joinColumns = @JoinColumn(name = "id_usuario"),
            inverseJoinColumns = @JoinColumn(name = "id_rol"))
    private Set<Rol> roles = new HashSet<>();
}
