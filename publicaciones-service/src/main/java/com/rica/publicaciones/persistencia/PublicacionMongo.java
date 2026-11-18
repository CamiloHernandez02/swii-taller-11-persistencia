package com.rica.publicaciones.persistencia;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.annotation.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "publicaciones")
public class PublicacionMongo {
    @Id
    private String id;

    private String investigadorId;
    private String titulo;
    private String doi;
    private int año;
    private long timestamp;
}
