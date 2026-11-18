package com.rica.polyglot.persistencia;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PublicacionSqlRepository extends JpaRepository<PublicacionSql, Long> {
    PublicacionSql findByDoi(String doi);
}
