package com.naterra.graphs.repository;

import com.naterra.graphs.model.entity.GraphEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface GraphRepository extends JpaRepository<GraphEntity, Long> {

    GraphEntity findByExternalGraphId(UUID externalGraphId);
}
