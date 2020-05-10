package com.naterra.graphs.service;

import com.naterra.graphs.exception.GraphException;
import com.naterra.graphs.model.dto.EdgeDTO;
import com.naterra.graphs.model.dto.GraphDTO;
import com.naterra.graphs.model.dto.VertexDTO;

import java.util.Set;
import java.util.UUID;

public interface GraphTraverseService {

    void addGraph(GraphDTO graphDTO);

    void addVertex(VertexDTO vertexDTO);

    void addEdge(EdgeDTO edgeDTO);

    void removeVertex(VertexDTO vertexDTO);

    void removeEdge(EdgeDTO edgeDTO);

    Set<VertexDTO> traverse(UUID externalGraphId, UUID rootVertexId) throws GraphException;

    Set<VertexDTO> getPath(UUID externalGraphId, UUID fromVertexId, UUID toVertexId) throws GraphException;
}
