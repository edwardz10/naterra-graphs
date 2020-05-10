package com.naterra.graphs.service.impl;

import com.naterra.graphs.exception.GraphException;
import com.naterra.graphs.model.dto.EdgeDTO;
import com.naterra.graphs.model.dto.GraphDTO;
import com.naterra.graphs.model.dto.VertexDTO;
import com.naterra.graphs.service.GraphTraverseService;
import com.naterra.graphs.util.GraphUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class GraphTraverseServiceImpl implements GraphTraverseService {

    private static final Logger LOGGER = LoggerFactory.getLogger(GraphTraverseServiceImpl.class);

    private Map<UUID, Map<VertexDTO, List<VertexDTO>>> adjacentMatrixes = new HashMap<>();

    @Override
    public void addGraph(GraphDTO graphDTO) {
        adjacentMatrixes.put(graphDTO.getExternalGraphId(), new HashMap<>());
    }

    @Override
    public void addVertex(VertexDTO vertexDTO) {
        adjacentMatrixes.get(vertexDTO.getExternalGraphId()).put(vertexDTO, new ArrayList<>());
    }

    @Override
    public void addEdge(EdgeDTO edgeDTO) {
        Map<VertexDTO, List<VertexDTO>> matrix = adjacentMatrixes.get(edgeDTO.getExternalGraphId());
        matrix.get(edgeDTO.getFromVertex()).add(edgeDTO.getToVertex());
        matrix.get(edgeDTO.getToVertex()).add(edgeDTO.getFromVertex());
    }

    @Override
    public void removeVertex(VertexDTO vertexDTO) {

    }

    @Override
    public void removeEdge(EdgeDTO edgeDTO) {

    }

    @Override
    public Set<VertexDTO> traverse(UUID externalGraphId, UUID rootVertexId) throws GraphException {
        VertexDTO rootVertex = validateAndFindRoot(externalGraphId, rootVertexId);
        Map<VertexDTO, List<VertexDTO>> matrix = adjacentMatrixes.get(externalGraphId);

        Set<VertexDTO> visited = new LinkedHashSet<>();
        Queue<VertexDTO> queue = new LinkedList<>();

        queue.add(rootVertex);
        visited.add(rootVertex);

        while (!queue.isEmpty()) {
            VertexDTO vertex = queue.poll();

            for (VertexDTO v : matrix.get(vertex)) {
                if (!visited.contains(v)) {
                    visited.add(v);
                    queue.add(v);
                }
            }
        }

        return visited;
    }

    @Override
    public Set<VertexDTO> getPath(UUID externalGraphId, UUID fromVertexId, UUID toVertexId) throws GraphException {
        boolean found = false;
        VertexDTO rootVertex = validateAndFindRoot(externalGraphId, fromVertexId, toVertexId);
        Map<VertexDTO, List<VertexDTO>> matrix = adjacentMatrixes.get(externalGraphId);

        Set<VertexDTO> path = new LinkedHashSet<>();
        Queue<VertexDTO> queue = new LinkedList<>();

        queue.add(rootVertex);
        path.add(rootVertex);

        while (!queue.isEmpty()) {
            VertexDTO vertex = queue.poll();

            for (VertexDTO v : matrix.get(vertex)) {
                if (v.getExternalId().equals(toVertexId)) {
                    path.add(v);
                    found = true;
                    break;
                }

                if (!path.contains(v)) {
                    path.add(v);
                    queue.add(v);
                }
            }
        }

        if (!found) {
            path.clear();
        }

        return path;
    }

    private VertexDTO validateAndFindRoot(UUID externalGraphId, UUID rootVertexId) throws GraphException {
        if (adjacentMatrixes.get(externalGraphId) == null) {
            GraphUtil.logAndThrowException(LOGGER, "Graph %s not found", externalGraphId.toString());
        }

        if (!isGraphContainVertex(externalGraphId, rootVertexId)) {
            GraphUtil.logAndThrowException(LOGGER, "Graph %s does not contain vertex %s",
                    externalGraphId.toString(), rootVertexId.toString());
        }

        return adjacentMatrixes.get(externalGraphId).keySet()
                .stream()
                .filter(vertex -> vertex.getExternalId().equals(rootVertexId))
                .findFirst()
                .get();

    }

    private VertexDTO validateAndFindRoot(UUID externalGraphId, UUID fromVertexId, UUID toVertexId) throws GraphException {
        if (adjacentMatrixes.get(externalGraphId) == null) {
            GraphUtil.logAndThrowException(LOGGER, "Graph %s not found", externalGraphId.toString());
        }

        if (!isGraphContainVertex(externalGraphId, fromVertexId)) {
            GraphUtil.logAndThrowException(LOGGER, "Graph %s does not contain vertex %s",
                    externalGraphId.toString(), fromVertexId.toString());
        }

        if (!isGraphContainVertex(externalGraphId, toVertexId)) {
            GraphUtil.logAndThrowException(LOGGER, "Graph %s does not contain vertex %s",
                    externalGraphId.toString(), toVertexId.toString());
        }

        return adjacentMatrixes.get(externalGraphId).keySet()
                .stream()
                .filter(vertex -> vertex.getExternalId().equals(fromVertexId))
                .findFirst()
                .get();

    }

    private boolean isGraphContainVertex(UUID externalGraphId, UUID rootVertexId) {
        return adjacentMatrixes.get(externalGraphId).keySet().stream()
                .anyMatch(vertex -> vertex.getExternalId().equals(rootVertexId));
    }
}
