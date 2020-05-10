package com.naterra.graphs.service;

import com.naterra.graphs.exception.GraphException;
import com.naterra.graphs.model.dto.EdgeDTO;
import com.naterra.graphs.model.entity.EdgeEntity;

import java.util.UUID;

public interface EdgeService {

    EdgeDTO addEdge(UUID externalGraphId, EdgeDTO edgeDTO) throws GraphException;

    EdgeDTO addEdge(UUID externalGraphId, String values) throws GraphException;

    EdgeDTO convert(EdgeEntity edgeEntity);
}
