package com.collectHub.domain;

import jakarta.persistence.*;
import java.io.Serializable;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "direccion_entrega")

public class DireccionEntrega implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_direccion")
    private Integer idDireccion;

    @Column(nullable = false, length = 100)
    private String destinatario;

    @Column(nullable = false, length = 20)
    private String telefono;

    @Column(nullable = false, length = 50)
    private String provincia;

    @Column(nullable = false, length = 50)
    private String canton;

    @Column(nullable = false, length = 50)
    private String distrito;

    @Column(name = "direccion_exacta",
            nullable = false, length = 255)
    private String direccionExacta;
}
