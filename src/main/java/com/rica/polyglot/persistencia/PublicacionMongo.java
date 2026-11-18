package com.rica.polyglot.persistencia;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.annotation.Id;

/**
 * PublicacionMongo: Almacenada en MongoDB
 * Datos desnormalizados para lectura rápida
 */
@Document(collection = "publicaciones")
public class PublicacionMongo {
    @Id
    private String id;
    
    private String titulo;
    private String autores;
    private Integer anio;
    private String doi;
    private String citaciones;
    private Long citacionesCount;

    public PublicacionMongo() {}

    public PublicacionMongo(String titulo, String autores, Integer anio, String doi) {
        this.titulo = titulo;
        this.autores = autores;
        this.anio = anio;
        this.doi = doi;
        this.citacionesCount = 0L;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getAutores() {
        return autores;
    }

    public void setAutores(String autores) {
        this.autores = autores;
    }

    public Integer getAnio() {
        return anio;
    }

    public void setAnio(Integer anio) {
        this.anio = anio;
    }

    public String getDoi() {
        return doi;
    }

    public void setDoi(String doi) {
        this.doi = doi;
    }

    public String getCitaciones() {
        return citaciones;
    }

    public void setCitaciones(String citaciones) {
        this.citaciones = citaciones;
    }

    public Long getCitacionesCount() {
        return citacionesCount;
    }

    public void setCitacionesCount(Long citacionesCount) {
        this.citacionesCount = citacionesCount;
    }
}
