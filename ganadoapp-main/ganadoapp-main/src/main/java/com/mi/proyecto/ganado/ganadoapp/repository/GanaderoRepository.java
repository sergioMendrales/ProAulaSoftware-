package com.mi.proyecto.ganado.ganadoapp.repository;

import com.mi.proyecto.ganado.ganadoapp.model.Ganadero;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GanaderoRepository extends MongoRepository<Ganadero, String> {
    Ganadero findByEmail(String email);
}
