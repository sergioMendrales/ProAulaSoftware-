package com.mi.proyecto.ganado.ganadoapp.repository;

import com.mi.proyecto.ganado.ganadoapp.model.Veterinario;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VeterinarioRepository extends MongoRepository<Veterinario, String> {
    Veterinario findByEmail(String email);
}
