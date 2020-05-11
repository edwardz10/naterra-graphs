package com.naterra.graphs.service;

import com.naterra.graphs.exception.GraphException;
import com.naterra.graphs.model.EdgeDTO;
import com.naterra.graphs.model.GraphDTO;
import com.naterra.graphs.model.VertexDTO;

import java.util.Set;
import java.util.UUID;

public interface GraphTraverseService {

    GraphDTO addGraph(GraphDTO graphDTO);

    void addVertex(UUID externalGraphId, VertexDTO vertexDTO);

    void addEdge(EdgeDTO edgeDTO);

    void removeVertex(VertexDTO vertexDTO);

    void removeEdge(EdgeDTO edgeDTO);

    GraphDTO getGraphById(UUID externalGraphId);

    Set<VertexDTO> traverse(UUID externalGraphId, UUID rootVertexId) throws GraphException;

    Set<VertexDTO> getPath(UUID externalGraphId, UUID fromVertexId, UUID toVertexId) throws GraphException;
}
