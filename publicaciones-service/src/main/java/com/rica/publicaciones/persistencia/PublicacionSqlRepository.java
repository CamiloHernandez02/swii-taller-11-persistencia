package com.rica.publicaciones.persistencia;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PublicacionSqlRepository extends JpaRepository<PublicacionSql, String> {
}
