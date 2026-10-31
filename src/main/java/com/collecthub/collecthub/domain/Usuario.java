package com.collecthub.collecthub.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import lombok.*;

/**
 * El correo es único dentro de cada empresa (RN-03). El administrador de la
 * plataforma no pertenece a ninguna empresa, por eso empresa puede ser null.
 */
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

    @Column(nullable = false, length = 100)
    @NotBlank
    @Size(max = 100)
    private String nombre;

    @Column(nullable = false, length = 100)
    @NotBlank
    @Email
    @Size(max = 100)
    private String correo;

    // Se guardará con hash (BCrypt) cuando se agregue Spring Security. Nunca en texto plano.
    @Column(nullable = false, length = 255)
    private String contrasena;

    @Column(nullable = false)
    private boolean activo = true;

    @ManyToOne
    @JoinColumn(name = "id_empresa")
    private Empresa empresa;

    @ManyToOne
    @JoinColumn(name = "id_rol", nullable = false)
    private Rol rol;
}
