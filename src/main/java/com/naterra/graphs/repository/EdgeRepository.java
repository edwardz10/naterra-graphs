package com.naterra.graphs.repository;

import com.naterra.graphs.model.entity.EdgeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface EdgeRepository extends JpaRepository<EdgeEntity, Long> {

    List<EdgeEntity> findAllByExternalGraphId(UUID externalGraphId);
}
