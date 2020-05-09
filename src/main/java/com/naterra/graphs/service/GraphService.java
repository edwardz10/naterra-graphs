package com.naterra.graphs.service;

import com.naterra.graphs.model.dto.GraphDTO;

import java.util.List;
import java.util.UUID;

public interface GraphService {

    List<GraphDTO> getAllGraphs();

    GraphDTO createGraph(GraphDTO graph);

    GraphDTO getGraphByExternalId(UUID externalGraphId);
}
