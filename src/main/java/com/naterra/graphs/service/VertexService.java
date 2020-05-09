package com.naterra.graphs.service;

import com.naterra.graphs.model.dto.VertexDTO;

import java.util.UUID;

public interface VertexService {

    VertexDTO addVertex(UUID externalGraphId, VertexDTO vertex) throws Exception;
}
