package com.naterra.graphs.repository;

import com.naterra.graphs.model.entity.VertexEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface VertexRepository extends CrudRepository<VertexEntity, Long> {

    VertexEntity findVertexEntityByExternalId(UUID externalId);

    VertexEntity findVertexEntityByValue(String value);

    List<VertexEntity> findAllByExternalGraphId(UUID externalGraphId);
}
