package com.mi.proyecto.ganado.ganadoapp.repository;

import com.mi.proyecto.ganado.ganadoapp.model.Ganado;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GanadoRepository extends MongoRepository<Ganado, String> {

    List<Ganado> findByPropietarioId(String propietarioId);

    Ganado findByCodigoOficial(String codigoOficial);


    long countBySexo(String sexo);

    long countByEstadoSalud(String estadoSalud);
}


