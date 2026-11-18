package com.rica.polyglot.persistencia;

import jakarta.persistence.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * PublicacionSql: Almacenada en PostgreSQL
 * Datos transaccionales: ID, titulo, año, etc.
 */
@Entity
@Table(name = "publicaciones")
public class PublicacionSql {
    @jakarta.persistence.Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String titulo;
    private String autores;
    private Integer anio;
    private String doi;

    public PublicacionSql() {}

    public PublicacionSql(String titulo, String autores, Integer anio, String doi) {
        this.titulo = titulo;
        this.autores = autores;
        this.anio = anio;
        this.doi = doi;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
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
}
