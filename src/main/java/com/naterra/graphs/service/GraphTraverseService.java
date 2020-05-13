package com.naterra.graphs.service;

import com.naterra.graphs.exception.GraphException;
import com.naterra.graphs.model.EdgeDTO;
import com.naterra.graphs.model.GraphDTO;
import com.naterra.graphs.model.TraverseDTO;
import com.naterra.graphs.model.VertexDTO;

import java.util.Set;
import java.util.UUID;

public interface GraphTraverseService {

    GraphDTO addGraph(GraphDTO graphDTO) throws GraphException;

    void addVertex(UUID externalGraphId, VertexDTO vertexDTO) throws GraphException;

    void addEdge(EdgeDTO edgeDTO) throws GraphException;

    GraphDTO getGraphById(UUID externalGraphId);

    Set<VertexDTO> traverse(TraverseDTO traverseDTO) throws GraphException;

    Set<VertexDTO> getPath(UUID externalGraphId, UUID fromVertexId, UUID toVertexId) throws GraphException;
}
