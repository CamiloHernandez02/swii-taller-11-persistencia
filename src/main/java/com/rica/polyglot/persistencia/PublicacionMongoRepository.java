package com.rica.polyglot.persistencia;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PublicacionMongoRepository extends MongoRepository<PublicacionMongo, String> {
    PublicacionMongo findByDoi(String doi);
}
