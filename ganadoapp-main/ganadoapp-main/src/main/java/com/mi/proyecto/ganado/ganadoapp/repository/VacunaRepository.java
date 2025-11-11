package com.mi.proyecto.ganado.ganadoapp.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.mi.proyecto.ganado.ganadoapp.model.Vacuna;

@Repository
public interface VacunaRepository extends MongoRepository<Vacuna, String> {
    List<Vacuna> findByGanadoId(String ganadoId);
    List<Vacuna> findByCodigoGanado(String codigoGanado);
}



