package com.rica.publicaciones.persistencia;

import javax.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "publicaciones")
public class PublicacionSql {
    @Id
    private String id;

    @Column(nullable = false)
    private String investigadorId;

    @Column(nullable = false)
    private String titulo;

    @Column(unique = true, nullable = false)
    private String doi;

    @Column(nullable = false)
    private int año;

    @Version
    private Long version;
}
